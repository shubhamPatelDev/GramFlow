package com.gramflow.repository;

import com.gramflow.entity.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private final Firestore firestore;

    public UserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(java.util.UUID.randomUUID().toString());
        }
        firestore.collection("users").document(user.getId()).set(user);
        return user;
    }

    public Optional<User> findById(String id) {
        try {
            DocumentSnapshot doc = firestore.collection("users").document(id).get().get();
            if (doc.exists()) {
                return Optional.of(doc.toObject(User.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public Optional<User> findByEmail(String email) {
        try {
            QuerySnapshot query = firestore.collection("users").whereEqualTo("email", email).get().get();
            if (!query.isEmpty()) {
                return Optional.of(query.getDocuments().get(0).toObject(User.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> findByVerificationToken(String token) {
        try {
            QuerySnapshot query = firestore.collection("users").whereEqualTo("verificationToken", token).get().get();
            if (!query.isEmpty()) {
                return Optional.of(query.getDocuments().get(0).toObject(User.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> findByRazorpayCustomerId(String razorpayCustomerId) {
        try {
            QuerySnapshot query = firestore.collection("users").whereEqualTo("razorpayCustomerId", razorpayCustomerId).get().get();
            if (!query.isEmpty()) {
                return Optional.of(query.getDocuments().get(0).toObject(User.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> findByRazorpaySubscriptionId(String razorpaySubscriptionId) {
        try {
            QuerySnapshot query = firestore.collection("users").whereEqualTo("razorpaySubscriptionId", razorpaySubscriptionId).get().get();
            if (!query.isEmpty()) {
                return Optional.of(query.getDocuments().get(0).toObject(User.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
