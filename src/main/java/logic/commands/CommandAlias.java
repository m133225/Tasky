package logic.commands;

import java.util.ArrayList;

import logic.PropertyHandler;
import parser.Parser;

public class CommandAlias extends Command {
    private static final String SUCCESS_ALIAS = "Alias '%s' has been added.";
    private static final String FAILURE_ALIAS = "Unable to add alias.";
    Parser parserObj = null;
    PropertyHandler propHandler = null;
    String existingKeyword = null;
    String newKeyword = null;
    String aliasPropertyString = null;

    public CommandAlias(Parser parserObj, PropertyHandler propHandler, ArrayList<String> argumentList, String aliasPropertiesString) {
        this.parserObj = parserObj;
        this.propHandler = propHandler;
        this.existingKeyword = argumentList.get(0);
        this.newKeyword = argumentList.get(1);
        this.aliasPropertyString = aliasPropertiesString;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public String execute() {
        boolean result = parserObj.addAlias(existingKeyword, newKeyword);
        if (result) {
            String curProperty = propHandler.getProperty(aliasPropertyString);
            if (curProperty.equals("")) {
                propHandler.setProperty(aliasPropertyString, newKeyword);
            } else {
                propHandler.setProperty(aliasPropertyString, curProperty + ", " + newKeyword);
            }
            return String.format(SUCCESS_ALIAS, newKeyword);
        } else {
            return FAILURE_ALIAS;
        }
    }

    @Override
    public String undo() {
        return null;
    }

}
