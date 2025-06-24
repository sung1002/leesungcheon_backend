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
        @NotNull String targetAccountNumber,
        @NotNull @Positive BigDecimal amount
    ) {

        public SendMoneyUseCase.SendMoneyCommand toCommand() {
            return new SendMoneyUseCase.SendMoneyCommand(null, targetAccountNumber, amount);
        }
    }

    public record WithdrawRequest(
        @NotNull String sourceAccountNumber,
        @NotNull @Positive BigDecimal amount
    ) {

        public SendMoneyUseCase.SendMoneyCommand toCommand() {
            return new SendMoneyUseCase.SendMoneyCommand(sourceAccountNumber, null, amount);
        }
    }

    public record TransferRequest(
        @NotNull String sourceAccountNumber,
        @NotNull String targetAccountNumber,
        @NotNull @Positive BigDecimal amount
    ) {

        public SendMoneyUseCase.SendMoneyCommand toCommand() {
            return new SendMoneyUseCase.SendMoneyCommand(sourceAccountNumber, targetAccountNumber, amount);
        }
    }

    @Builder
    public record HistoryResponse(
        Long transactionId,
        String sourceAccountNumber,
        String targetAccountNumber,
        BigDecimal amount,
        BigDecimal fee,
        String type,
        LocalDateTime transactionDate
    ) {

        public static HistoryResponse from(Transaction transaction) {
            return HistoryResponse.builder()
                .transactionId(transaction.getId())
                .sourceAccountNumber(transaction.getSourceAccountNumber())
                .targetAccountNumber(transaction.getTargetAccountNumber())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .type(transaction.getType().name())
                .transactionDate(transaction.getTransactionDate())
                .build();
        }
    }
}
