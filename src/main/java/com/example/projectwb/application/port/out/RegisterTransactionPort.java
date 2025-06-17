package com.example.projectwb.application.port.out;

import com.example.projectwb.domain.Transaction;

public interface RegisterTransactionPort {

    void saveTransaction(Transaction transaction);
}
