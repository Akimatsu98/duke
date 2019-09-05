package duke.storage;

import duke.task.*;
import duke.exception.DukeException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;

public class Storage {
    private String filePath;

    private ArrayList<Task> list;

    public Storage(String fp) {
        this.filePath = fp;
    }

    public ArrayList<Task> load() throws DukeException {
        list = new ArrayList<>();
        try {
            File f = new File(filePath);
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String item = sc.nextLine();
                String type = item.substring(0, 3);
                String status = item.substring(3, 6);
                String desc;
                String date = "";
                if (item.contains("(")) {
                    int dateDescInd = item.indexOf("(");
                    desc = item.substring(7, dateDescInd - 1);
                    date = item.substring(dateDescInd + 1, item.length() - 1);
                } else {
                    desc = item.substring(7);
                }
            Task t = null;
            switch (type) {
                case "[D]" : t = new Deadline(desc, date); break;
                case "[T]" : t = new ToDo(desc); break;
                case "[E]" : t = new Event(desc, date); break;
                default:
                    System.out.println("Unknown event type encountered.");
            }
            if (status.equals("\u2713")) {
                t.markAsDone();
            }
            list.add(t);
        }
        System.out.println("Loaded save data file successfully.");
    } catch (FileNotFoundException e) {
            //Create a new file if no file found
            File f = new File(filePath);
            try {
                if (f.createNewFile())
                {
                    System.out.println("No existing file found! New save data file created!");
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException ex) {
                throw new DukeException("Data file creation failed.");
            }
        }
        return list;
    }

    public void save(TaskList tl) throws DukeException {
        try {
            FileWriter fw = new FileWriter(filePath);
            for (Task t: tl.getTaskList()) {
                fw.write(t.toString());
                fw.write("\n");
            }
            fw.close();
        } catch (IOException ex) {
            throw new DukeException("Data file saving failed.");
        }
    }
}
