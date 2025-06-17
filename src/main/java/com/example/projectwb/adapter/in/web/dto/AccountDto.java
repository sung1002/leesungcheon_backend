package com.example.projectwb.adapter.in.web.dto;

import com.example.projectwb.domain.Account;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import lombok.Builder;

public class AccountDto {

    public record RegisterRequest(
        @NotEmpty String owner,
        @NotEmpty String accountNumber
    ) {}

    @Builder
    public record Response(
        Long id,
        String owner,
        String accountNumber,
        BigDecimal balance
    ) {

        public static Response from(Account account) {
            return Response.builder()
                .id(account.getId())
                .owner(account.getOwner())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
        }
    }
}
