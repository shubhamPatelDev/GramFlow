package com.example.demo.facade;

import com.example.demo.dto.ConnectAccountRequest;
import com.example.demo.dto.MediaResponse;
import com.example.demo.entity.InstagramAccount;

import java.util.List;

public interface InstagramFacade {
    InstagramAccount connectAccount(String email, ConnectAccountRequest request);
    List<MediaResponse> getMedia(String email);
    InstagramAccount getConnectedAccount(String email);
}
