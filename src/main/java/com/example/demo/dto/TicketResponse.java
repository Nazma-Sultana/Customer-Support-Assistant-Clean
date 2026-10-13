package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.TicketStatus;

public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private String aiSummary;
    private String aiPriority;
    private String aiSuggestedResolution;
    private String resolutionNotes;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private String createdByUsername;      // only the username, not the full User object
    private String assignedAgentUsername;  // null if not yet assigned

    public TicketResponse() {}

    public TicketResponse(Long id, String title, String description,
                          String aiSummary, String aiPriority, String aiSuggestedResolution,
                          String resolutionNotes, TicketStatus status, LocalDateTime createdAt,
                          String createdByUsername, String assignedAgentUsername) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.aiSummary = aiSummary;
        this.aiPriority = aiPriority;
        this.aiSuggestedResolution = aiSuggestedResolution;
        this.resolutionNotes = resolutionNotes;
        this.status = status;
        this.createdAt = createdAt;
        this.createdByUsername = createdByUsername;
        this.assignedAgentUsername = assignedAgentUsername;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getAiPriority() { return aiPriority; }
    public void setAiPriority(String aiPriority) { this.aiPriority = aiPriority; }

    public String getAiSuggestedResolution() { return aiSuggestedResolution; }
    public void setAiSuggestedResolution(String aiSuggestedResolution) { this.aiSuggestedResolution = aiSuggestedResolution; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public String getAssignedAgentUsername() { return assignedAgentUsername; }
    public void setAssignedAgentUsername(String assignedAgentUsername) { this.assignedAgentUsername = assignedAgentUsername; }
}
