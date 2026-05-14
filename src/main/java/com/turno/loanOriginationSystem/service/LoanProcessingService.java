package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanProcessingService {
    private final LoanStateTransitionService loanStateTransitionService;
    private final LoanDecisionService loanDecisionService;
    private final AgentLoanAssignService agentLoanAssignService;
    private final NotificationService notificationService;

    @Async("loanExecutor")
    public void processLoan(String loanId) {
        try {
            LoanApplication loan = loanStateTransitionService.getLoanById(loanId);
            simulateProcessingDelay();
            ApplicationStatus finalStatus = loanDecisionService.profileEvaluation(loan);
            loanStateTransitionService.updateFinalStatus(loan.getId(), finalStatus);

            if (finalStatus == ApplicationStatus.UNDER_REVIEW) {
                agentLoanAssignService.assignAgent(loan);
            }
            if(finalStatus == ApplicationStatus.APPROVED_BY_SYSTEM) {
                notificationService.notifyCustomerPostApproval(loan);
            }
            if(finalStatus == ApplicationStatus.REJECTED_BY_SYSTEM) {
                notificationService.notifyCustomerPostRejection(loan);
            }

        } catch (Exception ex) {
            log.error("Loan processing failed for loanId={}", loanId, ex);
        }
    }

    private void simulateProcessingDelay() throws InterruptedException {
        int delay= ThreadLocalRandom.current().nextInt(2000,5000);
        Thread.sleep(delay);
    }

}