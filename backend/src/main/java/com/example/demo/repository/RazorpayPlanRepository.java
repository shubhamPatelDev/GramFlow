package com.example.demo.repository;

import com.example.demo.entity.RazorpayPlan;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class RazorpayPlanRepository {

    private final Firestore firestore;

    public RazorpayPlanRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public RazorpayPlan save(RazorpayPlan plan) {
        if (plan.getId() == null) {
            plan.setId(java.util.UUID.randomUUID().toString());
        }
        firestore.collection("razorpay_plans").document(plan.getId()).set(plan);
        return plan;
    }

    public Optional<RazorpayPlan> findByName(String name) {
        try {
            QuerySnapshot query = firestore.collection("razorpay_plans").whereEqualTo("name", name).limit(1).get().get();
            if (!query.isEmpty()) {
                return Optional.of(query.getDocuments().get(0).toObject(RazorpayPlan.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
