package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.dto.AssignedLoanResponse;
import com.turno.loanOriginationSystem.dto.LoanRequest;
import com.turno.loanOriginationSystem.dto.LoanResponse;
import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    @Transactional
    public LoanResponse createLoan(LoanRequest request, String idempotencyKey) {
        try
        {
            return loanRepository.findByIdempotencyKey(idempotencyKey)
                .map(existingLoan -> LoanResponse.builder()
                        .loanId(existingLoan.getLoanId())
                        .status(existingLoan.getApplicationStatus())
                        .build())
                .orElseGet(() ->
                        createNewLoan(request, idempotencyKey)
                );
        }catch (DataIntegrityViolationException ex){
            LoanApplication existingLoan = loanRepository.findByIdempotencyKey(idempotencyKey)
                                            .orElseThrow(() -> new IllegalStateException("Loan exists but could not be fetched"));

            return LoanResponse.builder()
                    .loanId(existingLoan.getLoanId())
                    .status(existingLoan.getApplicationStatus())
                    .build();
        }
    }

    private LoanResponse createNewLoan(
            LoanRequest request,
            String idempotencyKey
    ) {

        LoanApplication loan = new LoanApplication();

        loan.setCustomerName(request.getCustomerName());
        loan.setCustomerPhone(request.getCustomerPhone());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setLoanType(request.getLoanType());

        loan.setApplicationStatus(ApplicationStatus.APPLIED);

        loan.setIdempotencyKey(idempotencyKey);

        LoanApplication savedLoan =
                loanRepository.save(loan);

        return LoanResponse.builder()
                .loanId(savedLoan.getLoanId())
                .status(savedLoan.getApplicationStatus())
                .build();
    }

    @Transactional
    public Page<AssignedLoanResponse>  fetchAssignedLoans(Long agentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return loanRepository.findByAssignedAgentId(agentId, pageable)
                .map(loan ->
                        AssignedLoanResponse.builder()
                                .loanId(loan.getLoanId())
                                .customerName(loan.getCustomerName())
                                .customerPhone(loan.getCustomerPhone())
                                .loanAmount(loan.getLoanAmount())
                                .loanType(loan.getLoanType())
                                .applicationStatus(loan.getApplicationStatus())
                                .assignedAgentId(loan.getAssignedAgentId())
                                .build());
    }
}