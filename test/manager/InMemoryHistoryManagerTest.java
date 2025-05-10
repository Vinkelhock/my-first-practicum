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
    void emptyHistoryTest() {
        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager historyManager = Managers.getDefaultHistory();
        ArrayList<Task> history = historyManager.getHistory();

        assertEquals(0, history.size(), "История должна быть пустая");
    }

    @Test
    void doublingTest() {
        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Первая", "Описание 1", TaskStatus.NEW);

        taskManager.addNewTask(task);

        historyManager.add(task);
        historyManager.add(task);

        ArrayList<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
    }

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

        Epic epic = new Epic("Первый эпик", "Описание 1");
        int epicId = taskManager.addNewEpic(epic);
        SubTask subtask1 = new SubTask(
                "Первая подзадача", "Описание1", epicId, TaskStatus.NEW);
        SubTask subtask2 = new SubTask(
                "Вторая подзадача", "Описание2", epicId, TaskStatus.NEW);
        SubTask subtask3 = new SubTask(
                "Третья подзадача", "Описание3", epicId, TaskStatus.NEW);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        Task task = new Task("Первая", "Описание 1", TaskStatus.NEW);
        taskManager.addNewTask(task);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(subtask3);

        ArrayList<Task> history = historyManager.getHistory();
        System.out.println(history);
        assertNotNull(history, "История не пустая.");
        assertEquals(5, history.size(), "Количество элементов должно быть 5.");
        //Удаление начального элемента
        historyManager.remove(history.getFirst().getId());
        history = historyManager.getHistory();
        System.out.println(history);
        assertEquals(4, history.size(), "Количество элементов должно быть 4.");
        //Удаление среднего элемента
        historyManager.remove(1);
        history = historyManager.getHistory();
        System.out.println(history);
        assertEquals(3, history.size(), "Количество элементов должно быть 3.");
        //Удаление последнего элемента
        historyManager.remove(history.getLast().getId());
        history = historyManager.getHistory();
        System.out.println(history);
        assertEquals(2, history.size(), "Количество элементов должно быть 2.");
    }
}