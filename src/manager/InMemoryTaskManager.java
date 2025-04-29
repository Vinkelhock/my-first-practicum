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

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected Integer generateId() {
        return nextId++;
    }

    //Получение списка всех задач
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    //Получение списка всех эпиков
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //Получение списка всех подзадач эпиков
    @Override
    public ArrayList<SubTask> getSubtasks() {
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
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    //Получение эпика по идентификатору.
    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    //Получение подзадачи эпика по идентификатору.
    @Override
    public SubTask getSubtask(int id) {
        SubTask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    //Создание задачи Task
    @Override
    public int addNewTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(task.getId(), task);
        return id;
    }

    //Создание задачи Epic
    @Override
    public int addNewEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(epic.getId(), epic);
        Epic newEpic = checkStatus(epic);
        return id;
    }

    //Создание подзадачи
    @Override
    public int addNewSubtask(SubTask subtask) {
        int id = generateId();
        subtask.setId(id);

        //Если EpicId подзадачи не содержится в хешмапе эпиков, но есть в хешмапе подзадач
        if (!epics.containsKey(subtask.getEpicId())) {
            if (subtasks.containsKey(subtask.getEpicId())) {
                System.out.println("Подзадача не может быть эпиком");
                return -3;
            }
        }

        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            System.out.println("Эпик с id " + subtask.getEpicId() + " не найден.");
            return -1;
        }

        subtasks.put(subtask.getId(), subtask);

        //Добавляем подзадачу к списку подзадач эпика, и попутно проверяем что это не эпик
        if (!addSubtask(epic, subtask.getId())) {
            return -2;
        }

        checkStatus(epic);
        return id;
    }

    // проверить что подзадача не эпик
    public boolean addSubtask(Epic epic, int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("В список подзадач эпика можно добавить только подзадачу");
            return false;
        } else {
            epic.addSubtask(id);
            return true;
        }
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
        historyManager.remove(id);
        tasks.remove(id);
    }

    //Удаление по идентификатору Эпика
    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        for (Integer idSubtask : epic.getListOfSubtask()) {
            subtasks.remove(idSubtask);
            historyManager.remove(idSubtask);
        }
    }

    //Удаление по идентификатору подзадачи
    @Override
    public void removeSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        subtasks.remove(id);
        historyManager.remove(id);
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

    protected Epic checkStatus(Epic epic) {
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
