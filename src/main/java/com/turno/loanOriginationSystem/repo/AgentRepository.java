package com.turno.loanOriginationSystem.repo;

import com.turno.loanOriginationSystem.entities.Agent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface AgentRepository
        extends JpaRepository<Agent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
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
