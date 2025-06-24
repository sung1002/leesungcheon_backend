package com.example.projectwb.application.service;

import com.example.projectwb.application.port.in.SendMoneyUseCase;
import com.example.projectwb.application.port.out.DistributedLockPort;
import com.example.projectwb.application.port.out.LoadAccountPort;
import com.example.projectwb.application.port.out.LoadTransactionPort;
import com.example.projectwb.application.port.out.RegisterTransactionPort;
import com.example.projectwb.application.port.out.UpdateAccountPort;
import com.example.projectwb.domain.Account;
import com.example.projectwb.domain.Transaction;
import com.example.projectwb.domain.Transaction.TransactionType;
import com.example.projectwb.domain.exception.AccountNotFoundException;
import com.example.projectwb.domain.exception.DailyLimitException;
import com.example.projectwb.domain.exception.InsufficientFundsException;
import com.example.projectwb.domain.exception.InvalidTransactionRequestException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendMoneyService implements SendMoneyUseCase {

    private final LoadAccountPort loadAccountPort;
    private final UpdateAccountPort updateAccountPort;
    private final LoadTransactionPort loadTransactionPort;
    private final RegisterTransactionPort registerTransactionPort;
    private final DistributedLockPort distributedLockPort;

    private static final BigDecimal WITHDRAWAL_DAILY_LIMIT = new BigDecimal("1000000");
    private static final BigDecimal TRANSFER_DAILY_LIMIT = new BigDecimal("3000000");
    private static final BigDecimal TRANSFER_FEE_RATE = new BigDecimal("0.01");

    @Override
    public boolean sendMoney(SendMoneyCommand command) {

        if (command.sourceAccountNumber() == null && command.targetAccountNumber() != null) {
            return distributedLockPort.withLock("account:" + command.targetAccountNumber(),
                () -> processTransaction(command));
        }

        if (command.sourceAccountNumber() != null && command.targetAccountNumber() == null) {
            return distributedLockPort.withLock("account:" + command.sourceAccountNumber(),
                () -> processTransaction(command));
        }

        if (command.sourceAccountNumber() != null && command.targetAccountNumber() != null) {
            String firstLockKey = command.sourceAccountNumber().compareTo(command.targetAccountNumber()) < 0 ? command.sourceAccountNumber() : command.targetAccountNumber();
            String secondLockKey = command.sourceAccountNumber().compareTo(command.targetAccountNumber()) < 0 ? command.targetAccountNumber() : command.sourceAccountNumber();

            Supplier<Boolean> process = () -> distributedLockPort.withLock("account:" + secondLockKey, () -> processTransaction(command));
            return distributedLockPort.withLock("account:" + firstLockKey, process);
        }
        throw new InvalidTransactionRequestException("잘못된 거래 요청입니다.");
    }

    private boolean processTransaction(SendMoneyCommand command) {

        if (command.sourceAccountNumber() == null) {
            return handleDeposit(command);
        }

        Account sourceAccount = loadAccountPort.loadAccountByNumber(command.sourceAccountNumber())
            .orElseThrow(() -> new AccountNotFoundException(
                "출금 계좌를 찾을 수 없습니다: " + command.sourceAccountNumber()));

        if (command.targetAccountNumber() == null) {
            return handleWithdrawal(command, sourceAccount);
        }

        return handleTransfer(command, sourceAccount);
    }

    private boolean handleDeposit(SendMoneyCommand command) {
        Account targetAccount = loadAccountPort.loadAccountByNumber(command.targetAccountNumber())
            .orElseThrow(() -> new AccountNotFoundException(
                "입금 계좌를 찾을 수 없습니다: " + command.targetAccountNumber()));

        targetAccount.deposit(command.amount());
        updateAccountPort.updateAccount(targetAccount);

        registerTransactionPort.saveTransaction(Transaction.builder()
            .targetAccountNumber(targetAccount.getAccountNumber())
            .amount(command.amount())
            .type(TransactionType.DEPOSIT)
            .transactionDate(LocalDateTime.now())
            .build());

        return true;
    }

    private boolean handleWithdrawal(SendMoneyCommand command, Account sourceAccount) {
        checkDailyLimit(sourceAccount.getAccountNumber(), command.amount(), TransactionType.WITHDRAWAL,
            WITHDRAWAL_DAILY_LIMIT);

        boolean success = sourceAccount.withdraw(command.amount());
        if (!success) {
            throw new InsufficientFundsException("잔액이 부족합니다.");
        }

        updateAccountPort.updateAccount(sourceAccount);
        registerTransactionPort.saveTransaction(Transaction.builder()
            .sourceAccountNumber(sourceAccount.getAccountNumber())
            .amount(command.amount())
            .type(TransactionType.WITHDRAWAL)
            .transactionDate(LocalDateTime.now())
            .build());
        return true;
    }

    private boolean handleTransfer(SendMoneyCommand command, Account sourceAccount) {
        checkDailyLimit(sourceAccount.getAccountNumber(), command.amount(), TransactionType.TRANSFER,
            TRANSFER_DAILY_LIMIT);

        Account targetAccount = loadAccountPort.loadAccountByNumber(command.targetAccountNumber())
            .orElseThrow(() -> new AccountNotFoundException("이체 대상 계좌를 찾을 수 없습니다." + command.targetAccountNumber()));

        BigDecimal fee = command.amount().multiply(TRANSFER_FEE_RATE);
        BigDecimal totalAmount = command.amount().add(fee);

        boolean success = sourceAccount.withdraw(totalAmount);
        if (!success) {
            throw new InsufficientFundsException("수수료 포함 잔액이 부족합니다.");
        }

        targetAccount.deposit(command.amount());

        updateAccountPort.updateAccount(sourceAccount);
        updateAccountPort.updateAccount(targetAccount);

        registerTransactionPort.saveTransaction(Transaction.builder()
            .sourceAccountNumber(sourceAccount.getAccountNumber())
            .targetAccountNumber(targetAccount.getAccountNumber())
            .amount(command.amount())
            .fee(fee)
            .type(TransactionType.TRANSFER)
            .transactionDate(LocalDateTime.now())
            .build());
        return true;
    }

    private void checkDailyLimit(String accountNumber, BigDecimal amount, TransactionType type,
        BigDecimal limit) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        BigDecimal todayTotal = loadTransactionPort.getDailyTransactionSum(accountNumber, type,
            startOfDay, endOfDay);

        if (todayTotal.add(amount).compareTo(limit) > 0) {
            throw new DailyLimitException(type.name() + " 일일 한도(" + limit + "원)를 초과했습니다.");
        }
    }
}
