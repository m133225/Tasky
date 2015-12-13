package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

import logic.TaskAbstraction;

public class ManualFormatStorage implements Storage {
    Storage storageObject;

    private static final String ARGUMENTS_SEPARATOR = ";";
    private static final String ARGUMENTS_DATE = "date ";

    public static String FILE_PATH = "save.txt";
    public static String FILE_NEWLINE = "\r\n";

    // date format converter
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

    /**
     * {@inheritDoc}
     * 
     * Task data is saved in the following format: "[task name];[date]" on each line. To be improved
     */
    @Override
    public boolean writeItemList(ArrayList<TaskAbstraction> task) throws IOException {
        String content = "";
        for (int i = 0; i < task.size(); i++) {
            TaskAbstraction curTask = task.get(i);
            if (curTask != null) {
                content += curTask.getName();
                if (curTask.getEndingTime() != null) {
                    content += ARGUMENTS_SEPARATOR + ARGUMENTS_DATE + sdf.format(task.get(i).getEndingTime().getTime());
                }
            }
            content += FILE_NEWLINE;
        }

        File file = new File(FILE_PATH);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
        return true;
    }

    /* (non-Javadoc)
     * @see storage.Storage#saveFileToPath(java.lang.String)
     */
    @Override
    public boolean saveFileToPath(String path) throws IOException {
        File file = new File(path);
        String newFolderPath = "";
        if (path.indexOf("/") != -1) {
            newFolderPath = path.substring(0,path.lastIndexOf("/"));
        }
        if (path.indexOf("\\") != -1) {
            newFolderPath = path.substring(0,path.lastIndexOf("\\"));
        }
        if (!newFolderPath.equals("")) {
            File Folderpath = new File(newFolderPath);
            if (!Folderpath.exists()) {
                Folderpath.mkdirs();
            }
        }

        if (!file.exists()) {
            file.createNewFile();
            FILE_PATH = path;
            return false;
        }
        FILE_PATH = path;
        return true;
    }

    /* (non-Javadoc)
     * @see storage.Storage#getItemList()
     */
    @Override
    public ArrayList<TaskAbstraction> getItemList() throws FileNotFoundException {
        File file = new File(FILE_PATH);
        Scanner sc = new Scanner(file);
        ArrayList<TaskAbstraction> taskList = new ArrayList<TaskAbstraction>();

        while (sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            TaskAbstraction taskObj = new TaskAbstraction();
            if (nextLine.contains(ARGUMENTS_DATE)) {
                extractDate(nextLine, taskObj);
            } else {
                taskObj.setName(nextLine);
            }
            taskList.add(taskObj);
        }
        sc.close();
        return taskList;
    }

    public void extractDate(String arg, TaskAbstraction taskObj) { // might want to store the date differently, up to you
        String[] newArgs = arg.split(ARGUMENTS_DATE);
        Calendar calendarRead = new GregorianCalendar();
        try {
            calendarRead.setTime(sdf.parse(newArgs[1]));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        taskObj.setEndingTime(calendarRead);
        taskObj.setName(arg.split(ARGUMENTS_SEPARATOR)[0]);

    }
}
