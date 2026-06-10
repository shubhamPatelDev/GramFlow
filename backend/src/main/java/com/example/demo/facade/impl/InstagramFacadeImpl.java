package com.example.demo.facade.impl;

import com.example.demo.dto.ConnectAccountRequest;
import com.example.demo.dto.MediaResponse;
import com.example.demo.entity.InstagramAccount;
import com.example.demo.entity.User;
import com.example.demo.facade.InstagramFacade;
import com.example.demo.service.InstagramService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class InstagramFacadeImpl implements InstagramFacade {

    private final InstagramService instagramService;
    private final UserService userService;

    @Override
    public InstagramAccount connectAccount(String email, ConnectAccountRequest request) {
        User user = userService.findByEmail(email);
        return instagramService.connectAccount(user, request.getAccessToken());
    }

    @Override
    public List<MediaResponse> getMedia(String email) {
        User user = userService.findByEmail(email);
        return instagramService.fetchMedia(user);
    }

    @Override
    public InstagramAccount getConnectedAccount(String email) {
        User user = userService.findByEmail(email);
        return instagramService.getConnectedAccount(user);
    }
}
