package com.example.projectwb.application.service;

import com.example.projectwb.application.port.in.RegisterAccountUseCase;
import com.example.projectwb.application.port.out.LoadAccountPort;
import com.example.projectwb.application.port.out.RegisterAccountPort;
import com.example.projectwb.domain.Account;
import com.example.projectwb.domain.exception.DuplicateAccountNumberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterAccountService implements RegisterAccountUseCase {

    private final RegisterAccountPort registerAccountPort;
    private final LoadAccountPort loadAccountPort;

    @Override
    public Account registerAccount(RegisterAccountCommand command) {
        loadAccountPort.loadAccountByNumber(command.accountNumber())
            .ifPresent(account -> {
                throw new DuplicateAccountNumberException("이미 존재하는 계좌번호입니다: " + command.accountNumber());
            });
        return registerAccountPort.createAccount(command.owner(), command.accountNumber());
    }
}
