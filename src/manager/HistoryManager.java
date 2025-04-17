package manager;

import model.Task;
import model.Epic;
import model.SubTask;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
