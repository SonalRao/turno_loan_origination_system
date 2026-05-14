package com.turno.loanOriginationSystem.controller;

import com.turno.loanOriginationSystem.dto.LoanRequest;
import com.turno.loanOriginationSystem.enums.LoanType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateLoanSuccessfully() throws Exception {

        LoanRequest request = new LoanRequest();

        request.setCustomerName("Sonal Rao");
        request.setCustomerPhone("9999999999");
        request.setLoanAmount(BigDecimal.valueOf(50000));
        request.setLoanType(LoanType.PERSONAL);

        mockMvc.perform(
                        post("/api/v1/loans")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Idempotency-Key", "loan-101")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }
}