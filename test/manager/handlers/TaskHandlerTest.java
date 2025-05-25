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

public class TaskHandlerTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    public TaskHandlerTest() throws IOException {
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
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW);

        Duration duration = Duration.ofMinutes(5);
        LocalDateTime time = LocalDateTime.now();
        task.setDuration(duration);
        task.setStartTime(time);

        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = this.manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        //Создаем задачи
        Task task1 = new Task("Сходить в магазин", "купить много продуктов", TaskStatus.NEW);
        Task task2 = new Task("Помыть кота", "приготовить набор первой помощи", TaskStatus.NEW);
        Task task3 = new Task("Попить кофе", "Во время кофе посмотреть Мандалорца", TaskStatus.NEW);

        //Отправляем задачу task1
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2025, 5, 24, 15, 0));
        String taskJson = gson.toJson(task1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        //Отправляем задачу task2
        task2.setDuration(Duration.ofMinutes(25));
        task2.setStartTime(LocalDateTime.of(2025, 5, 24, 20, 30));
        taskJson = gson.toJson(task2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        //Отправляем задачу task3
        task3.setDuration(Duration.ofMinutes(45));
        task3.setStartTime(LocalDateTime.of(2025, 5, 24, 11, 30));
        taskJson = gson.toJson(task3);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        //Отправляем запрос на список задач
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        client.close();
        assertEquals(200, response.statusCode());

        assertNotNull(tasksList, "Задачи не возвращаются");
        assertEquals(3, tasksList.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {

        Task task1 = new Task("Сходить в магазин", "купить много продуктов", TaskStatus.NEW);
        Task task2 = new Task("Помыть кота", "приготовить набор первой помощи", TaskStatus.NEW);
        Task task3 = new Task("Попить кофе", "Во время кофе посмотреть Мандалорца", TaskStatus.NEW);

        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2025, 5, 24, 15, 0));
        task2.setDuration(Duration.ofMinutes(25));
        task2.setStartTime(LocalDateTime.of(2025, 5, 24, 20, 30));
        task3.setDuration(Duration.ofMinutes(45));
        task3.setStartTime(LocalDateTime.of(2025, 5, 24, 11, 30));
        this.manager.addNewTask(task1);
        this.manager.addNewTask(task2);
        this.manager.addNewTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");

        //Получаем таску по id
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task savedTask = gson.fromJson(response.body(), Task.class);
        client.close();
        assertEquals(200, response.statusCode());
        System.out.println(savedTask);

        assertEquals(task2.getTitle(), savedTask.getTitle());
        assertEquals(task2.getStatus(), savedTask.getStatus());
        assertEquals(task2.getDescription(), savedTask.getDescription());
        assertEquals(task2.getDuration(), savedTask.getDuration());
        assertEquals(task2.getStartTime(), savedTask.getStartTime());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task3 = new Task("Попить кофе", "Во время кофе посмотреть Мандалорца", TaskStatus.NEW);

        //Отправляем задачу task3
        task3.setDuration(Duration.ofMinutes(45));
        task3.setStartTime(LocalDateTime.of(2025, 5, 24, 11, 30));
        URI url = URI.create("http://localhost:8080/tasks/");
        String taskJson = gson.toJson(task3);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        //Получаем id отправленной задачи
        int id = Integer.parseInt(gson.fromJson(response.body(), String.class));
        System.out.println(id);

        //Изменяем статус задачи и отправляем
        Task newTask3 = new Task("Попить кофе", "Во время кофе посмотреть Мандалорца", id, TaskStatus.IN_PROGRESS);
        newTask3.setDuration(Duration.ofMinutes(45));
        newTask3.setStartTime(LocalDateTime.of(2025, 5, 24, 11, 30));
        url = URI.create("http://localhost:8080/tasks/" + newTask3.getId());
        taskJson = gson.toJson(newTask3);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = this.manager.getTasks();
        assertEquals(1, tasksFromManager.size());
        assertEquals(newTask3.getStatus(), tasksFromManager.getFirst().getStatus());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        //Создаем задачи и помещаем их в менеджер
        Task task1 = new Task("Сходить в магазин", "купить много продуктов", TaskStatus.NEW);
        Task task2 = new Task("Помыть кота", "приготовить набор первой помощи", TaskStatus.NEW);
        Task task3 = new Task("Попить кофе", "Во время кофе посмотреть Мандалорца", TaskStatus.NEW);

        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2025, 5, 24, 15, 0));
        task2.setDuration(Duration.ofMinutes(25));
        task2.setStartTime(LocalDateTime.of(2025, 5, 24, 20, 30));
        task3.setDuration(Duration.ofMinutes(45));
        task3.setStartTime(LocalDateTime.of(2025, 5, 24, 11, 30));
        this.manager.addNewTask(task1);
        this.manager.addNewTask(task2);
        this.manager.addNewTask(task3);

        //Отправляем запрос на удаление
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = this.manager.getTasks();

        assertEquals(2, tasksFromManager.size());
    }

}
