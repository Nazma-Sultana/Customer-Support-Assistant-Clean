package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Ticket;
import com.example.demo.model.User;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Find all tickets belonging to a specific user (for CUSTOMER)
    List<Ticket> findByUser(User user);

    // Find all tickets assigned to a specific agent (for AGENT)
    List<Ticket> findByAssignedAgent(User agent);

    // Paginated versions — same logic but returns a Page instead of List
    Page<Ticket> findByUser(User user, Pageable pageable);
    Page<Ticket> findByAssignedAgent(User agent, Pageable pageable);
    //and automatically adds LIMIT and OFFSET to the SQL query.
}
