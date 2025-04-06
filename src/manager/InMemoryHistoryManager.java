package manager;

import model.Task;
import model.Epic;
import model.SubTask;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private HashMap<Integer, Node> history = new HashMap<>();
    private LinkedList mylinkedList = new LinkedList<>();
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    class Node<Task> {
        public Task data;
        public Node<Task> next;
        public Node<Task> prev;

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    public Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
        this.size++;
        return tail;
    }

    @Override
    public void add(Task task) {
        // if (history.size() >= 10) {
        //     history.remove(0);
        // }
        // history.add(task);
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }
        Node<Task> tail = linkLast(task);
        history.put(task.getId(), tail);
    }

    //Удалить узел из связного списка
    public void removeNode(Node node) {
        Node<Task> prevNode = node.prev;//ссылка на предыдущий узел
        Node<Task> nextNode = node.next;//ссылка на следующий узел
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
        node.next = null;
        node.prev = null;
    }

    //Удалить задачу из списка просмотров
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        System.out.println(history);
        return history;
    }
}
