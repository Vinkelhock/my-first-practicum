package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    List<Task> getHistory();

    //Получение списка всех задач
    ArrayList<Task> getTasks();

    //Получение списка всех эпиков
    ArrayList<Epic> getEpics();

    //Получение списка всех подзадач эпиков
    ArrayList<SubTask> getSubtasks();

    //Удаление всех задач
    void removeAllTasks();

    //Удаление всех эпиков
    void removeAllEpics();

    //Удаление всех подзадач
    void removeAllSubTasks();

    //Получение задачи по идентификатору.
    Task getTask(int id);

    //Получение эпика по идентификатору.
    Epic getEpic(int id);

    //Получение по подзадачи эпика по идентификатору.
    SubTask getSubtask(int id);

    //Создание задачи Task
    int addNewTask(Task task);

    //Создание задачи Epic
    int addNewEpic(Epic epic);

    //Создание подзадачи
    int addNewSubtask(SubTask subtask);

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
    List<String> getEpicSubtasks(int EpicId);

    boolean addSubtask(Epic epic1, int epic1Id);

    TreeSet getPrioritizedTasks();
}
