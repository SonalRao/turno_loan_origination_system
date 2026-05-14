package com.turno.loanOriginationSystem.controller;

import com.turno.loanOriginationSystem.dto.AgentDecisionRequest;
import com.turno.loanOriginationSystem.dto.AssignedLoanResponse;
import com.turno.loanOriginationSystem.service.AgentReviewService;
import com.turno.loanOriginationSystem.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final LoanService loanService;
    private final AgentReviewService agentReviewService;

    @GetMapping("/{agentId}/loans")
    public ResponseEntity<Page<AssignedLoanResponse>> fetchAssignedLoans(@PathVariable Long agentId, @RequestParam(defaultValue = "0")
    int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(loanService.fetchAssignedLoans(agentId, page, size));
    }

    @PutMapping("/{agentId}/loans/{loanId}/decision")
    public ResponseEntity<Void> reviewLoan(@PathVariable Long agentId, @PathVariable String loanId, @Valid @RequestBody AgentDecisionRequest request) {
        agentReviewService.reviewLoan(loanId, agentId, request);

        return ResponseEntity.ok().build();
    }
}
