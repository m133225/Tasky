package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import ui.formatter.FormatterHelper;
import ui.tasktable.TaskTableModel;

import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
/**
 * This class implements a JTable customized to display Task data. This class has to use 
 * <code>TaskTableModel</code> as its model since it needs to check whether a particular 
 * task has been done in order to give it green background color.<br>
 * @see ui.tasktable.TaskTableModel
 */
public class TaskTable extends JTable {
    
    private static final Color HEADER_COLOR = new Color(0x443266);
    private static final Color DEFAULT_ROW_COLOR = Color.WHITE;
    private static final Color ALTERNATE_ROW_COLOR = new Color(0xC3C3E5);
    private static final Color DONE_COLOR = Color.GREEN;
    
    private static final String HEADER_FONT_NAME = "SansSerif";
    private static final int HEADER_FONT_STYLE = Font.BOLD;
    private static final int HEADER_FONT_SIZE = 12;
    private static final int[] MAX_WIDTH = {30, 358, 150, 150, 150, 0};
    private static final int[] COLUMN_ALIGNMENTS = { SwingConstants.LEFT,
            SwingConstants.LEFT,
            SwingConstants.CENTER,
            SwingConstants.CENTER,
            SwingConstants.CENTER,
            SwingConstants.CENTER
    };
    
    private static final boolean[] SET_MAX_WIDTH = {true, false, false, false, false, false};
    
    private TaskTableModel model = null;
    
    class TaskTableCellRenderer extends JTextPane implements TableCellRenderer {
        int alignment = StyleConstants.ALIGN_CENTER;

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {

            this.setText(value.toString());
            StyledDocument fieldData = this.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, alignment);
            fieldData.setParagraphAttributes(0, fieldData.getLength(), center, false);
            this.setDocument(fieldData);

            return this;
        }
    }
    
    public TaskTable(TaskTableModel dm) {
        super(dm);
        setBorder(new LineBorder(Color.LIGHT_GRAY));
        
        assert COLUMN_ALIGNMENTS.length == FormatterHelper.COLUMN_COUNT;
        assert SET_MAX_WIDTH.length == FormatterHelper.COLUMN_COUNT;

        this.model = dm;
        
        prepareTable();
    }
    
    public TaskTable(TaskTableModel dm, TableColumnModel cm) {
        super(dm, cm);
        setBorder(new LineBorder(Color.LIGHT_GRAY));
        
        assert COLUMN_ALIGNMENTS.length == FormatterHelper.COLUMN_COUNT;
        assert SET_MAX_WIDTH.length == FormatterHelper.COLUMN_COUNT;

        this.model = dm;
        
        prepareTable();
    }
    
    private void prepareTable() {
        prepareTableHeader();
        prepareTableAlignment();
        prepareTableGrid();

        fixColumnHeight();
        fixColumnWidth();
    }
    
    private void fixColumnWidth() {
        TableColumnModel columnModel = getColumnModel();
        int columnCount = columnModel.getColumnCount();
        
        for (int i = 0; i < columnCount; i++) {
            int columnWidth = getColumnWidth(i);
            setColumnWidth(i, columnWidth);
        }
    }
    
    private void fixColumnHeight() {
        int totalRows = this.getRowCount();
        int totalCols = this.getColumnCount();
        int defaultRowHeight = this.getRowHeight();
        System.out.println("Default row height is " + defaultRowHeight);
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalCols; j++) {
                TableCellRenderer tableCellRenderer = getCellRenderer(i, j);
                Component component = prepareRenderer(tableCellRenderer, i, j);
                int cellPreferredWidth = component.getPreferredSize().width + getIntercellSpacing().width;
                int curWidth = MAX_WIDTH[j];
               // System.out.println("Preferred width is " + cellPreferredWidth);
               // System.out.println("Current width is " + curWidth);
                int minHeight = (int) Math.ceil((double) cellPreferredWidth / curWidth) * defaultRowHeight;
                int rowHeight = (int) Math.max(minHeight, this.getRowHeight(i));
                System.out.println("Current row ["+i+"] height is " + this.getRowHeight(i));
                System.out.println("Setting row height to: " + rowHeight);
                this.setRowHeight(i, rowHeight);
                System.out.println("Getting row height...: " + this.getRowHeight());
            }
        }
    }
    
    private void setColumnWidth(int columnIndex, int columnWidth) {
        TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
        tableColumn.setPreferredWidth(MAX_WIDTH[columnIndex]);
        tableColumn.setMaxWidth(MAX_WIDTH[columnIndex]);

        //TaskTableCellRenderer renderer = new TaskTableCellRenderer();
        //tableColumn.setCellRenderer(renderer);
    }
    
    private int getColumnWidth(int columnIndex) {
        int headerWidth = getHeaderWidth(columnIndex);
        int contentWidth = getContentWidth(columnIndex);
        int resultingWidth = Math.max(headerWidth, contentWidth);
        
        return resultingWidth;
    }
    
    private int getContentWidth(int columnIndex) {
        int rowCount = getRowCount();
        
        int maxContentWidth = 0;
        for (int i = 0; i < rowCount; i++) {
            TableCellRenderer tableCellRenderer = getCellRenderer(i, columnIndex);
            Component component = prepareRenderer(tableCellRenderer, i, columnIndex);
            
            int cellPreferredWidth = component.getPreferredSize().width + getIntercellSpacing().width;
            
            maxContentWidth = Math.max(maxContentWidth, cellPreferredWidth);
        }
        
        return maxContentWidth;
    }
    
    private int getHeaderWidth(int columnIndex) {
        TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();
        if (renderer == null) {
            renderer = getTableHeader().getDefaultRenderer();
        }
        Component component = renderer.getTableCellRendererComponent(this,
                tableColumn.getHeaderValue(), false, false, -1, columnIndex);
        
        return component.getPreferredSize().width;
    }
    
    private void prepareTableHeader() {
        JTableHeader tableHeader = getTableHeader();
        tableHeader.setFont(new Font(HEADER_FONT_NAME, HEADER_FONT_STYLE, HEADER_FONT_SIZE));
        tableHeader.setBackground(HEADER_COLOR);
        tableHeader.setForeground(Color.WHITE);    
    }
    
    private void prepareTableGrid() {
        setShowGrid(true);
        setGridColor(Color.LIGHT_GRAY);
    }
    
    private void prepareTableAlignment() {
        prepareHeaderAlignment();
        prepareContentAlignment();
    }
    
    private void prepareContentAlignment() {
        TableColumnModel tableColumnModel = getColumnModel();
        int columnCount = tableColumnModel.getColumnCount();
        
        for (int i = 0; i < columnCount; i++) {
            TableColumn currentTableColumn = tableColumnModel.getColumn(i);
            
            TaskTableCellRenderer renderer = new TaskTableCellRenderer();
            currentTableColumn.setCellRenderer(renderer);
        }
    }
    
    private void prepareHeaderAlignment() {
        TableCellRenderer headerRenderer = tableHeader.getDefaultRenderer();
        JLabel headerLabel = (JLabel) headerRenderer;
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        giveColour(c, row, column);
        return c;
    }
    
    private void giveColour(Component c, int row, int column) {
        if (row % 2 == 0) {
            c.setBackground(DEFAULT_ROW_COLOR);
        } else {
            c.setBackground(ALTERNATE_ROW_COLOR);
        }
        
        //If the task is done, make it green
        if (model.isTaskDone(row)) {
            c.setBackground(DONE_COLOR);
        }
    }
}
