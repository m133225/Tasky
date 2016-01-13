package logic.commands;

public abstract class Command {
    public abstract boolean isUndoable();
    public abstract String execute();
    public abstract String undo();
}
