package logic;

import global.Task;

import java.util.ArrayList;
import java.util.Collections;

public class CommandDelete extends Command {
	ArrayList<Integer> indexesToDelete = null;
	ArrayList<Task> listOfShownTasks = null;
	ArrayList<TaskAbstraction> listOfTasks = null;
	
	ArrayList<TaskAbstraction> deletedAbsTasks = null;
	ArrayList<TaskOccurrence> deletedTaskOccs = null;
	ArrayList<Integer> indexesToAddBack = null;
	
    CommandDelete(ArrayList<TaskAbstraction> listOfTasks, ArrayList<Task> listOfShownTasks,
            ArrayList<Integer> indexesToDelete) throws Exception {
        Collections.sort(indexesToDelete);
        for (int i = indexesToDelete.size() - 1; i >= 0; i--) {
            int curIndex = indexesToDelete.get(i);
            if (curIndex < 1 || curIndex > listOfShownTasks.size()) {
                throw new Exception("Invalid index");
            }
        }
        this.indexesToDelete = indexesToDelete;
        this.listOfShownTasks = listOfShownTasks;
        this.listOfTasks = listOfTasks;
    }
	
	String execute() {
	    deletedAbsTasks = new ArrayList<TaskAbstraction>();
	    deletedTaskOccs = new ArrayList<TaskOccurrence>();
	    indexesToAddBack = new ArrayList<Integer>();
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
					Task curTask = curAbstractTask.resolve(k);
					if (taskToDelete.sameAs(curTask)) {
						if(curAbstractTask.getTaskOccurrencesSize() == 1){
							listOfTasks.remove(curAbstractTask);
							deletedAbsTasks.add(curAbstractTask);
							deletedTaskOccs.add(null);
							indexesToAddBack.add(j);
						} else {
							deletedAbsTasks.add(curAbstractTask);
							deletedTaskOccs.add(curAbstractTask.removeTaskOccurrence(k));
							indexesToAddBack.add(k);
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
        for (int i = deletedAbsTasks.size() - 1; i >= 0; i--) {
            if (deletedTaskOccs.get(i) == null) {
                listOfTasks.add(indexesToAddBack.get(i), deletedAbsTasks.get(i));
            } else {
                deletedAbsTasks.get(i).addTaskOccurrence(indexesToAddBack.get(i), deletedTaskOccs.get(i));
            }
        }
		return "Undo: Item(s) added back.";
	}

	boolean isUndoable(){
		return true;
	}
}
