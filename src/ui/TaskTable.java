package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
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
    
    private static final boolean[] SET_MAX_WIDTH = {true, false, false, false, false, false};
    
    private TaskTableModel model = null;
    
    public TaskTable(TaskTableModel dm) {
        super(dm);
        setBorder(new LineBorder(Color.LIGHT_GRAY));
        
        assert SET_MAX_WIDTH.length == FormatterHelper.COLUMN_COUNT;

        this.model = dm;
        
        prepareTable();
    }
    
    public TaskTable(TaskTableModel dm, TableColumnModel cm) {
        super(dm, cm);
        setBorder(new LineBorder(Color.LIGHT_GRAY));
        
        assert SET_MAX_WIDTH.length == FormatterHelper.COLUMN_COUNT;

        this.model = dm;
        
        prepareTable();
    }
    
    private void prepareTable() {
        prepareTableRenderer();
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
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalCols; j++) {
                TableCellRenderer tableCellRenderer = getCellRenderer(i, j);
                Component component = prepareRenderer(tableCellRenderer, i, j);
                int cellPreferredWidth = component.getPreferredSize().width;
                int curWidth = MAX_WIDTH[j];
                int minHeight = (int) (Math.ceil((double) cellPreferredWidth / curWidth)) * defaultRowHeight;
                int rowHeight = (int) Math.max(minHeight, this.getRowHeight(i));
                this.setRowHeight(i, rowHeight);
            }
        }
    }
    
    private void setColumnWidth(int columnIndex, int columnWidth) {
        TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
        tableColumn.setPreferredWidth(MAX_WIDTH[columnIndex]);
        tableColumn.setMaxWidth(MAX_WIDTH[columnIndex]);
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
            
            int cellPreferredWidth = component.getPreferredSize().width;
            
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
    
    private void prepareTableGrid() {
        setShowGrid(true);
        setGridColor(Color.LIGHT_GRAY);
    }
    
    private void prepareTableRenderer() {
        prepareHeaderRenderer();
        prepareContentRenderer();
    }
    
    private void prepareContentRenderer() {
        TableColumnModel tableColumnModel = getColumnModel();
        int columnCount = tableColumnModel.getColumnCount();
        
        for (int i = 0; i < columnCount; i++) {
            TableColumn currentTableColumn = tableColumnModel.getColumn(i);
            
            TaskTableCellRenderer renderer = new TaskTableCellRenderer();
            currentTableColumn.setCellRenderer(renderer);
        }
    }
    
    private void prepareHeaderRenderer() {
    	TableCellRenderer headerRenderer = new TaskTableCellRenderer();
    	tableHeader.setDefaultRenderer(headerRenderer);

    	JTextPane headerPane = (JTextPane) headerRenderer;
    	headerPane.setFont(new Font(HEADER_FONT_NAME, HEADER_FONT_STYLE, HEADER_FONT_SIZE));
        headerPane.setBackground(HEADER_COLOR);
        headerPane.setForeground(Color.WHITE); 
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
