package logic;

import global.UserInput;
import global.ITask;
import global.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import parser.Parser;
import storage.Storage;
import storage.JsonFormatStorage;
import ui.GraphicalUI;
import ui.UI;
import ui.UI.DisplayType;

/**
 * This file contains the main program of the command-line calendar, Tasky.
 * 
 * The Logic constructor will initialize all the other components, Parser, Logic and Storage,
 * as well as its own sub-component History. It will also initialize the log file and
 * read from the configuration file if it exists.
 * 
 * Upon calling start(), Logic will begin requesting commands from the UI, and
 * executing them.
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
    Properties propObject;
    ArrayList<TaskAbstraction> listOfTasks = new ArrayList<TaskAbstraction>();
    ArrayList<Task> listOfShownTasks = new ArrayList<Task>();
    ArrayList<Task> listFilter = new ArrayList<Task>();
    boolean shouldShowDone = true;
    boolean shouldShowUndone = true;
    boolean isHelpDisplayed = false;
    
    int displaySize = DEFAULT_DISPLAY_SIZE;
    Level DEFAULT_LEVEL = Level.INFO;
    
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM YY");
    
    /*
     * Static strings
     */
    private static final String IDENTIFIER_ALL = "all";
    private static final String FILTER_TITLE_LOCATION = "Location: ";
    private static final String FILTER_TITLE_TASK_NAME = "Task: ";
    private static final String FILTER_TITLE_TIME = "Time: ";
    private static final String SEPARATOR = ", ";
    private static final String TITLE_TOP_DISPLAY = "Top %d Items for ";
    private static final String TITLE_TOMORROW = "Tomorrow";
    private static final String TITLE_TODAY = "Today";
    private static final String LOG_FILE_NAME = "tasky.log";
    private static final String CONFIG_FILE_NAME = "config.txt";
    private static final String DEFAULT_LOGGING_LEVEL_STRING = "INFO";
    private static final String DEFAULT_SAVE_FILE_PATH = "save.txt";
    private static final int DEFAULT_DISPLAY_SIZE = 3;
    private static final String PROPERTY_KEY_LOGGING_LEVEL = "loggingLevel";
    private static final String PROPERTY_KEY_SAVE_FILE = "saveFile";
    private static final String PROPERTY_KEY_DISPLAY_SIZE = "defaultDisplaySize";
    
    private static final String WHITE_SPACE_REGEX = "\\s+";

    private static final String PROPERTY_KEY_ALIAS_ADD = "addAlias";
    private static final String PROPERTY_KEY_ALIAS_EDIT = "editAlias";
    private static final String PROPERTY_KEY_ALIAS_DELETE = "deleteAlias";
    private static final String PROPERTY_KEY_ALIAS_UNDO = "undoAlias";
    private static final String PROPERTY_KEY_ALIAS_REDO = "redoAlias";
    private static final String PROPERTY_KEY_ALIAS_MARK = "markAlias";
    private static final String PROPERTY_KEY_ALIAS_UNMARK = "unmarkAlias";
    private static final String PROPERTY_KEY_ALIAS_EXIT = "exitAlias";
    private static final String PROPERTY_KEY_ALIAS_DISPLAY = "displayAlias";
    private static final String PROPERTY_KEY_ALIAS_SEARCH = "searchAlias";
    private static final String PROPERTY_KEY_ALIAS_SAVETO = "savetoAlias";
    private static final String PROPERTY_KEY_ALIAS_HELP = "helpAlias";
    private static final String[] PROPERTY_KEY_ALIAS_LIST = {
            PROPERTY_KEY_ALIAS_ADD, PROPERTY_KEY_ALIAS_EDIT,
            PROPERTY_KEY_ALIAS_DELETE, PROPERTY_KEY_ALIAS_UNDO,
            PROPERTY_KEY_ALIAS_REDO, PROPERTY_KEY_ALIAS_MARK,
            PROPERTY_KEY_ALIAS_UNMARK, PROPERTY_KEY_ALIAS_EXIT,
            PROPERTY_KEY_ALIAS_DISPLAY, PROPERTY_KEY_ALIAS_SEARCH,
            PROPERTY_KEY_ALIAS_SAVETO, PROPERTY_KEY_ALIAS_HELP };
    private static final String[] listOfDefaultKeywords = { "add", "edit", "delete", "mark",
            "unmark", "undo", "redo", "exit", "display", "search",
            "saveto", "help" }; // must be in same order as PROPERTY_KEY_ALIAS_LIST

    /*
     * Errors and messages
     */
    private static final String MESSAGE_WELCOME = "Welcome to Tasky!";
    private static final String MESSAGE_PROMPT_COMMAND = "Command : ";
    private static final String MESSAGE_UNDO = "Undo : ";
    private static final String MESSAGE_REDO = "Redo : ";
    private static final String MESSAGE_SUCCESS_HISTORY_ADD = "Deleted item(s) restored.";
    private static final String MESSAGE_SUCCESS_HISTORY_DELETE = "Added item(s) removed.";
    private static final String MESSAGE_SUCCESS_HISTORY_EDIT = "Reverted edits.";
    private static final String MESSAGE_SUCCESS_ADD = "Item(s) successfully added.";
    private static final String MESSAGE_SUCCESS_DELETE = "Item(s) successfully deleted.";
    private static final String MESSAGE_SUCCESS_MARK = "Item(s) successfully marked as done.";
    private static final String MESSAGE_SUCCESS_UNMARK = "Item(s) successfully marked as undone.";
    private static final String MESSAGE_SUCCESS_SEARCH = "Search results.";
    private static final String MESSAGE_SUCCESS_EDIT = "Item(s) successfully edited.";
    private static final String MESSAGE_SUCCESS_EXIT = "Exiting program...";
    private static final String MESSAGE_SUCCESS_DISPLAY = "Displaying items.";
    private static final String MESSAGE_SUCCESS_ALIAS = "Alias '%s' added for %s!";
    private static final String MESSAGE_SUCCESS_CHANGE_FILE_PATH = "File path successfully changed.";
    private static final String MESSAGE_SUCCESS_NO_CHANGE_FILE_PATH = "File path not changed. Entered file path is the same as current one used.";
    private static final String MESSAGE_DISPLAY_EMPTY = "No items to display.";
    private static final String MESSAGE_SUCCESS_HELP = "Toggling help message.";
    private static final String ERROR_WRITING_FILE = "Error: Unable to write file.";
    private static final String ERROR_CREATING_FILE = "Error: Unable to create file.";
    private static final String ERROR_FILE_NOT_FOUND = "Error: Data file not found.";
    private static final String ERROR_LOG_FILE_INITIALIZE = "Error: Cannot initialize log file.";
    private static final String ERROR_READ_CONFIG_FILE = "Error: Cannot read from configuration file.";
    private static final String ERROR_CREATE_CONFIG_FILE = "Error: Cannot create configuration file.";
    private static final String ERROR_INVALID_ARGUMENT = "Error: Invalid argument for command.";
    private static final String ERROR_INVALID_COMMAND = "Error: Invalid command.";
    private static final String ERROR_NO_COMMAND_HANDLER = "Error: Handler for this command type has not been defined.";
    private static final String ERROR_HISTORY_NO_COMMAND_HANDLER = "Error: History called by unidentified command.";
    private static final String ERROR_INVALID_INDEX = "Error: There is no item at this index.";
    private static final String ERROR_UI_INTERRUPTED = "Error: UI prompt has been interrupted.";
    private static final String ERROR_NO_HISTORY = "Error: No history found.";
    private static final String ERROR_CANNOT_WRITE_TO_HISTORY = "Error: Unable to store command in history.";
    private static final String ERROR_CANNOT_PARSE_PERIODIC_VALUES = "Error: Unable to parse values for periodic.";
    private static final String ERROR_NO_FILTER = "Error: No filter detected for search.";
    private static final String ERROR_EDIT_CANNOT_RECURRING = "Error: Cannot convert a normal task to recurring.";
    private static final String ERROR_START_TIME_WITHOUT_END_TIME = "Error: Cannot add start time without end time!";
    private static final String ERROR_START_TIME_BEFORE_END_TIME = "Error: Starting time cannot be after ending time.";
    
    private static final String WARNING_TIMING_CLASH = "WARNING: There are clashing timings between tasks.";
    
    
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
        try {
            initializeComponentObjects();
            initializeLogFile();
            initializeConfigFile();
            updateListOfTasks();
        } catch (FileNotFoundException e) {
            UIObject.showToUser(ERROR_FILE_NOT_FOUND);
        } catch (SecurityException | IOException | NumberFormatException e) {
            UIObject.showToUser(ERROR_LOG_FILE_INITIALIZE);
        } catch (Exception e) {
            UIObject.showToUser(e.getMessage());
        }
    }

    void initializeComponentObjects() {
        UIObject = new GraphicalUI();
        parserObject = new Parser();
        storageObject = new JsonFormatStorage(true);
        historyObject = new History();
        propObject = new Properties();
    }

    /**
     * Checks if there is an existing configuration file.
     * 
     * If there is, it assumes that the config file has not been incorrectly modified
     * and reads from it
     * 
     * Else, create a config file with default settings
     * 
     * At the end of this method, the latest properties should be set
     * 
     * @throws Exception with the respective status message
     */
    void initializeConfigFile() throws Exception {
        File configFile = new File(CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try {
                readConfigFile();
            } catch (IOException e) {
                throw new Exception(ERROR_READ_CONFIG_FILE);
            }
        } else {
            try {
                createAndWriteConfigFile(configFile);
            } catch (IOException e) {
                throw new Exception(ERROR_CREATE_CONFIG_FILE);
            }
        }
        setReadConfig();
    }

    /**
     * Loads the data that is stored in propObject
     * Used after reading of the configuration file
     * 
     * @throws IOException
     */
    void setReadConfig() throws IOException {
        storageObject.saveFileToPath(propObject.getProperty(PROPERTY_KEY_SAVE_FILE));
        String logLevelString = propObject.getProperty(PROPERTY_KEY_LOGGING_LEVEL);
        displaySize = Integer.parseInt(propObject.getProperty(PROPERTY_KEY_DISPLAY_SIZE));
        addAllConfigAliasToParser(); 
        switch (logLevelString) {
            case "WARNING":
                logger.setLevel(Level.WARNING);
                break;
            case "INFO":
                logger.setLevel(Level.INFO);
                break;
            case "FINE":
                logger.setLevel(Level.FINE);
                break;
            case "FINER":
                logger.setLevel(Level.FINER);
                break;
            case "FINEST":
                logger.setLevel(Level.FINEST);
                break;
            default:
                logger.setLevel(DEFAULT_LEVEL);
        }
    }

    /**
     * Creates a configuration file and writes the default values to it
     * @param configFile
     * @throws IOException
     */
    void createAndWriteConfigFile(File configFile) throws IOException {
        configFile.createNewFile();
        propObject.setProperty(PROPERTY_KEY_SAVE_FILE, DEFAULT_SAVE_FILE_PATH);
        propObject.setProperty(PROPERTY_KEY_LOGGING_LEVEL, DEFAULT_LOGGING_LEVEL_STRING);
        propObject.setProperty(PROPERTY_KEY_DISPLAY_SIZE, Integer.toString(DEFAULT_DISPLAY_SIZE));
        setAllConfigAlias();
        writeProperties();
    }

    void readConfigFile() throws FileNotFoundException, IOException {
        BufferedReader bufReader = new BufferedReader(new FileReader(
                new File(CONFIG_FILE_NAME)));
        propObject.load(bufReader);
        bufReader.close();
    }

    /**
     * Initialize the log file, and set the logger output format
     * to human-readable
     * 
     * @throws IOException
     */
    void initializeLogFile() throws IOException {
        FileHandler logHandler = new FileHandler(LOG_FILE_NAME);
        LogManager.getLogManager().reset(); // removes printout to console
                                            // aka root handler
        logHandler.setFormatter(new SimpleFormatter()); // set output to a
                                                        // human-readable
                                                        // log format
        logger.addHandler(logHandler);
    }
    
     /**
      * Sets the empty string for all the alias properties
      */
    void setAllConfigAlias(){
        for (int i = 0; i < PROPERTY_KEY_ALIAS_LIST.length; i++) {
            propObject.setProperty(PROPERTY_KEY_ALIAS_LIST[i], "");
        }
    }
    
    /**
     * Add the alias lists read from the config file to the parser
     */
    void addAllConfigAliasToParser(){
        for (int i = 0; i < listOfDefaultKeywords.length
                && i < PROPERTY_KEY_ALIAS_LIST.length; i++) {
            addConfigAlias(listOfDefaultKeywords[i],
                    propObject.getProperty(PROPERTY_KEY_ALIAS_LIST[i]));
        }
    }
    
    /**
     * Adds the new aliasString to parser's list of command keywords
     * @param existingKeyword
     * @param aliasString
     * @return true if added, false if unable to add
     */
    boolean addConfigAlias(String existingKeyword, String aliasString){
        String[] aliasWords = aliasString.split(SEPARATOR);
        boolean hasError = false;
        for (int i = 0; i < aliasWords.length; i++) {
            if (!parserObject.addAlias(existingKeyword, aliasWords[i])){
                hasError = true;
            }
        }
        return hasError;
    }

    void start() {
        initializeDisplay();
        readAndExecuteUserInput();
    }

    void initializeDisplay() {
        showUpdatedItems();
        UIObject.showStatusToUser(MESSAGE_WELCOME);
    }

    /**
     * Repeatedly Reads the user input, parses the command, executes the command
     * object, shows the result in UI, writes latest task list to file until the
     * program exits
     */
    void readAndExecuteUserInput() {
        while (true) {
            try {
                String userInput = UIObject.promptUser(MESSAGE_PROMPT_COMMAND);
                UserInput commandObject = parserObject.parseCommand(userInput);
                
                String executionResult = executeCommand(commandObject);
                UIObject.showStatusToUser(executionResult);
                /*if (commandObject.getCommandType() == UserInput.Type.HELP && isHelpDisplayed) {
                    showHelpMessage();
                } else {
                }*/

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
            	e.printStackTrace();
                UIObject.showStatusToUser(e.getMessage());
            }
        }
    }

    /**
     * Executes a command based on commandObject
     * 
     * 
     * @param userInputObject
     * @param isUserInput
     *            false if command is called from redo
     * @param isUndoHistory
     *            true if command is called from undo
     *            false if command is called by user directly
     * 
     * @return status string to be shown to user
     * @throws IOException 
     */
    String executeCommand(UserInput userInputObject) {
        if (userInputObject == null) {
            return ERROR_INVALID_COMMAND;
        }
        UserInput.Type commandType = userInputObject.getCommandType();
        Task userTask = userInputObject.getTask();
        ArrayList<String> argumentList = userInputObject.getArguments();
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        if (commandType == null) {
            logger.warning("Command type is null!");
            return ERROR_NO_COMMAND_HANDLER;
        } else {
            switch (commandType) {
                case ADD :
                    logger.info("ADD command detected");
                    Command addCommand = new CommandAdd(listOfTasks, userTask);
                    historyObject.pushCommand(addCommand, true);
                    historyObject.clearUndoHistoryList();
                    return addCommand.execute();
                case DELETE :
                    logger.info("DELETE command detected");
                    try {
                        indexList = parseIntList(argumentList);
                        Command deleteCommand;
                    	deleteCommand = new CommandDelete(listOfTasks, listOfShownTasks, indexList);
                        historyObject.pushCommand(deleteCommand, true);
                        historyObject.clearUndoHistoryList();
                        return deleteCommand.execute();
                    } catch (Exception e) {
                    	return e.getMessage();
                    }
                case EDIT :
                    logger.info("EDIT command detected");
                    try {
                        indexList = parseIntList(argumentList);
                        Command editCommand;
                        editCommand = new CommandEdit(listOfTasks, listOfShownTasks, indexList.get(0), userTask);

                        historyObject.pushCommand(editCommand, true);
                        historyObject.clearUndoHistoryList();
                        return editCommand.execute();
                    } catch (Exception e) {
                        return e.getMessage();
                    }
                case DISPLAY :
                    logger.info("DISPLAY command detected");
                    return "";//displayItems(argumentList);
                case UNDO :
                    logger.info("UNDO command detected");
                    Command previousCommand = historyObject.getPreviousCommand(true);
                    if (previousCommand == null) {
                        return ERROR_NO_HISTORY;
                    }
                    historyObject.pushCommand(previousCommand, false);
                    return previousCommand.undo();
                case REDO :
                    logger.info("REDO command detected");
                    Command previousUndoCommand = historyObject.getPreviousCommand(false);
                    if (previousUndoCommand == null) {
                        return ERROR_NO_HISTORY;
                    }
                    historyObject.pushCommand(previousUndoCommand, true);
                    return previousUndoCommand.execute();
                case SAVETO :
                    logger.info("SAVETO command detected");
                    Command saveToCommand = new CommandSaveTo(storageObject, argumentList.get(0), listOfTasks);
                    return saveToCommand.execute();
                case EXIT :
                    logger.info("EXIT command detected");
                    return exitProgram();
                case MARK:
                    logger.info("MARK command detected");
                    try {
                        indexList = parseIntList(argumentList);
                        Command markCommand;
                        markCommand = new CommandMark(listOfTasks, listOfShownTasks, indexList, true);

                        historyObject.pushCommand(markCommand, true);
                        historyObject.clearUndoHistoryList();
                        return markCommand.execute();
                    } catch (Exception e) {
                        return e.getMessage();
                    }
                case UNMARK:
                    logger.info("UNMARK command detected");
                    try {
                        indexList = parseIntList(argumentList);
                        Command unmarkCommand;
                        unmarkCommand = new CommandMark(listOfTasks, listOfShownTasks, indexList, false);

                        historyObject.pushCommand(unmarkCommand, true);
                        historyObject.clearUndoHistoryList();
                        return unmarkCommand.execute();
                    } catch (Exception e) {
                        return e.getMessage();
                    }
                case SEARCH:
                    logger.info("SEARCH command detected");
                    return "";//addSearchFilter(userTasks);
                case HELP:
                    logger.info("HELP command detected");
                   // toggleHelpDisplay();
                    return "";//MESSAGE_SUCCESS_HELP;
                case ALIAS:
                    logger.info("ALIAS command detected");
                    return "";//addAlias(argumentList);
                default :
                    logger.warning("Command type cannot be identified!");
                    return ERROR_NO_COMMAND_HANDLER;
            }
        }
    }
    
    /**
     * Converts a list of integer strings into list of integers
     * 
     * @param argumentList
     * @return intList the resulting integer list
     */
    ArrayList<Integer> parseIntList(ArrayList<String> argumentList) throws Exception {
        ArrayList<Integer> intList = new ArrayList<Integer>();
        try {
            for (int i = 0; i < argumentList.size(); i++) {
                intList.add(Integer.parseInt(argumentList.get(i)));
            }
        } catch (NumberFormatException e) {
            throw new Exception("Error parsing arguments.");
        }
        return intList;
    }
    
    // Create an array with all unique elements
    ArrayList<String> removeDuplicates(ArrayList<String> parsedIntArgumentList) {
        if (parsedIntArgumentList == null) {
            return new ArrayList<String>();
        }
        HashSet<String> hs = new HashSet<>();
        hs.addAll(parsedIntArgumentList);
        parsedIntArgumentList.clear();
        parsedIntArgumentList.addAll(hs);
        return parsedIntArgumentList;
    }
    
    /**
     * This method filters the list of tasks to be shown to the user,
     * based on the current list of filter keywords
     * 
     * It will attempt to show the 3 most urgent tasks in each category of
     * floating/deadline/event by default if there are no filter keywords
     * 
     * @return calls the UI to display updated list of items
     */
    boolean showUpdatedItems() {
        listOfShownTasks.clear();
        if (listFilter.isEmpty()) {
            // default view - first closest date, second closest date, floating
            ArrayList<Task> listOfFloating = new ArrayList<Task>();
            ArrayList<Task> listOfEventsDeadlines = new ArrayList<Task>();
            
            separateFloatingTasksFromOthers(listOfFloating,
                    listOfEventsDeadlines);
            Collections.sort(listOfEventsDeadlines);
            
            ArrayList<Task> listOfFirstDate = new ArrayList<Task>();
            ArrayList<Task> listOfSecondDate = new ArrayList<Task>();
            getTasksInFirstAndSecondDate(listOfEventsDeadlines,
                    listOfFirstDate, listOfSecondDate);
            
            addTasksToShownList(listOfFirstDate);
            addTasksToShownList(listOfSecondDate);
            addTasksToShownList(listOfFloating);
            
            List<String> listOfTitles = new ArrayList<String>();
            addTitleForDate(listOfFirstDate, listOfTitles);
            addTitleForDate(listOfSecondDate, listOfTitles);
            addTitleForFloating(listOfFloating, listOfTitles);
            
            return UIObject.showTasks(listOfShownTasks, DisplayType.DEFAULT, listOfTitles);
        } else {
            // filtered view
            listOfShownTasks = new ArrayList<Task>();
            List<String> searchStrings = new ArrayList<String>();
            
            filterTasksByDoneUndone();
            
            searchStrings.add(FILTER_TITLE_TASK_NAME);
            searchStrings.add(FILTER_TITLE_TIME);
            searchStrings.add(FILTER_TITLE_LOCATION);
            filterTasksAndGenerateSearchStrings(searchStrings);
            
            return UIObject.showTasks(listOfShownTasks, DisplayType.FILTERED, searchStrings);
        }
    }
    
    /**
     * Filters tasks by done and undone depending on the
     * variable shouldShowDone & shouldShowUndone, and put
     * them into listOfShownTasks
     * 
     * If they are both true, this simply adds all tasks
     * in listOfTasks to listOfShownTasks
     */
    void filterTasksByDoneUndone() {
        for (int i = 0; i < listOfTasks.size(); i++) {
            TaskAbstraction curTask = listOfTasks.get(i);
            if (curTask.isDone() && shouldShowDone) {
                listOfShownTasks.add(curTask);
            }
            if (!curTask.isDone() && shouldShowUndone) {
                listOfShownTasks.add(curTask);
            }
        }
    }

    /**
     * Separates floating tasks from the rest (events and deadlines)
     * @param listOfFloating
     * @param listOfEventsDeadlines
     */
    void separateFloatingTasksFromOthers(
            ArrayList<Task> listOfFloating,
            ArrayList<Task> listOfEventsDeadlines) {

        ArrayList<Task> resolvedTasks = new ArrayList<Task>();
        for (int i = 0; i < listOfTasks.size(); i++) {
            resolvedTasks.addAll(listOfTasks.get(i).resolveAll());
        }
        for (int i = 0; i < resolvedTasks.size(); i++) {
            Task curTask = resolvedTasks.get(i);
            if (!curTask.isDone()) {
                if (curTask.hasEndingTime()) {
                    listOfEventsDeadlines.add(curTask);
                } else {
                    listOfFloating.add(curTask);
                }
            }
        }
    }

    /**
     * Fills up listOfFirstDate and listOfSecondDate using the tasks
     * in listOfEventsDeadlines that are on the closest 2 dates
     * 
     * 
     * @param listOfEventsDeadlines sorted list of tasks, according to time
     * @param listOfFirstDate
     * @param listOfSecondDate
     */
    void getTasksInFirstAndSecondDate(
            ArrayList<Task> listOfEventsDeadlines,
            ArrayList<Task> listOfFirstDate, ArrayList<Task> listOfSecondDate) {
        if (listOfEventsDeadlines.size() != 0) {
            Task firstTask;
            Calendar todayDate = new GregorianCalendar();
            Calendar firstDate = null, secondDate = null;
            int i = 0;
            
            boolean hasFirstDate = false;
            
            while (i < listOfEventsDeadlines.size() && !hasFirstDate) {
                // prepare first task in the list for comparison
                firstTask = listOfEventsDeadlines.get(i);
                firstDate = firstTask.getTime();
                // compare to see if the task is before today's date. We only want tasks after/same as today's date
                if (firstDate.before(todayDate)) {
                    // date is before today's date, continue to iterate
                    i++;
                    firstDate = null;
                } else {
                    // first date is found
                    // so break loop and continue
                    hasFirstDate = true;
                }
            }
            
            if (firstDate != null) {
                getTasksInDay(listOfEventsDeadlines, firstDate,
                        listOfFirstDate);
                while (i < listOfEventsDeadlines.size()
                        && secondDate == null) {
                    Task curTask = listOfEventsDeadlines.get(i);
                    Calendar curDate = curTask.getTime();
                    if (!isTimingInDay(curDate, firstDate)) {
                        secondDate = curDate;
                    }
                    i++;
                }
                if (secondDate != null) {
                    getTasksInDay(listOfEventsDeadlines, secondDate,
                            listOfSecondDate);
                }
            }
        }
    }

    /**
     * Filter by the name, time and locations fields.
     * 
     * This method also adds the search filter titles
     * that are to be displayed to the user to searchStrings
     * 
     * @param searchStrings
     */
    void filterTasksAndGenerateSearchStrings(List<String> searchStrings) {
        boolean isEmptyName = true;
        boolean isEmptyTime = true;
        boolean isEmptyLocation = true;
        
        // Filter by name, time, and location
        for (int j = 0; j < listFilter.size(); j++) {
            Task curFilter = listFilter.get(j);
            isEmptyName = filterByName(searchStrings, isEmptyName,
                    curFilter);
            isEmptyTime = filterByTime(searchStrings, isEmptyTime,
                    curFilter);
            isEmptyLocation = filterByLocation(searchStrings,
                    isEmptyLocation, curFilter);
        }
    }

    void addTitleForFloating(ArrayList<Task> listOfFloating,
            List<String> listOfTitles) {
        if (listOfFloating.size() != 0) {
            listOfTitles.add("Other Tasks");
        } else {
            listOfTitles.add("No Other Tasks");
        }
    }

    /**
     * Goes through the current list of shown tasks, and remove it if it
     * doesn't fit the curFilter location
     * 
     * @param searchStrings
     * @param isEmptyLocation
     * @param curFilter
     * @return isEmptyLocation updated status
     */
    boolean filterByLocation(List<String> searchStrings,
            boolean isEmptyLocation, Task curFilter) {
        String searchLocation = curFilter.getLocation();
        int i = 0;
        if (searchLocation != null) {
            if (!isEmptyLocation) {
                searchStrings.set(2,
                        searchStrings.get(2).concat(SEPARATOR));
            }
            isEmptyLocation = false;
            searchStrings.set(2, searchStrings.get(2).concat(searchLocation));
            while (i < listOfShownTasks.size()) {
                Task curTask = listOfShownTasks.get(i);
                if (curTask.getLocation() == null || !curTask.getLocation().toLowerCase().contains(searchLocation)) {
                    listOfShownTasks.remove(i);
                } else {
                    i++;
                }
            }
        }
        return isEmptyLocation;
    }

    /**
     * Goes through the current list of shown tasks, and remove it if it
     * doesn't fit the curFilter time
     * 
     * @param searchStrings
     * @param isEmptyTime
     * @param curFilter
     * @return isEmptyTime updated status
     */
    boolean filterByTime(List<String> searchStrings,
            boolean isEmptyTime, Task curFilter) {
        Calendar filterTime = curFilter.getTime();
        if (filterTime != null) {
            Calendar filterTimeStart = (Calendar) filterTime.clone();
            filterTimeStart.set(Calendar.HOUR_OF_DAY, 0);
            filterTimeStart.set(Calendar.MINUTE, 0);
            Calendar filterTimeEnd = (Calendar) filterTime.clone();
            filterTimeEnd.add(Calendar.DATE, 1);
            filterTimeEnd.set(Calendar.HOUR_OF_DAY, 0);
            filterTimeEnd.set(Calendar.MINUTE, 0);
            
            if (!isEmptyTime) {
                searchStrings.set(
                        1,
                        searchStrings.get(1).concat(
                                SEPARATOR));
            }
            isEmptyTime = false;
            searchStrings.set(
                    1,
                    searchStrings.get(1).concat(
                            dateFormat.format(filterTime.getTime())));
            int i = 0;
            while (i < listOfShownTasks.size()) {
                Task curTask = listOfShownTasks.get(i);
                if (curTask.getTime() == null
                        || curTask.getTime().before(filterTimeStart)
                        || !curTask.getTime().before(filterTimeEnd)) {
                    listOfShownTasks.remove(i);
                } else {
                    i++;
                }
            }
        }
        return isEmptyTime;
    }

    /**
     * Goes through the current list of shown tasks, and remove it if it
     * doesn't fit the curFilter name
     * 
     * @param searchStrings
     * @param isEmptyName
     * @param curFilter
     * @return isEmptyName updated status
     */
    boolean filterByName(List<String> searchStrings,
            boolean isEmptyName, Task curFilter) {
        String searchTaskName = curFilter.getName();
        int i = 0;
        if (searchTaskName != null) {
            searchTaskName = searchTaskName.toLowerCase();
            if (!isEmptyName) {
                searchStrings.set(0,
                        searchStrings.get(0).concat(SEPARATOR));
            }
            isEmptyName = false;
            searchStrings.set(0, searchStrings.get(0).concat(searchTaskName));
            while (i < listOfShownTasks.size()) {
                Task curTask = listOfShownTasks.get(i);
                if (!curTask.getName().toLowerCase().contains(searchTaskName)) {
                    listOfShownTasks.remove(i);
                } else {
                    i++;
                }
            }
        }
        return isEmptyName;
    }

    /**
     * Adds a new title to listOfTitles based on dd mm yy of
     * the first task of the listOfItemsInDate
     * 
     * @param listOfItemsInDate
     * @param listOfTitles
     */
    void addTitleForDate(ArrayList<Task> listOfItemsInDate,
            List<String> listOfTitles) {
        if (listOfItemsInDate.size() != 0) {
            Calendar curTime = Calendar.getInstance();
            int curDate = curTime.get(Calendar.DATE);
            int curMonth = curTime.get(Calendar.MONTH) + 1;
            int curYear = curTime.get(Calendar.YEAR);
            Task curItem = listOfItemsInDate.get(0);
            
            Calendar curItemTime = curItem.getTime();
            int curItemDate = curItemTime.get(Calendar.DATE);
            int curItemMonth = curItemTime.get(Calendar.MONTH) + 1;
            int curItemYear = curItemTime.get(Calendar.YEAR);
            addTitleForDateHelper(listOfTitles, curDate, curMonth, curYear,
                    curItemDate, curItemMonth, curItemYear);
        } else {
            listOfTitles.add("No Upcoming Tasks");
        }
    }

    /**
     * Generate the title based on the given the dd mm yy of the task
     * @param listOfTitles
     * @param curDate
     * @param curMonth
     * @param curYear
     * @param curItemDate
     * @param curItemMonth
     * @param curItemYear
     */
    void addTitleForDateHelper(List<String> listOfTitles, int curDate,
            int curMonth, int curYear, int curItemDate, int curItemMonth, int curItemYear) {
        Calendar itemDate = new GregorianCalendar();
        itemDate.set(Calendar.DATE, curItemDate);
        itemDate.set(Calendar.MONTH, curItemMonth - 1);
        itemDate.set(Calendar.YEAR, curItemYear);
        String itemDateString = dateFormat.format(itemDate.getTime());
        String titleTop = String.format(TITLE_TOP_DISPLAY, displaySize);
        
        boolean isSameMonthAndYear = (curMonth == curItemMonth) && (curYear == curItemYear);
        if (isSameMonthAndYear && curDate == curItemDate) {
            listOfTitles.add(titleTop + TITLE_TODAY);
        } else if (isSameMonthAndYear && curDate == curItemDate - 1) {
            listOfTitles.add(titleTop + TITLE_TOMORROW);
        } else {
            listOfTitles.add(titleTop + itemDateString);
        }
    }

    void addTasksToShownList(ArrayList<Task> listOfFirstDate) {
        if (listOfFirstDate.size() >= displaySize) {
            listOfShownTasks.addAll(listOfFirstDate.subList(0, displaySize));
        } else {
            listOfShownTasks.addAll(listOfFirstDate);
        }
    }
    
    /**
     * Reads the task list from the data file
     * @return
     * @throws Exception status message if data file cannot be found
     */
    boolean updateListOfTasks() throws Exception {
        try {
            listOfTasks = storageObject.getItemList();
        } catch (FileNotFoundException e) {
            throw new Exception(ERROR_FILE_NOT_FOUND);
        }
        return true;
    }
    
    boolean isTimingInDay(Calendar time, Calendar date) {
        return time.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                time.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * Get the list of tasks that start on the date or has a deadline on the date,
     * and add them to tasksInDay
     * @param listOfEventsDeadlines
     * @param date
     * @return
     */
    void getTasksInDay(ArrayList<Task> listOfEventsDeadlines,
            Calendar date, ArrayList<Task> tasksInDay) {
        for (int i = 0; i < listOfEventsDeadlines.size(); i++) {
            Task curTask = listOfEventsDeadlines.get(i);
            Calendar itemTime = curTask.getTime();
            if (isTimingInDay(itemTime, date)) {
                tasksInDay.add(curTask);
            }
        }
    }
    
    boolean updateProperties(String key, String value) throws IOException{
        propObject.setProperty(key, value);
        writeProperties();
        return true;
    }
    
    /**
     * Writes the current property keys and values to the config file
     * @return 
     * @throws IOException if there is a problem with writing to file
     */
    boolean writeProperties() throws IOException{
        BufferedWriter bufWriter = new BufferedWriter(new FileWriter(new File(CONFIG_FILE_NAME)));
        propObject.store(bufWriter, null);
        bufWriter.close();
        return true;
    }

    String exitProgram() {
        System.exit(0);
        return MESSAGE_SUCCESS_EXIT;
    }
}