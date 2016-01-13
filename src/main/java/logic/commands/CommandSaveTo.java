package logic.commands;

import java.io.IOException;
import java.util.ArrayList;

import logic.TaskAbstraction;
import storage.Storage;

public class CommandSaveTo extends Command {
    public static final String SUCCESS_SAVETO_CHANGED = "Save file successfully changed.";
    public static final String SUCCESS_SAVETO_SAME = "Save file not changed. Same file path has been specified.";
    public static final String ERROR_SAVETO = "Error: Unable to get item list.";
    Storage storageObject = null;
    String newPath = null;
    ArrayList<TaskAbstraction> listOfTasks;
    
    public CommandSaveTo(Storage storageObject, String newPath, ArrayList<TaskAbstraction> listOfTasks) {
        this.storageObject = storageObject;
        this.newPath = newPath;
        this.listOfTasks = listOfTasks;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public String execute() {
        try {
            boolean hasFilePathChanged = storageObject.saveFileToPath(newPath);
            
            listOfTasks.clear();
            listOfTasks.addAll(storageObject.getItemList());

            if (hasFilePathChanged) {
                return SUCCESS_SAVETO_CHANGED;
            } else {
                return SUCCESS_SAVETO_SAME;
            }
        } catch (IOException e) {
            return ERROR_SAVETO;
        }
    }

    @Override
    public String undo() {
        return null;
    }

}
