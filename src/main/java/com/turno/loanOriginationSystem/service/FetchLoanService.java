package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FetchLoanService {

    private final LoanRepository loanRepository;

    @Transactional
    public List<String> fetchLoansForProcessing(int limit) {
        List<LoanApplication> loans = loanRepository
                .fetchLoansForProcessing(
                        ApplicationStatus.APPLIED.name(),
                        limit);
        loans.forEach(loan-> loan.setApplicationStatus(ApplicationStatus.PROCESSING));
        loanRepository.saveAll(loans);
        return loans.stream().map(LoanApplication:: getLoanId).toList();
    }
}
