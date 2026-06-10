package com.gramflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutomationResponse {
    private UUID id;
    private String mediaId;
    private String mediaUrl;
    private String mediaCaption;
    private String triggerKeyword;
    private String replyMessage;
    private boolean active;
    private String createdAt;
    private Integer repliesSent;
}
