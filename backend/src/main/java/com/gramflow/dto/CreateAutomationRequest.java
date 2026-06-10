package com.gramflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAutomationRequest {
    @NotBlank(message = "Media ID is required")
    private String mediaId;

    private String mediaUrl;
    
    private String mediaCaption;

    @NotBlank(message = "Trigger keyword is required")
    private String triggerKeyword;

    @NotBlank(message = "Reply message is required")
    private String replyMessage;
}
