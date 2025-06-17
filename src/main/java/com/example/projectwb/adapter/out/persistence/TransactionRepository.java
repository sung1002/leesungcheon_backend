package com.example.projectwb.adapter.out.persistence;

import com.example.projectwb.adapter.out.persistence.entity.TransactionJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionJpaEntity, Long>,
    CustomTransactionRepository {

    List<TransactionJpaEntity> findBySourceAccountIdOrTargetAccountIdOrderByTransactionDateDesc(
        Long sourceId, Long targetId);
}
