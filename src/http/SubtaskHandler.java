
package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IntersectionException;
import manager.TaskManager;
import model.SubTask;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем запрос для пути /subtasks");

        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        String method = exchange.getRequestMethod();
        if (splitStrings.length == 2) {
            switch (method) {
                case "GET":
                    handleGetSubtasks(exchange);
                    break;
                case "POST":
                    handleCreateSubtask(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } else if (splitStrings.length == 3) {
            int subtaskId = Integer.parseInt(splitStrings[2]);
            switch (method) {
                case "GET":
                    handleGetSubtaskById(exchange, subtaskId);
                    break;
                case "POST":
                    handleUpdateSubtask(exchange, subtaskId);
                    break;
                case "DELETE":
                    handleDeleteSubtaskById(exchange, subtaskId);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } else {
            System.out.println("Ошибка в URL");
            sendNotFound(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        System.out.println("Выводим список всех подзадач");
        List<SubTask> subtaskList = taskManager.getSubtasks();
        String jsonSubtaskList = gson.toJson(subtaskList);
        sendText(exchange, jsonSubtaskList);
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        String jsonBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        SubTask subtask = gson.fromJson(jsonBody, SubTask.class);
        System.out.printf("Получили задачу %s - добавляем ее в список задач", subtask.getTitle());
        try {
            int idTask = taskManager.addNewSubtask(subtask);
            String jsonString = gson.toJson(idTask);
            sendText(exchange, jsonString);
        } catch (IntersectionException exception) {
            String message = "Задача пересекается по времени выполнения с другой задачей";
            System.out.println("В консоли " + message);
            sendHasInteractions(exchange, message);
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange, int id) throws IOException {
        System.out.println("Получаем задачу по номеру");
        List<SubTask> subtaskList = taskManager.getSubtasks();
        SubTask subtask = null;
        for (SubTask currentSubtask : subtaskList) {
            if (currentSubtask.getId() == id) {
                subtask = currentSubtask;
            }
        }
        if (subtask != null) {
            String jsonString = gson.toJson(subtask);
            sendText(exchange, jsonString);
        } else sendNotFound(exchange);
    }


    private void handleUpdateSubtask(HttpExchange exchange, int id) throws IOException {
        System.out.println("Обновляем задачу в соответствии с номером");
        InputStream inputStream = exchange.getRequestBody();
        String jsonBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        SubTask subtask = gson.fromJson(jsonBody, SubTask.class);
        taskManager.updateSubtask(id, subtask);

        sendTextWithoutBody(exchange);
    }

    private void handleDeleteSubtaskById(HttpExchange exchange, int id) throws IOException {
        System.out.println("Удаляем задачу по id");
        taskManager.removeSubtaskById(id);
        String jsonString = gson.toJson(String.format("Удалили задачу по id = %d", id));
        sendText(exchange, jsonString);
    }
}