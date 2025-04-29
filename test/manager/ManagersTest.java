package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void shouldReturnInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager,
                "Метод getDefault() должен возвращать проинициализированный экземпляр TaskManager.");
    }

    @Test
    void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager,
                "Метод getDefaultHistory() должен возвращать проинициализированный экземпляр HistoryManager.");
    }
}