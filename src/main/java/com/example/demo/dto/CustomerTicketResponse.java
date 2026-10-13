package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.TicketStatus;

public class CustomerTicketResponse {

    private Long id;
    private String title;
    private String description;
    private String resolutionNotes;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private String createdByUsername;
    private String assignedAgentUsername;

    public CustomerTicketResponse() {}

    public CustomerTicketResponse(
            Long id,
            String title,
            String description,
            String resolutionNotes,
            TicketStatus status,
            LocalDateTime createdAt,
            String createdByUsername,
            String assignedAgentUsername) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.resolutionNotes = resolutionNotes;
        this.status = status;
        this.createdAt = createdAt;
        this.createdByUsername = createdByUsername;
        this.assignedAgentUsername = assignedAgentUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public String getAssignedAgentUsername() {
        return assignedAgentUsername;
    }

    public void setAssignedAgentUsername(String assignedAgentUsername) {
        this.assignedAgentUsername = assignedAgentUsername;
    }
}