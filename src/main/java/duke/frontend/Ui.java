package duke.frontend;

import duke.task.TaskList;
import static java.lang.Integer.parseInt;
import duke.exception.*;
import duke.task.*;
import duke.parser.Parser;
import java.util.ArrayList;
import duke.storage.Storage;

public class Ui {
    private static int cnt = 0;

    private static Parser p = new Parser();

    private TaskList list;

    private Storage storage;

    public Ui(TaskList ls, Storage st) {
        this.list = ls;
        this.storage = st;
    }

    public TaskList getFinalList() {
        return list;
    }

    public String action(String cmd) throws DukeWrongTaskException, UnknownCmdException, DeleteTaskException, CompleteTaskException {
        Task t;

        String command = p.parseCommand(cmd);

        int index = 0;

        String desc = "";

        String response = "";

        switch (command) {
            case "done":
                if (cmd.length() <= 5 || parseInt(cmd.substring(5)) >= list.size() + 1) {
                    throw (new CompleteTaskException());
                }
                index = parseInt(cmd.substring(5));
                list.get(index - 1).markAsDone();
                response = "Nice! I've marked this task as done:\n";
                response = response.concat(list.get(index - 1).toString()).concat("\n");
                return response;
            case "find":
                String keyWord = p.parseDesc(cmd);
                ArrayList<Task> lst = list.returnAllMatchingTasks(keyWord);
                if (lst.size() != 0) {
                    response = "Here are the matching tasks in your list:\n";
                    for (Task ta : lst) {
                        response = response.concat(String.format("%d.%s\n", list.getTaskList().indexOf(ta) + 1, ta.toString()));
                    }
                }
                return response;
            case "delete":
                if (cmd.length() <= 7 || parseInt(cmd.substring(7)) >= list.size() + 1) {
                    throw (new DeleteTaskException());
                }
                index = parseInt(cmd.substring(7));
                response = "Noted! I've removed this task:\n";
                response = response.concat(list.get(index - 1).toString()).concat("\n");
                list.remove(index - 1);
                cnt--;
                response = response.concat(String.format("Now you have %d tasks in the list.\n", list.size()));
                return response ;
            case "deadline":
                if (cmd.length() <= 9 || !cmd.contains("/")) {
                    throw (new DukeWrongTaskException("deadline"));
                }
                desc = p.parseDesc(cmd);
                String ddl = p.parseDate(cmd);
                t = new Deadline(desc, ddl);
                list.add(cnt++, t);
                break;
            case "event":
                if (cmd.length() <= 6 || !cmd.contains("/")) {
                    throw (new DukeWrongTaskException("event"));
                }
                desc = p.parseDesc(cmd);
                String dt = p.parseDate(cmd);
                t = new Event(desc, dt);
                list.add(cnt++, t);
                break;
            case "todo":
                if (cmd.length() <= 5) {
                    throw (new DukeWrongTaskException("toDo"));
                }
                t = new ToDo(p.parseDesc(cmd));
                list.add(cnt++, t);
                break;
            default:
                throw (new UnknownCmdException());
        }
        response = "Got it. I've added this task:\n";
        response = response.concat(t.toString()).concat("\n");
        response = response.concat(String.format("Now you have %d tasks in the list.\n", list.size()));
        return response;
    }

    public String showLoadingError() {
        return "There's no event in your task list!";
    }

    public String start(String input) throws DukeException {
        cnt = list.size();

        String command = p.parseCommand(input);
        String response;

        switch (command) {
            case "bye":
                storage.save(getFinalList());
                return "Saving tasks...\nBye. Hope to see you again soon!";
            case "list":
                try {
                    if (list.size() == 0) {
                        throw (new EmptyListException());
                    }
                    response = "Here are the tasks in your list:\n";
                    for (int i = 0; i < list.size(); i++) {
                        response = response.concat(((i + 1) + ".")
                                           .concat(list.get(i).toString()))
                                           .concat("\n");
                    }
                    return response;
                } catch (EmptyListException e) {
                    return e.getMessage();
                }
            default:
                try {
                    return action(input);
                } catch (DukeException e) {
                    return e.getMessage();
                }
        }
    }
}
