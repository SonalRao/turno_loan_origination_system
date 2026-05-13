package com.turno.loanOriginationSystem.controller;

import com.turno.loanOriginationSystem.dto.LoanRequest;
import com.turno.loanOriginationSystem.dto.LoanResponse;
import com.turno.loanOriginationSystem.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(

            @Valid
            @RequestBody
            LoanRequest request,

            @RequestHeader("Idempotency-Key")
            String idempotencyKey
    ) {

        LoanResponse response =
                loanService.createLoan(
                        request,
                        idempotencyKey
                );

        return ResponseEntity.ok(response);
    }
}
