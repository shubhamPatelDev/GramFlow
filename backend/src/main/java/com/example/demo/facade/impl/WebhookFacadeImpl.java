package com.example.demo.facade.impl;

import com.example.demo.facade.WebhookFacade;
import com.example.demo.service.WebhookService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebhookFacadeImpl implements WebhookFacade {

    private final WebhookService webhookService;

    @Override
    public void receiveWebhookEvent(String payload) {
        webhookService.processWebhookEvent(payload);
    }
}
