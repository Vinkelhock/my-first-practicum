package manager.handlers;

import com.google.gson.reflect.TypeToken;
import manager.TaskManager;
import manager.Managers;
import model.Task;
import model.enums.TaskStatus;

import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import manager.HttpTaskServer;
import com.google.gson.Gson;

public class PrioritizedHandlerTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    public PrioritizedHandlerTest() throws IOException {
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {
        // здесь ничего не нужно реализовывать
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void getPrioritizedTasksTest() throws IOException, InterruptedException {
        //Создаем задачи
        Task task1 = new Task("Сходить в магазин", "купить много продуктов", TaskStatus.NEW);
        Task task2 = new Task("Помыть кота", "приготовить набор первой помощи", TaskStatus.NEW);
        Task task3 = new Task("Попить кофе", "Во время кофе посмотреть Мандалорца", TaskStatus.NEW);


        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2025, 5, 24, 15, 0));
        task2.setDuration(Duration.ofMinutes(25));
        task2.setStartTime(LocalDateTime.of(2025, 5, 24, 20, 30));
        task3.setDuration(Duration.ofMinutes(45));
        task3.setStartTime(LocalDateTime.of(2025, 5, 24, 11, 30));
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);


        //Отправляем запрос на список приоритетных задач
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //TreeSet<Task> tasksList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        List<Task> tasksList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        client.close();
        assertEquals(200, response.statusCode());

        assertNotNull(tasksList, "Задачи не возвращаются");
        assertEquals(3, tasksList.size(), "Некорректное количество задач");
    }
}
