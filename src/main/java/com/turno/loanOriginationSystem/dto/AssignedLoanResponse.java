package com.turno.loanOriginationSystem.dto;

import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.enums.LoanType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AssignedLoanResponse {

    private String loanId;

    private String customerName;

    private String customerPhone;

    private BigDecimal loanAmount;

    private LoanType loanType;

    private ApplicationStatus applicationStatus;

    private Long assignedAgentId;
}