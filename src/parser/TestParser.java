package parser;

import static org.junit.Assert.assertEquals;
import global.UserInput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import logic.TaskAbstraction;

import org.junit.Before;
import org.junit.Test;

import storage.JsonFormatStorage;

public class TestParser {
    Parser parserObj;

    @Before
    public void setup(){
            parserObj = new Parser();
    }

    //test add without a name
    @Test
    public void testParserAddEmptyName() {
        UserInput message;
        try {
            message = parserObj.parseCommand("add");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertEquals("Error: Task name is empty.", e.getMessage());
        }
    
    }
    
    //test delete without a index
    @Test
    public void testParserDeleteEmptyName() {
        UserInput message;
        try {
            message = parserObj.parseCommand("delete");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertEquals("Error: Task name is empty", e.getMessage());
        }
    
    }
    
    //test edit without argument
    @Test
    public void testParserEditEmptyName() {
        UserInput message;
        try {
            message = parserObj.parseCommand("edit");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            assertEquals("Error: Invalid number of arguments.", e.getMessage());
        }
    
    }
    
    //test edit on special fields
    @Test
    public void testParserEditSpecial() throws Exception {
        UserInput message;

        message = parserObj.parseCommand("edit 1 every 2 days for 2");
        assertEquals("Name: null Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats: null Done: false", message.getTask(0).getAllInfo());
        
    
    }
    
    //test basic search
    @Test
    public void testParserSearch() throws Exception {
        UserInput message;

        message = parserObj.parseCommand("search task");
        String actual = "Name: task Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats: null Done: false";
        assertEquals(actual, message.getTask(0).getAllInfo());
        
    
    }
    
    //test saveto
    @Test
    public void testParserSaveto() throws Exception {
        UserInput message;
        message = parserObj.parseCommand("saveto new.txt");
        String actual = "new.txt";
        assertEquals(actual, message.getArguments().get(0));    
    }
    
    @Test
    public void testParserMark() throws Exception {
        UserInput message;
        message = parserObj.parseCommand("mark 1");
        String actual = "1";
        assertEquals(actual, message.getArguments().get(0));    
    }
    
    @Test
    public void testParserUnMark() throws Exception {
        UserInput message;
        message = parserObj.parseCommand("unmark 1");
        String actual = "1";
        assertEquals(actual, message.getArguments().get(0));    
    }
    
    @Test
    public void testParserAdd() throws Exception {
        UserInput message;
        TaskAbstraction task = new TaskAbstraction("task");
        UserInput cmd = new UserInput(UserInput.Type.ADD,task);
        message = parserObj.parseCommand("add task");
        assertEquals(true,cmd.compareTo(message) );
    }
    
    @Test
    public void testParserDelete() throws Exception {
        UserInput message;
        TaskAbstraction task = new TaskAbstraction();
        UserInput cmd = new UserInput(UserInput.Type.DELETE,task);
        message = parserObj.parseCommand("delete 1");
        assertEquals("DELETE",message.getCommandType().toString());
    }
    
    @Test
    public void testParseDate() throws Exception {
        Calendar expectedDate = new GregorianCalendar();
        expectedDate.clear();
        expectedDate.set(2015, 8, 18);
        expectedDate.set(Calendar.HOUR, 23);
        expectedDate.set(Calendar.MINUTE, 59);
        
        String[] dateArgs = { "18", "sep", "2015"};
        Calendar date = parserObj.parseTime(dateArgs, false);
        
        assertEquals(expectedDate, date);
    }

    
    @Test
    public void testGetNearestDate(){
        Calendar date = new GregorianCalendar();
        int todayDate = date.get(Calendar.DATE);
        int today = date.get(Calendar.DAY_OF_WEEK);
        
        assertEquals(todayDate+(5-today)%7, parserObj.getNearestDate(5));
    }
}
