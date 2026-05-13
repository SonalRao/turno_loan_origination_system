package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.dto.LoanRequest;
import com.turno.loanOriginationSystem.dto.LoanResponse;
import com.turno.loanOriginationSystem.entities.LoanApplications;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    @Transactional
    public com.turno.loanOriginationSystem.dto.LoanResponse createLoan(
            LoanRequest request,
            String idempotencyKey
    ) {

        return loanRepository.findByIdempotencyKey(idempotencyKey)
                .map(existingLoan -> LoanResponse.builder()
                        .loanId(existingLoan.getLoanId())
                        .status(existingLoan.getApplicationStatus())
                        .build())
                .orElseGet(() ->
                        createNewLoan(request, idempotencyKey)
                );
    }

    private LoanResponse createNewLoan(
            LoanRequest request,
            String idempotencyKey
    ) {

        LoanApplications loan = new LoanApplications();

        loan.setCustomerName(request.getCustomerName());
        loan.setCustomerPhone(request.getCustomerPhone());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setLoanType(request.getLoanType());

        loan.setApplicationStatus(ApplicationStatus.APPLIED);

        loan.setIdempotencyKey(idempotencyKey);

        LoanApplications savedLoan =
                loanRepository.save(loan);

        return LoanResponse.builder()
                .loanId(savedLoan.getLoanId())
                .status(savedLoan.getApplicationStatus())
                .build();
    }
}