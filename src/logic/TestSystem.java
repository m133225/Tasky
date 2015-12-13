package logic;


import static org.junit.Assert.assertEquals;


import global.UserInput;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import parser.Parser;
import storage.JsonFormatStorage;
import storage.Storage;
import logic.Logic;

public class TestSystem {
    Logic logicObject = null;
    Parser parserObj = null;
    Storage storageObj = null;

    @Before
    public void setup(){
            parserObj = new Parser();
            File newSaveFile = new File("newsave.txt");
            newSaveFile.delete();
            File saveFile = new File("save.txt");
            saveFile.delete();
            logicObject = new Logic();
            storageObj = new JsonFormatStorage(true);
    }
    
    /*
     * pass string from parser to logic, test adding a simple task
     * test on logic side, check whether the logic process command correct or not
     */
    @Test
    public void testLogicParserSimpleAdd() throws Exception {

        UserInput commandObject = parserObj.parseCommand("add task1");
        
        String executionResult = logicObject.executeCommand(commandObject, true,
                true);
        assertEquals("Item(s) successfully added.",executionResult);
    }
    
    /*
     * pass string from parser to logic, test deleting a task
     * add task1 before testing
     * test on logic side, check whether the logic process command correct or not
     */
    @Test
    public void testLogicParserSimpleDelete() throws Exception {
        UserInput commandObject = parserObj.parseCommand("add task1");
        
        logicObject.executeCommand(commandObject, true,
                true);

        commandObject = parserObj.parseCommand("delete 1");
        logicObject.showUpdatedItems();
        String executionResult = logicObject.executeCommand(commandObject, true,
                true);
        assertEquals("Item(s) successfully deleted.",executionResult);
    }
    
    /*
     * pass string from parser to logic and than save in storage, test undo a task
     * followed by passing the result task to storage and test whether the result stored is correct or not
     * add task1 before testing
     */
    @Test
    public void testLogicParserStorageSimpleUndo() throws Exception {
        UserInput commandObject = parserObj.parseCommand("add task1");
        
        logicObject.executeCommand(commandObject, true,
                true);

        commandObject = parserObj.parseCommand("delete 1");
        logicObject.showUpdatedItems();
        logicObject.executeCommand(commandObject, true,
                true);
        
        commandObject = parserObj.parseCommand("undo");
        logicObject.showUpdatedItems();
        logicObject.executeCommand(commandObject, true,
                true);
        
        ArrayList<TaskAbstraction> listOfTasks = logicObject.listOfTasks;
        storageObj.writeItemList(listOfTasks);
        ArrayList<TaskAbstraction> message = storageObj.getItemList();
        String resultStr = "";
        
        resultStr = message.get(0).getAllInfo();
        assertEquals("Name: task1 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats: null Done: false",resultStr);
    }
    
    @Test
    public void testLogicParserStorageSimpleMark() throws Exception {
        UserInput commandObject = parserObj.parseCommand("add task1");
        
        logicObject.executeCommand(commandObject, true,
                true);

        commandObject = parserObj.parseCommand("mark 1");
        logicObject.showUpdatedItems();
        logicObject.executeCommand(commandObject, true,
                true);
        
        ArrayList<TaskAbstraction> listOfTasks = logicObject.listOfTasks;
        storageObj.writeItemList(listOfTasks);
        ArrayList<TaskAbstraction> message = storageObj.getItemList();
        String resultStr = "";
        
        resultStr = message.get(0).getAllInfo();
        assertEquals("Name: task1 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats: null Done: true",resultStr);
    }
    
    /*
     * pass string from parser to logic , test redo a task
     * followed by passing the result task to storage and test whether the result stored is correct or not
     * add task1, task2 before testing
     */
    @Test
    public void testLogicParserStorageSimpleRedo() throws Exception {
        UserInput commandObject = parserObj.parseCommand("add task1");
        
        logicObject.executeCommand(commandObject, true,
                true);
        
          commandObject = parserObj.parseCommand("add task2");
            
            logicObject.executeCommand(commandObject, true,
                    true);

        commandObject = parserObj.parseCommand("delete 1");
        logicObject.showUpdatedItems();
        logicObject.executeCommand(commandObject, true,
                true);
        
        commandObject = parserObj.parseCommand("undo");
        logicObject.showUpdatedItems();
        logicObject.executeCommand(commandObject, true,
                true);
        commandObject = parserObj.parseCommand("redo");
        logicObject.showUpdatedItems();
        logicObject.executeCommand(commandObject, true,
                true);
        
        ArrayList<TaskAbstraction> listOfTasks = logicObject.listOfTasks;
        storageObj.writeItemList(listOfTasks);
        ArrayList<TaskAbstraction> message = storageObj.getItemList();
        String resultStr = "";
        
        resultStr = message.get(0).getAllInfo();
        assertEquals("Name: task2 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats: null Done: false",resultStr);
    }
    
