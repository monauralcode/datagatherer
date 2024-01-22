package datagatherer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class DataEntry {
    private DataEntry() {
    }

    /**
     * The actions that a user may perform on the component.
     */
    public interface Command {
        /**
         * Adds a digit to the current entry.
         * 
         * @param digit The digit to add
         */
        void append(int digit);

        /**
         * Removes the last digit of the current entry.
         */
        void correct();

        /**
         * Finalizes the entry. Causes a stat recomputation.
         */
        void commit();
    }

    /**
     * Client callbacks for state changes due to user actions.
     */
    public interface Activity {
        /**
         * Called once when this activity is attached to a model.
         * 
         * @param actions Commands to invoke when a user performs an action
         */
        void onReady(Command actions);

        /**
         * Called when a user presses a digit or backspace.
         *
         * @param newEntry The value to show
         */
        void onEntryChange(long newEntry);

        /**
         * Called when a user presses enter.
         *
         * @param newStats The stats to show
         */
        void onStatsChange(Statistics newStats);
    }

    public record Statistics(long min, long max, int count, BigDecimal mean) {
        public long roundedMean() {
            return mean.setScale(0, RoundingMode.HALF_UP).longValue();
        }
    }
}
