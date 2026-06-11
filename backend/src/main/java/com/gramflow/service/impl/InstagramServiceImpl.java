package com.gramflow.service.impl;

import com.gramflow.entity.InstagramAccount;
import com.gramflow.entity.User;
import com.gramflow.exception.CustomException;
import com.gramflow.dto.MediaResponse;
import com.gramflow.repository.InstagramAccountRepository;
import com.gramflow.service.InstagramService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InstagramServiceImpl implements InstagramService {

    private final InstagramAccountRepository instagramAccountRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InstagramServiceImpl(InstagramAccountRepository instagramAccountRepository) {
        this.instagramAccountRepository = instagramAccountRepository;
    }

    @Value("${app.facebook.client-id:}")
    private String clientId;

    @Value("${app.facebook.client-secret:}")
    private String clientSecret;

    private static final String GRAPH_API_BASE = "https://graph.facebook.com/v19.0";

    @Override
    public InstagramAccount connectAccount(User user, String shortLivedToken) {
        log.info("Connecting Instagram account for user: {}", user.getEmail());
        
        // 1. Exchange short-lived token for long-lived user access token
        String longLivedUserToken = exchangeForLongLivedToken(shortLivedToken);

        // Fetch the Facebook App-Scoped User ID (Required for Data Deletion Webhooks)
        String fbUserId = "";
        try {
            ResponseEntity<String> meResponse = restTemplate.getForEntity(GRAPH_API_BASE + "/me?fields=id&access_token=" + longLivedUserToken, String.class);
            JsonNode meNode = objectMapper.readTree(meResponse.getBody());
            fbUserId = meNode.has("id") ? meNode.get("id").asText() : "";
            log.info("Fetched Facebook User ID: {}", fbUserId);
        } catch (Exception e) {
            log.error("Failed to fetch Facebook User ID", e);
        }

        // 2. Fetch Facebook pages and associated Instagram Business Accounts
        java.net.URI pagesUri = UriComponentsBuilder.fromHttpUrl(GRAPH_API_BASE + "/me/accounts")
                .queryParam("fields", "name,access_token,instagram_business_account{id,username,profile_picture_url}")
                .queryParam("access_token", longLivedUserToken)
                .build().toUri();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(pagesUri, String.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new CustomException("Failed to fetch connected Facebook Pages", HttpStatus.BAD_REQUEST);
            }

            log.info("Facebook /me/accounts RAW response: {}", response.getBody());

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.get("data");

            if (dataNode == null || !dataNode.isArray() || dataNode.isEmpty()) {
                throw new CustomException("No Facebook Pages found. Make sure you are a page admin.", HttpStatus.BAD_REQUEST);
            }

            // Iterate through pages to find the one linked to an Instagram account
            for (JsonNode pageNode : dataNode) {
                if (pageNode.has("instagram_business_account")) {
                    JsonNode igNode = pageNode.get("instagram_business_account");
                    String pageId = pageNode.get("id").asText();
                    String pageName = pageNode.get("name").asText();
                    String pageAccessToken = pageNode.get("access_token").asText();
                    
                    String igAccountId = igNode.get("id").asText();
                    String igUsername = igNode.get("username").asText();
                    String igProfilePic = igNode.has("profile_picture_url") 
                            ? igNode.get("profile_picture_url").asText() 
                            : "";

                    // Check if this Instagram account is already linked to another user
                    java.util.Optional<InstagramAccount> existingAccount = instagramAccountRepository.findById(igAccountId);
                    if (existingAccount.isPresent() && !existingAccount.get().getUserId().equals(user.getId())) {
                        throw new CustomException("This Instagram account is already connected to another GramFlow account.", HttpStatus.CONFLICT);
                    }

                    // Create or update account info
                    InstagramAccount account = instagramAccountRepository.findByUserId(user.getId())
                            .orElse(new InstagramAccount());

                    account.setId(igAccountId);
                    account.setUserId(user.getId());
                    account.setPageId(pageId);
                    account.setPageName(pageName);
                    account.setPageAccessToken(pageAccessToken);
                    account.setInstagramUsername(igUsername);
                    account.setInstagramProfilePicture(igProfilePic);
                    account.setFacebookUserId(fbUserId);

                    // Subscribe the App to the Facebook Page's Webhooks
                    java.net.URI subscribeUri = UriComponentsBuilder.fromHttpUrl(GRAPH_API_BASE + "/" + pageId + "/subscribed_apps")
                            .queryParam("subscribed_fields", "feed")
                            .queryParam("access_token", pageAccessToken)
                            .build().toUri();
                    
                    try {
                        ResponseEntity<String> subResponse = restTemplate.postForEntity(subscribeUri, null, String.class);
                        log.info("Subscribed to Page webhooks successfully: {}", subResponse.getBody());
                    } catch (Exception e) {
                        log.error("Failed to subscribe app to page webhooks", e);
                        // We continue even if this fails, but log the error prominently
                    }

                    log.info("Linked Instagram account: {} for user: {}", igUsername, user.getEmail());
                    return instagramAccountRepository.save(account);
                }
            }

            throw new CustomException("No Instagram Business Account linked to your Facebook Page. Please link one first.", HttpStatus.BAD_REQUEST);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("HTTP Error from Facebook during connect: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException("Failed to connect Instagram account: " + e.getResponseBodyAsString(), HttpStatus.BAD_REQUEST);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error connecting Instagram account", e);
            throw new CustomException("Failed to complete Instagram connection", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<MediaResponse> fetchMedia(User user) {
        InstagramAccount account = instagramAccountRepository.findByUserId(user.getId())
                .orElse(null);
                
        if (account == null) {
            return new ArrayList<>(); // Return empty list instead of throwing 400 Bad Request
        }

        java.net.URI mediaUri = UriComponentsBuilder.fromHttpUrl(GRAPH_API_BASE + "/" + account.getId() + "/media")
                .queryParam("fields", "id,media_url,media_type,caption,permalink")
                .queryParam("access_token", account.getPageAccessToken())
                .build().toUri();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(mediaUri, String.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new CustomException("Failed to fetch Instagram posts", HttpStatus.BAD_REQUEST);
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.get("data");
            List<MediaResponse> mediaList = new ArrayList<>();

            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode media : dataNode) {
                    MediaResponse mr = new MediaResponse();
                    mr.setId(media.get("id").asText());
                    mr.setMediaType(media.has("media_type") ? media.get("media_type").asText() : "UNKNOWN");
                    mr.setMediaUrl(media.has("media_url") ? media.get("media_url").asText() : "");
                    mr.setCaption(media.has("caption") ? media.get("caption").asText() : "");
                    mr.setPermalink(media.has("permalink") ? media.get("permalink").asText() : "");
                    mediaList.add(mr);
                }
            }

            return mediaList;
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("HTTP Error from Facebook during fetchMedia: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException("Failed to fetch Instagram posts: " + e.getResponseBodyAsString(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error fetching media from Meta Graph API", e);
            throw new CustomException("Failed to retrieve posts from Instagram", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public InstagramAccount getConnectedAccount(User user) {
        return instagramAccountRepository.findByUserId(user.getId()).orElse(null);
    }

    @Override
    public void sendPrivateReply(String commentId, String replyMessage, String pageId, String pageAccessToken) {
        log.info("Sending private reply to comment: {} via Page ID: {}", commentId, pageId);

        java.net.URI replyUri = UriComponentsBuilder.fromHttpUrl(GRAPH_API_BASE + "/" + pageId + "/messages")
                .queryParam("access_token", pageAccessToken)
                .build().toUri();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Build the JSON payload as required by Meta for Instagram DMs
            Map<String, Object> recipient = Map.of("comment_id", commentId);
            Map<String, Object> message = Map.of("text", replyMessage);
            Map<String, Object> payload = Map.of("recipient", recipient, "message", message);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(replyUri, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Successfully sent private reply to comment {}", commentId);
            } else {
                log.error("Failed to send private reply, status code: {}, body: {}", response.getStatusCode(), response.getBody());
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("HTTP Error sending private reply: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error sending private reply via Meta Graph API", e);
        }
    }

    private String exchangeForLongLivedToken(String shortLivedToken) {
        java.net.URI tokenUri = UriComponentsBuilder.fromHttpUrl(GRAPH_API_BASE + "/oauth/access_token")
                .queryParam("grant_type", "fb_exchange_token")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("fb_exchange_token", shortLivedToken)
                .build().toUri();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(tokenUri, String.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new CustomException("Failed to exchange access token", HttpStatus.BAD_REQUEST);
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            if (!root.has("access_token")) {
                throw new CustomException("Access token missing in Facebook response", HttpStatus.BAD_REQUEST);
            }

            return root.get("access_token").asText();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("HTTP Error from Facebook during token exchange: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException("Failed to exchange access token: " + e.getResponseBodyAsString(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error exchanging for long lived token", e);
            throw new CustomException("Error exchanging for long lived token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
