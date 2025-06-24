package com.example.projectwb.application.service;

import com.example.projectwb.application.port.in.DeleteAccountUseCase;
import com.example.projectwb.application.port.out.DeleteAccountPort;
import com.example.projectwb.application.port.out.LoadAccountPort;
import com.example.projectwb.domain.Account;
import com.example.projectwb.domain.exception.AccountNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteAccountService implements DeleteAccountUseCase {

    private final DeleteAccountPort deleteAccountPort;
    private final LoadAccountPort loadAccountPort;

    @Override
    public void deleteAccount(DeleteAccountCommand command) {
        Account account = loadAccountPort.loadAccountByNumber(command.accountNumber())
            .orElseThrow(() -> new AccountNotFoundException("삭제할 계좌를 찾을 수 없습니다: " + command.accountNumber()));
        deleteAccountPort.deleteAccount(account.getId());
    }
}
