package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IntersectionException;
import manager.TaskManager;
import model.Task;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем запрос для пути /tasks");

        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        String method = exchange.getRequestMethod();
        if (splitStrings.length == 2) {
            switch (method) {
                case "GET":
                    handleGetTasks(exchange);
                    break;
                case "POST":
                    handleCreateTask(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } else if (splitStrings.length == 3) {
            int taskId = Integer.parseInt(splitStrings[2]);
            switch (method) {
                case "GET":
                    handleGetTaskById(exchange, taskId);
                    break;
                case "POST":
                    handleUpdateTask(exchange, taskId);
                    break;
                case "DELETE":
                    handleDeleteTaskById(exchange, taskId);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } else {
            System.out.println("Ошибка в URL");
            sendNotFound(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        System.out.println("Выводим список всех задач");
        List<Task> tasksList = taskManager.getTasks();
        String jsonTasksList = gson.toJson(tasksList);
        sendText(exchange, jsonTasksList);

    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        String jsonBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(jsonBody, Task.class);
        System.out.printf("Получили задачу %s - добавляем ее в список задач", task.getTitle());
        try {
            int idTask = taskManager.addNewTask(task);
            String jsonString = gson.toJson(idTask);
            sendText(exchange, jsonString);
        } catch (IntersectionException exception) {
            String message = "Задача пересекается по времени выполнения с другой задачей";
            System.out.printf("В консоли %s", message);
            sendHasInteractions(exchange, message);
        }
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        System.out.println("Получаем задачу по номеру");
        Task task = taskManager.getTask(id);
        String jsonString = gson.toJson(task);
        sendText(exchange, jsonString);
    }

    private void handleUpdateTask(HttpExchange exchange, int id) throws IOException {
        System.out.println("Обновляем задачу в соответствии с номером");
        InputStream inputStream = exchange.getRequestBody();
        String jsonBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(jsonBody, Task.class);
        taskManager.updateTask(id, task);

        sendTextWithoutBody(exchange);
    }

    private void handleDeleteTaskById(HttpExchange exchange, int id) throws IOException {
        System.out.println("Удаляем задачу по id");
        taskManager.removeTaskById(id);
        String jsonString = gson.toJson(String.format("Удаляем задачу по id = %d", id));
        sendText(exchange, jsonString);
    }
}