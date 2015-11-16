package parser;

import global.Command;
import global.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class contains the parser class which the logic passes user commands to,
 * to produce a command for execution.
 * 
 * It primarily provides 2 APIs to the Logic: parseCommand and addAlias.
 * 
 * If there are errors while parsing the user command using the parseCommand API,
 * it will throw an Exception with the appropriate status message.
 */
public class Parser {
    
    private static final int DEFAULT_AM_PM_FOR_END_TIME = 1;
    private static final int DEFAULT_MINUTE_FOR_END_TIME = 59;
    private static final int DEFAULT_HOUR_FOR_END_TIME = 11;

    private static final int DEFAULT_AM_PM_FOR_START_TIME = 0;
    private static final int DEFAULT_MINUTE_FOR_START_TIME = 0;
    private static final int DEFAULT_HOUR_FOR_START_TIME = 9;
    
    //status messages
    static final String ERROR_MISSING_INTERVAL = "Error: Missing repeat interval.";
    static final String ERROR_MISSING_REPEATS = "Error: Missing number of repeats.";
    static final String ERROR_INVALID_DATE_ARGUMENTS = "Error: Invalid arguments for date.";
    static final String ERROR_INVALID_PERIODIC_INSTANCES = "Error: Invalid periodic instances.";
    static final String ERROR_INVALID_PERIODIC_INTERVAL_VALUE = "Error: Invalid periodic interval value.";
    static final String ERROR_INVALID_MONTH_SPECIFIED = "Error: Invalid month specified!";
    static final String ERROR_INVALID_DATE_SPECIFIED = "Error: Invalid date specified!";
    static final String ERROR_INVALID_TIME = "Error: Invalid time!";
    static final String ERROR_INVALID_TIME_FORMAT = "Error: Invalid time format specified!";
    static final String ERROR_INVALID_PERIODIC_INTERVAL = "Error: Invalid periodic interval specified.";
    static final String ERROR_MISSING_START_TIME = "Error: An end time has been entered without start time!";
    static final String ERROR_MISSING_END_TIME = "Error: A start time has been entered without end time!";
    static final String ERROR_MISSING_START_OR_END_TIME = "Error: Start time or end time missing.";
    static final String ERROR_INVALID_DAY_SPECIFIED = "Error: Invalid day specified!";
    static final String ERROR_INVALID_NUMBER_OF_ARGUMENTS = "Error: Invalid number of arguments.";
    static final String ERROR_INVALID_COMMAND_SPECIFIED = "Error: Invalid command specified!";
    static final String ERROR_EMPTY_COMMAND_STRING = "Error: Command string is empty.";
    static final String ERROR_EMPTY_TASK_NAME = "Error: Task name is empty.";

    
    static final String WHITE_SPACE_REGEX = "\\s+";
    
    Logger logger = Logger.getGlobal();

    static final String[] COMMAND_ADD = { "add" };
    static final String[] COMMAND_EDIT = { "edit", "change" };
    static final String[] COMMAND_DELETE = { "delete", "del" };
    static final String[] COMMAND_UNDO = { "undo" };
    static final String[] COMMAND_REDO = { "redo" };
    static final String[] COMMAND_MARK = { "mark" };
    static final String[] COMMAND_UNMARK = { "unmark" };
    static final String[] COMMAND_EXIT = { "exit" };
    static final String[] COMMAND_DISPLAY = { "display" , "clear" };
    static final String[] COMMAND_SEARCH = { "search" };
    static final String[] COMMAND_SAVETO = { "saveto" };
    static final String[] COMMAND_HELP = { "help" };
    static final String[] COMMAND_ALIAS = { "alias" };
    static final String[][] COMMAND_LISTS = {
        COMMAND_ADD, COMMAND_EDIT, COMMAND_DELETE,
        COMMAND_UNDO, COMMAND_REDO, COMMAND_MARK,
        COMMAND_UNMARK, COMMAND_EXIT, COMMAND_DISPLAY,
        COMMAND_SEARCH, COMMAND_SAVETO, COMMAND_HELP,
        COMMAND_ALIAS
    };
    
    ArrayList<String> addKeywords = new ArrayList<String>();
    ArrayList<String> editKeywords = new ArrayList<String>();
    ArrayList<String> deleteKeywords = new ArrayList<String>();
    ArrayList<String> undoKeywords = new ArrayList<String>();
    ArrayList<String> redoKeywords = new ArrayList<String>();
    ArrayList<String> markKeywords = new ArrayList<String>();
    ArrayList<String> unmarkKeywords = new ArrayList<String>();
    ArrayList<String> exitKeywords = new ArrayList<String>();
    ArrayList<String> displayKeywords = new ArrayList<String>();
    ArrayList<String> searchKeywords = new ArrayList<String>();
    ArrayList<String> savetoKeywords = new ArrayList<String>();
    ArrayList<String> helpKeywords = new ArrayList<String>();
    ArrayList<String> aliasKeywords = new ArrayList<String>();
    ArrayList<ArrayList<String>> keywordLists;
    

