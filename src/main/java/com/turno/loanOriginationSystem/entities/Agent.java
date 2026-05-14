package com.turno.loanOriginationSystem.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
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

    @Column(nullable = false)
    private Long managerId;

    private Integer activeLoans;

    private Integer maxCapacity;

    private Boolean isAvailable;
}