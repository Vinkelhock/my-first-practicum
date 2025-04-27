package manager;

import exception.ManagerSaveException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.TaskTypes;
import model.enums.TaskStatus;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String DEFAULT = "autoSave.csv";
    private final File autoSaveFile;

    public FileBackedTaskManager(Path autoSave) {
        try {
            if (autoSave == null || !Files.exists(autoSave)) {
                Path defaultFile = Paths.get(DEFAULT);
                this.autoSaveFile = Files.createFile(defaultFile).toFile();
            } else {
                this.autoSaveFile = autoSave.toFile();
            }
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при создании файла для автосохранения.", exception);
        }
    }

    private FileBackedTaskManager(File autoSave) {
        this.autoSaveFile = autoSave;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(autoSaveFile, true))) {
            for (Task task : getTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                writer.write(epic.toString());
                writer.newLine();
            }

            for (SubTask subtask : getSubtasks()) {
                writer.write(subtask.toString());
                writer.newLine();
            }

        } catch (IOException exception) {
            throw new ManagerSaveException(exception);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String string = bufferedReader.readLine().trim();
                fileBackedTaskManager.fromString(string);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при загрузке данных из файла", exception);
        }
        return fileBackedTaskManager;
    }

    private void fromString(String value) {
        String[] fields = value.split(",");
        TaskTypes taskTypes = TaskTypes.valueOf(fields[1]);
        switch (taskTypes) {
            case TaskTypes.TASK:
                createTask(fields);
                break;
            case TaskTypes.EPIC:
                createEpic(fields);
                break;
            case TaskTypes.SUBTASK:
                createSubtask(fields);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + taskTypes);
        }
    }

    private void createSubtask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        int idEpic = Integer.parseInt(fields[5]);
        SubTask subtask = new SubTask(name, description, idEpic, status);
        subtask.setId(id);
        //Создали новую задачу - увеличили счетчик
        super.generateId();
        subtasks.put(id, subtask);
        //Добавляем к эпику
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(id);
    }

    private void createEpic(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        ArrayList<Integer> listOfSubtask = new ArrayList<>();

        Epic epic = new Epic(name, description, id, status, listOfSubtask);
        //Создали новую задачу - увеличили счетчик
        super.generateId();
        epics.put(id, epic);
    }

    private void createTask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Task task = new Task(name, description, id, status);
        //Создали новую задачу - увеличили счетчик
        super.generateId();
        tasks.put(id, task);
    }
}
