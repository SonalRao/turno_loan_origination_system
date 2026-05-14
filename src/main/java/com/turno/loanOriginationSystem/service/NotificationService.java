package com.turno.loanOriginationSystem.service;


import com.turno.loanOriginationSystem.entities.Agent;
import com.turno.loanOriginationSystem.entities.LoanApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void notifyAgent(Agent agent, LoanApplication loan) {
        log.info("Notification sent to Agent={} for loanId={}", agent.getId(), loan.getLoanId());
    }

    public void notifyManager(Long managerId, LoanApplication loan) {
        log.info("Notification sent to Manager={} for loanId={}", managerId, loan.getLoanId());
    }
}
