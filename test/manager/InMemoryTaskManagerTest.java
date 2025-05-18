package manager;

import static model.enums.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.TaskStatus;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        Path path = Paths.get("autosave7.csv");
        super.manager = new FileBackedTaskManager(path);
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void intersectionCheckTest() {
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");
        int epic1Id = taskManager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску напитки посмотреть Звездные войны Изгой",
                epic1Id,
                TaskStatus.NEW);
        SubTask subtask2 = new SubTask("Начать смотреть сериал Андор",
                "Приготовить закуску кофе выключить телефон нечать смотреть",
                epic1Id,
                TaskStatus.NEW);

        LocalDateTime izgoiZadacha = LocalDateTime.of(2025, 5, 9, 23, 0);
        Duration izgoyDuration = Duration.ofMinutes(180);
        subtask1.setStartTime(izgoiZadacha);
        subtask1.setDuration(izgoyDuration);

        LocalDateTime andorZadacha = LocalDateTime.of(2025, 5, 10, 20, 0);
        Duration andorDuration = Duration.ofMinutes(200);
        subtask2.setStartTime(andorZadacha);
        subtask1.setDuration(andorDuration);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        TreeSet set = taskManager.getPrioritizedTasks();
        assertEquals(2, set.size(), "Должно быть две задачи");

        SubTask subtask3 = new SubTask("Начать смотреть сериал Светлячок",
                "Приготовить закуску кофе нечать смотреть Светлячок",
                epic1Id,
                TaskStatus.NEW);
        LocalDateTime fireFlyZadacha = LocalDateTime.of(2025, 5, 9, 23, 50);
        Duration fireFlyDuration = Duration.ofMinutes(120);
        subtask3.setStartTime(fireFlyZadacha);
        subtask3.setDuration(fireFlyDuration);
        taskManager.addNewSubtask(subtask3);

        set = taskManager.getPrioritizedTasks();
        assertEquals(2, set.size(), "Должно быть две задачи. Третья не должна добавиться");
    }

    @Test
    void equalInheritorsOfTask() {
        Epic epic1 = new Epic("Эпичный эпик", "Описание 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Сходить в магазин", "купить много продуктов", epic1Id, TaskStatus.NEW);
        SubTask subtask2 = new SubTask("Помыть кота", "приготовить набор первой помощи", epic1Id, TaskStatus.NEW);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        Epic savedEpic = taskManager.getEpic(epic1Id);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        ArrayList<SubTask> listSubtasks = taskManager.getSubtasks();
        assertNotNull(listSubtasks, "Список сохраненных подзадач не найден");
        assertEquals(2, listSubtasks.size(), "Неправильное число задач");
        SubTask savedSubtask1 = taskManager.getSubtask(subtask1.getId());
        SubTask savedSubtask2 = taskManager.getSubtask(subtask2.getId());
        assertEquals(subtask1, savedSubtask1, "Подзадачи не равны");
        assertEquals(subtask2, savedSubtask2, "Подзадачи не равны");
    }

    @Test
    void epicIsSubtaskForItself() {
        Epic epic1 = new Epic("Неправильный эпик", "Описание 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        boolean result = taskManager.addSubtask(epic1, epic1Id);

        assertEquals(false, result, "Эпик не должен добавляться как собственная подзадача.");
    }

    @Test
    void subtaskIsEpicForItself() {
        Epic epic2 = new Epic("Второй эпик", "Описание 2");
        int epic2Id = taskManager.addNewEpic(epic2);
        System.out.println(taskManager.getEpics());
        SubTask subtask1 = new SubTask("Правильная подзадача",
                "Правильная подзадача",
                epic2Id,
                TaskStatus.NEW);
        int idSubtask1 = taskManager.addNewSubtask(subtask1);
        SubTask subtask2 = new SubTask("Неправильная подзадача",
                "Сама себе эпик",
                idSubtask1,
                TaskStatus.NEW);
        int result = taskManager.addNewSubtask(subtask2);

        assertEquals(-3, result, "Подзадача не может быть эпиком для себя");
    }

    @Test
    void subtaskWithInvalidEpicId() {
        SubTask subtask = new SubTask("Подзадача", "Описание", 999, TaskStatus.NEW);
        int result = taskManager.addNewSubtask(subtask);

        assertEquals(-1, result, "Подзадача с несуществующим эпиком не должна добавляться.");
    }

    @Test
    void conflictBetweenIdsTest() {
        Task task1 = new Task("Первая", "Описание 1", TaskStatus.NEW);
        int taskId1 = taskManager.addNewTask(task1);

        Task generateIdTask = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        int taskId2 = taskManager.addNewTask(generateIdTask);

        assertEquals("Первая", taskManager.getTask(taskId1).getTitle(),
                "Название первой задачи не совпадает.");
        assertEquals("Вторая", taskManager.getTask(taskId2).getTitle(),
                "Название второй задачи не совпадает.");
    }

    @Test
    void addAndGetTasks() {
        Task task1 = new Task("Первая", "Описание 1", TaskStatus.NEW);
        final int taskId = taskManager.addNewTask(task1);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1.getTitle(), savedTask.getTitle(), "Названия задач не совпадают.");
        assertEquals(task1.getDescription(), savedTask.getDescription(), "Описания задач не совпадают.");
        assertEquals(task1.getStatus(), savedTask.getStatus(), "Статусы задач не совпадают.");

        Task task2 = new Task("Вторая", "Описание 2", TaskStatus.DONE);
        taskManager.addNewTask(task2);

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Должны быть две задачи.");
        assertEquals(task1.getTitle(), tasks.getFirst().getTitle(), "Названия первой задачи не совпадают.");
    }

    @Test
    void addAndGetEpics() {
        Epic epic1 = new Epic("Первый эпик", "Описание 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Epic savedEpic = taskManager.getEpic(epic1Id);
        Epic epic2 = new Epic("Второй эпик", "Описание 2");
        taskManager.addNewEpic(epic2);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1.getTitle(), savedEpic.getTitle(), "Названия эпиков не совпадают.");
        assertEquals(epic1.getDescription(), savedEpic.getDescription(), "Описания эпиков не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Должны быть два эпика.");
        assertEquals(epic1.getTitle(), epics.get(0).getTitle(), "Названия первого эпика не совпадают.");
    }

    @Test
    void addAndGetSubtasks() {
        Epic epic1 = new Epic("Первый эпик", "Описание 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Epic savedEpic = taskManager.getEpic(epic1Id);
        Epic epic2 = new Epic("Второй эпик", "Описание 2");
        taskManager.addNewEpic(epic2);

        SubTask subtask1 = new SubTask(
                "Первая подзадача",
                "Описание 1",
                taskManager.getEpics().get(0).getId(),
                TaskStatus.NEW);
        int subtasksId = taskManager.addNewSubtask(subtask1);
        SubTask savedSubtask = taskManager.getSubtask(subtasksId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask1, savedSubtask, "Позадачи не совпадают.");

        SubTask subtask2 = new SubTask(
                "Вторая подзадача",
                "Описание2",
                taskManager.getEpics().get(0).getId(),
                TaskStatus.NEW);
        taskManager.addNewSubtask(subtask2);
        SubTask subtask3 = new SubTask(
                "Третья подзадача",
                "Описание 3",
                taskManager.getEpics().get(1).getId(),
                TaskStatus.NEW);
        taskManager.addNewSubtask(subtask3);

        final List<SubTask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Должны быть три подзадачи.");
        assertEquals(subtask1.getTitle(), subtasks.get(0).getTitle(),
                "Названия первой саб-задачи не совпадают.");
    }
}