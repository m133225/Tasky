package logic;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLogicUndo {
    Logic logicObject;

    public void addHelper(TaskAbstraction newTask) {
        logicObject.listOfTasks.add(newTask);
        logicObject.listOfShownTasks.add(newTask);
    }
    
    @Before
    public void setup(){
        logicObject = new Logic();
        File saveFile = new File("save.txt");
        saveFile.delete();
    }
    
    //test undo without any next execution
    @Test
    public void logicUndoEmpty() throws Exception{
        String message = logicObject.undoCommand();
        assertEquals("Error: No history found.", message);
    }
    
    //test undo before add
    @Test
    public void logicUndoAdd(){

        ArrayList<TaskAbstraction> newTasks = new ArrayList<TaskAbstraction>();
        newTasks.add(new TaskAbstraction("item 1"));
        logicObject.addItem(newTasks, new ArrayList<Integer>(), true, true);
        logicObject.showUpdatedItems();
        String message = logicObject.undoCommand();        
        assertEquals("Undo : Added item(s) removed.", message);
    }

    //test undo before multiple delete
    @Test
    public void logicUndoMultipleDelete(){
        logicObject.listOfTasks = new ArrayList<TaskAbstraction>();
        logicObject.listOfTasks.add(new TaskAbstraction("some item 1"));
        logicObject.listOfTasks.add(new TaskAbstraction("some item 2"));    
        logicObject.listOfTasks.add(new TaskAbstraction("some item 3"));
        
        ArrayList<Integer> indexList = new ArrayList<Integer>();        

        indexList.add(0);
        indexList.add(2);
        indexList.add(1);
        
        logicObject.deleteItem(indexList, true, true);
        String message = logicObject.undoCommand();
        assertEquals("Undo : Deleted item(s) restored.", message);
    }
    
    //test undo before edit
    @Test
    public void logicUndoEdit(){

        ArrayList<TaskAbstraction> listToEdit = new ArrayList<TaskAbstraction>();
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        
        listToEdit.add(new TaskAbstraction("Old item 1"));
        logicObject.addItem(listToEdit, new ArrayList<Integer>(), true, true);

        indexList.add(0);
        listToEdit.clear();
        listToEdit.add(new TaskAbstraction("New item 1"));
        logicObject.editItem(listToEdit, indexList, true, true);

        String message = logicObject.undoCommand();        
        assertEquals("Undo : Reverted edits.", message);
        
        assertEquals("Old item 1", logicObject.listOfTasks.get(0).getName());
    }
    
    @After
    public void cleanup(){
    }
}
