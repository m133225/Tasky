package parser;

import global.Command;
import global.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Parser {
	/**
	 * Parses the command string based on keyword
	 * @param command
	 * @return commandObject to be executed, or null if invalid
	 */
	
	// warning messages
	private static final String WARNING_INSUFFICIENT_ARGUMENT = "Warning: '%s': insufficient command arguments";
	
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_SAVEPATH = "savepath";
	private static final String ARGUMENTS_DATE = " date ";
	private static final String ARGUMENTS_DATE_SHORTHAND = " by ";
	private static final String ARGUMENTS_DAY_THIS = " this ";
	private static final String ARGUMENTS_DAY_NEXT = " next ";
	
	
	private static final String[] months = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug",
		"sep", "oct", "nov", "dec"};
	private static final String[] days = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
	
	/**
	 * Parses the string provided and returns the corresponding object
	 * @param command user input
	 * @return Command object for execution
	 * @throws Exception parsing error message
	 */
	public Command parseCommand(String command) throws Exception {
		String[] args = command.split(" ", 2); // extract CommandType from command
		Command commandObject;
		if (args[0].equalsIgnoreCase(COMMAND_ADD)) {
			try {
				Task taskObj = new Task();
				// extracts 'date' segment of the command if present, and returns the remaining 
				// string back to arg
				args[1] = extractDate(args[1], taskObj);
				taskObj.setName(args[1]);
				commandObject = new Command(Command.Type.ADD, taskObj);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_EDIT)) {
			try {
				String[] newArgs = args[1].split(" ", 2);
				String[] indexToDelete = {newArgs[0]};
				Task taskObj = new Task();
				if (newArgs[1].indexOf(ARGUMENTS_DATE) != -1) {
					extractDate(newArgs[1], taskObj);
				} else {
					taskObj.setName(newArgs[1]);
				}
				commandObject = new Command(Command.Type.EDIT, indexToDelete, new Task(newArgs[1]));
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_DELETE)) {
			if (args.length >= 2) { // this is to be edited when the parser becomes more complete
				String[] indexToDelete = {args[1]};
				commandObject = new Command(Command.Type.DELETE, indexToDelete);
			} else {
				throw new Exception(String.format(WARNING_INSUFFICIENT_ARGUMENT, args[0]));
			}
		} else if (args[0].equalsIgnoreCase(COMMAND_EXIT)) {
			commandObject = new Command(Command.Type.EXIT);
		} else if (args[0].equalsIgnoreCase(COMMAND_DISPLAY)) {
			commandObject = new Command(Command.Type.DISPLAY);
		} else if (args[0].equalsIgnoreCase(COMMAND_UNDO)) {
			commandObject = new Command(Command.Type.UNDO);
		} else if (args[0].equalsIgnoreCase(COMMAND_REDO)) {
			commandObject = new Command(Command.Type.REDO);
		} else if (args[0].equalsIgnoreCase(COMMAND_SAVEPATH)) {
			String[] newArgs = {args[1]};
			commandObject = new Command(Command.Type.SAVEPATH, newArgs);
		} else {
			commandObject = null;
		}
		return commandObject;
	}

	/*
	 * Parses raw data fed from Storage through Logic into Task objects
	 */
	public ArrayList<Task> parseFileData(ArrayList<String> fileData) {
		ArrayList<Task> taskList = new ArrayList<Task>();
		for (int i = 0; i < fileData.size(); i++) {
			Task taskObj = new Task();
			if (fileData.get(i).contains(ARGUMENTS_DATE)) {
				extractDate(fileData.get(i), taskObj);
			} else {
				taskObj.setName(fileData.get(i));
			}
			taskList.add(taskObj);
		}
		return taskList;
	}
	
	/*
	 * Extracts 'date' segment of the command if present and updates date field of taskObj. Extracts 'day'
	 * segment of the command if present and updates date field of taskObj - current supported parameters before
	 * day string are 'this' and 'next' 
	 * pre-condition: String must contain DATE_ARGUMENTS, date parameters are valid dates in format dd MMM yyyy
	 * 					OR
	 * 				  String must contain day arg in lowercase only
	 * post-condition: returns extracted string if date is present, else return original string if date
	 * 				   is not present
	 */
	public String extractDate(String arg, Task taskObj) {
		String[] newArgs;
		if (arg.indexOf(ARGUMENTS_DATE) != -1) {
			newArgs = arg.split(ARGUMENTS_DATE);
		} else if (arg.indexOf(ARGUMENTS_DATE_SHORTHAND) != -1){
			newArgs = arg.split(ARGUMENTS_DATE_SHORTHAND);
		} else if (arg.contains(ARGUMENTS_DAY_THIS)) {
				newArgs = arg.split(ARGUMENTS_DAY_THIS);
				for (int i = 0; i < newArgs.length - 1; i++) {
					for (int n = 0; n < days.length; n++) {
						System.out.println(newArgs[i+1].indexOf(days[n]));
						if (newArgs[i+1].indexOf(days[n]) == 0) {
							System.out.println("success");
							return days[n];
						}
					}
				}
		} else {
			// no date parameters found; return original string
			return arg;
		}
		String[] dateArgs = newArgs[1].split(" ");
		int day = Integer.parseInt(dateArgs[0]);
		int month = Arrays.asList(months).indexOf(dateArgs[1]);
		// year will be set to current year if not specified by user
		int year;
		try {
			year = Integer.parseInt(dateArgs[2]);
		} catch (ArrayIndexOutOfBoundsException e) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		Calendar date = new GregorianCalendar(year, month, day);
		taskObj.setEndingTime(date);
		return newArgs[0];
	}
}