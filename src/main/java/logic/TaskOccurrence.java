package logic;

import java.util.Calendar;

import global.Task;

public class TaskOccurrence extends Task {
	 /*
     * Declaration of variables
     */
    private TaskAbstraction abstractTask = null;

    /*
     * Constructor
     */
    public TaskOccurrence(String name) {
        super(name);
    }

    public TaskOccurrence(String name, Calendar endingTime) {
        super(name, endingTime);
    }

    public TaskOccurrence(String name, Calendar endingTime, String location) {
        super(name, endingTime, location);
    }

    public TaskOccurrence(String name, Calendar startingTime, Calendar endingTime) {
    	super(name, startingTime, endingTime);
    }

    public TaskOccurrence(String name, Calendar startingTime, Calendar endingTime,
            String location) {
    	super(name, startingTime, endingTime, location);
    }
/*
    public TaskOccurrence(String name, Calendar startingTime, Calendar endingTime,
            String location, String periodicInterval, String periodicRepeats) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.location = location;
        this.periodicInterval = periodicInterval;
        this.periodicRepeats = periodicRepeats;
    }
*/
    public TaskOccurrence() {
    }

    /*
     * Public methods
     */   
	public TaskAbstraction getAbstractTask() {
		return this.abstractTask;
	} 
    
	public boolean hasAbstractTask() {
		return this.abstractTask != null;
	}
    
	public boolean setAbstractTask(TaskAbstraction abstractTask) {
		this.abstractTask = abstractTask;
		return true;
	}
}
