package logic;

import global.ITask;
import global.Task;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This is a data structure to store the details of a task
 */
public class TaskAbstraction extends Task {

    /*
     * Declaration of variables
     */
    private ArrayList<TaskOccurrence> taskOccurrences = new ArrayList<TaskOccurrence>();

    /*
     * Constructor
     */
    public TaskAbstraction(String name) {
    	 super(name);
    }

    public TaskAbstraction(String name, Calendar endingTime) {
    	super(name, endingTime);
    }

    public TaskAbstraction(String name, Calendar endingTime, String location) {
    	super(name, endingTime, location);
    }

    public TaskAbstraction(String name, Calendar startingTime, Calendar endingTime) {
    	super(name, startingTime, endingTime);
    }

    public TaskAbstraction(String name, Calendar startingTime, Calendar endingTime,
            String location) {
    	super(name, startingTime, endingTime, location);
    }
/*
    public TaskAbstraction(String name, Calendar startingTime, Calendar endingTime,
            String location, String periodicInterval, String periodicRepeats) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.location = location;
        this.periodicInterval = periodicInterval;
        this.periodicRepeats = periodicRepeats;
    }
*/
    public TaskAbstraction() {
    }

    /*
     * Public methods
     */    
    public ArrayList<TaskOccurrence> getTaskOccurrences() {
    	return this.taskOccurrences;
    }
    
    public int getTaskOccurrencesSize(){
    	return this.taskOccurrences.size();
    }
    
    public boolean hasTaskOccurrence() {
    	return !(this.taskOccurrences.size() == 0);
    }
    
    public boolean addTaskOccurrence(TaskOccurrence newTaskOcc){
    	this.taskOccurrences.add(newTaskOcc);
    	return true;
    }
    
    public boolean addTaskOccurrence(int index, TaskOccurrence newTaskOcc){
    	this.taskOccurrences.add(index, newTaskOcc);
    	return true;
    }
    
    public TaskOccurrence removeTaskOccurrence(int index){
    	return this.taskOccurrences.remove(index);
    }

    public Task resolve(int index) {
        TaskOccurrence curOccurrence = taskOccurrences.get(index);
        String finalName = null;
        String finalLocation = null;
        Calendar finalStartingTime = null;
        Calendar finalEndingTime = null;

        if (curOccurrence.hasName()) {
            finalName = curOccurrence.getName();
        } else {
            finalName = this.getName();
        }
        if (curOccurrence.hasLocation()) {
            finalLocation = curOccurrence.getLocation();
        } else {
            finalLocation = this.getLocation();
        }
        if (curOccurrence.hasStartingTime()) {
            finalStartingTime = curOccurrence.getStartingTime();
        } else {
            finalStartingTime = this.getStartingTime();
        }
        if (curOccurrence.hasEndingTime()) {
            finalEndingTime = curOccurrence.getEndingTime();
        } else {
            finalEndingTime = this.getEndingTime();
        }

        return new Task(finalName, finalStartingTime, finalEndingTime, finalLocation);
    }
    
    public ArrayList<Task> resolveAll() {
        ArrayList<Task> resolvedTasks = new ArrayList<Task>();
        for (int i = 0; i < taskOccurrences.size(); i++) {
            resolvedTasks.add(resolve(i));
        }
        return resolvedTasks;
    }
}
