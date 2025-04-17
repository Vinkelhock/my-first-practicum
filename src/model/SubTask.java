package model;

import model.enums.TaskStatus;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description, int epicId, TaskStatus status) {
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
        return "Subtask{\n" +
                " title='" + getTitle() + "',\n" +
                " description='" + getDescription() + "',\n" +
                " id=" + getId() + ",\n" +
                " status=" + getStatus() + ",\n" +
                " epicId=" + epicId + "\n" +
                "}";
    }
}
