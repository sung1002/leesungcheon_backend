package com.example.projectwb.application.port.in;

import com.example.projectwb.domain.Account;

public interface RegisterAccountUseCase {

    Account registerAccount(RegisterAccountCommand command);

    record RegisterAccountCommand(
        String owner,
        String accountNuber
    ) {}
}
