package datagatherer.util;

import java.util.function.BiConsumer;

import javax.swing.Box;
import javax.swing.BoxLayout;

public interface Convenience {
    default Box columnWithRows(int numRows, BiConsumer<Integer, Box> each) {
        var column = new Box(BoxLayout.PAGE_AXIS);
        for (var i = 0; i < numRows; i++) {
            var row = new Box(BoxLayout.LINE_AXIS);
            column.add(row);
            each.accept(i, row);
        }
        return column;
    }
}
