package datagatherer.util;

import java.util.concurrent.Executor;

public class Signal extends Variable<Void> {
    public Signal(Executor main) {
        super(null, main);
    }
    
    public Signal() {
        this(Runnable::run);
    }

    public Runnable receive(Runnable listener) {
        return super.onSet((ignored) -> listener.run());
    }

    public void send() {
        super.set(null);
    }
}
