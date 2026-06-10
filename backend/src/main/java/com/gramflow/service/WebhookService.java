package com.gramflow.service;

public interface WebhookService {
    void processWebhookEvent(String payload);
}
