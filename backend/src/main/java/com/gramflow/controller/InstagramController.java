package com.gramflow.controller;

import com.gramflow.dto.ConnectAccountRequest;
import com.gramflow.dto.MediaResponse;
import com.gramflow.entity.InstagramAccount;
import com.gramflow.facade.InstagramFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instagram")
@RequiredArgsConstructor
public class InstagramController {

    private final InstagramFacade instagramFacade;

    @PostMapping("/connect")
    public ResponseEntity<InstagramAccount> connectAccount(
            Authentication authentication,
            @Valid @RequestBody ConnectAccountRequest request
    ) {
        InstagramAccount connected = instagramFacade.connectAccount(authentication.getName(), request);
        return ResponseEntity.ok(connected);
    }

    @GetMapping("/media")
    public ResponseEntity<List<MediaResponse>> getMedia(Authentication authentication) {
        List<MediaResponse> mediaList = instagramFacade.getMedia(authentication.getName());
        return ResponseEntity.ok(mediaList);
    }

    @GetMapping("/account")
    public ResponseEntity<InstagramAccount> getConnectedAccount(Authentication authentication) {
        InstagramAccount account = instagramFacade.getConnectedAccount(authentication.getName());
        return ResponseEntity.ok(account);
    }
}
