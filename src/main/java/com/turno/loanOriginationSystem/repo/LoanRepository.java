package com.turno.loanOriginationSystem.repo;

import com.turno.loanOriginationSystem.entities.LoanApplications;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface LoanRepository
        extends JpaRepository<LoanApplications, Long> {

    Optional<LoanApplications> findByIdempotencyKey(
            String idempotencyKey
    );

    Optional<LoanApplications> findByLoanId(
            String loanId
    );

    Page<LoanApplications> findByApplicationStatus(
            ApplicationStatus status,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = """
            SELECT *
            FROM loan_applications
            WHERE application_status = :status
            ORDER BY created_at
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """,
            nativeQuery = true)
    List<LoanApplications> fetchLoansForProcessing(
            @Param("status") String status,
            @Param("limit") int limit
    );
}
