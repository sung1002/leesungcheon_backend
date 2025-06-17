package com.example.projectwb.application.service;

import com.example.projectwb.application.port.in.GetAccountHistoryQuery;
import com.example.projectwb.application.port.out.LoadTransactionPort;
import com.example.projectwb.domain.Transaction;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GetAccountHistoryService implements GetAccountHistoryQuery {

    private final LoadTransactionPort loadTransactionPort;

    @Override
    public List<Transaction> getAccountHistory(Long accountId) {
        return loadTransactionPort.findTransactionsByAccountId(accountId);
    }
}
