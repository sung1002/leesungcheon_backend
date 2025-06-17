package com.example.projectwb.application.port.out;

import com.example.projectwb.domain.Account;

public interface RegisterAccountPort {

    Account createAccount(String owner, String accountName);
}
