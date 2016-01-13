package logic.commands;

public abstract class Command {
	abstract public boolean isUndoable();
	abstract public String execute();
	abstract public String undo();
}
