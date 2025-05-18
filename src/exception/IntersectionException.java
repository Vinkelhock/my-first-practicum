package exception;

import model.Task;

public class IntersectionException extends RuntimeException {
    private Task task;

    public IntersectionException(Task task) {
        this.task = task;
    }

    public String getDetailMessage() {
        return String.format("Задача с номером %d %s пересекается по времени выполнения с другой задачей",
                task.getId(), task.getTitle());
    }
}
