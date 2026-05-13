package com.turno.loanOriginationSystem.repo;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanRepository
        extends JpaRepository<LoanApplication, Long> {

    Optional<LoanApplication> findByIdempotencyKey(
            String idempotencyKey
    );

    Optional<LoanApplication> findByLoanId(
            String loanId
    );

    Page<LoanApplication> findByApplicationStatus(
            ApplicationStatus status,
            Pageable pageable
    );

    @Query(value = """
            SELECT *
            FROM loan_applications
            WHERE application_status = :status
            ORDER BY created_at
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true)
    List<LoanApplication> fetchLoansForProcessing(
            @Param("status") String status,
            @Param("limit") int limit
    );
    @Query("""
            SELECT COUNT(l)
            FROM LoanApplication l
            WHERE l.customerPhone = :phone
            AND (
                l.applicationStatus = 'APPROVED_BY_SYSTEM'
                OR l.applicationStatus = 'APPROVED_BY_AGENT'
            )
            """)
    int countApprovedLoans(
            @Param("phone") String phone
    );

    @Query("""
            SELECT COUNT(l)
            FROM LoanApplication l
            WHERE l.customerPhone = :phone
            AND (
                l.applicationStatus = 'REJECTED_BY_SYSTEM'
                OR l.applicationStatus = 'REJECTED_BY_AGENT'
            )
            """)
    int countRejectedLoans(
            @Param("phone") String phone
    );

    @Query("""
            SELECT COUNT(l)
            FROM LoanApplication l
            WHERE l.customerPhone = :phone
            AND l.createdAt >= :time
            """)
    int countRecentApplications(
            @Param("phone") String phone,
            @Param("time") LocalDateTime time
    );
}
