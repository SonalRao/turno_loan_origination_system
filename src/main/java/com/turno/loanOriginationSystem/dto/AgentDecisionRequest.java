package com.turno.loanOriginationSystem.dto;

import com.turno.loanOriginationSystem.enums.AgentDecision;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentDecisionRequest {
    private AgentDecision decision;
}