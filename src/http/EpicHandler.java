package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;
import model.SubTask;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем запрос для пути /epics");

        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        String method = exchange.getRequestMethod();
        if (splitStrings.length == 2) {
            switch (method) {
                case "GET":
                    handleGetEpics(exchange);
                    break;
                case "POST":
                    handleCreateEpic(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } else if (splitStrings.length == 3) {
            int epicId = Integer.parseInt(splitStrings[2]);
            switch (method) {
                case "GET":
                    handleGetEpicById(exchange, epicId);
                    break;
                case "DELETE":
                    handleDeleteEpicById(exchange, epicId);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } else if (splitStrings.length == 4) {
            int epicId = Integer.parseInt(splitStrings[2]);
            handleGetEpicSubtasks(exchange, epicId);
        } else {
            System.out.println("Ошибка в URL");
            sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        System.out.println("Выводим список всех подзадач");
        List<Epic> epicList = taskManager.getEpics();
        String jsonEpicList = gson.toJson(epicList);
        sendText(exchange, jsonEpicList);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        String jsonBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Epic epic = gson.fromJson(jsonBody, Epic.class);
        System.out.println("Получили задачу " + epic.getTitle() + " - добавляем ее в список задач");
        int idEpic = taskManager.addNewEpic(epic);
        String jsonString = gson.toJson(idEpic);
        sendText(exchange, jsonString);
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        System.out.println("Получаем задачу по номеру");
        List<Epic> epicList = taskManager.getEpics();
        Epic epic = null;
        for (Epic currentEpic : epicList) {
            if (currentEpic.getId() == id) {
                epic = currentEpic;
            }
        }
        if (epic != null) {
            String jsonString = gson.toJson(epic);
            sendText(exchange, jsonString);
        } else sendNotFound(exchange);
    }

    private void handleDeleteEpicById(HttpExchange exchange, int id) throws IOException {
        System.out.println("Удаляем задачу по id");
        taskManager.removeEpicById(id);
        String jsonString = gson.toJson("Удалили задачу по id = " + id);
        sendText(exchange, jsonString);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, int id) throws IOException {
        System.out.println("Получаем подзадачи эпика");
        List<Epic> epicList = taskManager.getEpics();
        Epic epic = null;
        for (Epic currentEpic : epicList) {
            if (currentEpic.getId() == id) {
                epic = currentEpic;
            }
        }
        if (epic != null) {
            ArrayList<SubTask> listEpicSubtasks = new ArrayList<>();
            ArrayList<Integer> listSubtaskIds = epic.getListOfSubtask();
            ArrayList<SubTask> listSubtasks = taskManager.getSubtasks();
            for (int i = 0; i < listSubtaskIds.size(); i++) {
                SubTask subtask = listSubtasks.get(i);
                listEpicSubtasks.add(subtask);
            }
            String jsonString = gson.toJson(listEpicSubtasks);
            sendText(exchange, jsonString);
        } else sendNotFound(exchange);
    }
}