package com.gramflow.mapper;

import com.gramflow.dto.AuthResponse;
import com.gramflow.dto.SignupRequest;
import com.gramflow.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Mapping for signup request to user entity, ignoring generated fields
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "subscriptionTier", constant = "FREE")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "razorpayCustomerId", ignore = true)
    @Mapping(target = "razorpaySubscriptionId", ignore = true)
    @Mapping(target = "subscriptionStatus", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    User signupRequestToUser(SignupRequest request);

    // Mapping user entity to auth response
    @Mapping(target = "subscriptionTier", expression = "java(user.getSubscriptionTier().name())")
    AuthResponse userToAuthResponse(User user, String token);
}
