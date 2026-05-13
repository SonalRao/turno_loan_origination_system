package com.turno.loanOriginationSystem.dto;

import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoanResponse {

    private String loanId;

    private ApplicationStatus status;
}
