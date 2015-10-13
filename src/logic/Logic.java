package logic;

import global.Command;
import global.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import parser.Parser;
import storage.Storage;
import storage.ManualFormatStorage;
import storage.JsonFormatStorage;
import ui.UI;


/**
 * This file contains the main program of the command-line calendar, Tasky.
 * Please read our user guide at README.md if there are any questions.
 * 
 * @author cs2103aug2015-w10-4j
 *
 */
public class Logic {
	
	/*
	 * Declaration of object variables
	 */
	Logger logger = Logger.getGlobal();
	UI UIObject;
	Parser parserObject;
	Storage storageObject;
	History historyObject;
	ArrayList<Task> listOfTasks = new ArrayList<Task>();
	
	// date format converter
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
	
	/*
	 * Static strings - errors and messages
	 */
	private static final String MESSAGE_WELCOME = "Welcome to Tasky! This is an open source project";
	private static final String MESSAGE_PROMPT_COMMAND = "command :";

	private static final String MESSAGE_SUCCESS_UNDO_ADD = "Undo : Deleted item restored.";
	private static final String MESSAGE_SUCCESS_REDO_ADD = "Redo : Deleted item restored.";
	private static final String MESSAGE_SUCCESS_UNDO_DELETE = "Undo : Added item removed.";
	private static final String MESSAGE_SUCCESS_REDO_DELETE = "Redo : Added item removed.";
	private static final String MESSAGE_SUCCESS_UNDO_EDIT = "Undo : Reverted edits.";
	private static final String MESSAGE_SUCCESS_REDO_EDIT = "Redo : Reverted edits.";
	private static final String MESSAGE_SUCCESS_ADD = "Item successfully added.";
	private static final String MESSAGE_SUCCESS_DELETE = "Item successfully deleted.";
	private static final String MESSAGE_SUCCESS_EDIT = "Item successfully edited.";
	private static final String MESSAGE_SUCCESS_EXIT = "Exiting program...";
	private static final String MESSAGE_SUCCESS_DISPLAY = "Displaying items.";
	private static final String MESSAGE_SUCCESS_CHANGE_FILE_PATH = "File path successfully changed.";
	private static final String MESSAGE_SUCCESS_CHANGE_FILE_PATH_BUT_CREATE_FILE = "File path successfully changed. \nNo file was detected, so Tasky has created one for you.";
	private static final String MESSAGE_DISPLAY_TASKLINE_INDEX = "%3d. ";
	private static final String MESSAGE_DISPLAY_NEWLINE = "\r\n"; // isolated this string for ease of concatenation
	private static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
	private static final String SEPARATOR_DISPLAY_FIELDS = " | ";
	private static final String ERROR_WRITING_FILE = "Error: Unable to write file.";
	private static final String ERROR_CREATING_FILE = "Error: Unable to create file.";
	private static final String ERROR_FILE_NOT_FOUND = "Error: Data file not found.";
	private static final String ERROR_LOG_FILE_INITIALIZE = "Error: Cannot initialize log file.";
	private static final String ERROR_INVALID_ARGUMENT = "Error: Invalid argument for command.";
	private static final String ERROR_INVALID_COMMAND = "Error: Invalid command.";
	private static final String ERROR_NO_COMMAND_HANDLER = "Error: Handler for this command type has not been defined.";
	private static final String ERROR_INVALID_INDEX = "Error: There is no item at this index.";
	private static final String ERROR_UI_INTERRUPTED = "Error: UI prompt has been interrupted.";
	private static final String ERROR_NO_HISTORY = "Error: No history found.";
	private static final String ERROR_CANNOT_WRITE_TO_HISTORY = "Error: Unable to store command in history.";
	/*
	 * Main program
	 */
	public static void main(String[] args) {
		Logic logicObject = new Logic();
		logicObject.start();
	}
	
	/*
	 * Constructor to initialize object variables
	 */
	public Logic() {
		UIObject = new UI();
		parserObject = new Parser();
		storageObject = new JsonFormatStorage();
		historyObject = new History();
		try {
			FileHandler logHandler = new FileHandler("tasky.log");
			LogManager.getLogManager().reset(); // removes printout to console aka root handler
			logHandler.setFormatter(new SimpleFormatter()); // set output to a human-readable log format
			logger.addHandler(logHandler);
			logger.setLevel(Level.INFO); // setting of log level
			
			listOfTasks = storageObject.getItemList();
		} catch (FileNotFoundException e) {
			UIObject.showToUser(ERROR_FILE_NOT_FOUND);
		} catch (SecurityException e) {
			UIObject.showToUser(ERROR_LOG_FILE_INITIALIZE);
		} catch (IOException e) {
			UIObject.showToUser(ERROR_LOG_FILE_INITIALIZE);
		}
	}
	
	void start() {
		showWelcomeMessage();
		readAndExecuteUserInput();
	}
	
	void showWelcomeMessage() {
		UIObject.showToUser(MESSAGE_WELCOME);
	}
	
