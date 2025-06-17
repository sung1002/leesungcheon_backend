package com.example.projectwb.application.port.in;

import com.example.projectwb.domain.Transaction;
import java.util.List;

public interface GetAccountHistoryQuery {

    List<Transaction> getAccountHistory(Long accountId);
}
