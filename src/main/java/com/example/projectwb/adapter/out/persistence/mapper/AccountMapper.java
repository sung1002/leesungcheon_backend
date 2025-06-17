package com.example.projectwb.adapter.out.persistence.mapper;

import com.example.projectwb.adapter.out.persistence.entity.AccountJpaEntity;
import com.example.projectwb.domain.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account mapToDomain(AccountJpaEntity entity) {
        return new Account(
            entity.getId(),
            entity.getAccountNumber(),
            entity.getBalance(),
            entity.getOwner()
        );
    }
}
