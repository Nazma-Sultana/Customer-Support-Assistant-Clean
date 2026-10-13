package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CreateTicketRequest;
import com.example.demo.dto.TicketResponse;
import com.example.demo.dto.ai.AiAnalysisResult;
import com.example.demo.exception.APIException;
import com.example.demo.model.Role;
import com.example.demo.model.Ticket;
import com.example.demo.model.TicketStatus;
import com.example.demo.model.User;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ai.AiAnalysisService;
import com.example.demo.dto.CustomerTicketResponse;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AiAnalysisService aiAnalysisService;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository,
                          AiAnalysisService aiAnalysisService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.aiAnalysisService = aiAnalysisService;
    }

    private TicketResponse toResponse(Ticket ticket) {
        String assignedAgentUsername = ticket.getAssignedAgent() != null
                ? ticket.getAssignedAgent().getUsername() : null;
        return new TicketResponse(ticket.getId(), ticket.getTitle(), ticket.getDescription(),
                ticket.getAiSummary(), ticket.getAiPriority(), ticket.getAiSuggestedResolution(),
                ticket.getResolutionNotes(), ticket.getStatus(), ticket.getCreatedAt(),
                ticket.getUser().getUsername(), assignedAgentUsername);
    }

    public TicketResponse createTicket(CreateTicketRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new APIException("User not found: " + username));
        AiAnalysisResult ai = aiAnalysisService.analyze(request.getTitle(), request.getDescription());
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setUser(user);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAiSummary(ai.summary());
        ticket.setAiPriority(ai.priority());
        ticket.setAiSuggestedResolution(ai.suggestedResolution());
        return toResponse(ticketRepository.save(ticket));
    }
private CustomerTicketResponse toCustomerResponse(Ticket ticket) {

    String assignedAgentUsername = ticket.getAssignedAgent() != null
            ? ticket.getAssignedAgent().getUsername()
            : null;

    return new CustomerTicketResponse(
            ticket.getId(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getResolutionNotes(),
            ticket.getStatus(),
            ticket.getCreatedAt(),
            ticket.getUser().getUsername(),
            assignedAgentUsername
    );
}
    public Page<?> getMyTickets(Pageable pageable) {

    String username =
            SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();

    User user =
            userRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new APIException("User not found: " + username));

    Page<Ticket> ticketPage;

    if (user.getRole() == Role.ADMIN) {

        // ADMIN → all tickets
        ticketPage = ticketRepository.findAll(pageable);

        // ADMIN can see AI information
        return ticketPage.map(this::toResponse);

    } else if (user.getRole() == Role.AGENT) {

        // AGENT → only tickets assigned to them
        ticketPage = ticketRepository.findByAssignedAgent(user, pageable);

        // AGENT can see AI information
        return ticketPage.map(this::toResponse);

    } else {

        // CUSTOMER → only tickets created by them
        ticketPage = ticketRepository.findByUser(user, pageable);

        // CUSTOMER must NOT see AI information
        return ticketPage.map(this::toCustomerResponse);
    }
}
    public TicketResponse assignTicket(Long ticketId, String agentUsername) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new APIException("User not found: " + currentUsername));
        if (currentUser.getRole() != Role.ADMIN) {
            throw new APIException("Access denied: only ADMIN can assign tickets");
        }
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new APIException("Ticket not found: " + ticketId));
        User agent = userRepository.findByUsername(agentUsername)
                .orElseThrow(() -> new APIException("Agent not found: " + agentUsername));
        ticket.setAssignedAgent(agent);
        return toResponse(ticketRepository.save(ticket));
    }

    public TicketResponse updateTicketStatus(Long ticketId, TicketStatus newStatus, String resolutionNotes) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new APIException("User not found: " + currentUsername));
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new APIException("Ticket not found: " + ticketId));
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isAssignedAgent = ticket.getAssignedAgent() != null
                && ticket.getAssignedAgent().getUsername().equals(currentUsername);
        if (!isAdmin && !isAssignedAgent) {
            throw new APIException("Access denied: you are not assigned to this ticket");
        }
        ticket.setStatus(newStatus);
        if (resolutionNotes != null) {
            ticket.setResolutionNotes(resolutionNotes);
        }
        return toResponse(ticketRepository.save(ticket));
    }
}