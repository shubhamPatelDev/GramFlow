package com.gramflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSubscriptionResponse {
    private String subscriptionId;
}
