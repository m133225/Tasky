package logic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import logic.commands.Command;
import logic.commands.CommandAdd;
import org.junit.Test;

import global.Task;

public class TestLogicAdd {
    /*
     * Tests for basic addition of task with characters and numbers
     */
    @Test
    public void logicAddOne() {
        String taskName = "item 1";
        ArrayList<TaskAbstraction> listOfTasks = new ArrayList<>();
        Task taskToAdd = new Task(taskName);

        Command commandAdd = new CommandAdd(listOfTasks, taskToAdd);
        String result = commandAdd.execute();
        assertEquals("Task 'item 1' successfully added.", result);
        assertEquals(1, listOfTasks.size());
        assertEquals(taskName, listOfTasks.get(0).resolve(0).getName());
    }

    @Test
    public void logicAddTwo() {
        String taskName = "new item";
        String taskLocation = "home";

        ArrayList<TaskAbstraction> listOfTasks = new ArrayList<>();
        Task taskToAdd = new Task(taskName, taskLocation);

        Command commandAdd = new CommandAdd(listOfTasks, taskToAdd);
        String result = commandAdd.execute();

        assertEquals("Task 'new item' successfully added.", result);
        assertEquals(1, listOfTasks.size());
        assertEquals(taskName, listOfTasks.get(0).resolve(0).getName());
        assertEquals(taskLocation, listOfTasks.get(0).resolve(0).getLocation());

        String taskTwoName = "new item 2";
        Task taskTwoToAdd = new Task(taskTwoName);

        Command commandAddTwo = new CommandAdd(listOfTasks, taskTwoToAdd);
        String resultTwo = commandAddTwo.execute();

        assertEquals("Task 'new item 2' successfully added.", resultTwo);
        assertEquals(2, listOfTasks.size());
        assertEquals(taskTwoName, listOfTasks.get(1).resolve(0).getName());
    }
}
