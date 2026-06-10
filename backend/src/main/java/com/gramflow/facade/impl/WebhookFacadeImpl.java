package com.gramflow.facade.impl;

import com.gramflow.facade.WebhookFacade;
import com.gramflow.service.WebhookService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebhookFacadeImpl implements WebhookFacade {

    private final WebhookService webhookService;

    @Override
    public void receiveWebhookEvent(String payload) {
        webhookService.processWebhookEvent(payload);
    }
}
