package com.example.demo.mapper;

import com.example.demo.dto.AutomationResponse;
import com.example.demo.dto.CreateAutomationRequest;
import com.example.demo.entity.Automation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AutomationMapper {
    AutomationMapper INSTANCE = Mappers.getMapper(AutomationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "instagramAccountId", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    Automation createRequestToAutomation(CreateAutomationRequest request);

    @Mapping(target = "createdAt", expression = "java(automation.getCreatedAt() != null ? automation.getCreatedAt().toString() : null)")
    AutomationResponse automationToResponse(Automation automation);
}
