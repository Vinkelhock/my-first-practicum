package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    //Получение списка всех задач
    ArrayList<Task> allTasks();

    //Получение списка всех эпиков
    ArrayList<Epic> allEpics();

    //Получение списка всех подзадач эпиков
    ArrayList<SubTask> allSubtasks();

    //Удаление всех задач
    void removeAllTasks();

    //Удаление всех эпиков
    void removeAllEpics();

    //Удаление всех подзадач
    void removeAllSubTasks();

    //Получение задачи по идентификатору.
    Task getTaskById(int id);

    //Получение эпика по идентификатору.
    Epic getEpicById(int id);

    //Получение по подзадачи эпика по идентификатору.
    SubTask getSubtaskById(int id);

    //Создание задачи Task
    void add(Task task);

    //Создание задачи Epic
    Epic add(Epic epic);

    //Создание подзадачи
    void add(SubTask subtask);

    //Обновление задачи
    void updateTask(int id, Task task);

    //Обновление эпика
    void updateEpic(int id, Epic epic);

    //Обновление подзадачи
    void updateSubtask(int id, SubTask subtask);

    //Удаление по идентификатору задачи Task
    void removeTaskById(int id);

    //Удаление по идентификатору Эпика
    void removeEpicById(int id);

    //Удаление по идентификатору подзадачи
    void removeSubtaskById(int id);

    //Получение списка всех подзадач определенного эпика
    ArrayList<String> subtasksByEpic(int id);
}
