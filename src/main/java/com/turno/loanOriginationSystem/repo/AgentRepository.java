package com.turno.loanOriginationSystem.repo;

import com.turno.loanOriginationSystem.entities.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AgentRepository
        extends JpaRepository<Agent, Long> {

    @Query(value = """
            SELECT *
            FROM agents
            WHERE is_available = true
            AND active_loans < max_capacity
            ORDER BY active_loans ASC
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true)
    Optional<Agent> fetchAvailableAgent();
}
