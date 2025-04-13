package manager;

import static model.enums.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;
import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.TaskStatus;
import manager.Managers;
import manager.TaskManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Epic epic1;
    private int epic1Id;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        this.epic1 = new Epic("Первый эпик", "Описание 1");
        this.epic1Id = taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Второй эпик", "Описание 2");
        taskManager.addNewEpic(epic2);
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
}