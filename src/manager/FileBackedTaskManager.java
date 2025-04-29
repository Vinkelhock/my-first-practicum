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

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Path path = Paths.get("autosave5.csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        Task task1 = new Task("Сходить в магазин", "купить много продуктов", TaskStatus.NEW);
        Task task2 = new Task("Помыть кота", "приготовить набор первой помощи", TaskStatus.NEW);

        manager.addNewTask(task1);
        manager.addNewTask(task2);

        Epic epic2 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");
        int epic2Id = manager.addNewEpic(epic2);
        SubTask subtask3 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску, напитки, посмотреть Звездные войны Изгой",
                epic2Id,
                TaskStatus.NEW);
        SubTask subtask4 = new SubTask("Начать смотреть сериал Андор",
                "Приготовить закуску, кофе, выключить телефон, нечать смотреть",
                epic2Id,
                TaskStatus.NEW);
        manager.addNewSubtask(subtask3);
        manager.addNewSubtask(subtask4);

        Task task3 = new Task("Доделать ТЗ", "Сдать ТЗ на проверку", TaskStatus.NEW);
        Task task4 = new Task("Посмотреть фильм", "RIDDIK", TaskStatus.NEW);

        manager.addNewTask(task3);
        manager.addNewTask(task4);

        System.out.println(manager.getTasks());
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
