package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.TaskStatus;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addTest() {
        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Первая", "Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Первый эпик", "Описание 1");
        SubTask subtask = new SubTask(
                "Первая подзадача", "Описание1", epic.getId(), TaskStatus.NEW);

        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "Количество элементов не совпадает.");
        assertEquals(task, history.get(0), "Первым элементом в истории должна быть Task.");
        assertEquals(epic, history.get(1), "Вторым элементом в истории должен быть Epic.");
        assertEquals(subtask, history.get(2), "Третьим элементом в истории должна быть Subtask.");
    }

    @Test
    void removeTest() {
        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Первая", "Описание 1", TaskStatus.NEW);
        Epic epic = new Epic("Первый эпик", "Описание 1");
        SubTask subtask = new SubTask(
                "Первая подзадача", "Описание1", epic.getId(), TaskStatus.NEW);

        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "Количество элементов не совпадает.");

        historyManager.remove(1);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Количество элементов не совпадает.");
        historyManager.remove(0);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "Количество элементов не совпадает.");
    }
}