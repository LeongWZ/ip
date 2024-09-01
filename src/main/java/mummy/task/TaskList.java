package mummy.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TaskList {
    private final String label;

    private final ArrayList<Task> tasks;

    public TaskList(String label) {
        this.label = label;
        this.tasks = new ArrayList<>();
    }

    public TaskList(String label, List<String> lines) {
        this.label = label;
        this.tasks = parseLines(lines);
    }

    public Task get(int index) throws TaskListException {
        // index starts from 1
        try {
            return this.tasks.get(index - 1);
        } catch (IndexOutOfBoundsException exception) {
            throw new TaskListException("No such item");
        }
    }

    public void add(Task task) {
        this.tasks.add(task);
    }

    public Task remove(int index) throws TaskListException {
        try {
            return this.tasks.remove(index - 1);
        } catch (IndexOutOfBoundsException exception) {
            throw new TaskListException("No such item");
        }
    }

    public void markTask(int index) throws TaskListException {
        try {
            this.tasks.get(index - 1).setAsDone();
        } catch (IndexOutOfBoundsException exception) {
            throw new TaskListException("No such item");
        }
    }

    public void unmarkTask(int index) throws TaskListException {
        try {
            this.tasks.get(index - 1).setAsUndone();
        } catch (IndexOutOfBoundsException exception) {
            throw new TaskListException("No such item");
        }
    }

    public int getCount() {
        return this.tasks.size();
    }

    @Override
    public String toString() {
        return IntStream.range(0, this.tasks.size())
                .mapToObj(i -> (i + 1) + ". " + this.tasks.get(i))
                .reduce(
                        new StringBuilder(label).append("\n"),
                        (acc, x) -> acc.append(x).append("\n"),
                        StringBuilder::append
                ).toString();
    }

    public List<String> toFileRecords() {
        return this.tasks.stream().map(Task::toFileRecord).toList();
    }

    public TaskList filter(String keyword) {
        List<String> filteredTasks = this.tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .map(Task::toFileRecord)
                .toList();
        return new TaskList("Here are the matching tasks in your list:", filteredTasks);
    }

    private static ArrayList<Task> parseLines(List<String> fileRecords) {
        return fileRecords.stream()
                .map(fileRecord -> fileRecord.split("\\s*\\|\\s*"))
                .flatMap(tokens -> {
                    try {
                        switch (tokens[0]) {
                        case "T":
                            return Stream.of(new ToDo(tokens[2], tokens[1].equals("1")));
                        case "D":
                            return Stream.of(new Deadline(tokens[2], tokens[1].equals("1"), tokens[3]));
                        case "E":
                            return Stream.of(new Event(tokens[2], tokens[1].equals("1"), tokens[3], tokens[4]));
                        default:
                            return Stream.of();
                        }
                    } catch (IndexOutOfBoundsException e) {
                        return Stream.of();
                    }
                })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
