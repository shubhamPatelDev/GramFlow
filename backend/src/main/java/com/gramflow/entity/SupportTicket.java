package com.gramflow.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicket {
    private String id;
    private String userId;
    private String email;
    private String subject;
    private String message;
    
    @Builder.Default
    private String status = "OPEN";
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
