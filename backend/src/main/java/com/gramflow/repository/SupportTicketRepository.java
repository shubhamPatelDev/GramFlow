package com.gramflow.repository;

import com.gramflow.entity.SupportTicket;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class SupportTicketRepository {

    private final Firestore firestore;

    public SupportTicketRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public SupportTicket save(SupportTicket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(UUID.randomUUID().toString());
        }
        firestore.collection("support_tickets").document(ticket.getId()).set(ticket);
        return ticket;
    }
}
