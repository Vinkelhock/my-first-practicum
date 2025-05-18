package model;

import model.enums.TaskStatus;
import model.enums.TaskTypes;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private final String title;
    private final String description;
    private int id;
    private TaskStatus status;
    private Duration duration = Duration.ofMinutes(0);
    private LocalDateTime startTime = LocalDateTime.of(1900, 1, 1, 0, 0);

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description, int id, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        if (this.startTime == null || this.duration == null) return null;
        else return this.startTime.plus(this.duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s",
                getId(), TaskTypes.TASK, getTitle(), getStatus(), getDescription(),
                this.duration.toMinutes(), this.startTime.toString());
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

}
