package manager;

import java.util.HashMap;
import java.util.ArrayList;

import model.enums.TaskStatus;
import model.Task;
import model.Epic;
import model.SubTask;

public class TaskManager {

    private int nextId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();

    private Integer generateId() {
        return nextId++;
    }

    //Получение списка всех задач
    public ArrayList<Task> allTasks() {
        return new ArrayList<>(tasks.values());
    }

    //Получение списка всех эпиков
    public ArrayList<Epic> allEpics() {
        return new ArrayList<>(epics.values());
    }

    //Получение списка всех подзадач эпиков
    public ArrayList<SubTask> allSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    //Удаление всех задач
    public void removeAllTasks() {
        tasks.clear();
    }

    //Удаление всех эпиков
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    //Удаление всех подзадач
    public void removeAllSubTasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            checkStatus(epic);
        }
    }

    //Получение задачи по идентификатору.
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    //Получение эпика по идентификатору.
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    //Получение по подзадачи эпика по идентификатору.
    public SubTask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    //Создание задачи Task
    public void add(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    //Создание задачи Epic
    public Epic add(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        Epic newEpic = checkStatus(epic);
        return newEpic;
    }

    //Создание подзадачи
    public void add(SubTask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getListOfSubtask().add(subtask.getId());
        checkStatus(epic);
    }

    //Обновление задачи
    public void updateTask(int id, Task task) {
        tasks.put(id, task);
    }

    //Обновление эпика
    public void updateEpic(int id, Epic epic) {
        epics.put(id, epic);
    }

    //Обновление подзадачи
    public void updateSubtask(int id, SubTask subtask) {
        subtasks.put(id, subtask);
        Epic newEpic = checkStatus(epics.get(subtask.getEpicId()));
    }

    //Удаление по идентификатору задачи Task
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    //Удаление по идентификатору Эпика
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        for (Integer idSubtask : epic.getListOfSubtask()) {
            subtasks.remove(idSubtask);
        }
    }

    //Удаление по идентификатору подзадачи
    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        Epic epic = epics.get(epicId);
        ArrayList<Integer> listIdOfSubtask = epic.getListOfSubtask();
        listIdOfSubtask.remove(id);
        checkStatus(epic);
    }

    //Получение списка всех подзадач определенного эпика
    public ArrayList<String> subtasksByEpic(int id) {
        ArrayList<String> listOfSubtasks = new ArrayList<>();
        Epic epic = epics.get(id);
        for (Integer idOfSubtask : epic.getListOfSubtask()) {
            SubTask subtask = subtasks.get(idOfSubtask);
            listOfSubtasks.add(subtask.getTitle());
        }
        return listOfSubtasks;
    }

    private Epic checkStatus(Epic epic) {
        boolean checkNew = true;
        boolean checkDone = true;
        for (Integer id : epic.getListOfSubtask()) {
            if (subtasks.get(id).getStatus() != TaskStatus.NEW) {
                checkNew = false;
                break;
            }
        }
        for (Integer id : epic.getListOfSubtask()) {
            if (subtasks.get(id).getStatus() != TaskStatus.DONE) {
                checkDone = false;
                break;
            }
        }

        if (epic.getListOfSubtask().isEmpty() || checkNew) {
            if (epic.getStatus() != (TaskStatus.NEW)) {
                Epic newEpic = new Epic(epic.getTitle(), epic.getDescription(), epic.getId(), TaskStatus.NEW,
                        epic.getListOfSubtask());
                updateEpic(epic.getId(), newEpic);
                return newEpic;
            }
        } else if (checkDone) {
            if (epic.getStatus() != (TaskStatus.DONE)) {
                Epic newEpic = new Epic(epic.getTitle(), epic.getDescription(), epic.getId(), TaskStatus.DONE,
                        epic.getListOfSubtask());
                updateEpic(epic.getId(), newEpic);
                return newEpic;
            }
        } else {
            if (epic.getStatus() != (TaskStatus.IN_PROGRESS)) {
                Epic newEpic = new Epic(epic.getTitle(), epic.getDescription(), epic.getId(), TaskStatus.IN_PROGRESS,
                        epic.getListOfSubtask());
                updateEpic(epic.getId(), newEpic);
                return newEpic;
            }
        }
        return epic;
    }
}
