package model;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
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
