package com.example.demo.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstagramAccount {

    private String id; // Instagram Business Account ID
    private String userId;
    private String facebookUserId;
    private String pageId;
    private String pageName;
    private String pageAccessToken;
    private String instagramUsername;
    private String instagramProfilePicture;
}
