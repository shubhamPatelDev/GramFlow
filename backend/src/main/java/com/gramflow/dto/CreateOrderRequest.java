package com.gramflow.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private int amount; // in paise
    private String currency = "INR";
    private String receipt;
}
