package datagatherer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Main implements DataEntryView.Setup {
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
        var frame = new JFrame();
        var col = new Box(BoxLayout.PAGE_AXIS);
        var padding = new EmptyBorder(6, 12, 6, 12);
        var baseFont = entry.getFont();
        var labelFont = baseFont.deriveFont(Font.BOLD);
        var saveButton = new JButton("save csv");
        var labels = new JLabel[] {
                new JLabel("Range"),
                new JLabel("Average")
        };
        var rows = new Box[] {
                new Box(BoxLayout.LINE_AXIS),
                new Box(BoxLayout.LINE_AXIS),
                new Box(BoxLayout.LINE_AXIS)
        };
        entry.setFont(baseFont.deriveFont(Font.BOLD, 24f));
        entry.setBorder(new EmptyBorder(6, 0, 6, 0));
        saveButton.setFocusable(false);
        labels[0].setFont(labelFont);
        labels[1].setFont(labelFont);
        rows[0].setBorder(padding);
        rows[1].setBorder(padding);
        rows[2].setBorder(padding);
        rows[0].add(labels[0]);
        rows[0].add(Box.createHorizontalGlue());
        rows[0].add(range);
        rows[1].add(labels[1]);
        rows[1].add(Box.createHorizontalGlue());
        rows[1].add(average);
        rows[2].add(Box.createHorizontalGlue());
        rows[2].add(saveButton);
        col.add(entry);
        col.add(rows[0]);
        col.add(rows[1]);
        col.add(rows[2]);
        frame.setTitle("Data Gatherer");
        frame.setContentPane(col);
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
