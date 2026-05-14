package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.enums.LoanType;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanDecisionServiceTest {
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanDecisionService loanDecisionService;

    private LoanApplication loan;

    @BeforeEach
    void setUp() {

        loan = LoanApplication.builder()
                .customerPhone("9999999999")
                .loanAmount(BigDecimal.valueOf(50000))
                .loanType(LoanType.PERSONAL)
                .build();
    }

    @Test
    void shouldAutoApproveSmallLoan() {

        when(loanRepository.countApprovedLoans(any()))
                .thenReturn(0);

        when(loanRepository.countRejectedLoans(any()))
                .thenReturn(0);

        when(loanRepository.countRecentApplications(any(), any(LocalDateTime.class)))
                .thenReturn(0);

        ApplicationStatus status = loanDecisionService.profileEvaluation(loan);

        assertEquals(ApplicationStatus.APPROVED_BY_SYSTEM, status);
    }

    @Test
    void shouldMarkLargeBusinessLoanAsManualReview() {

        loan.setLoanType(LoanType.BUSINESS);
        loan.setLoanAmount(BigDecimal.valueOf(2500000));

        when(loanRepository.countApprovedLoans(any()))
                .thenReturn(0);

        when(loanRepository.countRejectedLoans(any()))
                .thenReturn(0);

        when(loanRepository.countRecentApplications(any(), any(LocalDateTime.class)))
                .thenReturn(0);

        ApplicationStatus status =
                loanDecisionService.profileEvaluation(loan);

        assertEquals(ApplicationStatus.UNDER_REVIEW, status);
    }

    @Test
    void shouldRejectCustomerWithTooManyRejections() {

        when(loanRepository.countApprovedLoans(any()))
                .thenReturn(0);

        when(loanRepository.countRejectedLoans(any()))
                .thenReturn(5);

        when(loanRepository.countRecentApplications(any(), any(LocalDateTime.class)))
                .thenReturn(0);

        ApplicationStatus status = loanDecisionService.profileEvaluation(loan);

        assertEquals(ApplicationStatus.REJECTED_BY_SYSTEM, status);
    }
}