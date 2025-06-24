package com.example.projectwb.adapter.out.persistence;

import com.example.projectwb.adapter.out.persistence.entity.AccountJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountJpaEntity, Long> {

    Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);
}
