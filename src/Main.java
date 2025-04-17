import model.enums.TaskStatus;
import manager.InMemoryTaskManager;
import model.Task;
import model.Epic;
import model.SubTask;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Сходить в магазин", "купить много продуктов", TaskStatus.NEW);
        Task task2 = new Task("Помыть кота", "приготовить набор первой помощи", TaskStatus.NEW);
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        Epic epic1 = new Epic("Переезд", "Переезд на новую квартиру");
        int epic1Id = manager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Собрать вещи", "Целый день собирать вещи", epic1Id, TaskStatus.NEW);
        SubTask subtask2 = new SubTask("Погладить кота", "Целый день гладить кота", epic1Id, TaskStatus.NEW);
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

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

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1.getId(), task1);
        manager.updateSubtask(subtask1.getId(), subtask1);
        epic1 = manager.getEpic(subtask1.getEpicId());
        System.out.println(task1);
        System.out.println(subtask1);
        System.out.println(epic1);

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
