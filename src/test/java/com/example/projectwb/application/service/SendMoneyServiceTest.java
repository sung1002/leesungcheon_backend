package com.example.projectwb.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.projectwb.application.port.in.SendMoneyUseCase.SendMoneyCommand;
import com.example.projectwb.application.port.out.DistributedLockPort;
import com.example.projectwb.application.port.out.LoadAccountPort;
import com.example.projectwb.application.port.out.LoadTransactionPort;
import com.example.projectwb.application.port.out.RegisterTransactionPort;
import com.example.projectwb.application.port.out.UpdateAccountPort;
import com.example.projectwb.domain.Account;
import com.example.projectwb.domain.Transaction;
import com.example.projectwb.domain.exception.DailyLimitException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendMoneyServiceTest {

    @InjectMocks
    private SendMoneyService sendMoneyService;

    @Mock
    private LoadAccountPort loadAccountPort;
    @Mock
    private UpdateAccountPort updateAccountPort;
    @Mock
    private LoadTransactionPort loadTransactionPort;
    @Mock
    private RegisterTransactionPort registerTransactionPort;
    @Mock
    private DistributedLockPort distributedLockPort;

    @Test
    @DisplayName("계좌 이체 성공")
    void transfer_success() {
        // given
        String sourceAccountNumber = "1111-1111-1111";
        String targetAccountNumber = "2222-2222-2222";
        BigDecimal amount = new BigDecimal("5000");

        SendMoneyCommand command = new SendMoneyCommand(sourceAccountNumber, targetAccountNumber, amount);

        Account sourceAccount = new Account(1L, "1111-1111-1111",
            new BigDecimal("10000"), "일성천");
        Account targetAccount = new Account(2L, "2222-2222-2222",
            new BigDecimal("10000"), "이성천");

        given(loadAccountPort.loadAccountByNumber(sourceAccountNumber)).willReturn(Optional.of(sourceAccount));
        given(loadAccountPort.loadAccountByNumber(targetAccountNumber)).willReturn(Optional.of(targetAccount));
        given(loadTransactionPort.getDailyTransactionSum(eq(sourceAccountNumber),
            any(Transaction.TransactionType.class), any(LocalDateTime.class),
            any(LocalDateTime.class)))
            .willReturn(BigDecimal.ZERO);

        given(distributedLockPort.withLock(any(String.class), any())).willAnswer(invocation -> {
            return invocation.getArgument(1, java.util.function.Supplier.class).get();
        });

        // when
        boolean result = sendMoneyService.sendMoney(command);

        // then
        assertThat(result).isTrue();

        verify(updateAccountPort).updateAccount(sourceAccount);
        verify(updateAccountPort).updateAccount(targetAccount);
        verify(registerTransactionPort).saveTransaction(any(Transaction.class));

        assertThat(sourceAccount.getBalance()).isEqualTo(new BigDecimal("4950.00"));
        assertThat(targetAccount.getBalance()).isEqualTo(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("일일 이체 한도 초과 시 이체 실패")
    void transfer_fail_daily_limit_exceeded() {
        // given
        String sourceAccountNumber = "1111-1111-1111";
        String targetAccountNumber = "2222-2222-2222";
        BigDecimal amount = new BigDecimal("1000000");

        SendMoneyCommand command = new SendMoneyCommand(sourceAccountNumber, targetAccountNumber, amount);
        Account sourceAccount = new Account(1L, "1111-1111-1111",
            new BigDecimal("5000000"), "이성천");

        given(loadAccountPort.loadAccountByNumber(sourceAccountNumber)).willReturn(Optional.of(sourceAccount));

        given(loadTransactionPort.getDailyTransactionSum(eq(sourceAccountNumber),
            eq(Transaction.TransactionType.TRANSFER), any(LocalDateTime.class),
            any(LocalDateTime.class)))
            .willReturn(new BigDecimal("2500000"));
        given(distributedLockPort.withLock(any(String.class), any())).willAnswer(invocation -> {
            return invocation.getArgument(1, java.util.function.Supplier.class).get();
        });

        // when & then
        assertThatThrownBy(() -> sendMoneyService.sendMoney(command))
            .isInstanceOf(DailyLimitException.class)
            .hasMessageContaining("일일 한도");
    }

    @Test
    @DisplayName("일일 출금 한도 초과 시 출금 실패")
    void withdraw_fail_daily_limit_exceeded() {
        // given
        String sourceAccountNumber = "1111-1111-1111";
        BigDecimal amount = new BigDecimal("300000");

        SendMoneyCommand command = new SendMoneyCommand(sourceAccountNumber, null, amount);

        Account sourceAccount = new Account(1L, "1111-1111-1111",
            new BigDecimal("2000000"), "이성천");

        given(loadAccountPort.loadAccountByNumber(sourceAccountNumber)).willReturn(Optional.of(sourceAccount));
        given(loadTransactionPort.getDailyTransactionSum(
            eq(sourceAccountNumber),
            eq(Transaction.TransactionType.WITHDRAWAL),
            any(LocalDateTime.class),
            any(LocalDateTime.class)))
            .willReturn(new BigDecimal("800000"));

        given(distributedLockPort.withLock(any(String.class), any())).willAnswer(invocation ->
            invocation.getArgument(1, java.util.function.Supplier.class).get()
        );

        assertThatThrownBy(() -> sendMoneyService.sendMoney(command))
            .isInstanceOf(DailyLimitException.class)
            .hasMessageContaining("일일 한도");
    }
}