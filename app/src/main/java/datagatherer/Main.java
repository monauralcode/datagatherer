package datagatherer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import datagatherer.util.Convenience;

public class Main implements DataEntryView.Context, Convenience {

    private Main(DataEntryModel model) {
        model.attach(view());
    }

    @Override
    public void prepare() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println("unable to load system look-and-feel; using default");
        }
    }

    @Override
    public void layout(JLabel entry, JLabel range, JLabel average) {
        var padding = new EmptyBorder(6, 12, 6, 12);
        var baseFont = entry.getFont();
        var labelFont = baseFont.deriveFont(Font.BOLD);
        var frame = new JFrame();
        frame.setContentPane(columnWithRows(4, (i, row) -> {
            row.setBorder(padding);
            switch (i) {
                case 0:
                    row.add(entry).setFont(baseFont.deriveFont(Font.BOLD, 24f));
                    break;
                case 1:
                    row.add(new JLabel("Range")).setFont(labelFont);
                    row.add(Box.createHorizontalGlue());
                    row.add(range);
                    break;
                case 2:
                    row.add(new JLabel("Average")).setFont(labelFont);
                    row.add(Box.createHorizontalGlue());
                    row.add(average);
                    break;
                case 3:
                    row.add(Box.createHorizontalGlue());
                    row.add(new JButton("save csv")).setFocusable(false);
                    break;
                default:
                    break;
            }
        }));
        frame.setTitle("Data Gatherer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 180));
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void onKeyPress(Consumer<Integer> listener) {
        var fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        fm.addKeyEventDispatcher((event) -> {
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                listener.accept(event.getKeyCode());
            }
            return false;
        });
    }

    public static void main(String[] args) {
        new Main(new DataEntryModel(SwingUtilities::invokeLater));
    }
}
