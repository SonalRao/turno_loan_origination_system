package com.turno.loanOriginationSystem.controller;

import com.turno.loanOriginationSystem.dto.AssignedLoanResponse;
import com.turno.loanOriginationSystem.dto.LoanRequest;
import com.turno.loanOriginationSystem.dto.LoanResponse;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        LoanResponse response = loanService.createLoan(request, idempotencyKey);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<AssignedLoanResponse>> fetchLoansByStatus(
            @RequestParam ApplicationStatus status,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(loanService.fetchLoansByStatus(status, page, size));
    }

    @GetMapping("/status-count")
    public ResponseEntity<Map<ApplicationStatus, Long>> getLoanStatusCounts() {
        return ResponseEntity.ok(loanService.getLoanStatusCounts());
    }
}
