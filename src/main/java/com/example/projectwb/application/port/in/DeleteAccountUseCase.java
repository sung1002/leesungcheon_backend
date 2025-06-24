package com.example.projectwb.application.port.in;

public interface DeleteAccountUseCase {

    void deleteAccount(DeleteAccountCommand command);

    record DeleteAccountCommand(String accountNumber) {}
}
