package com.gramflow.service;

import com.gramflow.dto.MediaResponse;
import com.gramflow.entity.InstagramAccount;
import com.gramflow.entity.User;

import java.util.List;

public interface InstagramService {
    InstagramAccount connectAccount(User user, String shortLivedToken);
    List<MediaResponse> fetchMedia(User user);
    InstagramAccount getConnectedAccount(User user);
    void sendPrivateReply(String commentId, String replyMessage, String pageAccessToken);
}
