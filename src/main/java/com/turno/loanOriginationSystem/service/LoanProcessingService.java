package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanProcessingService {
    private final LoanStateTransitionService loanStateTransitionService;
    private final LoanDecisionService loanDecisionService;

    @Async("loanExecutor")
    public void processLoan(String loanId) {
        try {
            LoanApplication loan = loanStateTransitionService.getLoanById(loanId);

            loanStateTransitionService.markAsProcessing(loan);
            simulateProcessingDelay();
            ApplicationStatus finalStatus = loanDecisionService.profileEvaluation(loan);
            loanStateTransitionService.updateFinalStatus(loan.getId(), finalStatus);

        } catch (Exception ex) {
            log.error("Loan processing failed for loanId={}", loanId, ex);
        }
    }

    private void simulateProcessingDelay()
            throws InterruptedException {

        Thread.sleep(5000);
    }

}