    /*
     * pass string from parser to logic, test deleting a task
     * followed by passing the result task to storage and test whether the result stored is correct or not
     * add task1 before testing
     */
    @Test
    public void testLogicParserStorageEditOne() throws Exception {
        UserInput commandObject = parserObj.parseCommand("add task1");
        
        logicObject.executeCommand(commandObject, true,
                true);

        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int date = Calendar.getInstance().get(Calendar.DATE);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        
        date += (3 - day + 7)%7 + 7;
        commandObject = parserObj.parseCommand("edit 1 homework by next tuesday loc nus");
        logicObject.showUpdatedItems();
        logicObject.executeCommand(commandObject, true,
                true);
        ArrayList<TaskAbstraction> listOfTasks = logicObject.listOfTasks;
        storageObj.writeItemList(listOfTasks);
        ArrayList<TaskAbstraction> message = storageObj.getItemList();
        String resultStr = "";
        
        resultStr = message.get(0).getAllInfo();
        
            
        assertEquals("Name: homework Starting time: null Ending Time: java.util.GregorianCalendar[time=?,areFieldsSet=false,areAllFieldsSet=false,lenient=true,zone=sun.util.calendar.ZoneInfo[id=\"Asia/Singapore\",offset=28800000,dstSavings=0,useDaylight=false,transitions=9,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=?,YEAR="+year+",MONTH="+month+",WEEK_OF_YEAR=?,WEEK_OF_MONTH=?,DAY_OF_MONTH="+date+",DAY_OF_YEAR=?,DAY_OF_WEEK=?,DAY_OF_WEEK_IN_MONTH=?,AM_PM=1,HOUR=11,HOUR_OF_DAY=23,MINUTE=59,SECOND=0,MILLISECOND=?,ZONE_OFFSET=?,DST_OFFSET=?] Location: nus Period Interval: null Period Repeats: null Done: false", resultStr);
    }
    
    /*
     * pass string from parser to logic, test editing a few tasks using special editing
     * followed by passing the result task to storage and test whether the result stored is correct or not
     * add task1,task2,task3 before testing
     */
    @Test
    public void testLogicParserStorageEditTwo() throws Exception {
        UserInput commandObject = parserObj.parseCommand("add task1");
        logicObject.executeCommand(commandObject, true,
                true);
        
        commandObject = parserObj.parseCommand("add task2");
        logicObject.executeCommand(commandObject, true,
                    true);
            
        commandObject = parserObj.parseCommand("add task3");
        logicObject.executeCommand(commandObject, true,
                        true);    
        
        logicObject.showUpdatedItems();
        commandObject = parserObj.parseCommand("edit 1 homework next tuesday loc nus every 2 days for 2");
        logicObject.executeCommand(commandObject, true,
                true);
        
         commandObject = parserObj.parseCommand("edit 1 loc nus");
            logicObject.executeCommand(commandObject, true,
                    true);
        
        
        ArrayList<TaskAbstraction> listOfTasks = logicObject.listOfTasks;
        storageObj.writeItemList(listOfTasks);
        ArrayList<TaskAbstraction> message = storageObj.getItemList();
        String resultStr = "";
        
        for( int i=0; i < listOfTasks.size(); i++ ) {
            resultStr += message.get(i).getAllInfo() +" ";
            }
    
        assertEquals("Name: homework next tuesday Starting time: null Ending Time: null Location: nus Period Interval: null Period Repeats: null Done: false Name: task2 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats: null Done: false Name: task3 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats: null Done: false ", resultStr);
    }
    
    
    /*
     * pass string from parser to logic, test deleting a task
     * followed by passing the result task to storage and test whether the result stored is correct or not
     * add 3 tasks before testing
     */
    @Test
    public void testLogicParserStorageDelete() throws Exception {
        UserInput commandObject = parserObj.parseCommand("add task1");
        logicObject.executeCommand(commandObject, true, true);
        commandObject = parserObj.parseCommand("add task2");
        logicObject.executeCommand(commandObject, true, true);
        commandObject = parserObj.parseCommand("add task3");
        logicObject.executeCommand(commandObject, true, true);
        

        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int date = Calendar.getInstance().get(Calendar.DATE);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        
        date += (3 - day + 7)%7 + 7;
        
        commandObject = parserObj.parseCommand("edit 1 homework by next tuesday loc nus");
        logicObject.showUpdatedItems();
        logicObject.executeCommand(commandObject, true,
                true);
    
        ArrayList<TaskAbstraction> listOfTasks = logicObject.listOfTasks;
        storageObj.writeItemList(listOfTasks);
        ArrayList<TaskAbstraction> message = storageObj.getItemList();
        String resultStr = "";
        for (int i = 0; i < listOfTasks.size(); i++) {
            resultStr += message.get(i).getAllInfo() + " ";
        }
            
        assertEquals("Name: homework Starting time: null Ending Time: java.util.GregorianCalendar[time=?,areFieldsSet=false,areAllFieldsSet=false,lenient=true,zone=sun.util.calendar.ZoneInfo[id=\"Asia/Singapore\",offset=28800000,dstSavings=0,useDaylight=false,transitions=9,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=?,YEAR="+year+",MONTH="+month+",WEEK_OF_YEAR=?,WEEK_OF_MONTH=?,DAY_OF_MONTH="+date+",DAY_OF_YEAR=?,DAY_OF_WEEK=?,DAY_OF_WEEK_IN_MONTH=?,AM_PM=1,HOUR=11,HOUR_OF_DAY=23,MINUTE=59,SECOND=0,MILLISECOND=?,ZONE_OFFSET=?,DST_OFFSET=?] Location: nus Period Interval: null Period Repeats: null Done: false Name: task2 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats: null Done: false Name: task3 Starting time: null Ending Time: null Location: null Period Interval: null Period Repeats: null Done: false ", resultStr);
    }
    
    /*
     * pass data from logic to storage to store file
     * test on storage side, whether the file stored is the correct one or not
    */
    @Test
    public void testLogicStorage() throws Exception {

        ArrayList<TaskAbstraction> listOfTasks = new ArrayList<TaskAbstraction>();
        TaskAbstraction task1 = new TaskAbstraction("task");
        listOfTasks.add(task1);
        storageObj.writeItemList(listOfTasks);
        ArrayList<TaskAbstraction> message = storageObj.getItemList();
        String resultStr = "";
        
        for(int i = 0; i < message.size(); i++){
            resultStr += message.get(i).getName();
        }
            
        assertEquals("task", resultStr);
    }
}
