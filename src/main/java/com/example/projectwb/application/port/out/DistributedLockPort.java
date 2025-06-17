package com.example.projectwb.application.port.out;

import java.util.function.Supplier;

public interface DistributedLockPort {

    <T> T withLock(String lockKey, Supplier<T> supplier);

    void runWithLock(String lockKey, Runnable runnable);
}
