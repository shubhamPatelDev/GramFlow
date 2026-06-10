package com.example.demo.service;

import com.example.demo.dto.MediaResponse;
import com.example.demo.entity.InstagramAccount;
import com.example.demo.entity.User;

import java.util.List;

public interface InstagramService {
    InstagramAccount connectAccount(User user, String shortLivedToken);
    List<MediaResponse> fetchMedia(User user);
    InstagramAccount getConnectedAccount(User user);
    void sendPrivateReply(String commentId, String replyMessage, String pageAccessToken);
}
