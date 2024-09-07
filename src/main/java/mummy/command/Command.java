package mummy.command;

import java.io.IOException;
import java.util.HashMap;

import mummy.task.Task;
import mummy.task.TaskList;
import mummy.ui.MummyException;
import mummy.utility.Storage;

/**
 * Represents an abstract class for commands in the application.
 * Each command is responsible for executing a specific action and
 * can be extended to implement different types of commands.
 */
public abstract class Command {

    private final HashMap<String, String> arguments;

    /**
     * Represents the types of commands that can be executed.
     */
    public enum CommandType {
        BYE, LIST, MARK, UNMARK, TODO, DEADLINE,
        EVENT, DELETE, FIND, UNKNOWN
    }

    /**
     * Constructs a new Command object with the given arguments.
     *
     * @param arguments the arguments for the command
     */
    public Command(HashMap<String, String> arguments) {
        this.arguments = arguments;
    }

    /**
     * Creates a Command object based on the given full command string.
     *
     * @param fullCommand the full command string
     * @return a Command object corresponding to the given command string
     * @throws MummyException if an error occurs during parsing or if the command is unknown
     */
    public static Command of(String fullCommand) throws MummyException {
        HashMap<String, String> arguments = Parser.parse(fullCommand);

        CommandType commandType;

        try {
            commandType = CommandType.valueOf(
                    arguments.getOrDefault("command", "")
                            .toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            commandType = CommandType.UNKNOWN;
        }

        switch (commandType) {
        case BYE:
            return new ByeCommand(arguments);
        case LIST:
            return new ListCommand(arguments);
        case MARK:
            return new MarkCommand(arguments);
        case UNMARK:
            return new UnmarkCommand(arguments);
        case TODO:
            return new ToDoCommand(arguments);
        case DEADLINE:
            return new DeadlineCommand(arguments);
        case EVENT:
            return new EventCommand(arguments);
        case DELETE:
            return new DeleteCommand(arguments);
        case FIND:
            return new FindCommand(arguments);
        default:
            throw new MummyException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Executes the command.
     *
     * @param taskList the task list to be modified
     * @param storage the storage for saving and loading tasks
     * @return message generated from executing the command
     * @throws MummyException if there is an error executing the command
     */
    public abstract String execute(TaskList taskList, Storage storage) throws MummyException;

    /**
     * Returns a boolean value indicating whether the command is an exit command.
     *
     * @return true if the command is an exit command, false otherwise.
     */
    public abstract boolean isExit();

    /**
     * Returns the type of the command.
     * @return the type of the command
     */
    public abstract CommandType getCommandType();

    /**
     * Retrieves the value associated with the specified key from the arguments map.
     *
     * @param key the key whose associated value is to be retrieved
     * @return the value associated with the specified key, or null if the key is not present in the map
     */
    public String getArgument(String key) {
        return this.arguments.get(key);
    }

    /**
     * If the key is not found, returns the default argument.
     *
     * @param key The key to retrieve the value for.
     * @param defaultArgument The default value to return if the key is not found.
     * @return The value associated with the key, or the default argument if the key is not found.
     */
    public String getArgument(String key, String defaultArgument) {
        return this.arguments.getOrDefault(key, defaultArgument);
    }

    /**
     * Saves the given task list to the specified storage.
     *
     * @param taskList The task list to be saved.
     * @param storage The storage to save the task list to.
     * @throws MummyException If an error occurs while saving the task list to the storage.
     */
    public void saveTaskListToStorage(TaskList taskList, Storage storage) throws MummyException {
        try {
            storage.save(taskList.toFileRecords());
        } catch (IOException exception) {
            throw new MummyException("Something went wrong when saving to file: "
                    + exception.getMessage());
        }
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to be added
     * @param taskList the task list to add the task to
     * @param storage the storage to save the task list
     * @throws MummyException if there is an error adding the task or saving the task list
     */
    public String addTask(Task task, TaskList taskList, Storage storage) throws MummyException {
        taskList.add(task);
        saveTaskListToStorage(taskList, storage);
        return String.format(
                "Got it. I've added this task:\n\t%s\nNow you have %d tasks in the list.\n",
                task, taskList.count()
        );
    }
}
