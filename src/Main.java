import model.enums.TaskStatus;
import manager.InMemoryTaskManager;
import model.Task;
import model.Epic;
import model.SubTask;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Сходить в магазин", "купить много продуктов");
        Task task2 = new Task("Помыть кота", "приготовить набор первой помощи");
        task1.setStatus(TaskStatus.NEW);
        task2.setStatus(TaskStatus.NEW);
        manager.add(task1);
        manager.add(task2);

        Epic epic1 = new Epic("Переезд", "Переезд на новую квартиру");
        epic1 = manager.add(epic1);
        SubTask subtask1 = new SubTask("Собрать вещи", "Целый день собирать вещи", epic1.getId());
        SubTask subtask2 = new SubTask("Погладить кота", "Целый день гладить кота", epic1.getId());
        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.NEW);
        manager.add(subtask1);
        manager.add(subtask2);

        Epic epic2 = new Epic("Погрузиться в Звездные войны","Посмотреть фильмы и  сериалы Звездных войн");
        epic2 = manager.add(epic2);
        SubTask subtask3 = new SubTask("Посмотреть Звездные войны", "Посмотреть Звездные войны Изгой",
                epic2.getId());
        subtask3.setStatus(TaskStatus.NEW);
        manager.add(subtask3);

        System.out.println(manager.allTasks());
        System.out.println(manager.allEpics());
        System.out.println(manager.allSubtasks());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1.getId(), task1);
        manager.updateSubtask(subtask1.getId(), subtask1);
        epic1 = manager.getEpicById(subtask1.getEpicId());
        System.out.println(task1);
        System.out.println(subtask1);
        System.out.println(epic1);

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
