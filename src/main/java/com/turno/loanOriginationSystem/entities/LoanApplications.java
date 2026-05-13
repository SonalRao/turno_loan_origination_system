package com.turno.loanOriginationSystem.entities;

import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.enums.LoanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "loan_applications",
        indexes = {

                @Index(
                        name = "idx_loan_status_created",
                        columnList = "applicationStatus, createdAt"
                ),

                @Index(
                        name = "idx_customer_phone",
                        columnList = "customerPhone"
                ),

                @Index(
                        name = "idx_assigned_agent",
                        columnList = "assignedAgentId"
                )
        },
        uniqueConstraints = {

                @UniqueConstraint(
                        name = "uk_idempotency_key",
                        columnNames = "idempotencyKey"
                )
        }
)
public class LoanApplications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loanId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private BigDecimal loanAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus applicationStatus;

    private Long assignedAgentId;

    @Column(nullable = false)
    private String idempotencyKey;

    @Version
    private Long version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        this.loanId = UUID.randomUUID().toString();

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}