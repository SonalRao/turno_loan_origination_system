package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.dto.*;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<ApplicationStatus, Long> getLoanStatusCounts() {
        List<Object[]> results = loanRepository.fetchLoanStatusCounts();
        Map<ApplicationStatus, Long> response = new HashMap<>();

        for (Object[] row : results) {
            ApplicationStatus status = (ApplicationStatus) row[0];
            Long count = (Long) row[1];
            response.put(status, count);
        }

        return response;
    }

    public Page<AssignedLoanResponse> fetchLoansByStatus(ApplicationStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return loanRepository.findByApplicationStatus(status, pageable)
                .map(loan ->
                        AssignedLoanResponse.builder()
                                .loanId(loan.getLoanId())
                                .customerName(loan.getCustomerName())
                                .customerPhone(loan.getCustomerPhone())
                                .loanAmount(loan.getLoanAmount())
                                .loanType(loan.getLoanType())
                                .applicationStatus(loan.getApplicationStatus())
                                .assignedAgentId(loan.getAssignedAgentId()
                                )
                                .build()
                );
    }

    public List<TopCustomerResponse> fetchTopCustomers() {
        Pageable pageable = PageRequest.of(0, 3);

        return loanRepository.fetchTopCustomers(pageable);
    }
}