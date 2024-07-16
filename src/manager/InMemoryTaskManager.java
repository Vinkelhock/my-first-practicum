package manager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import model.enums.TaskStatus;
import model.Task;
import model.Epic;
import model.SubTask;

public class InMemoryTaskManager implements TaskManager {

    private int nextId = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Integer generateId() {
        return nextId++;
    }

    //Получение списка всех задач
    @Override
    public ArrayList<Task> allTasks() {
        return new ArrayList<>(tasks.values());
    }

    //Получение списка всех эпиков
    @Override
    public ArrayList<Epic> allEpics() {
        return new ArrayList<>(epics.values());
    }

    //Получение списка всех подзадач эпиков
    @Override
    public ArrayList<SubTask> allSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    //Удаление всех задач
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    //Удаление всех эпиков
    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    //Удаление всех подзадач
    @Override
    public void removeAllSubTasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            checkStatus(epic);
        }
    }

    //Получение задачи по идентификатору.
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    //Получение эпика по идентификатору.
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    //Получение по подзадачи эпика по идентификатору.
    @Override
    public SubTask getSubtaskById(int id) {
        SubTask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    //Создание задачи Task
    @Override
    public void add(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    //Создание задачи Epic
    @Override
    public Epic add(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        Epic newEpic = checkStatus(epic);
        return newEpic;
    }

    //Создание подзадачи
    @Override
    public void add(SubTask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getListOfSubtask().add(subtask.getId());
        checkStatus(epic);
    }

    //Обновление задачи
    @Override
    public void updateTask(int id, Task task) {
        tasks.put(id, task);
    }

    //Обновление эпика
    @Override
    public void updateEpic(int id, Epic epic) {
        epics.put(id, epic);
    }

    //Обновление подзадачи
    @Override
    public void updateSubtask(int id, SubTask subtask) {
        subtasks.put(id, subtask);
        Epic newEpic = checkStatus(epics.get(subtask.getEpicId()));
    }

    //Удаление по идентификатору задачи Task
    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    //Удаление по идентификатору Эпика
    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        for (Integer idSubtask : epic.getListOfSubtask()) {
            subtasks.remove(idSubtask);
        }
    }

    //Удаление по идентификатору подзадачи
    @Override
    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        Epic epic = epics.get(epicId);
        ArrayList<Integer> listIdOfSubtask = epic.getListOfSubtask();
        listIdOfSubtask.remove(id);
        checkStatus(epic);
    }

    //Получение списка всех подзадач определенного эпика
    @Override
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
