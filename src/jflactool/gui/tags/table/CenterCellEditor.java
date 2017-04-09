package jflactool.gui.tags.table;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class CenterCellEditor extends DefaultCellEditor
{
    public CenterCellEditor()
    {
        super(new JTextField());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column)
    {
        JTextField editor = (JTextField) super.getTableCellEditorComponent(
                table, value, isSelected, row, column);

        editor.setHorizontalAlignment(SwingConstants.CENTER);

        return editor;
    }
}