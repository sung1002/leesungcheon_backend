package com.example.projectwb.application.port.out;

import com.example.projectwb.domain.Account;
import java.util.Optional;

public interface LoadAccountPort {
    Optional<Account> loadAccount(Long accountId);
}
