package logic;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import global.UserInput;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class contains test cases of the executeCommand function of Logic
 */
public class TestLogicExecute {
    Logic logicObject;
    File saveFile;
    
    public void addHelper(TaskAbstraction newTask){
        logicObject.listOfTasks.add(newTask);
        logicObject.listOfShownTasks.add(newTask);
    }
    
    @Before
    public void setup(){
        File saveFile = new File("save.txt");
        File anotherSaveFile = new File("anotherSave.txt");
        saveFile.delete();
        anotherSaveFile.delete();
        logicObject = new Logic();
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        TaskAbstraction task_1 = new TaskAbstraction();
        task_1.setName("Item 1");
        TaskAbstraction task_2 = new TaskAbstraction();
        task_2.setName("Item 2");
        TaskAbstraction task_3 = new TaskAbstraction();
        task_3.setName("Item 3");
        addHelper(task_1);
        addHelper(task_2);
        addHelper(task_3);
    }
    
    @Test
    public void logicExecuteAdd(){
        TaskAbstraction task;
        UserInput commandObject;
        // case 1
        task = new TaskAbstraction();
        task.setName("Submit assignment");
        commandObject = new UserInput(UserInput.Type.ADD, task);
        assertEquals("Item(s) successfully added.", logicObject.executeCommand(commandObject, true, true));
        
        // case 2
        task.setName("%% Random item %%");
        commandObject = new UserInput(UserInput.Type.ADD, task);
        assertEquals("Item(s) successfully added.", logicObject.executeCommand(commandObject, true, true));
    }
    
    @Test
    public void logicExecuteDelete(){
        UserInput commandObject;
        String[] args;
        
        args = new String[1];
        args[0] = "2";
        commandObject = new UserInput(UserInput.Type.DELETE, args);
        assertEquals("Item(s) successfully deleted.", logicObject.executeCommand(commandObject, true, true));
        
    }
    
    @Test
    public void logicExecuteDisplay(){
        UserInput commandObject;
        
        // case 1
        commandObject = new UserInput(UserInput.Type.DISPLAY, new String[]{});
        assertEquals("Displaying items.", logicObject.executeCommand(commandObject, true, true));
    }
    
    @Test
    public void logicExecuteUndo(){
        UserInput commandObject;
        // case 1
        commandObject = new UserInput(UserInput.Type.UNDO);
        assertEquals("Error: No history found.", logicObject.executeCommand(commandObject, true, true));
    }
    
    @Test
    public void logicExecuteRedo(){
        UserInput commandObject;
        // case 1
        commandObject = new UserInput(UserInput.Type.REDO);
        assertEquals("Error: No history found.", logicObject.executeCommand(commandObject, true, true));
    }
    
    @Test
    public void logicExecuteSavePath(){
        UserInput commandObject;
        String[] args;
        
        args = new String[1];
        args[0] = "save.txt";
        commandObject = new UserInput(UserInput.Type.SAVETO, args);
        assertEquals("File path not changed. Entered file path is the same as current one used.", logicObject.executeCommand(commandObject, true, true));
        
        args = new String[1];
        args[0] = "anotherSave.txt";
        commandObject = new UserInput(UserInput.Type.SAVETO, args);
        assertEquals("File path successfully changed.", logicObject.executeCommand(commandObject, true, true));
        
        args = new String[1];
        args[0] = "save.txt";
        commandObject = new UserInput(UserInput.Type.SAVETO, args);
        assertEquals("File path successfully changed.", logicObject.executeCommand(commandObject, true, true));
    }
    
    @Test
    public void logicExecuteMark(){
        UserInput commandObject;
        String[] args;
        
        args = new String[1];
        args[0] = "1";
        commandObject = new UserInput(UserInput.Type.MARK, args);
        assertEquals("Item(s) successfully marked as done.", logicObject.executeCommand(commandObject, true, true));
    }
    
    @Test
    public void logicExecuteNull(){
        UserInput commandObject = null;
        assertEquals("Error: Invalid command.", logicObject.executeCommand(commandObject, true, true));
        
        commandObject = new UserInput(null, new String[]{"123"});
        assertEquals("Error: Handler for this command type has not been defined.", logicObject.executeCommand(commandObject, true, true));
        
    }
    
    @After
    public void cleanup(){
        File saveFile = new File("save.txt");
        File anotherSaveFile = new File("anotherSave.txt");
        saveFile.delete();
        anotherSaveFile.delete();
    }
}
