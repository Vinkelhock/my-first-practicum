package manager.handlers;

import com.google.gson.reflect.TypeToken;
import manager.TaskManager;
import manager.Managers;
import model.Epic;
import model.SubTask;
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

public class SubtaskHandlerTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    public SubtaskHandlerTest() throws IOException {
    }

    static class SubTaskListTypeToken extends TypeToken<List<SubTask>> {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём подзадачу
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");
        int epic1Id = manager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску напитки посмотреть Звездные войны Изгой",
                epic1Id,
                TaskStatus.NEW);

        Duration duration = Duration.ofMinutes(120);
        LocalDateTime time = LocalDateTime.of(2025, 5, 24, 22, 0);
        subtask1.setDuration(duration);
        subtask1.setStartTime(time);

        // конвертируем её в JSON
        String taskJson = gson.toJson(subtask1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
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
        List<SubTask> tasksFromManager = this.manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Посмотреть Звездные войны Изгой", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        //Создаем задачи
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");
        int epic1Id = manager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску напитки посмотреть Звездные войны Изгой",
                epic1Id,
                TaskStatus.NEW);
        SubTask subtask2 = new SubTask("Начать смотреть сериал Андор",
                "Приготовить закуску кофе выключить телефон нечать смотреть",
                epic1Id,
                TaskStatus.NEW);
        SubTask subtask3 = new SubTask("Посмотреть сериал Андор 2 сезон",
                "Приготовить закуску выключить телефон нечать смотреть 2 сезон Андора сначала",
                epic1Id,
                TaskStatus.NEW);

        subtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 21, 0));
        subtask1.setDuration(Duration.ofMinutes(180));

        subtask2.setStartTime(LocalDateTime.of(2025, 5, 25, 20, 0));
        subtask2.setDuration(Duration.ofMinutes(180));

        subtask3.setStartTime(LocalDateTime.of(2025, 5, 26, 22, 0));
        subtask3.setDuration(Duration.ofMinutes(90));

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        //Отправляем запрос на список задач
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        List<SubTask> subtasksList = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());
        assertEquals(200, response.statusCode());

        assertNotNull(subtasksList, "Задачи не возвращаются");
        assertEquals(3, subtasksList.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");

        //Создаем задачи
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");
        int epic1Id = manager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску напитки посмотреть Звездные войны Изгой",
                epic1Id,
                TaskStatus.NEW);
        SubTask subtask2 = new SubTask("Начать смотреть сериал Андор",
                "Приготовить закуску кофе выключить телефон нечать смотреть",
                epic1Id,
                TaskStatus.NEW);
        SubTask subtask3 = new SubTask("Посмотреть сериал Андор 2 сезон",
                "Приготовить закуску выключить телефон нечать смотреть 2 сезон Андора сначала",
                epic1Id,
                TaskStatus.NEW);

        subtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 21, 0));
        subtask1.setDuration(Duration.ofMinutes(180));

        subtask2.setStartTime(LocalDateTime.of(2025, 5, 25, 20, 0));
        subtask2.setDuration(Duration.ofMinutes(180));

        subtask3.setStartTime(LocalDateTime.of(2025, 5, 26, 22, 0));
        subtask3.setDuration(Duration.ofMinutes(90));

        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        manager.addNewSubtask(subtask3);

        //Получаем таску по id
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        SubTask savedSubtask = gson.fromJson(response.body(), SubTask.class);
        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromManager = this.manager.getSubtasks();

        System.out.println(tasksFromManager);
        System.out.println(savedSubtask);
        System.out.println(tasksFromManager.size());

        assertEquals(subtask3.getTitle(), savedSubtask.getTitle());
        assertEquals(subtask3.getStatus(), savedSubtask.getStatus());
        assertEquals(subtask3.getDescription(), savedSubtask.getDescription());
        assertEquals(subtask3.getDuration(), savedSubtask.getDuration());
        assertEquals(subtask3.getStartTime(), savedSubtask.getStartTime());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        //Создаем подзадачу
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");
        int epic1Id = manager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску напитки посмотреть Звездные войны Изгой",
                epic1Id,
                TaskStatus.NEW);
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 21, 0));
        subtask1.setDuration(Duration.ofMinutes(180));
        int idSubtask1 = manager.addNewSubtask(subtask1);
        subtask1.setId(idSubtask1);

        //Изменяем статус задачи и отправляем
        SubTask newSubtask1 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску напитки посмотреть Звездные войны Изгой",
                epic1Id,
                TaskStatus.IN_PROGRESS);
        newSubtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 21, 0));
        newSubtask1.setDuration(Duration.ofMinutes(180));
        newSubtask1.setId(idSubtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + newSubtask1.getId());
        String taskJson = gson.toJson(newSubtask1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        assertEquals(201, response.statusCode());

        List<SubTask> subtaskList = manager.getSubtasks();
        SubTask savedSubtask = subtaskList.getFirst();

        assertEquals(newSubtask1.getTitle(), savedSubtask.getTitle());
        assertEquals(newSubtask1.getStatus(), savedSubtask.getStatus());
        assertEquals(newSubtask1.getDescription(), savedSubtask.getDescription());
        assertEquals(newSubtask1.getDuration(), savedSubtask.getDuration());
        assertEquals(newSubtask1.getStartTime(), savedSubtask.getStartTime());
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        //Создаем подзадачу
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");
        int epic1Id = manager.addNewEpic(epic1);
        SubTask subtask1 = new SubTask("Посмотреть Звездные войны Изгой",
                "Приготовить закуску напитки посмотреть Звездные войны Изгой",
                epic1Id,
                TaskStatus.NEW);
        subtask1.setStartTime(LocalDateTime.of(2025, 5, 24, 21, 0));
        subtask1.setDuration(Duration.ofMinutes(180));
        int idSubtask1 = manager.addNewSubtask(subtask1);
        subtask1.setId(idSubtask1);

        //Отправляем запрос на удаление
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        assertEquals(200, response.statusCode());

        List<SubTask> tasksFromManager = this.manager.getSubtasks();

        assertEquals(0, tasksFromManager.size());
    }
}
