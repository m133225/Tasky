package logic.commands;

import global.Task;
import logic.TaskAbstraction;
import logic.TaskOccurrence;

import java.util.ArrayList;
import java.util.Collections;

public class CommandDelete extends Command {
    public static final String SUCCESS_DELETE = "Item(s) deleted.";
    public static final String SUCCESS_UNDO_DELETE = "Undo: Item(s) added back.";
    public static final String ERROR_INVALID_INDEX = "Error: Invalid index.";
    // information needed to execute delete
	ArrayList<Integer> indexesToDelete = null;
	ArrayList<Task> listOfShownTasks = null;
	ArrayList<TaskAbstraction> listOfTasks = null;
	
	// information kept for undo
	ArrayList<TaskAbstraction> deletedAbsTasks = null;
	ArrayList<TaskOccurrence> deletedTaskOccs = null;
	ArrayList<Integer> indexesToAddBack = null;
	
    public CommandDelete(ArrayList<TaskAbstraction> listOfTasks, ArrayList<Task> listOfShownTasks,
            ArrayList<Integer> indexesToDelete) throws Exception {
        Collections.sort(indexesToDelete);
        for (int i = indexesToDelete.size() - 1; i >= 0; i--) {
            int curIndex = indexesToDelete.get(i);
            if (curIndex < 1 || curIndex > listOfShownTasks.size()) {
                throw new Exception(ERROR_INVALID_INDEX);
            }
        }
        this.indexesToDelete = indexesToDelete;
        this.listOfShownTasks = listOfShownTasks;
        this.listOfTasks = listOfTasks;
    }

    @Override
	public String execute() {
	    deletedAbsTasks = new ArrayList<TaskAbstraction>();
	    deletedTaskOccs = new ArrayList<TaskOccurrence>();
	    indexesToAddBack = new ArrayList<Integer>();
		for (int i = indexesToDelete.size() - 1; i >= 0; i--) {
			int curIndex = indexesToDelete.get(i);
			Task taskToDelete = listOfShownTasks.get(curIndex - 1);
			
			// finds the task and deletes it
			int j = 0;
			int numberOfTasksDeleted = 0;
			while (j < listOfTasks.size() && numberOfTasksDeleted < indexesToDelete.size()) {
				TaskAbstraction curAbstractTask = listOfTasks.get(j);
				int k = 0;
				while (k < curAbstractTask.getTaskOccurrencesSize()) {
					Task curTask = curAbstractTask.resolve(k);
					if (taskToDelete.sameAs(curTask)) {
                        if (curAbstractTask.getTaskOccurrencesSize() == 1) {
                            listOfTasks.remove(curAbstractTask);
                            deletedAbsTasks.add(curAbstractTask);
                            deletedTaskOccs.add(null);
                            indexesToAddBack.add(j);
                        } else {
                            deletedAbsTasks.add(curAbstractTask);
                            deletedTaskOccs.add(curAbstractTask.removeTaskOccurrence(k));
                            indexesToAddBack.add(k);
                        }
						numberOfTasksDeleted++;
					}
					k++;
				}
				j++;
			}
		}
		return SUCCESS_DELETE;
	}

	@Override
	public String undo() {
        for (int i = deletedAbsTasks.size() - 1; i >= 0; i--) {
            if (deletedTaskOccs.get(i) == null) {
                listOfTasks.add(indexesToAddBack.get(i), deletedAbsTasks.get(i));
            } else {
                deletedAbsTasks.get(i).addTaskOccurrence(indexesToAddBack.get(i), deletedTaskOccs.get(i));
            }
        }
		return SUCCESS_UNDO_DELETE;
	}

	@Override
	public boolean isUndoable(){
		return true;
	}
}
