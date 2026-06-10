package com.gramflow.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class SupportRequest {
    @NotBlank
    private String subject;
    
    @NotBlank
    private String message;
}
