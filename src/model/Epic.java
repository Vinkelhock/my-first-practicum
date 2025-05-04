package model;

import java.util.ArrayList;

import model.enums.TaskStatus;
import model.enums.TaskTypes;

public class Epic extends Task {
    private ArrayList<Integer> listOfSubtask;

    public Epic(String title, String description) {
        super(title, description);
        this.listOfSubtask = new ArrayList<>();
    }

    public Epic(String title, String description, int id, TaskStatus status, ArrayList<Integer> listOfSubtask) {
        super(title, description, id, status);
        this.listOfSubtask = listOfSubtask;
    }

    public void addSubtask(int id) {
        listOfSubtask.add(id);
    }

    public ArrayList<Integer> getListOfSubtask() {
        return listOfSubtask;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s",
                getId(), TaskTypes.EPIC, getTitle(), getStatus(), getDescription());
    }

}
