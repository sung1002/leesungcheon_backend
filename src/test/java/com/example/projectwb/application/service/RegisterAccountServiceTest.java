package com.example.projectwb.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.projectwb.application.port.in.RegisterAccountUseCase.RegisterAccountCommand;
import com.example.projectwb.application.port.out.LoadAccountPort;
import com.example.projectwb.application.port.out.RegisterAccountPort;
import com.example.projectwb.domain.Account;
import com.example.projectwb.domain.exception.DuplicateAccountNumberException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterAccountServiceTest {

    @InjectMocks
    private RegisterAccountService registerAccountService;
    @Mock
    private RegisterAccountPort registerAccountPort;
    @Mock
    private LoadAccountPort loadAccountPort;

    @Test
    @DisplayName("계좌 등록 성공")
    void registerAccount_success() {
        // given
        RegisterAccountCommand command = new RegisterAccountCommand("이성천", "1111-1111-1111");
        given(loadAccountPort.loadAccountByNumber(command.accountNumber())).willReturn(
            Optional.empty());

        // when
        registerAccountService.registerAccount(command);

        // then
        verify(registerAccountPort).createAccount(command.owner(), command.accountNumber());
    }

    @Test
    @DisplayName("계좌 등록 실패 - 계좌번호가 이미 존재할 때")
    void registerAccount_fail_when_accountNumber_already_exists() {
        // given
        RegisterAccountCommand command = new RegisterAccountCommand("이성천", "1111-1111-1111");
        Account existingAccount = new Account(1L, "1111-1111-1111", BigDecimal.TEN, "일성천");

        given(loadAccountPort.loadAccountByNumber(command.accountNumber())).willReturn(
            Optional.of(existingAccount));

        // when & then
        assertThatThrownBy(() -> registerAccountService.registerAccount(command))
            .isInstanceOf(DuplicateAccountNumberException.class)
            .hasMessageContaining("이미 존재하는 계좌번호입니다");

        verify(registerAccountPort, never()).createAccount(anyString(), anyString());
    }
}