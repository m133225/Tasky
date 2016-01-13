package logic.commands;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import logic.TaskAbstraction;
import org.junit.Test;

import global.Task;

public class TestLogicAdd {
    /*
     * Tests for basic addition of task with characters and numbers
     */
    @Test
    public void logicAddOne(){
        ArrayList<TaskAbstraction> listOfTasks = new ArrayList<TaskAbstraction>();
        Command commandAdd = new CommandAdd(listOfTasks, new Task("item 1"));
        String result = commandAdd.execute();
        assertEquals("Task 'item 1' successfully added.", result);
    }
}
