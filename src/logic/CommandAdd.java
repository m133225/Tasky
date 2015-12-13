package logic;

import global.Task;
import global.UserInput;

import java.util.ArrayList;
import java.util.Calendar;

public class CommandAdd extends Command {
	TaskAbstraction taskToAdd;
	private ArrayList<TaskAbstraction> listOfTasks;
	
	CommandAdd(ArrayList<TaskAbstraction> logicTaskList, Task userTask){
		this.listOfTasks = logicTaskList;
		String taskName = userTask.getName();
		String taskLocation = userTask.getLocation();
		Calendar taskStartingTime = userTask.getStartingTime();
		Calendar taskEndingTime = userTask.getEndingTime();
		
		taskToAdd = new TaskAbstraction(taskName, taskStartingTime, taskEndingTime, taskLocation);
		taskToAdd.addTaskOccurrence(new TaskOccurrence());
	}
	
	String execute() {
		listOfTasks.add(taskToAdd);

		return "Task successfully added.";
	}

	String undo() {
		listOfTasks.remove(taskToAdd);
		
		return "Undo: Task successfully deleted.";
	}
	
	boolean isUndoable(){
		return true;
	}

}
