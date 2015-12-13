package logic;

public abstract class Command {
	abstract boolean isUndoable();
	abstract String execute();
	abstract String undo();
}
