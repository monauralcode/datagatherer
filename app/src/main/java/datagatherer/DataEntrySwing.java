package datagatherer;

import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import javax.swing.JLabel;

public class DataEntrySwing implements DataEntry.Activity {

    /**
     * All abstract methods will be called once in the Swing main thread.
     */
    public interface Context {
        /**
         * The first method to be called when the view is attached to the model.
         * 
         * Set the look-and-feel here and other components that might be needed
         * for the two other methods.
         */
        void prepare();

        /**
         * Place these labels in a layout and style them as needed.
         * 
         * @param entry   Shows the current entry
         * @param range   Shows the minimum and maximum of the entries so far
         * @param average Shows the mean of the entries so far
         */
        void layout(JLabel entry, JLabel range, JLabel average);

        /**
         * Call this function when the user presses a number, backspace or enter.
         *
         * @param listener Should receive the VK_* code of the pressed key
         */
        void onKeyPress(Consumer<Integer> listener);

        /**
         * Creates a view in this context.
         *
         * @return A view instance
         */
        default DataEntry.Activity view() {
            return new DataEntrySwing(this);
        }
    }

    private static final DecimalFormat WITH_COMMAS = new DecimalFormat("#,###");
    private final Context context;
    private JLabel entry;
    private JLabel range;
    private JLabel average;

    public DataEntrySwing(Context context) {
        this.context = context;
    }

    @Override
    public void onReady(DataEntry.Command actions) {
        context.prepare();
        entry = new JLabel();
        range = new JLabel();
        average = new JLabel();
        context.layout(entry, range, average);
        context.onKeyPress((keyCode) -> {
            switch (keyCode) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DECIMAL:
                    actions.correct();
                    break;
                case KeyEvent.VK_ENTER:
                    actions.commit();
                    break;
                case KeyEvent.VK_0:
                case KeyEvent.VK_NUMPAD0:
                    actions.append(0);
                    break;
                case KeyEvent.VK_1:
                case KeyEvent.VK_NUMPAD1:
                    actions.append(1);
                    break;
                case KeyEvent.VK_2:
                case KeyEvent.VK_NUMPAD2:
                    actions.append(2);
                    break;
                case KeyEvent.VK_3:
                case KeyEvent.VK_NUMPAD3:
                    actions.append(3);
                    break;
                case KeyEvent.VK_4:
                case KeyEvent.VK_NUMPAD4:
                    actions.append(4);
                    break;
                case KeyEvent.VK_5:
                case KeyEvent.VK_NUMPAD5:
                    actions.append(5);
                    break;
                case KeyEvent.VK_6:
                case KeyEvent.VK_NUMPAD6:
                    actions.append(6);
                    break;
                case KeyEvent.VK_7:
                case KeyEvent.VK_NUMPAD7:
                    actions.append(7);
                    break;
                case KeyEvent.VK_8:
                case KeyEvent.VK_NUMPAD8:
                    actions.append(8);
                    break;
                case KeyEvent.VK_9:
                case KeyEvent.VK_NUMPAD9:
                    actions.append(9);
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public void onEntryChange(long newEntry) {
        entry.setText(WITH_COMMAS.format(newEntry));
    }

    @Override
    public void onStatsChange(DataEntry.Statistics newStats) {
        range.setText(""
                + WITH_COMMAS.format(newStats.min())
                + " \u2014 "
                + WITH_COMMAS.format(newStats.max()));
        average.setText(WITH_COMMAS.format(newStats.roundedMean()));
    }
}
