package datagatherer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import datagatherer.util.Signal;

public class DataEntryModel implements DataEntry.Command {

    private enum Tag {
        NONE, ENTRY, STATS
    }

    private static final int REPEATS_TO_DELETE = 3;
    private static final DataEntry.Statistics ZERO_STATS = new DataEntry.Statistics(0, 0, 0, BigDecimal.ZERO);
    private final List<Long> samples = new ArrayList<>();
    private final Signal update;
    private final Executor main;
    private Tag tag = Tag.NONE;
    private long entry = 0;
    private int count = 0;
    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;
    private long sum = 0;
    private BigDecimal mean = BigDecimal.ZERO;
    private int repeatedCorrections = 0;

    public DataEntryModel(Executor main) {
        this.main = main;
        update = new Signal(main);
    }

    public DataEntryModel() {
        this(Runnable::run);
    }

    @Override
    public void append(int digit) {
        // TODO: overflow check
        entry = entry * 10 + digit;
        tag = Tag.ENTRY;
        update.send();
    }

    @Override
    public void correct() {
        if (entry > 0) {
            entry /= 10;
            repeatedCorrections = 0;
            tag = Tag.ENTRY;
            update.send();
        } else if (++repeatedCorrections == REPEATS_TO_DELETE && count > 0) {
            var last = samples.removeLast();
            sum -= last;
            count -= 1;
            mean = count > 0 ? BigDecimal.valueOf(sum / (double) count) : BigDecimal.ZERO;
            var newMin = Long.MAX_VALUE;
            var newMax = Long.MIN_VALUE;
            for (var n : samples) {
                if (n < newMin) {
                    newMin = n;
                }
                if (n > newMax) {
                    newMax = n;
                }
            }
            min = newMin;
            max = newMax;
            repeatedCorrections = 0;
            tag = Tag.STATS;
            update.send();
        }
    }

    @Override
    public void commit() {
        samples.add(entry);
        sum += entry;
        count += 1;
        mean = BigDecimal.valueOf(sum / (double) count);
        if (entry < min) {
            min = entry;
        }
        if (entry > max) {
            max = entry;
        }
        entry = 0;
        repeatedCorrections = 0;
        tag = Tag.STATS;
        update.send();
    }

    /**
     * Add a listener for state updates.
     * 
     * @param listener Reflects the state changes to the user
     * @return Run this to detach the listener
     */
    public Runnable attach(DataEntry.Activity listener) {
        var detach = update.receive(() -> tell(listener));
        main.execute(() -> {
            listener.onReady(this);
            listener.onEntryChange(entry);
            listener.onStatsChange(ZERO_STATS);
        });
        return detach;
    }

    void tell(DataEntry.Activity listener) {
        switch (tag) {
            case NONE:
                break;
            case ENTRY:
                listener.onEntryChange(entry);
                break;
            case STATS:
                listener.onEntryChange(entry);
                listener.onStatsChange(count > 0
                        ? new DataEntry.Statistics(min, max, count, mean)
                        : ZERO_STATS);
                break;
            default:
                break;
        }
    }
}
