package com.turno.loanOriginationSystem.dto;

import com.turno.loanOriginationSystem.enums.LoanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanRequest {

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerPhone;

    @NotNull
    @Positive
    private BigDecimal loanAmount;

    @NotNull
    private LoanType loanType;
}