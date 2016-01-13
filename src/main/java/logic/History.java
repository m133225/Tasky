package logic;

import logic.commands.Command;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This file contains the history class in which the Logic pushes its reversed command to
 */
public class History {
    Logger logger = Logger.getGlobal();
    ArrayList<Command> commandHistoryList  = new ArrayList<Command>();
    ArrayList<Command> commandUndoHistoryList = new ArrayList<Command>();
    
    
    boolean clearUndoHistoryList(){
        commandUndoHistoryList.clear();
        return true;
    }
    
    boolean pushCommand(Command commandObject, boolean isForUndo) {
        if (isForUndo) {
            commandHistoryList.add(commandObject);
        } else {
            commandUndoHistoryList.add(commandObject);
        }
        return true;
    }
    
    Command getPreviousCommand(boolean isForUndo) {
        ArrayList<Command> arrayListToUse = (isForUndo) ? commandHistoryList : commandUndoHistoryList; 
        int historySize = arrayListToUse.size();
        if (historySize > 0) {
            Command commandObjectToReturn = arrayListToUse.get(historySize - 1);
            arrayListToUse.remove(historySize - 1);
            return commandObjectToReturn;
        } else {
            return null;
        }
    }
}
