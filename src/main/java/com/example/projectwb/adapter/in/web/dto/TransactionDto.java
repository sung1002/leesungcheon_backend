package com.example.projectwb.adapter.in.web.dto;

import com.example.projectwb.application.port.in.SendMoneyUseCase;
import com.example.projectwb.domain.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

public class TransactionDto {

    public record DepositRequest(
        @NotNull Long targetAccountId,
        @NotNull @Positive BigDecimal amount
    ) {

        public SendMoneyUseCase.SendMoneyCommand toCommand() {
            return new SendMoneyUseCase.SendMoneyCommand(null, targetAccountId, amount);
        }
    }

    public record WithdrawRequest(
        @NotNull Long sourceAccountId,
        @NotNull @Positive BigDecimal amount
    ) {

        public SendMoneyUseCase.SendMoneyCommand toCommand() {
            return new SendMoneyUseCase.SendMoneyCommand(sourceAccountId, null, amount);
        }
    }

    public record TransferRequest(
        @NotNull Long sourceAccountId,
        @NotNull Long targetAccountId,
        @NotNull @Positive BigDecimal amount
    ) {

        public SendMoneyUseCase.SendMoneyCommand toCommand() {
            return new SendMoneyUseCase.SendMoneyCommand(sourceAccountId, targetAccountId, amount);
        }
    }

    @Builder
    public record HistoryResponse(
        Long transactionId,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        BigDecimal fee,
        String type,
        LocalDateTime transactionDate
    ) {

        public static HistoryResponse from(Transaction transaction) {
            return HistoryResponse.builder()
                .transactionId(transaction.getId())
                .sourceAccountId(transaction.getSourceAccountId())
                .targetAccountId(transaction.getTargetAccountId())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .type(transaction.getType().name())
                .transactionDate(transaction.getTransactionDate())
                .build();
        }
    }
}
