package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.enums.LoanType;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoanDecisionService {
    private final LoanRepository loanRepository;

    public ApplicationStatus profileEvaluation(LoanApplication loan
    ) {

        int previousApprovedLoans = loanRepository.countApprovedLoans(loan.getCustomerPhone());

        int previousRejectedLoans = loanRepository.countRejectedLoans(loan.getCustomerPhone());

        int recentApplications = loanRepository.countRecentApplications(loan.getCustomerPhone(), LocalDateTime.now().minusDays(7));

        /*
         * Fraud-like behavior: if customer has too many applications recently
         */
        if (recentApplications >= 5) {
            return ApplicationStatus.UNDER_REVIEW;
        }

        /*
         * If customer has too many previous rejections
         */
        if (previousRejectedLoans >= 3) {
            return ApplicationStatus.REJECTED_BY_SYSTEM;
        }

        /*
         * In case of Large BUSINESS loans require manual review
         */
        if (loan.getLoanType() == LoanType.BUSINESS && loan.getLoanAmount().doubleValue() > 1000000) {
            return ApplicationStatus.UNDER_REVIEW;
        }

        /*
         * Customer has a Good history and smaller loan
         */
        if (previousApprovedLoans >= 2 && loan.getLoanAmount().doubleValue() <= 500000) {
            return ApplicationStatus.APPROVED_BY_SYSTEM;
        }

        /*
        * Customers with smaller loans are approved
         */
        if(loan.getLoanAmount().doubleValue() <= 50000){
            return ApplicationStatus.APPROVED_BY_SYSTEM;
        }
        /*
         * Default manual review
         */
        return ApplicationStatus.UNDER_REVIEW;
    }
}