	/**
	 * Repeatedly
	 *  Reads the user input, parses the command, executes the command object,
	 *  shows the result in UI, writes latest task list to file
	 * until the program exits
	 */
	void readAndExecuteUserInput() {
		while (true) {
			try {
				String userInput = UIObject.promptUser(MESSAGE_PROMPT_COMMAND);
				Command commandObject = parserObject.parseCommand(userInput);
				String executionResult = executeCommand(commandObject, true, true);
				UIObject.showStatusToUser(executionResult);
				showUpdatedItems();
				storageObject.writeItemList(listOfTasks);
			} catch (InterruptedException e) {
				// something interrupted the UI's wait for user input
				UIObject.showStatusToUser(ERROR_UI_INTERRUPTED);
			} catch (IOException e) {
				// error writing
				UIObject.showStatusToUser(ERROR_WRITING_FILE);
			} catch (Exception e) {
				// warning from parsing user command
				UIObject.showStatusToUser(e.getMessage());
			}
		}
	}
	
	/**
	 * Executes a command based on commandObject
	 * @return status string to be shown to user
	 */
	String executeCommand(Command commandObject, boolean shouldPushToHistory, boolean isUndoHistory) {
		if (commandObject == null) {
			return ERROR_INVALID_COMMAND;
		}
		Command.Type commandType = commandObject.getCommandType();
		Task userTask = commandObject.getTask();
		ArrayList<String> argumentList = commandObject.getArguments();
		switch (commandType) {
			case ADD :
				logger.info("ADD command detected");
				return addItem(userTask, argumentList, shouldPushToHistory, isUndoHistory);
			case DELETE :
				logger.info("DELETE command detected");
				return deleteItem(argumentList, shouldPushToHistory, isUndoHistory);
			case EDIT :
				logger.info("EDIT command detected");
				return editItem(userTask, argumentList, shouldPushToHistory, isUndoHistory);
			case DISPLAY :
				logger.info("DISPLAY command detected");
				return displayItems();
			case UNDO :
				logger.info("UNDO command detected");
				return undoCommand();
			case REDO :
				logger.info("REDO command detected");
				return redoCommand();
			case SAVEPATH :
				logger.info("SAVEPATH command detected");
				return saveFilePath(argumentList);
			case EXIT :
				logger.info("EXIT command detected");
				return exitProgram();
			default :
		}
		return ERROR_NO_COMMAND_HANDLER;
	}
	
	/**
	 * Adds an item to the list of tasks in memory
	 * @param argumentList if empty, last element is used. if not, the index string is read from position 0
	 * @param shouldPushToHistory
	 * @return status string
	 */
	String addItem(Task userTask, ArrayList<String> argumentList, boolean shouldPushToHistory, boolean isUndoHistory) {
		try {
			int index;
			if (isEmptyArgumentList(argumentList)) {
				index = listOfTasks.size();
			} else {
				index = Integer.parseInt(argumentList.get(0)) - 1;
			}
			listOfTasks.add(index, userTask);

			if (shouldPushToHistory) {
				if (isUndoHistory) {
					//	handle history
					String[] indexString = {Integer.toString(index + 1)};
					if (!pushToHistory(new Command(Command.Type.DELETE, indexString))) {
						return ERROR_CANNOT_WRITE_TO_HISTORY;
					}
					return MESSAGE_SUCCESS_ADD;
				} else {
					String[] indexString = {Integer.toString(index + 1)};
					if (!pushToUndoHistory(new Command(Command.Type.DELETE, indexString))) {
						return ERROR_CANNOT_WRITE_TO_HISTORY;
					}
					return MESSAGE_SUCCESS_UNDO_ADD;
				}
			} else {
				return MESSAGE_SUCCESS_REDO_ADD;
			}
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		}
	}
	
	/**
	 * Deletes an item from the list of tasks in memory
	 * @param argumentList the index string is read from position 0
	 * @param shouldPushToHistory
	 * @return status string
	 */
	String deleteItem(ArrayList<String> argumentList, boolean shouldPushToHistory, boolean isUndoHistory) {
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		try {
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			if (isValidIndex(index)) {
				// for history
				Task taskRemoved = listOfTasks.remove(index);
				
				if(shouldPushToHistory){
					if(isUndoHistory){
						//handle history
						String[] indexString = {Integer.toString(index + 1)};
						if (!pushToHistory(new Command(Command.Type.ADD, indexString, taskRemoved))) {
							return ERROR_CANNOT_WRITE_TO_HISTORY;
						}
						return MESSAGE_SUCCESS_DELETE;
					}else{
						String[] indexString = {Integer.toString(index + 1)};
						if (!pushToUndoHistory(new Command(Command.Type.ADD, indexString, taskRemoved))) {
							return ERROR_CANNOT_WRITE_TO_HISTORY;
						}
						return MESSAGE_SUCCESS_UNDO_DELETE;
					}
				} else {
					
					return MESSAGE_SUCCESS_REDO_DELETE;
				}
				
			} else {
				return ERROR_INVALID_INDEX;
			}
			
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		}
		
	}
	
