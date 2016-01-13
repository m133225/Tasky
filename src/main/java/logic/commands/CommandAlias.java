package logic.commands;

import java.util.ArrayList;

import logic.PropertyHandler;
import parser.Parser;

public class CommandAlias extends Command {
    Parser parserObj = null;
    PropertyHandler propHandler = null;
    String existingKeyword = null;
    String newKeyword = null;
    String aliasPropertyString = null;
    
    public CommandAlias(Parser parserObj, PropertyHandler propHandler, ArrayList<String> argumentList, String aliasPropertiesString){
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
            return "Alias '" + newKeyword + "' has been added";
        } else {
            return "Unable to add alias.";
        }
    }

    @Override
    public String undo() {
        return null;
    }

}
