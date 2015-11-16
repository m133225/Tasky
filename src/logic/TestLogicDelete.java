package logic;

import static org.junit.Assert.assertEquals;
import global.Task;

import java.io.File;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicDelete {
    Logic logicObject;
    
    @Before
    public void setup(){
        File saveFile = new File("save1.txt");
        saveFile.delete();
        logicObject = new Logic();
    }    
    
    /*
     * Tests corner cases of erroneous deletions
     */
    @Test
    public void logicDeleteOne(){
        logicObject.listOfShownTasks = new ArrayList<Task>();
        Task task1 = new Task("item 1");
        Task task2 = new Task("item 2");
        Task task3 = new Task("item 3");
        logicObject.listOfTasks.add(task1);
        logicObject.listOfTasks.add(task2);
        logicObject.listOfTasks.add(task3);
    
        
        ArrayList<Integer> argumentList = new ArrayList<Integer>();
        String result;
        argumentList.clear();
        argumentList.add(4);
        result = logicObject.deleteItem(argumentList, true, true);
        assertEquals("Error: There is no item at this index.", result);
        
        argumentList.clear();
        argumentList.add(-1);
        result = logicObject.deleteItem(argumentList, true, true);
        assertEquals("Error: There is no item at this index.", result);
        
        argumentList.clear();
        argumentList.add(-2);
        result = logicObject.deleteItem(argumentList, true, true);
        assertEquals("Error: There is no item at this index.", result);
        
        argumentList.clear();
        argumentList.add(55);
        result = logicObject.deleteItem(argumentList, true, true);
        assertEquals("Error: There is no item at this index.", result);
        
        argumentList.clear();
        argumentList.add(2);
        result = logicObject.deleteItem(argumentList, true, true);
        assertEquals("Item(s) successfully deleted.", result);
    }
    
    /*
     * Tests multiple deletion
     */
    @Test
    public void logicDeleteMultipleOne(){
        logicObject.listOfTasks = new ArrayList<Task>();
        logicObject.listOfTasks.add(new Task("some item 1"));
        logicObject.listOfTasks.add(new Task("some item 2"));    
        logicObject.listOfTasks.add(new Task("some item 3"));
        
        ArrayList<Integer> indexList = new ArrayList<Integer>();        

        indexList.add(0);
        indexList.add(2);
        indexList.add(1);
        
        String result = logicObject.deleteItem(indexList, true, true);
        assertEquals("Item(s) successfully deleted.", result);
    }
    
    /*
     * Tests multiple deletion of a subset of tasks
     */
    @Test
    public void logicDeleteMultipleTwo(){
        logicObject.listOfTasks = new ArrayList<Task>();
        logicObject.listOfTasks.add(new Task("some item 1"));
        logicObject.listOfTasks.add(new Task("some item 2"));
        logicObject.listOfTasks.add(new Task("some item 3"));
        logicObject.listOfTasks.add(new Task("some item 4"));
        logicObject.listOfTasks.add(new Task("some item 5"));
        logicObject.listOfTasks.add(new Task("some item 6"));
        
        
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        
        indexList.add(0);
        indexList.add(2);
        indexList.add(1);
        
        String result = logicObject.deleteItem(indexList, true, true);
        assertEquals("Item(s) successfully deleted.", result);
    }
    
    /*
     * Tests multiple deletion with invalid indexes
     */
    @Test
    public void logicDeleteMultipleThree(){
        logicObject.listOfTasks = new ArrayList<Task>();
        logicObject.listOfTasks.add(new Task("some item 1"));
        logicObject.listOfTasks.add(new Task("some item 2"));    
        logicObject.listOfTasks.add(new Task("some item 3"));
        logicObject.listOfTasks.add(new Task("some item 4"));
        logicObject.listOfTasks.add(new Task("some item 5"));    
        logicObject.listOfTasks.add(new Task("some item 6"));
        
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        String result;
        
        indexList.add(6);
        indexList.add(22);
        indexList.add(2);
        
        result = logicObject.deleteItem(indexList, true, true);
        assertEquals("Error: There is no item at this index.", result);
        assertEquals(logicObject.listOfTasks.size(), 6);
    }
    
    /*
     * Tests multiple deletion when there are more tasks in list
     */
    @Test
    public void logicDeleteMultipleItemsSuccess() {
        logicObject.listOfTasks = new ArrayList<Task>();
        for (int i = 'a'; i <= 'z'; i++) {
            logicObject.listOfTasks.add((new Task(String.valueOf(i))));
        }
        
        ArrayList<Integer> indexList = new ArrayList<>();
        String result;
        
        indexList.add(1);
        indexList.add(2);
        indexList.add(3);
        indexList.add(4);
        indexList.add(5);
        indexList.add(24);
        indexList.add(25);
        
        result = logicObject.deleteItem(indexList, true, true);
        assertEquals("Item(s) successfully deleted.", result);
        assertEquals(logicObject.listOfTasks.size(), 19);
    }
    
    /*
     * Tests multiple deletion with invalid index
     */
    @Test
    public void logicDeleteMultipleItemsFail() {
        logicObject.listOfTasks = new ArrayList<Task>();
        for (int i = 'a'; i <= 'z'; i++) {
            logicObject.listOfTasks.add(new Task(String.valueOf(i)));
        }
        
        ArrayList<Integer> indexList = new ArrayList<>();
        String result;
        
        indexList.add(-1);
        indexList.add(2);
        indexList.add(-3);
        
        result = logicObject.deleteItem(indexList, true, true);
        assertEquals("Error: There is no item at this index.", result);
        assertEquals(logicObject.listOfTasks.size(), 26);
    }
    
    @After
    public void cleanup(){
        File saveFile = new File("save.txt");
        saveFile.delete();
    }
}
