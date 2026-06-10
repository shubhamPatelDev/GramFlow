package com.gramflow.repository;

import com.gramflow.entity.InstagramAccount;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class InstagramAccountRepository {

    private final Firestore firestore;

    public InstagramAccountRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public InstagramAccount save(InstagramAccount account) {
        if (account.getId() == null) {
            account.setId(java.util.UUID.randomUUID().toString());
        }
        firestore.collection("instagram_accounts").document(account.getId()).set(account);
        return account;
    }

    public Optional<InstagramAccount> findById(String id) {
        try {
            DocumentSnapshot doc = firestore.collection("instagram_accounts").document(id).get().get();
            if (doc.exists()) {
                return Optional.of(doc.toObject(InstagramAccount.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<InstagramAccount> findByUserId(String userId) {
        try {
            QuerySnapshot query = firestore.collection("instagram_accounts").whereEqualTo("userId", userId).get().get();
            if (!query.isEmpty()) {
                return Optional.of(query.getDocuments().get(0).toObject(InstagramAccount.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<InstagramAccount> findByFacebookUserId(String facebookUserId) {
        try {
            QuerySnapshot query = firestore.collection("instagram_accounts").whereEqualTo("facebookUserId", facebookUserId).get().get();
            if (!query.isEmpty()) {
                return Optional.of(query.getDocuments().get(0).toObject(InstagramAccount.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void delete(InstagramAccount account) {
        if (account != null && account.getId() != null) {
            firestore.collection("instagram_accounts").document(account.getId()).delete();
        }
    }
}
