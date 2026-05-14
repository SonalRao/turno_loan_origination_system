package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.dto.AgentDecisionRequest;
import com.turno.loanOriginationSystem.entities.Agent;
import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.AgentDecision;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.repo.AgentRepository;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentReviewService {
    private final LoanRepository loanRepository;
    private final AgentRepository agentRepository;
    private final NotificationService notificationService;

    @Transactional
    public void reviewLoan(String loanId, Long agentId, AgentDecisionRequest request) {
        LoanApplication loan = loanRepository.findByLoanIdAndAssignedAgentId(loanId, agentId)
                        .orElseThrow(() -> new IllegalStateException("Loan not assigned to agent"));

        if (loan.getApplicationStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new IllegalStateException();
        }

        if (request.getDecision() == AgentDecision.APPROVE) {
            loan.setApplicationStatus(ApplicationStatus.APPROVED_BY_AGENT);
            notificationService.notifyCustomerPostApproval(loan);
        } else {
            loan.setApplicationStatus(ApplicationStatus.REJECTED_BY_AGENT);
            notificationService.notifyCustomerPostApproval(loan);
        }

        Agent agent = agentRepository.findById(agentId).orElseThrow(() -> new IllegalStateException("Agent not found"));

        agent.setActiveLoans(Math.max(0, agent.getActiveLoans() - 1));

        loanRepository.save(loan);
        agentRepository.save(agent);
    }
}
