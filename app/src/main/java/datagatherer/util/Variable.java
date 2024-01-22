package datagatherer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

public class Variable<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private final Lock read;
    private final Lock write;
    private final Executor main;
    private T value;

    public Variable(T initial, Executor main) {
        this.main = main;
        value = initial;
        var rw = new ReentrantReadWriteLock();
        read = rw.readLock();
        write = rw.writeLock();
    }

    public Variable(T initial) {
        this(initial, Runnable::run);
    }

    public Runnable onSet(Consumer<T> listener) {
        write.lock();
        try {
            var index = listeners.size();
            listeners.add(listener);
            return () -> {
                write.lock();
                try {
                    listeners.set(index, null);
                } finally {
                    write.unlock();
                }
            };
        } finally {
            write.unlock();
        }
    }

    public void set(T newValue) {
        write.lock();
        try {
            value = newValue;
            main.execute(() -> {
                read.lock();
                try {
                    for (var listener : listeners) {
                        if (listener != null) {
                            listener.accept(newValue);
                        }
                    }
                } finally {
                    read.unlock();
                }    
            });                
        } finally {
            write.unlock();
        }
    }

    public void update(Function<? super T, ? extends T> fn) {
        set(fn.apply(value));
    }

    public T get() {
        return value;
    }
}
