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

public class EpicHandlerTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    public EpicHandlerTest() throws IOException {
    }

    static class EpicListTypeToken extends TypeToken<List<Epic>> {
        // здесь ничего не нужно реализовывать
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
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём подзадачу
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть фильмы и  сериалы Звездных войн");

        // конвертируем в JSON
        String taskJson = gson.toJson(epic1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
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
        List<Epic> tasksFromManager = this.manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Погрузиться в Звездные войны", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        //Создаем эпики
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть несколько фильмов из вселенной Звездных войн");
        Epic epic2 = new Epic("Посмотреть сериал Мандалорец", "Посмотреть три сезона мандалорца");
        Epic epic3 = new Epic("Посмотреть сериал Андор", "Посмотреть два сезона Андора");

        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);
        manager.addNewEpic(epic3);

        //Отправляем запрос на список эпиков
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        List<Epic> epicList = gson.fromJson(response.body(), new EpicHandlerTest.EpicListTypeToken().getType());
        assertEquals(200, response.statusCode());

        assertNotNull(epicList, "Задачи не возвращаются");
        assertEquals(3, epicList.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {

        //Создаем эпики
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть несколько фильмов из вселенной Звездных войн");
        Epic epic2 = new Epic("Посмотреть сериал Мандалорец", "Посмотреть три сезона мандалорца");
        Epic epic3 = new Epic("Посмотреть сериал Андор", "Посмотреть два сезона Андора");

        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);
        int idEpic3 = manager.addNewEpic(epic3);
        epic3.setId(idEpic3);

        //Получаем эпик epic3 по id
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic3.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        Epic savedEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());

        assertEquals(epic3.getTitle(), savedEpic.getTitle());
        assertEquals(epic3.getDescription(), savedEpic.getDescription());
        assertEquals(epic3.getDuration(), savedEpic.getDuration());
        assertEquals(epic3.getStartTime(), savedEpic.getStartTime());
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        //Создаем эпик
        Epic epic1 = new Epic("Погрузиться в Звездные войны", "Посмотреть несколько фильмов из вселенной Звездных войн");
        int idEpic1 = manager.addNewEpic(epic1);
        epic1.setId(idEpic1);

        //Отправляем запрос на удаление
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = this.manager.getEpics();

        assertEquals(0, tasksFromManager.size());
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
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

        //Получаем эпик epic1 по id
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();
        List<SubTask> subtasksList = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals(3, subtasksList.size());
    }
}
