package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.enums.LoanType;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoanDecisionService {
    private final LoanRepository loanRepository;
    private static int MAX_RECENT_APPLICATIONS = 5;
    private static int MAX_PREVIOUS_REJECTION = 3;
    private static BigDecimal MAXIMUM_LOAN_APPLICATION_AMOUNT_THRESHOLD = BigDecimal.valueOf(1_000_000);
    private static BigDecimal MINIMUM_LOAN_APPLICATION_AMOUNT_THRESHOLD = BigDecimal.valueOf(1_000_000);

    public ApplicationStatus profileEvaluation(LoanApplication loan
    ) {

        int previousApprovedLoans = loanRepository.countApprovedLoans(loan.getCustomerPhone());

        int previousRejectedLoans = loanRepository.countRejectedLoans(loan.getCustomerPhone());

        int recentApplications = loanRepository.countRecentApplications(loan.getCustomerPhone(), LocalDateTime.now().minusDays(7));

        /*
         * Fraud-like behavior: if customer has too many applications recently
         */
        if (recentApplications >= MAX_RECENT_APPLICATIONS) {
            return ApplicationStatus.UNDER_REVIEW;
        }

        /*
         * If customer has too many previous rejections
         */
        if (previousRejectedLoans >= MAX_PREVIOUS_REJECTION) {
            return ApplicationStatus.REJECTED_BY_SYSTEM;
        }

        /*
         * In case of Large BUSINESS loans require manual review
         */
        if (loan.getLoanType() == LoanType.BUSINESS && loan.getLoanAmount().compareTo(MAXIMUM_LOAN_APPLICATION_AMOUNT_THRESHOLD)>0) {
            return ApplicationStatus.UNDER_REVIEW;
        }

        /*
         * Customer has a Good history and smaller loan
         */
        if (previousApprovedLoans >= 2 && loan.getLoanAmount().compareTo(MINIMUM_LOAN_APPLICATION_AMOUNT_THRESHOLD)>0) {
            return ApplicationStatus.APPROVED_BY_SYSTEM;
        }

        /*
        * Customers with smaller loans are approved
         */
        if(loan.getLoanAmount().compareTo(MINIMUM_LOAN_APPLICATION_AMOUNT_THRESHOLD)>0){
            return ApplicationStatus.APPROVED_BY_SYSTEM;
        }
        /*
         * Default manual review
         */
        return ApplicationStatus.UNDER_REVIEW;
    }
}
