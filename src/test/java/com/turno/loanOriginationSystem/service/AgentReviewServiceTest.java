package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.dto.AgentDecisionRequest;
import com.turno.loanOriginationSystem.entities.Agent;
import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.AgentDecision;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.repo.AgentRepository;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentReviewServiceTest {
    @Mock
    private LoanRepository loanRepository;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AgentReviewService agentReviewService;

    @Test
    void shouldRejectReviewIfLoanAssignedToDifferentAgent() {

        LoanApplication loan = LoanApplication.builder()
                .loanId("LOAN-1001")
                .assignedAgentId(2L)
                .applicationStatus(ApplicationStatus.UNDER_REVIEW)
                .build();

        when(loanRepository.findByLoanIdAndAssignedAgentId("LOAN-1001", 5L) ).thenReturn(Optional.of(loan));

        AgentDecisionRequest request = new AgentDecisionRequest();

        request.setDecision(AgentDecision.APPROVE);

        assertThrows(IllegalStateException.class, () -> agentReviewService.reviewLoan(
                        "LOAN-1001",
                        5L,
                        request));
    }

    @Test
    void shouldApproveLoanSuccessfully() {

        LoanApplication loan = LoanApplication.builder()
                .loanId("LOAN-1002")
                .assignedAgentId(1L)
                .applicationStatus(ApplicationStatus.UNDER_REVIEW)
                .build();

        Agent agent = Agent.builder()
                .id(1L)
                .activeLoans(2)
                .build();

        when(loanRepository.findByLoanIdAndAssignedAgentId("LOAN-1002", 1L)).thenReturn(Optional.of(loan));

        when(agentRepository.findById(1L)).thenReturn(Optional.of(agent));

        AgentDecisionRequest request = new AgentDecisionRequest();
        request.setDecision(AgentDecision.APPROVE);

        agentReviewService.reviewLoan("LOAN-1002", 1L, request);
        verify(loanRepository, times(1)).save(any(LoanApplication.class));

        verify(agentRepository, times(1)).save(any(Agent.class));

        verify(notificationService, times(1)).notifyCustomerPostApproval(any());
    }
}