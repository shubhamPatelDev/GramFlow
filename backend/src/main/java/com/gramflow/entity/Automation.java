package com.gramflow.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Automation {

    private String id;
    private String userId;
    private String instagramAccountId;
    private String mediaId;
    private String mediaUrl;
    private String mediaCaption;
    private String triggerKeyword;
    private String replyMessage;
    private boolean active;
    private LocalDateTime createdAt;
    
    @Builder.Default
    private Integer repliesSent = 0;
}
