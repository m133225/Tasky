package global;

import java.util.Calendar;

public interface ITask extends Comparable<ITask>{
    public String getName();
    
    public Calendar getEndingTime();

    public Calendar getStartingTime();
    
    /**
     * This is mainly used to return the date this item is to be classified in
     * 
     * @return
     */
    public Calendar getTime();

    public String getLocation();
    
    public String getAllInfo();
    
    public boolean isDone();

    public boolean hasStartingTime();
    
    public boolean hasName();
    
    public boolean hasLocation();
    
    public boolean hasEndingTime();

    public boolean setName(String newName);

    public boolean setEndingTime(Calendar endingTime);

    public boolean setStartingTime(Calendar startingTime);

    public boolean setLocation(String location);

    public boolean setDone(boolean status);
    
    public ITask clone();
}