package com.gramflow.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RazorpayPlan {
    private String id; // This will be the same as planId
    private String planId; // Razorpay Plan ID (e.g., plan_abcd1234)
    private String name;
    private int amount;
}
