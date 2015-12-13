package logic;

import global.Task;

import java.util.ArrayList;
import java.util.Collections;

public class CommandDelete extends Command {
	ArrayList<Integer> indexesToDelete = null;
	ArrayList<Task> listOfShownTasks = null;
	ArrayList<TaskAbstraction> listOfTasks = null;
	
	TaskAbstraction deletedAbsTask = null;
	TaskOccurrence deletedTaskOcc = null;
	Integer indexToAddBack = null;
	
	CommandDelete(ArrayList<TaskAbstraction> listOfTasks, ArrayList<Task> listOfShownTasks, ArrayList<Integer> indexesToDelete) throws Exception{
		Collections.sort(indexesToDelete);
		for (int i = indexesToDelete.size() - 1; i >= 0; i--) {
			int curIndex = indexesToDelete.get(i);
			if(curIndex < 1 || curIndex > listOfShownTasks.size()){
				throw new Exception("Invalid index");
			}
		}
		this.indexesToDelete = indexesToDelete;
		this.listOfShownTasks = listOfShownTasks;
		this.listOfTasks = listOfTasks;
	}
	
	@Override
	String execute() {
		for (int i = indexesToDelete.size() - 1; i >= 0; i--) {
			int curIndex = indexesToDelete.get(i);
			Task taskToDelete = listOfShownTasks.get(curIndex - 1);
			
			// finds the task and deletes it
			int j = 0;
			boolean foundTask = false;
			while (j < listOfTasks.size() && !foundTask) {
				TaskAbstraction curAbstractTask = listOfTasks.get(j);
				int k = 0;
				while (k < curAbstractTask.getTaskOccurrencesSize()) {
					Task curOccurrence = curAbstractTask.resolve(k);
					if (taskToDelete.compareTo(curOccurrence) == 0) {
						if(curAbstractTask.getTaskOccurrencesSize() == 1){
							listOfTasks.remove(curAbstractTask);
							deletedAbsTask = curAbstractTask;
							indexToAddBack = j;
						} else {
							deletedAbsTask = curAbstractTask;
							deletedTaskOcc = curAbstractTask.removeTaskOccurrence(k);
							indexToAddBack = k;
						}
						foundTask = true;
					}
					k++;
				}
				j++;
			}
		}
		return "Item deleted";
	}

	@Override
	String undo() {
		if (deletedTaskOcc == null){
			listOfTasks.add(indexToAddBack, deletedAbsTask);
		} else {
			deletedAbsTask.addTaskOccurrence(indexToAddBack, deletedTaskOcc);
		}
		return "Undo: Item added back.";
	}

	boolean isUndoable(){
		return true;
	}
}
