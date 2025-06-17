package com.example.projectwb.application.port.in;

import java.math.BigDecimal;

public interface SendMoneyUseCase {

    boolean sendMoney(SendMoneyCommand command);

    record SendMoneyCommand(
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount
    ) {}
}
