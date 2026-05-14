package com.turno.loanOriginationSystem.controller;


import com.turno.loanOriginationSystem.dto.TopCustomerResponse;
import com.turno.loanOriginationSystem.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final LoanService loanService;

    @GetMapping("/top")
    public ResponseEntity<List<TopCustomerResponse>> fetchTopCustomers() {
        return ResponseEntity.ok(loanService.fetchTopCustomers());
    }
}
