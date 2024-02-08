package datagatherer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import datagatherer.util.Convenience;

public class Main implements DataEntrySwing.Context, Convenience {

    private final DataEntryModel model;

    private Main(DataEntryModel model) {
        this.model = model;
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
        var dumpBtn = new JButton("flush to clipboard");
        dumpBtn.addActionListener((ignored) -> model.flush(Main::copyToClipboard));
        frame.setContentPane(columnWithRows(4, (pos, row) -> {
            row.setBorder(padding);
            switch (pos) {
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
                    row.add(dumpBtn).setFocusable(false);
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

    static void copyToClipboard(List<Long> data) {
        if (data.isEmpty()) {
            return;
        }
        var text = data.stream()
            .map(String::valueOf)
            .collect(Collectors.joining("\n"));
        Toolkit.getDefaultToolkit()
            .getSystemClipboard()
            .setContents(new StringSelection(text), null);
    }

    public static void main(String[] args) {
        new Main(new DataEntryModel(SwingUtilities::invokeLater));
    }
}
