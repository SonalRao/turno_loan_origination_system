package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanStateTransitionService {
    private final LoanRepository loanRepository;

    @Transactional
    public LoanApplication markAsProcessing(LoanApplication loan) {
        loan.setApplicationStatus(ApplicationStatus.PROCESSING);

        return loanRepository.save(loan);
    }

    @Transactional
    public void updateFinalStatus(Long loanId, ApplicationStatus status) {
        LoanApplication loan = loanRepository.findById(loanId).orElseThrow();
        loan.setApplicationStatus(status);

        loanRepository.save(loan);
    }

    public LoanApplication getLoanById(String loanId) {

        return loanRepository.findByLoanId(loanId).orElseThrow(() -> new IllegalStateException("Loan not found for id: " + loanId));
    }
}
