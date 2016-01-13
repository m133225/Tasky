package logic.commands;

import java.util.ArrayList;

import global.Task;

public class CommandFilter extends Command {
    ArrayList<Task> listFilters = null;
    Task filterToAdd = null;
    
    public CommandFilter(ArrayList<Task> listFilters, Task newFilter) {
        this.listFilters = listFilters;
        this.filterToAdd = newFilter;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public String execute() {
        if (!(filterToAdd.hasName() || filterToAdd.hasLocation() || filterToAdd.hasStartingTime() || filterToAdd.hasEndingTime())) {
            listFilters.clear();
            return "Filters cleared";
        } else {
            listFilters.add(filterToAdd);
            return "Searching";
        }
    }

    @Override
    public String undo() {
        return null;
    }

}
