package com.turno.loanOriginationSystem.repo;

import com.turno.loanOriginationSystem.entities.Agents;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface AgentRepository
        extends JpaRepository<Agents, Long> {

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
    Optional<Agents> fetchAvailableAgent();
}
