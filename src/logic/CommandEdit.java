package logic;

import java.util.ArrayList;
import java.util.Calendar;

import global.Task;

public class CommandEdit extends Command {
    ArrayList<Task> listOfShownTasks = null;
    ArrayList<TaskAbstraction> listOfTasks = null;
    Integer indexToEdit = null;
    Task userTask = null;
    
    TaskAbstraction editedAbstractTask = null;
    TaskOccurrence editedTaskOcc = null;
    Integer indexToEditBack = null;

    CommandEdit(ArrayList<TaskAbstraction> listOfTasks, ArrayList<Task> listOfShownTasks, int indexToEdit, Task userTask) throws Exception {
        if (indexToEdit < 1 || indexToEdit > listOfShownTasks.size()) {
            throw new Exception("Invalid index");
        }
        this.listOfTasks = listOfTasks;
        this.listOfShownTasks = listOfShownTasks;
        this.indexToEdit = indexToEdit;
        this.userTask = userTask;
    }
    
    boolean isUndoable() {
        return true;
    }

    String execute() {
        Task taskToEdit = listOfShownTasks.get(indexToEdit - 1);
        // finds the task and edits it
        int j = 0;
        boolean foundTask = false;
        while (j < listOfTasks.size() && !foundTask) {
            TaskAbstraction curAbstractTask = listOfTasks.get(j);
            int k = 0;
            while (k < curAbstractTask.getTaskOccurrencesSize()) {
                Task curTask = curAbstractTask.resolve(k);
                if (taskToEdit.compareTo(curTask) == 0) {
                    TaskOccurrence curOccurrence = curAbstractTask.getTaskOccurrences().remove(k);
                    
                    String finalName = (userTask.hasName()) ? userTask.getName() : curOccurrence.getName();
                    String finalLocation = (userTask.hasLocation()) ? userTask.getLocation() : curOccurrence.getLocation();
                    Calendar finalStartingTime = (userTask.hasStartingTime()) ? userTask.getStartingTime() : curOccurrence.getStartingTime();
                    Calendar finalEndingTime = (userTask.hasEndingTime()) ? userTask.getEndingTime() : curOccurrence.getEndingTime();
                    TaskOccurrence newTaskOcc = new TaskOccurrence(finalName, finalStartingTime, finalEndingTime, finalLocation);
                    if (curOccurrence.isDone()) {
                        newTaskOcc.setDone(true);
                    }
                    curAbstractTask.getTaskOccurrences().add(k, newTaskOcc);
                    foundTask = true;
                    
                    indexToEditBack = k;
                    editedAbstractTask = curAbstractTask;
                    editedTaskOcc = curOccurrence;
                }
                k++;
            }
            j++;
        }
        return "Edited";
    }

    String undo() {
        TaskOccurrence oldTaskOcc = editedTaskOcc;
        editedTaskOcc = editedAbstractTask.getTaskOccurrences().remove((int)indexToEditBack);
        editedAbstractTask.getTaskOccurrences().add(indexToEditBack, oldTaskOcc);
        
        return "Undo: Item Edited";
    }

}
