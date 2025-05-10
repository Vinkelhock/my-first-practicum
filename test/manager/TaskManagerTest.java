package manager;


import model.Epic;
import model.SubTask;
import model.Task;
import model.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    void printTasks() {
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
    }

    @Test
    void epicForSubTask() {
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

        assertEquals(epic1.getId(), subtask1.getEpicId(), "id эпика не совпадает с epicId подзадачи");
        assertEquals(epic1.getId(), subtask2.getEpicId(), "id эпика не совпадает с epicId подзадачи");

    }

    @Test
    void checkEpicStatus() {
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

        Epic savedEpic = manager.getEpic(epic1Id);
        assertEquals(savedEpic.getStatus(), TaskStatus.NEW, " Должен быть статус NEW");

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1.getId(), subtask1);
        manager.updateSubtask(subtask2.getId(), subtask2);
        savedEpic = manager.getEpic(epic1Id);
        assertEquals(savedEpic.getStatus(), TaskStatus.DONE, " Должен быть статус DONE");

        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1.getId(), subtask1);
        manager.updateSubtask(subtask2.getId(), subtask2);
        savedEpic = manager.getEpic(epic1Id);
        assertEquals(savedEpic.getStatus(), TaskStatus.IN_PROGRESS, " Должен быть статус IN_PROGRESS");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1.getId(), subtask1);
        manager.updateSubtask(subtask2.getId(), subtask2);
        savedEpic = manager.getEpic(epic1Id);
        assertEquals(savedEpic.getStatus(), TaskStatus.IN_PROGRESS, " Должен быть статус IN_PROGRESS");
    }
}
