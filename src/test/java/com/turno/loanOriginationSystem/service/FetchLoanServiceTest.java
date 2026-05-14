package com.turno.loanOriginationSystem.service;

import com.turno.loanOriginationSystem.entities.LoanApplication;
import com.turno.loanOriginationSystem.enums.ApplicationStatus;
import com.turno.loanOriginationSystem.repo.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchLoanServiceTest {
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private FetchLoanService fetchLoanService;

    @Test
    void shouldFetchAndMarkLoansAsProcessing() {

        LoanApplication loan1 = LoanApplication.builder()
                .id(1L)
                .loanId("LOAN-1001")
                .applicationStatus(ApplicationStatus.APPLIED)
                .build();

        LoanApplication loan2 = LoanApplication.builder()
                .id(2L)
                .loanId("LOAN-1002")
                .applicationStatus(ApplicationStatus.APPLIED)
                .build();
        when(loanRepository.fetchLoansForProcessing(any(), anyInt())).thenReturn(List.of(loan1, loan2));

        fetchLoanService.fetchLoansForProcessing(2);

        verify(loanRepository, times(1))
                .saveAll(anyList());
    }

    }