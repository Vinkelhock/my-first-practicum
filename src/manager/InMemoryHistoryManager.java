package manager;

import model.Task;

import java.util.ArrayList;

import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private HashMap<Integer, Node> historyList = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    //Внутренний класс-узел двухсвязного списка
    public static class Node<Task> {
        private Task data;
        private Node<Task> next;
        private Node<Task> prev;

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        public Task getData() {
            return this.data;
        }

        public Node<Task> getNext() {
            return this.next;
        }

    }

    //Добавить узел в конец двухсвязного списка
    public Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        this.tail = newNode;
        if (oldTail == null)
            this.head = newNode;
        else
            oldTail.next = newNode;
        this.size++;
        return this.tail;
    }

    //Добавить узел в связный список и хешмап
    @Override
    public void add(Task task) {
        if (historyList.containsKey(task.getId())) {
            removeNode(historyList.get(task.getId()));
        }
        Node<Task> node = linkLast(task);
        historyList.put(task.getId(), node);
    }

    //Удалить задачу из списка просмотров
    @Override
    public void remove(int id) {
        if (historyList.containsKey(id)) {
            removeNode(historyList.get(id));
            historyList.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> tasks = getTasks();
        return tasks;
    }

    //Собрать содержимое двухсвязного списка в ArrayList
    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    //Удалить узел из связного списка
    public void removeNode(Node node) {
        Node<Task> prevNode = node.prev;//ссылка на предыдущий узел
        Node<Task> nextNode = node.next;//ссылка на следующий узел
        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            this.head = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            this.tail = prevNode;
        }
        node.next = null;
        node.prev = null;
    }
}