    static final String[] DATE_SPECIAL = { "this", "next", "today", "tomorrow" };
    static final String[] MONTHS = { "jan", "feb", "mar", "apr", "may",
            "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
    static final String[] DAYS = { "sunday" , "monday" , "tuesday" , "wednesday" , "thursday" ,"friday", "saturday" };
    static final String[] DAYS_COMPACT = { "sun" , "mon" , "tue" , "wed" , "thu" , "fri", "sat" };
    

    static final String[] PERIODIC = { "days" , "day" , "week" , "weeks" , "month" , "months" , "year" , "years" };

    enum FieldType {
        START_EVENT, END_EVENT, DEADLINE, LOCATION, INTERVAL_PERIODIC, INSTANCES_PERIODIC
    }

    static final String[] LOCATION = { "loc", "at" };
    static final String[] DEADLINE = { "by" , "before" , "on" };
    static final String[] TIME = { "H", "AM", "PM" , "am" , "pm" };
    static final String TIME_SEPARATOR = ".";
    static final String[] START_EVENT = { "start" , "from" , "starts" , "starting" };
    static final String[] END_EVENT = { "end" , "to" , "ends" , "until" , "ending" };
    static final String[] INTERVAL_PERIODIC = { "every" , "repeats" };
    static final String[] INSTANCES_PERIODIC = { "for" };
    
    class KeywordMarker implements Comparable<KeywordMarker> {
        int index;
        FieldType typeOfField;

        int getIndex() {
            return index;
        }

        void setIndex(int i) {
            index = i;
        }

        FieldType getFieldType() {
            return typeOfField;
        }

        void setFieldType(FieldType fieldType) {
            typeOfField = fieldType;
        }

        @Override
        public int compareTo(KeywordMarker o) {
            if (this.index < o.getIndex()) {
                return -1;
            } else if (this.index > o.getIndex()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    
    public Parser(){
        initialiseKeywordLists();
        addAllDefaultKeywords();
    }
    
    boolean initialiseKeywordLists(){
        keywordLists = new ArrayList<ArrayList<String>>();
        keywordLists.add(addKeywords);
        keywordLists.add(editKeywords);
        keywordLists.add(deleteKeywords);
        keywordLists.add(undoKeywords);
        keywordLists.add(redoKeywords);
        keywordLists.add(markKeywords);
        keywordLists.add(unmarkKeywords);
        keywordLists.add(exitKeywords);
        keywordLists.add(displayKeywords);
        keywordLists.add(searchKeywords);
        keywordLists.add(savetoKeywords);
        keywordLists.add(helpKeywords);
        keywordLists.add(aliasKeywords);
        return true;
    }
    
    boolean addAllDefaultKeywords(){
        for(int i = 0; i < COMMAND_LISTS.length && i < keywordLists.size(); i++){
            addDefaultKeywordsToList(COMMAND_LISTS[i], keywordLists.get(i));
        }
        return true;
    }
    
    boolean addDefaultKeywordsToList(String[] listOfKeywords, ArrayList<String> listInMemory){
        for (int i = 0; i < listOfKeywords.length; i++) {
            listInMemory.add(listOfKeywords[i]);
        }
        return true;
    }
    
    /**
     * Parses the command string based on keyword
     * 
     * @param command
     * @return commandObject to be executed, or null if invalid
     */
    public Command parseCommand(String commandString) throws Exception {
        commandString = commandString.trim();
        Command.Type commandType = identifyType(commandString);
        commandString = removeFirstWord(commandString);
        Command commandObject = new Command(commandType);
        Task taskObject = new Task();
        String[] argumentArray;
        
        switch (commandType) {
            case ADD :
                extractTaskInformation(commandString, taskObject);
                commandObject.addTask(taskObject);
                break;
            case EDIT :
                if (commandString.split(WHITE_SPACE_REGEX).length == 1) {// if insufficient arguments .eg "edit"
                    throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
                } else {
                    argumentArray = getParameterOneAsArray(commandString);
                    commandObject.setArguments(argumentArray);
                    commandString = removeFirstWord(commandString);

                    extractFieldInformation(commandString, taskObject);
                    commandObject.addTask(taskObject);
                }
                break;
            case DELETE :
                argumentArray = getMultipleIndexes(commandString);
                commandObject.setArguments(argumentArray);
                break;
            case SAVETO :
                argumentArray = getSaveToArgument(commandString);
                commandObject.setArguments(argumentArray);
                break;
            case MARK :
                argumentArray = getMultipleIndexes(commandString);
                commandObject.setArguments(argumentArray);
                break;
            case DISPLAY :
                argumentArray = getSaveToArgument(commandString);
                commandObject.setArguments(argumentArray);
            case UNMARK :
                argumentArray = getMultipleIndexes(commandString);
                commandObject.setArguments(argumentArray);
                break;
            case SEARCH :
                extractFieldInformation(commandString, taskObject);
                commandObject.addTask(taskObject);
                break;
            case ALIAS :
                argumentArray = getAliasArgument(commandString);
                commandObject.setArguments(argumentArray);
                break;
            default:
        }
        return commandObject;
    }
    
    String[] getAliasArgument(String commandString) {
        return commandString.split(WHITE_SPACE_REGEX, 2);
    }
    
    String[] getSaveToArgument(String commandString) {
        return new String[] { commandString };
    }
    
    String[] getParameterOneAsArray(String commandString) {
        String indexString = commandString.split(WHITE_SPACE_REGEX, 2)[0];
        return new String[] { indexString };
    }
    
    String getParameterTwo(String commandString) {
        return commandString.split(WHITE_SPACE_REGEX)[1];
    }

    String[] getMultipleIndexes(String commandString) {
        if(commandString.length() > 0){
            String[] indexArray = commandString.split(WHITE_SPACE_REGEX);
            return indexArray;
        } else {
            return new String[0];
        }
    }

    String removeFirstWord(String commandString) {
        String[] splitCommand = commandString.split(WHITE_SPACE_REGEX, 2);
        assert (splitCommand.length >= 1);
        if (splitCommand.length == 1) {
            return "";
        } else {
            return splitCommand[1];
        }
    }
    
    /**
     * Attempts to extract field information for edit and search commands, where
     * task name is not compulsory
     * @param commandString
     * @param taskObject
     * @return
     * @throws Exception
     */
    boolean extractFieldInformation(String commandString, Task taskObject)
            throws Exception {
        logger.fine("extractFieldInformation: getting keyword markers");
        ArrayList<KeywordMarker> keywordMarkers = getArrayOfKeywordMarkers(commandString);
    
        Collections.sort(keywordMarkers);
        
        logger.fine("extractedFieldInformation: extracting data from string");
        extractName(commandString, keywordMarkers, taskObject, false);
        extractDate(commandString, keywordMarkers, taskObject, false);
        extractLocation(commandString, keywordMarkers, taskObject);
        return true;
    }

    Command.Type identifyType(String commandString) throws Exception {
        if (commandString.length() == 0) {
            logger.info("identifyType: Command string is empty!");
            throw new Exception(ERROR_EMPTY_COMMAND_STRING);
        } else {
            String firstWord = commandString.split(WHITE_SPACE_REGEX, 2)[0];
            if (isCommandKeyword(firstWord, addKeywords)) {
                return Command.Type.ADD;
            } else if (isCommandKeyword(firstWord, editKeywords)) {
                return Command.Type.EDIT;
            } else if (isCommandKeyword(firstWord, deleteKeywords)) {
                return Command.Type.DELETE;
            } else if (isCommandKeyword(firstWord, undoKeywords)) {
                return Command.Type.UNDO;
            } else if (isCommandKeyword(firstWord, redoKeywords)) {
                return Command.Type.REDO;
            } else if (isCommandKeyword(firstWord, savetoKeywords)) {
                return Command.Type.SAVETO;
            } else if (isCommandKeyword(firstWord, displayKeywords)) {
                return Command.Type.DISPLAY;
            } else if (isCommandKeyword(firstWord, exitKeywords)) {
                return Command.Type.EXIT;
            } else if (isCommandKeyword(firstWord, markKeywords)) {
                return Command.Type.MARK;
            } else if (isCommandKeyword(firstWord, unmarkKeywords)) {
                return Command.Type.UNMARK;
            } else if (isCommandKeyword(firstWord, searchKeywords)) {
                return Command.Type.SEARCH;
            } else if (isCommandKeyword(firstWord, helpKeywords)) {
                return Command.Type.HELP;
            } else if (isCommandKeyword(firstWord, aliasKeywords)) {
                return Command.Type.ALIAS;
            } else {
                logger.info("identifyType: invalid command");
                throw new Exception(ERROR_INVALID_COMMAND_SPECIFIED);
            }
        }
    }

    boolean isCommandKeyword(String firstWordInCommandString,
            String[] keywords) {
        for (int i = 0; i < keywords.length; i++) {
            if (firstWordInCommandString.equalsIgnoreCase(keywords[i])) {
                return true;
            }
        }
        return false;
    }
    
    boolean isCommandKeyword(String firstWordInCommandString,
            ArrayList<String> keywords) {
        for (int i = 0; i < keywords.size(); i++) {
            if (firstWordInCommandString.equalsIgnoreCase(keywords.get(i))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Extracts data from the command string and puts them into the relevant field in the 
     * task object
     * @param commandString
     * @param taskObject
     * @return
     * @throws Exception
     */
    boolean extractTaskInformation(String commandString, Task taskObject)
            throws Exception {
        logger.fine("extractTaskInformation: getting keyword markers");
        ArrayList<KeywordMarker> keywordMarkers = getArrayOfKeywordMarkers(commandString);
    
        Collections.sort(keywordMarkers);
        
        logger.fine("extractedTaskInformation: extracting data from string");
        extractName(commandString, keywordMarkers, taskObject, true);
        boolean hasDate = extractDate(commandString, keywordMarkers, taskObject, true);
        extractLocation(commandString, keywordMarkers, taskObject);
        extractPeriodic(commandString, keywordMarkers, taskObject, hasDate); // valid only if date is specified
        return true;
    }
    
    boolean extractPeriodic(String commandString, ArrayList<KeywordMarker> keywordMarkers, Task taskObject, boolean hasDate) throws Exception{
        String[] periodicIntervalArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.INTERVAL_PERIODIC);
        String[] periodicInstancesArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.INSTANCES_PERIODIC);
        if (hasDate && periodicIntervalArguments != null
                && periodicInstancesArguments != null) {
            if (periodicIntervalArguments.length == 2) {
                logger.finer("extractPeriodic: interval argument length is 2.");
                int periodicIntervalValue;
                try {
                    periodicIntervalValue = Integer
                            .parseInt(periodicIntervalArguments[0]);
                } catch (NumberFormatException e) {
                    throw new Exception(ERROR_INVALID_PERIODIC_INTERVAL_VALUE);
                }

                String periodicIntervalUnit = periodicIntervalArguments[1];
                if (hasKeyword(periodicIntervalUnit, PERIODIC)) {
                    taskObject.setPeriodicInterval(periodicIntervalValue + " "
                            + periodicIntervalUnit);
                } else {
                    logger.info("extractPeriodic: invalid period interval");
                    throw new Exception(ERROR_INVALID_PERIODIC_INTERVAL);
                }
            } else {
                logger.info("extractPeriodic: invalid number of interval arguments - "
                        + periodicIntervalArguments.length);
                throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
            }

            if (periodicInstancesArguments.length >= 1) {
                logger.finer("extractPeriodic: periodic argument length is 1.");
                int periodicInstancesValue;
                try {
                    periodicInstancesValue = Integer
                            .parseInt(periodicInstancesArguments[0]);
                } catch (NumberFormatException e) {
                    throw new Exception(ERROR_INVALID_PERIODIC_INSTANCES);
                }
                taskObject.setPeriodicRepeats(periodicInstancesArguments[0]);
            } else {
                logger.info("extractPeriodic: invalid number of instance arguments - "
                        + periodicInstancesArguments.length);
                throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
            }

            return true;
        }
        return false;
    }
    
    /**
     * Extracts the location arguments as an array, joins
     * them up into a location string, and set it as the location
     * @param commandString
     * @param keywordMarkers
     * @param taskObject
     * @return
     * @throws Exception
     */
    boolean extractLocation(String commandString, ArrayList<KeywordMarker> keywordMarkers, Task taskObject) throws Exception{
        String[] locationArguments = getArgumentsForField(commandString, keywordMarkers, FieldType.LOCATION);
        String location = "";
        if (locationArguments != null) {
            for (int i = 0; i < locationArguments.length; i++) {
                location += locationArguments[i] + " ";
            }
            location = location.trim();
            taskObject.setLocation(location);
            logger.finer("extractLocation: location added");
            return true;
        }
        return false;
    }

    boolean extractDate(String commandString,
            ArrayList<KeywordMarker> keywordMarkers, Task taskObject, boolean isNewTask) throws Exception {
        // check deadline
        logger.fine("extractDate: getting date arguments");
        String[] deadlineArguments = getArgumentsForField(commandString,
                keywordMarkers, FieldType.DEADLINE);

        if (deadlineArguments != null) {
            for (int i = 0; i < deadlineArguments.length; i++) {
                logger.finer("extractDate: deadlineArguments[" + i
                        + "] contains " + deadlineArguments[i]);
            }
        }
        
        // check start/end event
        String[] startEventArguments = getArgumentsForField(commandString,
                keywordMarkers, FieldType.START_EVENT);
        String[] endEventArguments = getArgumentsForField(commandString,
                keywordMarkers, FieldType.END_EVENT);
        
        if (startEventArguments != null) {
            for (int i = 0; i < startEventArguments.length; i++) {
                logger.finer("extractDate: startEventArguments[" + i
                        + "] contains " + startEventArguments[i]);
            }
        }
        
        logger.fine("extractDate: got date arguments. attempting to parse dates");
        if (deadlineArguments != null) {
            Calendar argumentDate = parseTime(deadlineArguments, false);
            taskObject.setEndingTime(argumentDate);
            logger.fine("extractDate: deadline set");
            return true;
        } else if (startEventArguments != null && endEventArguments != null) {
            Calendar argumentStartDate = parseTime(startEventArguments, true);
            Calendar argumentEndDate = parseTime(endEventArguments, false);

            if (argumentStartDate.before(argumentEndDate)) {
                taskObject.setStartingTime(argumentStartDate);
                taskObject.setEndingTime(argumentEndDate);
            } else {
                taskObject.setStartingTime(argumentEndDate);
                taskObject.setEndingTime(argumentStartDate);
            }
            return true;
        } else if ((startEventArguments != null || endEventArguments != null)
                && isNewTask) {
            throw new Exception(ERROR_MISSING_START_OR_END_TIME);
        } else if (startEventArguments != null) {
            Calendar argumentStartDate = parseTime(startEventArguments, true);
            taskObject.setStartingTime(argumentStartDate);
            return true;
        } else if (endEventArguments != null) {
            Calendar argumentEndDate = parseTime(endEventArguments, false);
            taskObject.setEndingTime(argumentEndDate);
            return true;
        } else {
            return false;
        }
    }

    // if time not specified, it will be parsed to 11:59 PM
    // TIME keyword in commandString must be capitalized
    Calendar parseTime(String[] dateArgumentsTemp, boolean isStart) throws Exception {
        logger.fine("parseTime: parsing time");
        int date, month, year, hour, minute, isAMorPM;
        if (isStart) {
            hour = DEFAULT_HOUR_FOR_START_TIME;
            minute = DEFAULT_MINUTE_FOR_START_TIME;
            isAMorPM = DEFAULT_AM_PM_FOR_START_TIME;
        } else {
            hour = DEFAULT_HOUR_FOR_END_TIME;
            minute = DEFAULT_MINUTE_FOR_END_TIME;
            isAMorPM = DEFAULT_AM_PM_FOR_END_TIME;
        }
        
        Integer hourOfDay = null;
        Calendar helperDate;
        
        // start of parsing time
        // time argument in dateArguments is removed from array
        // new array is created since array length cannot be modified
        logger.fine("parsing time component");
        String[] dateArguments;
        String timeArgument = dateArgumentsTemp[dateArgumentsTemp.length - 1];
		if (isTimeKeyword(timeArgument, TIME)) {
		    for (int n = 0; n < TIME.length; n++) {
				if (timeArgument.endsWith(TIME[n])) {
					try {
						timeArgument = timeArgument.replace(TIME[n], "");
						if (n == 0) { // h: 24 hour time format
							hourOfDay = Integer.parseInt(timeArgument.substring(0, 2));
							minute = Integer.parseInt(timeArgument.substring(2));
						} else { // am/pm: 12 hour time format
							isAMorPM = (n == 1 || n == 3) ? 0 : 1;
							if (timeArgument.contains(TIME_SEPARATOR)) { // check if
																		// minutes
																		// is
																		// specified
								String[] tempTimeSplit = timeArgument.split("\\" + TIME_SEPARATOR);
								minute = Integer.parseInt(tempTimeSplit[1]);
								hour = Integer.parseInt(tempTimeSplit[0]);
							} else {
								// if no minutes is specified, then set to 0
								// (e.g. '8PM' = 8:00PM)
								minute = 0;
								hour = Integer.parseInt(timeArgument);
							}
							// for 12 hour time format, 12am/pm means hour = 0
							hour = hour == 12 ? 0 : hour;
						}
					} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
						throw new Exception(ERROR_INVALID_TIME_FORMAT);
					} catch (Exception e) {
						throw new Exception(ERROR_INVALID_TIME_FORMAT);
					}
				}
			}
		    
		    dateArguments = new String[dateArgumentsTemp.length - 1];
            for (int i = 0; i < dateArgumentsTemp.length - 1; i++) {
                dateArguments[i] = dateArgumentsTemp[i];
            }
        } else {
            dateArguments = dateArgumentsTemp;
        }
        
        // start parsing of date
        logger.fine("parsing date component");
        if (dateArguments.length == 0) {
            throw new Exception(ERROR_INVALID_NUMBER_OF_ARGUMENTS);
        } else if(!hasKeyword(dateArguments, DATE_SPECIAL)
                && dateArguments.length != 1) {
            // 2+ words without special keywords
            // attempt to parse dates in the format 'date month year'
            // month can be either in the number range 1 - 12 or 3-char format
            // date and month positions can be swapped, if the month is in 3-char format
            logger.info("Date format: ii ii ii");
            try {
                date = extractDate(dateArguments[0]);
                month = extractMonth(dateArguments[1]);
            } catch (NumberFormatException e) {
                try {
                    date = extractDate(dateArguments[1]);
                    month = extractMonth(dateArguments[0]);
                } catch (NumberFormatException e2) {
                    throw new Exception(ERROR_INVALID_DATE_ARGUMENTS);
                }
            }
            
            if (dateArguments.length == 3) {
                year = Integer.parseInt(dateArguments[2]);
                if (year < 100) { // is 2 digits, assume shortform for year
                    int curYear = Calendar.getInstance().get(Calendar.YEAR);
                    year = year + (curYear - curYear % 100);
                }
            } else {
                year = Calendar.getInstance().get(Calendar.YEAR);
            }

            helperDate = new GregorianCalendar();
            helperDate.clear();
            helperDate.set(year, month, date);
        } else if (dateArguments.length == 2) {
            // attempt to parse dates in the format 'this/next <day>'
            logger.info("Date format: 'this/next <day>'");
            String firstWord = dateArguments[0];
            String secondWord = dateArguments[1];
            
            boolean hasDaysKeyword = hasKeyword(secondWord, DAYS);
            boolean hasDaysCompactKeyword = hasKeyword(secondWord, DAYS_COMPACT);
            if (hasDaysKeyword || hasDaysCompactKeyword) {
                int dayIndex;
                if (hasDaysKeyword) {
                    dayIndex = getIndexOfList(secondWord, Arrays.asList(DAYS)) + 1;
                } else {
                    dayIndex = getIndexOfList(secondWord,
                            Arrays.asList(DAYS_COMPACT)) + 1;
                }
                assert (firstWord.equalsIgnoreCase(DATE_SPECIAL[0]) || firstWord
                        .equalsIgnoreCase(DATE_SPECIAL[1]));
                if (firstWord.equalsIgnoreCase(DATE_SPECIAL[0])) {// this
                    date = getNearestDate(dayIndex);
                } else {// next
                    date = getNearestDate(dayIndex) + DAYS.length;
                }
                logger.finer("parseDate: this/next day determined to be "
                        + date);
            } else {
                logger.info("parseDate: invalid day");
                throw new Exception(ERROR_INVALID_DAY_SPECIFIED);
            }

            month = Calendar.getInstance().get(Calendar.MONTH);
            year = Calendar.getInstance().get(Calendar.YEAR);

            helperDate = new GregorianCalendar();
            helperDate.clear();
            helperDate.set(year, month, date);
        } else if (hasKeyword(dateArguments, DATE_SPECIAL)
                && dateArguments.length == 1) {
            // attempt to parse 'today'/'tomorrow'
            logger.info("Date format: 'today/tomorrow'");
            if (dateArguments[0].equalsIgnoreCase(DATE_SPECIAL[2])) {
                helperDate = new GregorianCalendar();
            } else {
                helperDate = new GregorianCalendar();
                helperDate.add(Calendar.DATE, 1);
            }
        } else if ((hasKeyword(dateArguments, DAYS) || hasKeyword(dateArguments, DAYS_COMPACT)) && dateArguments.length == 1) {
            // attempt to parse dates in the format <day> e.g. 'mon' or 'monday'
            logger.info("Date format: '<day>'");
            int dayIndex;
            if (hasKeyword(dateArguments, DAYS)) {
                dayIndex = getIndexOfList(dateArguments[0], Arrays.asList(DAYS)) + 1;
            } else {
                dayIndex = getIndexOfList(dateArguments[0],
                        Arrays.asList(DAYS_COMPACT)) + 1;
            }
            date = getNearestDate(dayIndex);
            
            month = Calendar.getInstance().get(Calendar.MONTH);
            year = Calendar.getInstance().get(Calendar.YEAR);

            helperDate = new GregorianCalendar();
            helperDate.clear();
            helperDate.set(year, month, date);
        } else if (dateArguments.length == 1) {
            // attempt to parse dates in the form 'ddmm<yy>' or 'dd/mm</yy>' 
            logger.info("Date format: 'ddmm<yy>' or 'dd/mm</yy>'");
            try {
                int enteredDate = Integer.parseInt(dateArguments[0]);
                if (enteredDate <  100 || enteredDate >= 320000) {
                    throw new Exception(ERROR_INVALID_DATE_ARGUMENTS);
                } else {
                    if (enteredDate < 10000) {
                        date = enteredDate/100;
                        month = enteredDate % 100 - 1;
                        year = Calendar.getInstance().get(Calendar.YEAR);
                        
                        helperDate = new GregorianCalendar();
                        helperDate.clear();
                        helperDate.set(year, month, date);
                    } else {
                        date = enteredDate/10000;
                        month = (enteredDate % 10000) / 100 - 1;
                        year = enteredDate % 100;
                        
                        helperDate = new GregorianCalendar();
                        helperDate.clear();
                        helperDate.set(year, month, date);
                    }
                }
            } catch (NumberFormatException e) {
                dateArguments = dateArguments[0].split("/");
                if (dateArguments.length >= 2 && dateArguments.length <= 3) {
                    date = extractDate(dateArguments[0]);
                    month = extractMonth(dateArguments[1]);
                    
                    try {
                        if (dateArguments.length == 3) {
                            year = Integer.parseInt(dateArguments[2]);
                        } else {
                            year = Calendar.getInstance().get(Calendar.YEAR);
                        }
                    } catch (NumberFormatException e2) {
                        throw new Exception(ERROR_INVALID_DATE_ARGUMENTS);
                    }
                    
                    helperDate = new GregorianCalendar();
                    helperDate.clear();
                    helperDate.set(year, month, date);
                } else {
                    throw new Exception(ERROR_INVALID_DATE_ARGUMENTS);
                }
            }
        } else {
            logger.info("parseDate: unknown date arguments");
            throw new Exception(ERROR_INVALID_DATE_ARGUMENTS);
        }

        if (hourOfDay == null) {
            if (hour > 12 || hour < 0 || minute > 59) {
                throw new Exception(ERROR_INVALID_TIME);
            }
            helperDate.set(Calendar.HOUR, hour);
            helperDate.set(Calendar.AM_PM, isAMorPM);
        } else {
            if (hourOfDay > 23 || hour < 0 || minute > 59) {
                throw new Exception(ERROR_INVALID_TIME);
            }
            helperDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        }
        helperDate.set(Calendar.MINUTE, minute);
        return helperDate;
    }
    
    private int extractDate(String dateArgument) throws Exception {
        int date = Integer.parseInt(dateArgument);
        if (date < 0 || date > 31){
            throw new Exception(ERROR_INVALID_DATE_ARGUMENTS);
        }
        return date;
    }

    private int extractMonth(String dateArgument)
            throws Exception {
        boolean integerMonth;
        int monthOne = getIndexOfList(dateArgument, Arrays.asList(MONTHS));
        
        // attempt to check if month is in integer from 1-12
        int monthTwo = -1;
        try {
            monthTwo = Integer.parseInt(dateArgument) - 1;
            integerMonth = (monthTwo >= 0 && monthTwo <= 11);
        } catch (NumberFormatException e) {
            integerMonth = false;
        }

        if (monthOne == -1 && integerMonth == false) {
            throw new Exception(ERROR_INVALID_MONTH_SPECIFIED); // not in 3-char word or int
        } else if (monthTwo != -1) {
            return monthTwo;
        } else {
            return monthOne;
        }
    }
    
    /**
     * Functions similarly to <List>.indexOf(<String>), but is not case-sensitive
     * 
     * @param word
     * @param listOfWords
     * @return index of word in list if found, else -1
     */
    private int getIndexOfList(String word, List<String> listOfWords) {
        for (int i = 0; i < listOfWords.size(); i++) {
            if (listOfWords.get(i).equalsIgnoreCase(word)) {
                return i;
            }
        }
        return -1;
    }
    

    /**
     * @param givenDayIndex day of the week sunday to saturday -> 1 to 7 
     * @return date of the nearest day
     */
    int getNearestDate(int givenDayIndex) {
        Calendar dateHelper = Calendar.getInstance();
        int curDayIndex = dateHelper.get(Calendar.DAY_OF_WEEK);
        logger.fine("getNearestDate: given day is " + givenDayIndex);
        logger.fine("getNearestDate: today is " + curDayIndex);
        int todayDate = dateHelper.get(Calendar.DATE);

        int difference = ((givenDayIndex - curDayIndex) % DAYS.length + DAYS.length)
                % DAYS.length;
        logger.fine("getNearestDate: difference is " + difference);
        int newDate = todayDate + difference;
        return newDate;
    }
    
    boolean hasKeyword(String word, String[] keywords) {
        for (int i = 0; i < keywords.length; i++) {
            if (word.equalsIgnoreCase(keywords[i])) {
                return true;
            }
        }
        return false;
    }
    
    boolean hasKeyword(String[] words, String[] keywords) {
        for (int i = 0; i < words.length; i++) {
            if (hasKeyword(words[i], keywords)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Method created to search for TIME keywords. Can't use hasKeyword since 
     * the keyword is concatenated with the time itself, e.g. '6pm' instead of '6 pm'
     */
    boolean isTimeKeyword(String word, String[] keywords) {
        for (int n = 0; n < keywords.length; n++) {
            if (word.contains(keywords[n])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Method to obtain arguments after a keyword and before the
     * next keyword
     * @param commandString
     * @param keywordMarkers
     * @param typeOfField
     * @return String array of argument words
     */
    String[] getArgumentsForField(String commandString,
            ArrayList<KeywordMarker> keywordMarkers, FieldType typeOfField) {
        for (int i = 0; i < keywordMarkers.size(); i++) {
            KeywordMarker curKeywordMarker = keywordMarkers.get(i);
            if (curKeywordMarker.getFieldType() == typeOfField) {
                int indexSearch;
                if (i < keywordMarkers.size() - 1) {
                    // get index of the next field argument
                    indexSearch = keywordMarkers.get(i + 1).getIndex() - 1;
                    logger.finer("getArgumentsForField: search starting from " + indexSearch);
                    while (commandString.charAt(indexSearch) == ' ') {
                        indexSearch--;
                    }
                    while (commandString.charAt(indexSearch) != ' ') {
                        indexSearch--;
                    }
                    while (commandString.charAt(indexSearch) == ' ') {
                        indexSearch--;
                    }
                    indexSearch++;
                } else {
                    // until the end of the string
                    indexSearch = commandString.length();
                }
                
                int curIndex = curKeywordMarker.getIndex();
                logger.finer("getArgumentsForField: curIndex is " + curIndex);
                logger.finer("getArgumentsForField: indexSearch is " + indexSearch);
                String argumentString = commandString.substring(curIndex,
                        indexSearch).replaceAll("\\\\", "");
                String[] argumentWords = argumentString.split(WHITE_SPACE_REGEX);
                return argumentWords;
            }
        }
        return null;
    }

    /**
     * Extracts task name from a string
     * It is assumed that the start of the string is the task name
     * @param commandString
     * @param keywordMarkers
     * @param taskObject
     * @return
     * @throws Exception
     */
    boolean extractName(String commandString,
            ArrayList<KeywordMarker> keywordMarkers, Task taskObject, boolean isNewTask)
            throws Exception {
        logger.fine("extractName: extracting name");
        String taskName = null;
        if (commandString.length() == 0 && isNewTask) {
            logger.info("extractName: no task information");
            throw new Exception(ERROR_EMPTY_TASK_NAME);
        } else if (keywordMarkers.size() > 0) {
            logger.finer("extractName: markersize > 0");
            int searchIndex = keywordMarkers.get(0).getIndex() - 1;
            
            logger.finer("extractName: searchIndex starts from " + searchIndex);
            while (searchIndex >= 0 && commandString.charAt(searchIndex) == ' ') {
                searchIndex--;
            }
            
            logger.finer("extractName: reached next command word at " + searchIndex);
            while (searchIndex >= 0 && commandString.charAt(searchIndex) != ' ') {
                searchIndex--;
            }
            
            logger.finer("extractName: past next command word at " + searchIndex);
            if (searchIndex >= 0) {
                taskName = commandString.substring(0, searchIndex);
            } else {
                if (isNewTask) {
                    throw new Exception(ERROR_EMPTY_TASK_NAME);
                }
            }
        } else {
            taskName = commandString;
        }
        if (taskName != null) {
            taskName = taskName.replaceAll("\\\\", "");
            taskObject.setName(taskName);
        }
        return true;
    }

    /**
     * Attempts to mark the relevant fields of a task, and adds a marker
     * to mark the starting of each field's arguments
     * 
     * @param commandString
     * @return
     * @throws Exception
     */
    ArrayList<KeywordMarker> getArrayOfKeywordMarkers (
            String commandString) throws Exception {
        ArrayList<KeywordMarker> keywordMarkerList = new ArrayList<KeywordMarker>();
        getLocationField(keywordMarkerList, commandString);
        getDateField(keywordMarkerList, commandString);
        getPeriodicField(keywordMarkerList, commandString);
        
        return keywordMarkerList;
    }
    
    /**
     * Marks indexes at which the periodic arguments are found
     * @param curMarkerList
     * @param commandString
     * @return
     * @throws Exception
     */
    boolean getPeriodicField(ArrayList<KeywordMarker> curMarkerList,
            String commandString) throws Exception {
        KeywordMarker markerForIntervalPeriodic = getKeywordMarker(commandString,
                INTERVAL_PERIODIC);
        KeywordMarker markerForInstancesPeriodic = getKeywordMarker(commandString,
                INSTANCES_PERIODIC);
        if (markerForIntervalPeriodic != null && markerForInstancesPeriodic != null) {
            markerForIntervalPeriodic.setFieldType(FieldType.INTERVAL_PERIODIC);
            curMarkerList.add(markerForIntervalPeriodic);
            markerForInstancesPeriodic.setFieldType(FieldType.INSTANCES_PERIODIC);
            curMarkerList.add(markerForInstancesPeriodic);
            return true;
        } else if (markerForIntervalPeriodic != null) {
            throw new Exception(ERROR_MISSING_REPEATS);
        } else if (markerForInstancesPeriodic != null) {
            throw new Exception(ERROR_MISSING_INTERVAL);
        } else {
            return false;
        }
    }

    /**
     * Marks indexes at which the location arguments are found
     * @param curMarkerList
     * @param commandString
     * @return
     */
    boolean getLocationField(ArrayList<KeywordMarker> curMarkerList,
            String commandString) {
        KeywordMarker markerForLocation = getKeywordMarker(commandString,
                LOCATION);
        if (markerForLocation != null) {
            markerForLocation.setFieldType(FieldType.LOCATION);
            curMarkerList.add(markerForLocation);
            return true;
        }
        return false;
    }

    /**
     * Marks indexes at which the date arguments are found
     * @param curMarkerList
     * @param commandString
     * @return
     * @throws Exception
     */
    boolean getDateField(ArrayList<KeywordMarker> curMarkerList,
            String commandString) throws Exception {
        KeywordMarker markerForDeadline = getKeywordMarker(commandString,
                DEADLINE);
        if (markerForDeadline != null) {
            markerForDeadline.setFieldType(FieldType.DEADLINE);
            curMarkerList.add(markerForDeadline);
            return true;
        }

        KeywordMarker markerForStartEvent = getKeywordMarker(commandString,
                START_EVENT);
        KeywordMarker markerForEndEvent = getKeywordMarker(commandString,
                END_EVENT);
        if (markerForStartEvent != null) {
            markerForStartEvent.setFieldType(FieldType.START_EVENT);
            curMarkerList.add(markerForStartEvent);
        }
        if (markerForEndEvent != null) {
            markerForEndEvent.setFieldType(FieldType.END_EVENT);
            curMarkerList.add(markerForEndEvent);
        }
        return true;
    }

    /**
     * Marks the index 2 positions after the found word. 
     * This index is supposed to indicate the start of the field's arguments
     * @param commandString
     * @param listOfKeywords
     * @return
     */
    KeywordMarker getKeywordMarker(String commandString,
            String[] listOfKeywords) {
        for (int i = 0; i < listOfKeywords.length; i++) {
            String curKeyword = String.format(" %s ", listOfKeywords[i]);
            String curKeywordStart = String.format("%s ", listOfKeywords[i]);
            int keywordIndex = commandString.indexOf(curKeyword);
            if (keywordIndex != -1 || commandString.startsWith(curKeywordStart)) {
                int indexOfArgument;
                if(keywordIndex != -1) {
                    indexOfArgument = keywordIndex + curKeyword.length();
                } else {
                    indexOfArgument = -1 + curKeyword.length();
                }
                int lengthOfCommandString = commandString.length();
                logger.finer("getKeywordMarker: Attempting to check " + curKeyword);
                logger.finer("getKeywordMarker: found at " + keywordIndex + " and argument is at " + indexOfArgument);
                while (lengthOfCommandString > indexOfArgument
                        && commandString.charAt(indexOfArgument) == ' ') {
                    indexOfArgument++;
                }
                KeywordMarker newMarker = new KeywordMarker();
                newMarker.setIndex(indexOfArgument);
                return newMarker;
            }
        }
        return null;
    }
    
    /**
     * Adds the new alias newCommandKeyword to the existing keyword/alias existingCommandKeyword
     * @param existingCommandKeyword
     * @param newCommandKeyword
     * @return true if added, false if cannot be added
     */
    public boolean addAlias(String existingCommandKeyword, String newCommandKeyword){
        int i = 0;
        while(i < keywordLists.size()){
            ArrayList<String> curList = keywordLists.get(i);
            if(curList.contains(existingCommandKeyword)){
                curList.add(newCommandKeyword);
                return true;
            }
            i++;
        }
        return false;
    }
}