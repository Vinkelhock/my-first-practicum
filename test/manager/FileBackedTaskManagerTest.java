package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    Path path = Paths.get("autosave7.csv");

    @BeforeEach
    void setUp() {
        Path path = Paths.get("autosave7.csv");
        super.manager = new FileBackedTaskManager(path);
    }

    @Test
    void saveAndLoadEmptyFile() {
        Path path = Paths.get("empty.csv");

        FileBackedTaskManager secondManager = FileBackedTaskManager.loadFromFile(path.toFile());

        ArrayList<Task> listTask = secondManager.getTasks();
        ArrayList<Epic> listEpic = secondManager.getEpics();
        ArrayList<SubTask> listSubtask = secondManager.getSubtasks();

        assertEquals(0, listTask.size(), "Разное количество задач");
        assertEquals(0, listEpic.size(), "Разное количество Эпиков");
        assertEquals(0, listSubtask.size(), "Разное количество Подзадач");
    }


    @Test
    void saveSomeTasks() {
        Path path = Paths.get("autosave5.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        Task task1 = new Task("Сходить в магазин", "купить много продуктов", TaskStatus.NEW);
        Task task2 = new Task("Помыть кота", "приготовить набор первой помощи", TaskStatus.NEW);
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        Epic epic2 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");
        int epic2Id = manager.addNewEpic(epic2);
        SubTask subtask3 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску напитки посмотреть Звездные войны Изгой",
                epic2Id,
                TaskStatus.NEW);
        SubTask subtask4 = new SubTask("Начать смотреть сериал Андор",
                "Приготовить закуску кофе выключить телефон нечать смотреть",
                epic2Id,
                TaskStatus.NEW);
        manager.addNewSubtask(subtask3);
        manager.addNewSubtask(subtask4);

        FileBackedTaskManager secondManager = FileBackedTaskManager.loadFromFile(path.toFile());
        ArrayList<Task> listTask = secondManager.getTasks();
        ArrayList<Epic> listEpic = secondManager.getEpics();
        ArrayList<SubTask> listSubtask = secondManager.getSubtasks();
        assertEquals(2, listTask.size(), "Разное количество задач");
        assertEquals(1, listEpic.size(), "Разное количество Эпиков");
        assertEquals(2, listSubtask.size(), "Разное количество Подзадач");
    }

    @Test
    void loadSomeTasks() {
        Path path = Paths.get("autosave4.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(path);

        Task task1 = new Task("Сходить в магазин", "купить много продуктов", TaskStatus.NEW);
        Task task2 = new Task("Помыть кота", "приготовить набор первой помощи", TaskStatus.NEW);
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");
        int epic1Id = manager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску напитки посмотреть Звездные войны Изгой",
                epic1Id,
                TaskStatus.NEW);
        SubTask subtask2 = new SubTask("Начать смотреть сериал Андор",
                "Приготовить закуску кофе выключить телефон нечать смотреть",
                epic1Id,
                TaskStatus.NEW);
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        FileBackedTaskManager secondManager = FileBackedTaskManager.loadFromFile(path.toFile());

        Task savedTask1 = secondManager.getTask(task1.getId());
        Task savedTask2 = secondManager.getTask(task2.getId());
        Epic savedEpic1 = secondManager.getEpic(epic1.getId());
        SubTask savedSubtask1 = secondManager.getSubtask(subtask1.getId());
        SubTask savedSubtask2 = secondManager.getSubtask(subtask2.getId());

        //Сравнение полей у созданной и восстановленной задачи
        assertEquals(task1.getTitle(), savedTask1.getTitle(), "Заголовки не совпадают");
        assertEquals(task1.getDescription(), savedTask1.getDescription(), "Описания не совпадают");
        assertEquals(task1.getStatus(), savedTask1.getStatus(), "Статусы не совпадают");
        assertEquals(task1.getDuration(), savedTask1.getDuration(), "Продолжительность задачи не совпадает");
        assertEquals(task1.getStartTime(), savedTask1.getStartTime(), "Время начала задачи не совпадает");

        assertEquals(task2.getTitle(), savedTask2.getTitle(), "Заголовки не совпадают");
        assertEquals(task2.getDescription(), savedTask2.getDescription(), "Описания не совпадают");
        assertEquals(task2.getStatus(), savedTask2.getStatus(), "Статусы не совпадают");
        assertEquals(task2.getDuration(), savedTask2.getDuration(), "Продолжительность задачи не совпадает");
        assertEquals(task2.getStartTime(), savedTask2.getStartTime(), "Время начала задачи не совпадает");

        assertEquals(epic1.getTitle(), savedEpic1.getTitle(), "Заголовки не совпадают");
        assertEquals(epic1.getDescription(), savedEpic1.getDescription(), "Описания не совпадают");

        assertEquals(subtask1.getTitle(), savedSubtask1.getTitle(), "Заголовки не совпадают");
        assertEquals(subtask1.getDescription(), savedSubtask1.getDescription(), "Описания не совпадают");
        assertEquals(subtask1.getStatus(), savedSubtask1.getStatus(), "Статусы не совпадают");
        assertEquals(subtask1.getEpicId(), savedSubtask1.getEpicId(), "Id эпика не совпадают");

        assertEquals(subtask2.getTitle(), savedSubtask2.getTitle(), "Заголовки не совпадают");
        assertEquals(subtask2.getDescription(), savedSubtask2.getDescription(), "Описания не совпадают");
        assertEquals(subtask2.getStatus(), savedSubtask2.getStatus(), "Статусы не совпадают");
        assertEquals(subtask2.getEpicId(), savedSubtask2.getEpicId(), "Id эпика не совпадают");
    }


}