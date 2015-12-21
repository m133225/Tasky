package logic;

import java.io.IOException;
import java.util.ArrayList;

import storage.Storage;

public class CommandSaveTo extends Command {
    Storage storageObject = null;
    String newPath = null;
    ArrayList<TaskAbstraction> listOfTasks;
    
    CommandSaveTo(Storage storageObject, String newPath, ArrayList<TaskAbstraction> listOfTasks){
        this.storageObject = storageObject;
        this.newPath = newPath;
        this.listOfTasks = listOfTasks;
    }
    
    boolean isUndoable() {
        return false;
    }

    @Override
    String execute() {
        try {
            storageObject.saveFileToPath(newPath);
            
            listOfTasks.clear();
            listOfTasks.addAll(storageObject.getItemList());
            return "New path saved.";
        } catch (IOException e) {
            return "Error saving new path.";
        }
    }

    @Override
    String undo() {
        return null;
    }

}
