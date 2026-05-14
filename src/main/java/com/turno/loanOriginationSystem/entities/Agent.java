package com.turno.loanOriginationSystem.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(
        name = "agents",
        indexes = {
                @Index(
                        name = "idx_agent_manager",
                        columnList = "managerId"
                ),
                @Index(
                        name = "idx_agent_availability",
                        columnList = "isAvailable, activeLoans"
                )
        }
)
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private Agent manager;

    private Integer activeLoans;

    private Integer maxCapacity;

    private Boolean isAvailable;
}