package com.example.projectwb.adapter.out.redis;

import com.example.projectwb.application.port.out.DistributedLockPort;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLcokAdapter implements DistributedLockPort {

    private final RedissonClient redissonClient;

    @Override
    public <T> T withLock(String lockKey, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("락을 획득할 수 없습니다: " + lockKey);
            }
            log.info("Lock acquired: {}", lockKey);
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Lock released: {}", lockKey);
            }
        }
    }

    @Override
    public void runWithLock(String lockKey, Runnable runnable) {
        withLock(lockKey, () -> {
            runnable.run();
            return null;
        });
    }
}
