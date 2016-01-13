package logic.commands;

import java.util.ArrayList;
import java.util.Collections;

import global.Task;
import logic.TaskAbstraction;
import logic.TaskOccurrence;

public class CommandMark extends Command {
    public static final String ERROR_INVALID_INDEX = "Error: Invalid index.";
    public static final String SUCCESS_MARK = "Items(s) marked.";
    public static final String SUCCESS_UNDO_MARK = "Undo: Item(s) marked.";
    private ArrayList<TaskAbstraction> listOfTasks;
    private ArrayList<Task> listOfShownTasks;
    private ArrayList<Integer> indexesToMark;
    private boolean isDoneStatus;
    
    ArrayList<TaskOccurrence> markedTaskOccs = null;
    ArrayList<Integer> indexesToMarkBack = null;
    
    public CommandMark(ArrayList<TaskAbstraction> listOfTasks, ArrayList<Task> listOfShownTasks, ArrayList<Integer> indexesToMark, boolean setAsDone) throws Exception{
        Collections.sort(indexesToMark);
        for (int i = indexesToMark.size() - 1; i >= 0; i--) {
            int curIndex = indexesToMark.get(i);
            if (curIndex < 1 || curIndex > listOfShownTasks.size()) {
                throw new Exception(ERROR_INVALID_INDEX);
            }
        }
        
        this.listOfTasks = listOfTasks;
        this.listOfShownTasks = listOfShownTasks;
        this.indexesToMark = indexesToMark;
        this.isDoneStatus = setAsDone;
    }
    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public String execute() {
        markedTaskOccs = new ArrayList<TaskOccurrence>();
        indexesToMarkBack = new ArrayList<Integer>();
        for (int i = indexesToMark.size() - 1; i >= 0; i--) {
            int curIndex = indexesToMark.get(i);
            Task taskToMark = listOfShownTasks.get(curIndex - 1);
            
            // finds the task and deletes it
            int j = 0;
            boolean foundTask = false;
            while (j < listOfTasks.size() && !foundTask) {
                TaskAbstraction curAbstractTask = listOfTasks.get(j);
                int k = 0;
                while (k < curAbstractTask.getTaskOccurrencesSize()) {
                    Task curTask = curAbstractTask.resolve(k);
                    if (taskToMark.sameAs(curTask)) {
                        TaskOccurrence taskOcc = curAbstractTask.getTaskOccurrence(k);
                        taskOcc.setDone(isDoneStatus);
                        markedTaskOccs.add(taskOcc);
                        indexesToMarkBack.add(k);
                        foundTask = true;
                    }
                    k++;
                }
                j++;
            }
        }
        return SUCCESS_MARK;
    }

    @Override
    public String undo() {
        for (int i = indexesToMark.size() - 1; i >= 0; i--) {
            markedTaskOccs.get(i).setDone(!isDoneStatus);
        }
        return SUCCESS_UNDO_MARK;
    }

}
