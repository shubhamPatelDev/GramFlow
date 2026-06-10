package com.example.demo.dto;

import lombok.Data;

@Data
public class VerifyPaymentRequest {
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}
