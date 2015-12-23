package logic;

import java.util.ArrayList;

import global.Task;

public class CommandSearch extends Command {
    ArrayList<Task> listFilters = null;
    Task filterToAdd = null;
    
    CommandSearch(ArrayList<Task> listFilters, Task newFilter) {
        this.listFilters = listFilters;
        this.filterToAdd = newFilter;
    }

    @Override
    boolean isUndoable() {
        return false;
    }

    @Override
    String execute() {
        if (!(filterToAdd.hasName() || filterToAdd.hasLocation() || filterToAdd.hasStartingTime() || filterToAdd.hasEndingTime())) {
            listFilters.clear();
            return "Filters cleared";
        } else {
            listFilters.add(filterToAdd);
            return "Searching";
        }
    }

    @Override
    String undo() {
        return null;
    }

}
