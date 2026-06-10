package com.gramflow.service;

import com.gramflow.dto.CreateOrderRequest;
import com.gramflow.dto.CreateOrderResponse;
import com.gramflow.dto.CreateSubscriptionResponse;
import com.gramflow.dto.VerifyPaymentRequest;
import com.gramflow.dto.VerifySubscriptionRequest;
import com.gramflow.entity.RazorpayPlan;
import com.gramflow.entity.SubscriptionTier;
import com.gramflow.entity.User;
import com.gramflow.exception.CustomException;
import com.gramflow.repository.RazorpayPlanRepository;
import com.gramflow.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.Plan;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Subscription;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.webhook.secret:}")
    private String webhookSecret;

    private final UserRepository userRepository;
    private final RazorpayPlanRepository razorpayPlanRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RazorpayService(UserRepository userRepository, RazorpayPlanRepository razorpayPlanRepository) {
        this.userRepository = userRepository;
        this.razorpayPlanRepository = razorpayPlanRepository;
    }

    public CreateSubscriptionResponse createSubscription(String email) {
        try {
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);
            String planId = getOrCreateProPlan(razorpay);

            JSONObject subscriptionRequest = new JSONObject();
            subscriptionRequest.put("plan_id", planId);
            subscriptionRequest.put("total_count", 120); // E.g., 10 years of monthly billing
            subscriptionRequest.put("customer_notify", 1);

            Subscription subscription = razorpay.subscriptions.create(subscriptionRequest);
            String subscriptionId = subscription.get("id");

            return CreateSubscriptionResponse.builder().subscriptionId(subscriptionId).build();

        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay subscription", e);
            throw new CustomException("Failed to create subscription", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void verifySubscription(String email, VerifySubscriptionRequest request) {
        try {
            String generatedSignature = "";
            try {
                String payloadData = request.getRazorpayPaymentId() + "|" + request.getRazorpaySubscriptionId();
                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(keySecret.getBytes("UTF-8"), "HmacSHA256");
                sha256_HMAC.init(secret_key);
                byte[] hash = sha256_HMAC.doFinal(payloadData.getBytes("UTF-8"));
                generatedSignature = encodeHexString(hash);
            } catch (Exception e) {
                throw new CustomException("Failed to process signature", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            boolean isValid = generatedSignature.equals(request.getRazorpaySignature());

            if (!isValid) {
                throw new CustomException("Invalid subscription signature", HttpStatus.BAD_REQUEST);
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
            
            user.setSubscriptionTier(SubscriptionTier.PAID);
            user.setRazorpaySubscriptionId(request.getRazorpaySubscriptionId());
            user.setSubscriptionStatus("active");
            userRepository.save(user);

        } catch (Exception e) {
            log.error("Failed to verify Razorpay subscription signature", e);
            throw new CustomException("Failed to verify signature", HttpStatus.BAD_REQUEST);
        }
    }

    public void handleWebhook(String payload, String signature) {
        try {
            // Verify Webhook Signature
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = encodeHexString(hash);

            if (!expectedSignature.equals(signature)) {
                log.warn("Invalid Razorpay webhook signature");
                return;
            }

            JsonNode root = objectMapper.readTree(payload);
            String event = root.get("event").asText();
            JsonNode payloadNode = root.get("payload");

            if (payloadNode.has("subscription")) {
                JsonNode subNode = payloadNode.get("subscription").get("entity");
                String subId = subNode.get("id").asText();

                if ("subscription.charged".equals(event)) {
                    log.info("Subscription charged successfully: {}", subId);
                    updateSubscriptionStatus(subId, "active", SubscriptionTier.PAID);
                } else if ("subscription.halted".equals(event) || "subscription.cancelled".equals(event)) {
                    log.info("Subscription halted/cancelled: {}", subId);
                    updateSubscriptionStatus(subId, event.split("\\.")[1], SubscriptionTier.FREE);
                }
            }
        } catch (Exception e) {
            log.error("Error processing Razorpay webhook", e);
        }
    }

    private void updateSubscriptionStatus(String subId, String status, SubscriptionTier tier) {
        userRepository.findByRazorpaySubscriptionId(subId).ifPresent(user -> {
            user.setSubscriptionStatus(status);
            user.setSubscriptionTier(tier);
            userRepository.save(user);
        });
    }

    private String getOrCreateProPlan(RazorpayClient razorpay) throws RazorpayException {
        RazorpayPlan existingPlan = razorpayPlanRepository.findByName("Pro Monthly").orElse(null);
        if (existingPlan != null) {
            return existingPlan.getPlanId();
        }

        JSONObject planRequest = new JSONObject();
        planRequest.put("period", "monthly");
        planRequest.put("interval", 1);
        
        JSONObject item = new JSONObject();
        item.put("name", "Pro Monthly");
        item.put("amount", 2900); // 29.00 USD/INR equivalent in paise
        item.put("currency", "USD"); // Or INR
        planRequest.put("item", item);

        Plan plan = razorpay.plans.create(planRequest);
        String planId = plan.get("id");

        RazorpayPlan newPlan = RazorpayPlan.builder()
                .planId(planId)
                .name("Pro Monthly")
                .amount(2900)
                .build();
        razorpayPlanRepository.save(newPlan);

        return planId;
    }

    // Retaining old one-time order methods just in case
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        if (request.getAmount() < 100) {
            throw new CustomException("Minimum amount must be 100 paise", HttpStatus.BAD_REQUEST);
        }
        try {
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount());
            orderRequest.put("currency", request.getCurrency());
            orderRequest.put("receipt", request.getReceipt());
            Order order = razorpay.orders.create(orderRequest);
            return CreateOrderResponse.builder()
                    .orderId(order.get("id"))
                    .amount(order.get("amount"))
                    .currency(order.get("currency"))
                    .build();
        } catch (RazorpayException e) {
            throw new CustomException("Failed to create order", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void verifyPayment(String email, VerifyPaymentRequest request) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());
            if (!Utils.verifyPaymentSignature(options, keySecret)) {
                throw new CustomException("Invalid payment signature", HttpStatus.BAD_REQUEST);
            }
            User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
            user.setSubscriptionTier(SubscriptionTier.PAID);
            userRepository.save(user);
        } catch (RazorpayException e) {
            throw new CustomException("Failed to verify signature", HttpStatus.BAD_REQUEST);
        }
    }

    private String encodeHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (byte b : byteArray) {
            hexStringBuffer.append(byteToHex(b));
        }
        return hexStringBuffer.toString();
    }
    private String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
}
