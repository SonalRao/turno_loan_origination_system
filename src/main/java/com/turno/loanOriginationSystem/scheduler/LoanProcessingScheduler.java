package com.turno.loanOriginationSystem.scheduler;

import com.turno.loanOriginationSystem.service.FetchLoanService;
import com.turno.loanOriginationSystem.service.LoanProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanProcessingScheduler {

    private final FetchLoanService fetchLoanService;
    private final LoanProcessingService loanProcessingService;

    @Scheduled(fixedDelay = 5000)
    public void processLoans() {

        List<String> loanIds = fetchLoanService.fetchLoansForProcessing(5);

        if (loanIds.isEmpty()) {
            return;
        }

        log.info("Picked {} loans for processing", loanIds.size());

        loanIds.forEach(
                loanProcessingService::processLoan
        );
    }
}
