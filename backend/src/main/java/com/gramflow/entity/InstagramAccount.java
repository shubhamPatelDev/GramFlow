package com.gramflow.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstagramAccount {

    @DocumentId
    private String id; // Instagram Business Account ID
    private String userId;
    private String facebookUserId;
    private String pageId;
    private String pageName;
    private String pageAccessToken;
    private String instagramUsername;
    private String instagramProfilePicture;
}
