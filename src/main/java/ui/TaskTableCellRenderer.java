package ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
class TaskTableCellRenderer extends JTextPane implements TableCellRenderer {
    int alignment;
    
    TaskTableCellRenderer() {
        alignment = StyleConstants.ALIGN_CENTER;
    }
    
    TaskTableCellRenderer(int alignmentConstant) {
        alignment = alignmentConstant;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        this.setText(value.toString());
        this.setMargin(null);
        StyledDocument fieldData = this.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributes, alignment);
        fieldData.setParagraphAttributes(0, fieldData.getLength(), attributes, false);
        this.setDocument(fieldData);
        return this;
    }
}