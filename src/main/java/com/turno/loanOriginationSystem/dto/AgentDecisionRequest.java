package com.turno.loanOriginationSystem.dto;

import com.turno.loanOriginationSystem.enums.AgentDecision;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentDecisionRequest {
    private AgentDecision decision;
}