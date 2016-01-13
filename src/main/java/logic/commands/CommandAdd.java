package logic.commands;

import global.Task;
import logic.TaskAbstraction;
import logic.TaskOccurrence;

import java.util.ArrayList;
import java.util.Calendar;

public class CommandAdd extends Command {
	private static final String SUCCESS_ADD = "Task '%s' successfully added.";
    private static final String SUCCESS_UNDO_ADD = "Undo: Task successfully deleted.";
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

	@Override
	public String execute() {
		listOfTasks.add(taskToAdd);

		return String.format(SUCCESS_ADD, taskToAdd.getName());
	}

	@Override
	public String undo() {
		listOfTasks.remove(taskToAdd);
		
		return SUCCESS_UNDO_ADD;
	}

	@Override
	public boolean isUndoable() {
		return true;
	}

}
