package model;

import model.enums.TaskStatus;
import model.enums.TaskTypes;

public class SubTask extends Task {
    private int epicId;

    public SubTask( String title, String description, int epicId, TaskStatus status) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int id) {
        this.epicId = id;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d",
                getId(), TaskTypes.SUBTASK, getTitle(),
                getStatus(), getDescription(), getEpicId());
    }
}
