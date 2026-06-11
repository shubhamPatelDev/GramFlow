package com.gramflow.service;

import com.gramflow.entity.SubscriptionTier;
import com.gramflow.entity.User;
import com.gramflow.repository.UserRepository;
import com.razorpay.Plan;
import com.razorpay.RazorpayClient;
import com.razorpay.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    // Hardcoded for now. In production, create this in Razorpay dashboard.
    private static final String PLAN_ID_PRO = "plan_dummy123";

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() {
        try {
            this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            log.info("Razorpay client initialized.");
        } catch (Exception e) {
            log.error("Failed to initialize Razorpay client", e);
        }
    }

    public String createSubscriptionLink(String userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            JSONObject options = new JSONObject();
            options.put("plan_id", PLAN_ID_PRO);
            options.put("total_count", 120); // 10 years of monthly billing
            options.put("quantity", 1);
            options.put("customer_notify", 1);

            Subscription subscription = razorpayClient.subscriptions.create(options);

            user.setRazorpaySubscriptionId(subscription.get("id"));
            userRepository.save(user);

            return subscription.get("id");

        } catch (Exception e) {
            log.error("Failed to create Razorpay subscription link", e);
            throw new RuntimeException("Payment service unavailable", e);
        }
    }

    public void handleSubscriptionCharged(String subscriptionId) {
        userRepository.findByRazorpaySubscriptionId(subscriptionId).ifPresent(user -> {
            user.setSubscriptionTier(SubscriptionTier.PAID);
            user.setSubscriptionStatus("active");
            user.setSubscriptionValidUntil(LocalDateTime.now().plusDays(30));
            userRepository.save(user);
            log.info("Subscription charged for user {}. Extended valid until {}", user.getEmail(), user.getSubscriptionValidUntil());
        });
    }

    public void handleSubscriptionCancelled(String subscriptionId) {
        userRepository.findByRazorpaySubscriptionId(subscriptionId).ifPresent(user -> {
            user.setSubscriptionStatus("cancelled");
            userRepository.save(user);
            log.info("Subscription cancelled for user {}", user.getEmail());
            // We do NOT change the ValidUntil date here. 
            // They get to use the remainder of the month they paid for!
        });
    }
}
