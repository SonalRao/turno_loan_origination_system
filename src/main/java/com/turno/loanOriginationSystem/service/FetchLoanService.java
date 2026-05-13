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
        return loanRepository
                .fetchLoansForProcessing(
                        ApplicationStatus.APPLIED.name(),
                        limit
                )
                .stream()
                .map(LoanApplication::getLoanId)
                .toList();
    }
}
