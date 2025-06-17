package com.example.projectwb.application.service;

import com.example.projectwb.application.port.in.DeleteAccountUseCase;
import com.example.projectwb.application.port.out.DeleteAccountPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteAccountService implements DeleteAccountUseCase {

    private final DeleteAccountPort deleteAccountPort;

    @Override
    public void deleteAccount(Long accountId) {
        deleteAccountPort.deleteAccount(accountId);
    }
}
