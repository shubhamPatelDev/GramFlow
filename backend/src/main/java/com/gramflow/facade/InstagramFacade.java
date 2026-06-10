package com.gramflow.facade;

import com.gramflow.dto.ConnectAccountRequest;
import com.gramflow.dto.MediaResponse;
import com.gramflow.entity.InstagramAccount;

import java.util.List;

public interface InstagramFacade {
    InstagramAccount connectAccount(String email, ConnectAccountRequest request);
    List<MediaResponse> getMedia(String email);
    InstagramAccount getConnectedAccount(String email);
}
