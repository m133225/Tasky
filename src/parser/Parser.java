package parser;

import global.Command;
import global.Task;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Parser {
	/**
	 * Parses the command string based on keyword
	 * 
	 * @param command
	 * @return commandObject to be executed, or null if invalid
	 */
	private static Parser parserInstance = null;

	// warning messages
	private static final String WARNING_INSUFFICIENT_ARGUMENT = "Warning: '%s': insufficient command arguments";
	private static final String WARNING_INVALID_DAY = "Invalid day specified!";
	private static final String WARNING_INVALID_MONTH = "Invalid month specified!";

	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_EXIT = "exit";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_SAVETO = "saveto";
	private static final String ARGUMENT_FROM = "start";
	private static final String ARGUMENT_TO = "end";

	private static final String[] ARGUMENT_EVENT = { "start", "end" };
	private static final String[] ARGUMENTS_END_DATE = { " date ", " by " };
	private static final String[] ARGUMENTS_END_DATE_SPECIAL = { " this ",
			" next ", " tomorrow", " today" };
	private static final String ARGUMENTS_PERIODIC = " every ";
	private static final String ARGUMENT_LOC = "loc";
	private static final String DEFAULT_DAY = "friday";
	private static final String ARGUMENT_STARTING = "starting time";
	private static final String ARGUMENT_ENDING = "ending time";

	private static final String[] MONTHS = { "jan", "feb", "mar", "apr", "may",
			"jun", "jul", "aug", "sep", "oct", "nov", "dec" };
	private static final String[] DAYS = { "sunday", "monday", "tuesday",
			"wednesday", "thursday", "friday", "saturday" };

	/**
	 * Parses the string provided and returns the corresponding object
	 * 
	 * @param command
	 *            user input
	 * @return Command object for execution
	 * @throws Exception
	 *             parsing error message
	 */
	public Command parseCommand(String command) throws Exception {
		String[] commandSplit = command.split(" ", 2);
		
		String commandWord, arguments = null;
		commandWord = commandSplit[0];
		if(commandSplit.length >= 2){
			arguments = commandSplit[1];
		}
		
		Command commandObject;
		if (commandWord.equalsIgnoreCase(COMMAND_ADD)) {
			try {
				Task taskObj = new Task();
				extractTaskInformation(taskObj, arguments);
				commandObject = new Command(Command.Type.ADD, taskObj);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(
						WARNING_INSUFFICIENT_ARGUMENT, commandWord));
			}
		} else if (commandWord.equalsIgnoreCase(COMMAND_EDIT)) {
			try {
				String[] argumentSplit = arguments.split(" ", 2);
				String[] indexToDelete = { argumentSplit[0] };
				String taskInformation = argumentSplit[1];
				
				Task taskObj = new Task();
				extractTaskInformation(taskObj, taskInformation);
				commandObject = new Command(Command.Type.EDIT, indexToDelete,
						taskObj);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new Exception(String.format(
						WARNING_INSUFFICIENT_ARGUMENT, commandWord));
			}
		} else if (commandWord.equalsIgnoreCase(COMMAND_DELETE)) {
			if (commandSplit.length >= 2) {
				String[] indexToDelete = arguments.split(" ");

				commandObject = new Command(Command.Type.DELETE, indexToDelete);
			} else {
				throw new Exception(String.format(
						WARNING_INSUFFICIENT_ARGUMENT, commandWord));
			}
		} else if (commandWord.equalsIgnoreCase(COMMAND_EXIT)) {
			commandObject = new Command(Command.Type.EXIT);
		} else if (commandWord.equalsIgnoreCase(COMMAND_DISPLAY)) {
			System.out.println("?");
			commandObject = new Command(Command.Type.DISPLAY);
		} else if (commandWord.equalsIgnoreCase(COMMAND_UNDO)) {
			commandObject = new Command(Command.Type.UNDO);
		} else if (commandWord.equalsIgnoreCase(COMMAND_REDO)) {
			commandObject = new Command(Command.Type.REDO);
		} else if (commandWord.equalsIgnoreCase(COMMAND_SAVETO)) {
			String[] argumentArray = { arguments };
			commandObject = new Command(Command.Type.SAVETO, argumentArray);
		} else {
			commandObject = null;
		}
		return commandObject;
	}

	/*
	 * Extracts and returns 'name' segment of the command.
	 */
	private String extractTaskName(String arg) throws Exception {
		return arg.split("'")[1];
	}

	private String extractTaskNameWithoutCommand(String arg) {
		return arg.split(" ")[0];
	}

	/*
	 * Extracts 'date' segment of the command if present returns Calendar
	 * object. Extracts 'day' segment of the command if present and returns
	 * Calendar object - current supported parameters before day string are
	 * 'this' and 'next' Special argument: 'tomorrow' will set date to the next
	 * day from current date pre-condition: String must contain DATE_ARGUMENTS,
	 * date parameters are valid dates in format dd MMM yyyy OR String must
	 * contain day arg in lowercase only post-condition: returns parsed Calendar
	 * object if date is present, else return null. Exception if day is not
	 * spelt in full.
	 */
	private String extractDate(String arguments, Task taskObj) throws Exception {
		Calendar dateOne = new GregorianCalendar();
		Calendar dateTwo = new GregorianCalendar();
		if (hasKeyword(arguments, ARGUMENTS_END_DATE)) {
			return extractOneDateInput(arguments, dateOne, taskObj, ARGUMENT_ENDING);
		} else if (hasKeyword(arguments, ARGUMENT_EVENT)) {
			return extractEventDatesInput(arguments, dateOne, dateTwo, taskObj);
		} else if (hasKeyword(arguments, ARGUMENTS_END_DATE_SPECIAL)) {
			return extractSpecialDateInput(arguments, dateOne, taskObj,
					ARGUMENT_ENDING);
		} else {
			return arguments;
		}

	}

	// construct task when there is just one date in the input
	public String extractOneDateInput(String arg, Calendar date,
			Task taskObj, String timeArg) throws Exception {
		String keywordToSplitAt = getKeyword(arg, ARGUMENTS_END_DATE);
		String[] newArgs = arg.split(keywordToSplitAt);

		String[] dateArgs = newArgs[1].split(" ");

		int day = Integer.parseInt(dateArgs[0]);

		int month = Arrays.asList(MONTHS).indexOf(dateArgs[1]);
		if (month == -1) {
			throw new Exception(WARNING_INVALID_MONTH);
		}

		// year will be set to current year if not specified by user
		int year;
		try {
			year = Integer.parseInt(dateArgs[2]);
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			year = Calendar.getInstance().get(Calendar.YEAR);
		}

		date.set(year, month, day);
		if (timeArg.equals(ARGUMENT_ENDING)) {
			taskObj.setEndingTime(date);
		} else if (timeArg.equals(ARGUMENT_STARTING)) {
			taskObj.setStartingTime(date);
		} else {
			// return an exception
		}
		return newArgs[0];
	}
	
	private boolean extractTaskInformation(Task taskObject, String arguments) throws Exception{
		taskObject.setName(extractDate(arguments, taskObject));
		arguments = extractLocation(arguments, taskObject);
		return true;
	}

	// construct task when there are both starting time and endingtime
	public String extractEventDatesInput(String arg, Calendar dateOne,
			Calendar dateTwo, Task taskObj) throws Exception {
		String name = extractTaskNameWithoutCommand(arg);
		taskObj.setName(name);
		String[] newArgs = arg.split(ARGUMENT_TO);
		if (newArgs[0].indexOf(ARGUMENT_FROM) != -1) {// if there is a "start"
														// in the input

			String[] tempArgs = newArgs[0].split(ARGUMENT_FROM);
			arg = tempArgs[0];

			if (hasKeyword(tempArgs[1], ARGUMENTS_END_DATE_SPECIAL)) {
				extractSpecialDateInput(name + tempArgs[1], dateOne, taskObj,
						ARGUMENT_STARTING);
				extractOneDateInput(name + newArgs[1], dateOne, taskObj,
						ARGUMENT_ENDING);
				return name;
			} else {
				String[] fromArgs = tempArgs[1].split(" ");

				int day = Integer.parseInt(fromArgs[1]);
				int month = Arrays.asList(MONTHS).indexOf(fromArgs[2]);
				if (month == -1) {
					throw new Exception(WARNING_INVALID_MONTH);
				}

				// year will be set to current year if not specified by user
				int year;
				try {
					year = Integer.parseInt(fromArgs[3]);
				} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
					year = Calendar.getInstance().get(Calendar.YEAR);
				}

				dateTwo.set(year, month, day);
				taskObj.setStartingTime(dateTwo);

				String[] dateArgs = newArgs[1].split(" ");
				day = Integer.parseInt(dateArgs[1]);

				month = Arrays.asList(MONTHS).indexOf(dateArgs[2]);
				if (month == -1) {
					throw new Exception(WARNING_INVALID_MONTH);
				}

				// year will be set to current year if not specified by user

				try {
					year = Integer.parseInt(dateArgs[3]);
				} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
					year = Calendar.getInstance().get(Calendar.YEAR);
				}

				dateOne.set(year, month, day);
				if (dateOne.before(dateTwo)) {
					taskObj.setEndingTime(dateTwo);
					taskObj.setStartingTime(dateOne);
				} else {
					taskObj.setEndingTime(dateOne);
					taskObj.setStartingTime(dateTwo);
				}

				return arg;
			}
		} else {
			throw new Exception(WARNING_INVALID_DAY);
		}

	}

	// construct task when there is a special date argument
	public String extractSpecialDateInput(String arg, Calendar date,
			Task taskObj, String timeArg) throws Exception {
		String keywordToSplitAt = getKeyword(arg, ARGUMENTS_END_DATE_SPECIAL);
		String[] newArgs = arg.split(keywordToSplitAt);

		date = new GregorianCalendar();
		int setDate, today, todayDate, offset;

		today = date.get(Calendar.DAY_OF_WEEK);
		todayDate = date.get(Calendar.DATE);

		if (keywordToSplitAt.equalsIgnoreCase(ARGUMENTS_END_DATE_SPECIAL[0])) {
			if (!hasKeyword(newArgs[1], DAYS)) {
				throw new Exception(WARNING_INVALID_DAY);
			}
			offset = dayOfTheWeek(getKeyword(newArgs[1], DAYS)) - today;
			if (offset < 0) {
				offset += DAYS.length;
			}
			setDate = todayDate + offset;
		} else if (keywordToSplitAt
				.equalsIgnoreCase(ARGUMENTS_END_DATE_SPECIAL[1])) {
			if (!hasKeyword(newArgs[1], DAYS)) {
				throw new Exception(WARNING_INVALID_DAY);
			}
			offset = dayOfTheWeek(getKeyword(newArgs[1], DAYS)) - today;
			if (offset < 0) {
				offset += DAYS.length;
			}
			setDate = todayDate + offset + DAYS.length;
		} else if (keywordToSplitAt
				.equalsIgnoreCase(ARGUMENTS_END_DATE_SPECIAL[2])) {
			setDate = todayDate + 1;
		} else if (keywordToSplitAt
				.equalsIgnoreCase(ARGUMENTS_END_DATE_SPECIAL[3])) {
			setDate = todayDate;
		} else {
			offset = dayOfTheWeek(DEFAULT_DAY) - today;
			if (offset < 0) {
				offset += DAYS.length;
			}
			setDate = todayDate + offset;
		}
		date.set(Calendar.DATE, setDate);
		if (timeArg.equals(ARGUMENT_ENDING)) {
			taskObj.setEndingTime(date);
		} else if (timeArg.equals(ARGUMENT_STARTING)) {
			taskObj.setStartingTime(date);
		} else {
			// return an exception
		}
		return newArgs[0];
	}

	/*
	 * Extracts 'loc' segment pre-condition: String must contain
	 * LOCATION_ARGUMENTS post-condition: returns extracted string if
	 * LOCATION_ARGUMENTS is present, else return original string if date is not
	 * present
	 */
	private String extractLocation(String arg, Task taskObj) throws Exception {
		String[] newArgs;
		String returnArg = "";
		
		if (arg.contains(ARGUMENT_LOC)) {
			newArgs = arg.split(ARGUMENT_LOC);
			taskObj.setLocation(newArgs[1]);
			returnArg = newArgs[0];
		} else {
			returnArg = arg;
		}

		return returnArg;
	}

	private String extractPeriodic(String arg, Task taskObj) {
		if (arg.contains(ARGUMENTS_PERIODIC)) {
			String argPeriodic = arg.split(ARGUMENTS_PERIODIC)[1];
			for (int i = 0; i < DAYS.length; i++) {
				if (argPeriodic.indexOf(DAYS[i]) == 0) {
					return DAYS[i];
				}
			}
		}
		return null;
	}

	private boolean hasKeyword(String str, String[] keywords) {
		for (int i = 0; i < keywords.length; i++) {
			if (str.contains(keywords[i])) {
				return true;
			}
		}
		return false;
	}

	private String getKeyword(String str, String[] keywords) {
		for (int i = 0; i < keywords.length; i++) {
			if (str.contains(keywords[i])) {
				return keywords[i];
			}
		}
		return null; // should never happen
	}

	private int dayOfTheWeek(String dayString) {
		return Arrays.asList(DAYS).indexOf(dayString) + 1;
	}

	public static Parser getInstance() {
		if (parserInstance == null) {
			parserInstance = new Parser();
		}
		return parserInstance;
	}
}