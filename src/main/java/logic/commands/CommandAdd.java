package logic.commands;

import global.Task;
import logic.TaskAbstraction;
import logic.TaskOccurrence;

import java.util.ArrayList;
import java.util.Calendar;

public class CommandAdd extends Command {
	TaskAbstraction taskToAdd;
	private ArrayList<TaskAbstraction> listOfTasks;
	
    public CommandAdd(ArrayList<TaskAbstraction> logicTaskList, Task userTask) {
        this.listOfTasks = logicTaskList;
        String taskName = userTask.getName();
        String taskLocation = userTask.getLocation();
        Calendar taskStartingTime = userTask.getStartingTime();
        Calendar taskEndingTime = userTask.getEndingTime();

        taskToAdd = new TaskAbstraction(taskName, taskStartingTime, taskEndingTime, taskLocation);
        taskToAdd.addTaskOccurrence(new TaskOccurrence());
    }
	
	public String execute() {
		listOfTasks.add(taskToAdd);

		return "Task '"+ taskToAdd.getName() +"' successfully added.";
	}

	public String undo() {
		listOfTasks.remove(taskToAdd);
		
		return "Undo: Task successfully deleted.";
	}

	public boolean isUndoable(){
		return true;
	}

}
