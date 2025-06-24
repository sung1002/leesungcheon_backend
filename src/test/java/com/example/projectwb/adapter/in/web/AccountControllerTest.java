package com.example.projectwb.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.projectwb.adapter.in.web.dto.AccountDto;
import com.example.projectwb.adapter.in.web.dto.TransactionDto;
import com.example.projectwb.application.port.in.DeleteAccountUseCase;
import com.example.projectwb.application.port.in.GetAccountHistoryQuery;
import com.example.projectwb.application.port.in.RegisterAccountUseCase;
import com.example.projectwb.application.port.in.SendMoneyUseCase;
import com.example.projectwb.application.port.in.SendMoneyUseCase.SendMoneyCommand;
import com.example.projectwb.domain.Account;
import com.example.projectwb.domain.Transaction;
import com.example.projectwb.domain.exception.AccountNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterAccountUseCase registerAccountUseCase;

    @MockBean
    private DeleteAccountUseCase deleteAccountUseCase;

    @MockBean
    private SendMoneyUseCase sendMoneyUseCase;

    @MockBean
    private GetAccountHistoryQuery getAccountHistoryQuery;


    @Test
    @DisplayName("계좌 등록 API - 성공")
    void registerAccount_success() throws Exception {
        // given
        AccountDto.RegisterRequest request = new AccountDto.RegisterRequest("이성천",
            "1111-1111-1111");
        Account createdAccount = new Account(1L, "1111-1111-1111", BigDecimal.ZERO, "이성천");

        given(registerAccountUseCase.registerAccount(
            any(RegisterAccountUseCase.RegisterAccountCommand.class)))
            .willReturn(createdAccount);

        // when & then
        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.owner").value("이성천"))
            .andExpect(jsonPath("$.accountNumber").value("1111-1111-1111"));
    }

    @Test
    @DisplayName("계좌 등록 API - 실패 (입력값 오류)")
    void registerAccount_fail_invalidInput() throws Exception {
        // given
        AccountDto.RegisterRequest request = new AccountDto.RegisterRequest("", "1111-1111-1111");

        // when & then
        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("계좌 삭제 API - 성공")
    void deleteAccount_success() throws Exception {
        // given
        String accountNumber = "1111-1111-1111";

        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountNumber}", accountNumber))
            .andExpect(status().isOk());

        verify(deleteAccountUseCase).deleteAccount(any(DeleteAccountUseCase.DeleteAccountCommand.class));
    }

    @Test
    @DisplayName("계좌 삭제 실패 - 존재하지 않는 계좌번호")
    void deleteAccount_fail_notFound() throws Exception {
        // given
        String nonExistentAccountNumber = "1111-1111-1111";

        doThrow(new AccountNotFoundException("삭제할 계좌를 찾을 수 없습니다."))
            .when(deleteAccountUseCase).deleteAccount(any(DeleteAccountUseCase.DeleteAccountCommand.class));

        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountNumber}", nonExistentAccountNumber))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("입금 API - 성공")
    void deposit_success() throws Exception {
        // given
        TransactionDto.DepositRequest request = new TransactionDto.DepositRequest("1111-1111-1111",
            new BigDecimal("10000"));

        // when & then
        mockMvc.perform(post("/api/v1/accounts/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        ArgumentCaptor<SendMoneyCommand> commandCaptor = ArgumentCaptor.forClass(
            SendMoneyUseCase.SendMoneyCommand.class);
        verify(sendMoneyUseCase).sendMoney(commandCaptor.capture());

        SendMoneyUseCase.SendMoneyCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.sourceAccountNumber()).isNull();
        assertThat(capturedCommand.targetAccountNumber()).isEqualTo("1111-1111-1111");
        assertThat(capturedCommand.amount()).isEqualByComparingTo(new BigDecimal("10000"));
    }

    @Test
    @DisplayName("출금 API - 성공")
    void withdraw_success() throws Exception {
        // given
        TransactionDto.WithdrawRequest request = new TransactionDto.WithdrawRequest("1111-1111-1111",
            new BigDecimal("5000"));

        // when & then
        mockMvc.perform(post("/api/v1/accounts/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        ArgumentCaptor<SendMoneyUseCase.SendMoneyCommand> commandCaptor = ArgumentCaptor.forClass(
            SendMoneyUseCase.SendMoneyCommand.class);
        verify(sendMoneyUseCase).sendMoney(commandCaptor.capture());

        SendMoneyUseCase.SendMoneyCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.sourceAccountNumber()).isEqualTo("1111-1111-1111");
        assertThat(capturedCommand.targetAccountNumber()).isNull();
        assertThat(capturedCommand.amount()).isEqualByComparingTo(new BigDecimal("5000"));
    }

    @Test
    @DisplayName("이체 API - 성공")
    void transfer_success() throws Exception {
        // given
        TransactionDto.TransferRequest request = new TransactionDto.TransferRequest("1111-1111-1111", "2222-2222-2222",
            new BigDecimal("3000"));

        // when & then
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        ArgumentCaptor<SendMoneyUseCase.SendMoneyCommand> commandCaptor = ArgumentCaptor.forClass(
            SendMoneyUseCase.SendMoneyCommand.class);
        verify(sendMoneyUseCase).sendMoney(commandCaptor.capture());

        SendMoneyUseCase.SendMoneyCommand capturedCommand = commandCaptor.getValue();
        assertThat(capturedCommand.sourceAccountNumber()).isEqualTo("1111-1111-1111");
        assertThat(capturedCommand.targetAccountNumber()).isEqualTo("2222-2222-2222");
        assertThat(capturedCommand.amount()).isEqualByComparingTo(new BigDecimal("3000"));
    }

    @Test
    @DisplayName("거래 내역 조회 API - 성공")
    void getHistory_success() throws Exception {
        // given
        String accountNumber = "1111-1111-1111";
        Transaction transaction1 = Transaction.builder()
            .id(101L).type(Transaction.TransactionType.DEPOSIT).targetAccountNumber(accountNumber)
            .amount(new BigDecimal("10000")).transactionDate(
                LocalDateTime.now())
            .build();
        Transaction transaction2 = Transaction.builder()
            .id(102L).type(Transaction.TransactionType.WITHDRAWAL).sourceAccountNumber(accountNumber)
            .amount(new BigDecimal("2000")).transactionDate(LocalDateTime.now().minusHours(1))
            .build();
        List<Transaction> history = List.of(transaction1, transaction2);

        given(getAccountHistoryQuery.getAccountHistory(eq(accountNumber))).willReturn(history);

        // when & then
        mockMvc.perform(get("/api/v1/accounts/{accountNumber}/history", accountNumber))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].transactionId").value(101L))
            .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
            .andExpect(jsonPath("$[1].transactionId").value(102L))
            .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"));
    }
}