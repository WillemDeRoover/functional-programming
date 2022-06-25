package be.desorted.functional.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;

public class LockedAccess {

    private final ReadWriteLock readWriteLock;

    public static LockedAccess from(ReadWriteLock readWriteLock) {
        return new LockedAccess(readWriteLock);
    }

    private LockedAccess(ReadWriteLock readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    public <T> T read(Supplier<T> supplier) {
        Lock lock = readWriteLock.readLock();
        return getLocked(supplier, lock);
    }

    public <T> T write(Supplier<T> supplier) {
        Lock lock = readWriteLock.writeLock();
        return getLocked(supplier, lock);
    }

    private <T> T getLocked(Supplier<T> supplier, Lock lock) {
        try {
            lock.lock();
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    public void write(Runnable runnable) {
        write(() -> {
            runnable.run();
            return null;
        });
    }

}
