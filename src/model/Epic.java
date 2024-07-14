package model;

import java.util.ArrayList;

import enums.TaskStatus;

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
        return "Epic{\n" +
                " title='" + getTitle() + "',\n" +
                " description='" + getDescription() + "',\n" +
                " id=" + getId() + ",\n" +
                " status=" + getStatus() + ",\n" +
                " listOfSubtask=" + listOfSubtask + ",\n" +
                "}";
    }

}
