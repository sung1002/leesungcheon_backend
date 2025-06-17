package com.example.projectwb.application.service;

import com.example.projectwb.application.port.in.RegisterAccountUseCase;
import com.example.projectwb.application.port.out.RegisterAccountPort;
import com.example.projectwb.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterAccountService implements RegisterAccountUseCase {

    private final RegisterAccountPort registerAccountPort;

    @Override
    public Account registerAccount(RegisterAccountCommand command) {
        return registerAccountPort.createAccount(command.owner(), command.accountNuber());
    }
}
