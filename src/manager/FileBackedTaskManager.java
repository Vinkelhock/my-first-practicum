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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.TaskTypes;
import model.enums.TaskStatus;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String DEFAULT = "autosave1.csv";
    private final Path autoSaveFile;

    public FileBackedTaskManager(Path file) {
        try {
            if (file == null) {
                this.autoSaveFile = Paths.get(DEFAULT);
                Files.createFile(this.autoSaveFile);
            } else {
                this.autoSaveFile = file;
            }
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при создании файла для автосохранения.", exception);
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.autoSaveFile.toFile()))) {
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
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.toPath());
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

    void fromString(String value) {
        String[] lineFromFile = value.split(",");
        TaskTypes taskTypes = TaskTypes.valueOf(lineFromFile[1]);
        switch (taskTypes) {
            case TaskTypes.TASK:
                createTask(lineFromFile);
                break;
            case TaskTypes.EPIC:
                createEpic(lineFromFile);
                break;
            case TaskTypes.SUBTASK:
                createSubtask(lineFromFile);
                break;
            default:
                throw new IllegalStateException(String.format("Unexpected value: %s", taskTypes));
        }
    }

    private void createSubtask(String[] subtaskLine) {
        int id = Integer.parseInt(subtaskLine[0]);
        String name = subtaskLine[2];
        TaskStatus status = TaskStatus.valueOf(subtaskLine[3]);
        String description = subtaskLine[4];
        int idEpic = Integer.parseInt(subtaskLine[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(subtaskLine[6]));
        LocalDateTime startTime = null;
        if (subtaskLine[7] != null) startTime = LocalDateTime.parse(subtaskLine[7]);

        SubTask subtask = new SubTask(name, description, idEpic, status);
        subtask.setId(id);
        subtask.setDuration(duration);
        subtask.setStartTime(startTime);

        //Создали новую задачу - увеличили счетчик
        super.generateId();
        subtasks.put(id, subtask);
        //Добавляем к эпику
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(id);
    }

    private void createEpic(String[] epicLine) {
        int id = Integer.parseInt(epicLine[0]);
        String name = epicLine[2];
        TaskStatus status = TaskStatus.valueOf(epicLine[3]);
        String description = epicLine[4];
        ArrayList<Integer> listOfSubtask = new ArrayList<>();
        Duration duration = Duration.ofMinutes(Long.parseLong(epicLine[5]));
        LocalDateTime startTime = null;
        if (epicLine[6] != null) startTime = LocalDateTime.parse(epicLine[6]);
        LocalDateTime endTime = null;
        if (epicLine[7] != null) endTime = LocalDateTime.parse(epicLine[7]);

        Epic epic = new Epic(name, description, id, status, listOfSubtask);
        epic.setDuration(duration);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);

        //Создали новую задачу - увеличили счетчик
        super.generateId();
        epics.put(id, epic);
    }

    private void createTask(String[] taskLine) {
        int id = Integer.parseInt(taskLine[0]);
        String name = taskLine[2];
        TaskStatus status = TaskStatus.valueOf(taskLine[3]);
        String description = taskLine[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(taskLine[5]));
        LocalDateTime startTime = null;
        if (taskLine[6] != null) startTime = LocalDateTime.parse(taskLine[6]);

        Task task = new Task(name, description, id, status);
        task.setDuration(duration);
        task.setStartTime(startTime);

        //Создали новую задачу - увеличили счетчик
        super.generateId();
        tasks.put(id, task);
    }

    //Удаление всех задач
    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    //Удаление всех эпиков
    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    //Удаление всех подзадач
    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    //Создание задачи Task
    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    //Создание задачи Epic
    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    //Создание подзадачи
    @Override
    public int addNewSubtask(SubTask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    //Обновление задачи
    @Override
    public void updateTask(int id, Task task) {
        super.updateTask(id, task);
        save();
    }

    //Обновление эпика
    @Override
    public void updateEpic(int id, Epic epic) {
        super.updateEpic(id, epic);
        save();
    }

    //Обновление подзадачи
    @Override
    public void updateSubtask(int id, SubTask subtask) {
        super.updateSubtask(id, subtask);
        save();
    }

    //Удаление по идентификатору задачи Task
    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    //Удаление по идентификатору Эпика
    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    //Удаление по идентификатору подзадачи
    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }
}
