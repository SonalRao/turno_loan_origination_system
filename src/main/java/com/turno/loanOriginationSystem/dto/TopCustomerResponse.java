package com.turno.loanOriginationSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopCustomerResponse {

    private String customerName;

    private Long approvedLoanCount;
}