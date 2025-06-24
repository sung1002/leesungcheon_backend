package com.example.projectwb.adapter.in.web;

import com.example.projectwb.adapter.in.web.dto.AccountDto;
import com.example.projectwb.adapter.in.web.dto.TransactionDto;
import com.example.projectwb.adapter.in.web.dto.TransactionDto.HistoryResponse;
import com.example.projectwb.application.port.in.DeleteAccountUseCase;
import com.example.projectwb.application.port.in.DeleteAccountUseCase.DeleteAccountCommand;
import com.example.projectwb.application.port.in.GetAccountHistoryQuery;
import com.example.projectwb.application.port.in.RegisterAccountUseCase;
import com.example.projectwb.application.port.in.SendMoneyUseCase;
import com.example.projectwb.domain.Account;
import com.example.projectwb.domain.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "계좌 API", description = "계좌 관리 및 거래 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final RegisterAccountUseCase registerAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final SendMoneyUseCase sendMoneyUseCase;
    private final GetAccountHistoryQuery getAccountHistoryQuery;

    @Operation(summary = "신규 계좌 등록", description = "새로운 계좌를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "계좌 등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력값")
    })
    @PostMapping
    public ResponseEntity<AccountDto.Response> registerAccount(
        @RequestBody @Valid AccountDto.RegisterRequest request) {
        Account account = registerAccountUseCase.registerAccount(
            new RegisterAccountUseCase.RegisterAccountCommand(request.owner(),
                request.accountNumber())
        );
        return ResponseEntity.ok(AccountDto.Response.from(account));
    }

    @Operation(summary = "계좌 삭제", description = "기존 계좌를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "계좌 삭제 성공")
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        deleteAccountUseCase.deleteAccount(
            new DeleteAccountCommand(accountNumber)
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "입금", description = "특정 계좌에 금액을 입금합니다.")
    @ApiResponse(responseCode = "200", description = "입금 성공")
    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@RequestBody @Valid TransactionDto.DepositRequest request) {
        sendMoneyUseCase.sendMoney(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "출금", description = "특정 계좌에서 금액을 출금합니다. (일일 한도: 1,000,000원)")
    @ApiResponse(responseCode = "200", description = "출금 성공")
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
        @RequestBody @Valid TransactionDto.WithdrawRequest request) {
        sendMoneyUseCase.sendMoney(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "계좌 이체", description = "한 계좌에서 다른 계좌로 금액을 이체합니다. (수수료 1%, 일일 한도: 3,000,000원)")
    @ApiResponse(responseCode = "200", description = "이체 성공")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(
        @RequestBody @Valid TransactionDto.TransferRequest request) {
        sendMoneyUseCase.sendMoney(request.toCommand());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "거래 내역 조회", description = "특정 계좌의 모든 거래 내역을 최신순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{accountId}/history")
    public ResponseEntity<List<HistoryResponse>> getHistory(@PathVariable Long accountId) {
        List<Transaction> history = getAccountHistoryQuery.getAccountHistory(accountId);
        List<TransactionDto.HistoryResponse> response = history.stream()
            .map(TransactionDto.HistoryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
