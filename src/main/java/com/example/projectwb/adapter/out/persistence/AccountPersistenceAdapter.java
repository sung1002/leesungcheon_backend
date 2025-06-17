package com.example.projectwb.adapter.out.persistence;

import com.example.projectwb.adapter.out.persistence.entity.AccountJpaEntity;
import com.example.projectwb.adapter.out.persistence.entity.exception.AccountEntityNotFoundException;
import com.example.projectwb.adapter.out.persistence.mapper.AccountMapper;
import com.example.projectwb.application.port.out.DeleteAccountPort;
import com.example.projectwb.application.port.out.LoadAccountPort;
import com.example.projectwb.application.port.out.RegisterAccountPort;
import com.example.projectwb.application.port.out.UpdateAccountPort;
import com.example.projectwb.domain.Account;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements LoadAccountPort, UpdateAccountPort,
    RegisterAccountPort,
    DeleteAccountPort {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public Optional<Account> loadAccount(Long accountId) {
        return accountRepository.findById(accountId)
            .map(accountMapper::mapToDomain);
    }

    @Override
    public void updateAccount(Account account) {
        AccountJpaEntity entity = accountRepository.findById(account.getId())
            .orElseThrow(AccountEntityNotFoundException::new);
        entity.updateBalance(account.getBalance());
        accountRepository.save(entity);
    }

    @Override
    public Account createAccount(String owner, String accountNumber) {
        AccountJpaEntity entity = new AccountJpaEntity(owner, accountNumber, BigDecimal.ZERO);
        AccountJpaEntity savedEntity = accountRepository.save(entity);
        return accountMapper.mapToDomain(savedEntity);
    }

    @Override
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }
}
