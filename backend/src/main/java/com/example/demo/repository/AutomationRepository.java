package com.example.demo.repository;

import com.example.demo.entity.Automation;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class AutomationRepository {

    private final Firestore firestore;

    public AutomationRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public Automation save(Automation automation) {
        if (automation.getId() == null) {
            automation.setId(java.util.UUID.randomUUID().toString());
        }
        firestore.collection("automations").document(automation.getId()).set(automation);
        return automation;
    }

    public Optional<Automation> findById(String id) {
        try {
            DocumentSnapshot doc = firestore.collection("automations").document(id).get().get();
            if (doc.exists()) {
                return Optional.of(doc.toObject(Automation.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void deleteById(String id) {
        firestore.collection("automations").document(id).delete();
    }

    public void deleteAll(List<Automation> automations) {
        WriteBatch batch = firestore.batch();
        for (Automation automation : automations) {
            DocumentReference docRef = firestore.collection("automations").document(automation.getId());
            batch.delete(docRef);
        }
        batch.commit();
    }

    public List<Automation> findByUserId(String userId) {
        try {
            QuerySnapshot query = firestore.collection("automations").whereEqualTo("userId", userId).get().get();
            return query.getDocuments().stream().map(doc -> doc.toObject(Automation.class)).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public List<Automation> findByInstagramAccountId(String instagramAccountId) {
        try {
            QuerySnapshot query = firestore.collection("automations").whereEqualTo("instagramAccountId", instagramAccountId).get().get();
            return query.getDocuments().stream().map(doc -> doc.toObject(Automation.class)).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public List<Automation> findByInstagramAccountIdAndMediaIdAndActiveTrue(String instagramAccountId, String mediaId) {
        try {
            QuerySnapshot query = firestore.collection("automations")
                    .whereEqualTo("instagramAccountId", instagramAccountId)
                    .whereEqualTo("mediaId", mediaId)
                    .whereEqualTo("active", true)
                    .get().get();
            return query.getDocuments().stream().map(doc -> doc.toObject(Automation.class)).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return List.of();
    }
    
    public long countByUserIdAndActiveTrue(String userId) {
        try {
            QuerySnapshot query = firestore.collection("automations")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("active", true)
                    .get().get();
            return query.size();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean existsByUserIdAndMediaIdAndActiveTrue(String userId, String mediaId) {
        try {
            QuerySnapshot query = firestore.collection("automations")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("mediaId", mediaId)
                    .whereEqualTo("active", true)
                    .get().get();
            return !query.isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Optional<Automation> findFirstByUserIdAndActiveTrue(String userId) {
        try {
            QuerySnapshot query = firestore.collection("automations")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("active", true)
                    .limit(1)
                    .get().get();
            if (!query.isEmpty()) {
                return Optional.of(query.getDocuments().get(0).toObject(Automation.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
