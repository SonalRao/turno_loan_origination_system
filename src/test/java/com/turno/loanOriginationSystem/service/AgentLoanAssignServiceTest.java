package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.repo.AgentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentLoanAssignServiceTest {
    @Mock
    private AgentRepository agentRepository;

    @InjectMocks
    private AgentLoanAssignService agentLoanAssignService;

    @Test
    void shouldThrowExceptionWhenNoAgentsAvailable() {

        LoanApplication loan = LoanApplication.builder()
                .loanId("LOAN-1001")
                .build();

        when(agentRepository.fetchAvailableAgent()).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> agentLoanAssignService.assignAgent(loan));
    }


}