	/**
	 * Replaces an item from the list of tasks in memory with the new userTask
	 * @param userTask the new task to be replaced with
	 * @param argumentList the index string is read from position 0
	 * @return status string
	 */
	String editItem(Task userTask, ArrayList<String> argumentList, boolean shouldPushToHistory, boolean isUndoHistory) {
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		try {
			int index = Integer.parseInt(argumentList.get(0)) - 1;
			if (isValidIndex(index)) {
				// for history
				Task taskEdited = listOfTasks.get(index);

				listOfTasks.remove(index);
				listOfTasks.add(index, userTask);
				
				if(shouldPushToHistory){
					if(isUndoHistory){
						//	handle history
						String[] indexString = {Integer.toString(index + 1)};
						if (!pushToHistory(new Command(Command.Type.EDIT, indexString, taskEdited))) {
							return ERROR_CANNOT_WRITE_TO_HISTORY;
						}
						return MESSAGE_SUCCESS_EDIT;
					}else{
						String[] indexString = {Integer.toString(index + 1)};
						if (!pushToUndoHistory(new Command(Command.Type.EDIT, indexString, taskEdited))) {
							return ERROR_CANNOT_WRITE_TO_HISTORY;
						}
						return MESSAGE_SUCCESS_UNDO_EDIT;
					}
				}else{
					return MESSAGE_SUCCESS_REDO_EDIT;
				}
			} else {
				return ERROR_INVALID_INDEX;
			}
		} catch (NumberFormatException e) {
			return ERROR_INVALID_ARGUMENT;
		}
	}
	
	boolean isValidIndex(int index) {
		return index >= 0 && index < listOfTasks.size();
	}
	
	String displayItems(){
		if (listOfTasks.isEmpty()) {
			return MESSAGE_DISPLAY_EMPTY;
		}else{
		   // showUpdatedItems();
			return MESSAGE_SUCCESS_DISPLAY;
		}
	}
	
	/**
	 * @return calls the UI to display updated list of items
	 */
	boolean showUpdatedItems() {
		if (listOfTasks.isEmpty()) {
			UIObject.showToUser(MESSAGE_DISPLAY_EMPTY);
		}else{
			String stringToDisplay = "";
			for (int i = 0; i < listOfTasks.size(); i++) {
				Task curTask = listOfTasks.get(i);
				stringToDisplay += String.format(MESSAGE_DISPLAY_TASKLINE_INDEX, i + 1);
				if (curTask != null) {
					stringToDisplay += String.format("%-30s", curTask.getName()) + SEPARATOR_DISPLAY_FIELDS;
					// leave a blank column for all additional params even if not present for better organisation
					stringToDisplay += String.format("%-11s", (curTask.getEndingTime() != null ?
							dateFormat.format(curTask.getEndingTime().getTime()) : ""))
							+ SEPARATOR_DISPLAY_FIELDS;
					stringToDisplay += String.format("%-15s", (curTask.getLocation() != null ?
							curTask.getLocation() : "")) + SEPARATOR_DISPLAY_FIELDS;
					stringToDisplay += String.format("%-15s", (curTask.getPeriodic() != null ?
							curTask.getPeriodic() : ""));
				}
				stringToDisplay += MESSAGE_DISPLAY_NEWLINE;
			}	
			UIObject.showToUser(stringToDisplay);
		}
		return true;
	}
	
	String undoCommand(){
		Command previousCommand = historyObject.getPreviousCommand(true);
		if (previousCommand == null) {
			return ERROR_NO_HISTORY;
		}
		return executeCommand(previousCommand, true, false);
	}
	
	String redoCommand(){
		Command previousCommand = historyObject.getPreviousCommand(false);
		if (previousCommand == null) {
			return ERROR_NO_HISTORY;
		}
		return executeCommand(previousCommand, false, false);
	}
		
	boolean pushToHistory(Command commandObject){
		return historyObject.pushCommand(commandObject, true);
	}
	
	boolean pushToUndoHistory(Command commandObject){
		return historyObject.pushCommand(commandObject, false);
	}
	
	/**
	 * Sets the data file path
	 * @param argumentList the file path string is read from position 0
	 * @return status string
	 */
	String saveFilePath(ArrayList<String> argumentList){
		if (isEmptyArgumentList(argumentList)) {
			return ERROR_INVALID_ARGUMENT;
		}
		String filePath = argumentList.get(0);
		try {
			boolean locationChanged = storageObject.saveFileToPath(filePath);
			listOfTasks = storageObject.getItemList();
			if (locationChanged) {
				return MESSAGE_SUCCESS_CHANGE_FILE_PATH;
			} else {
				return MESSAGE_SUCCESS_CHANGE_FILE_PATH_BUT_CREATE_FILE;
			}
		} catch (IOException e) {
			return ERROR_CREATING_FILE;
		}
	}
	
	boolean isEmptyArgumentList(ArrayList<String> argumentList) {
		if (argumentList == null || argumentList.isEmpty()) {
			return true;
		}
		return false;
	}
	
	String exitProgram() {
		System.exit(0);
		return MESSAGE_SUCCESS_EXIT;
	}
}