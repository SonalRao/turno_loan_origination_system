package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.Agent;
import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.repo.AgentRepository;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentLoanAssignService {
    private final AgentRepository agentRepository;
    private final LoanRepository loanRepository;
    private final NotificationService notificationService;

    @Transactional
    public void assignAgent(LoanApplication loan){
        Agent agent = agentRepository.fetchAvailableAgent().orElseThrow(() ->
                                new IllegalStateException("No agents available"));

        loan.setAssignedAgentId(agent.getId());
        agent.setActiveLoans(agent.getActiveLoans()+1);
        loanRepository.save(loan);
        agentRepository.save(agent);
        notificationService.notifyAgent(agent, loan);
        notificationService.notifyManager(agent.getManagerId(), loan);
    }
}
