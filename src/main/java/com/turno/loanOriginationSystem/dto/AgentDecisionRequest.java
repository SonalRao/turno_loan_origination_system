package com.turno.loanOriginationSystem.dto;

import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentDecisionRequest {

    @NotBlank
    private ApplicationStatus status;
}