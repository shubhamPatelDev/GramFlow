package com.gramflow.facade;

public interface WebhookFacade {
    void receiveWebhookEvent(String payload);
}
