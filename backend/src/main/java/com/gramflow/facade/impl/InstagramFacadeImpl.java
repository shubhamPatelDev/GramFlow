package com.gramflow.facade.impl;

import com.gramflow.dto.ConnectAccountRequest;
import com.gramflow.dto.MediaResponse;
import com.gramflow.entity.InstagramAccount;
import com.gramflow.entity.User;
import com.gramflow.facade.InstagramFacade;
import com.gramflow.service.InstagramService;
import com.gramflow.service.UserService;
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
