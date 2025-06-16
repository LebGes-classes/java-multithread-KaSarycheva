package org.example;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testAddWorker() {
        Main.workers.add(new Worker("Иван", "Программирование", 10));
        assertEquals(1, Main.workers.size());
        assertEquals("Иван", Main.workers.get(0).getName());
    }

    @Test
    void testRemoveWorker() {
        Main.workers.add(new Worker("Петр", "Тестирование", 5));
        Main.workers.remove(0);
        assertTrue(Main.workers.isEmpty());
    }

    @Test
    void testReport() {
        Worker worker = new Worker("Алексей", "Дизайн", 8);
        Main.workers.add(worker);

        worker.setWorkHours(4);
        worker.setDowntimeHours(4);
        worker.setTaskHours(4);

        assertEquals(4, worker.getWorkHours());
        assertEquals(4, worker.getDowntimeHours());
        assertEquals(4, worker.getTaskHours());
    }

    @Test
    void testRatingCalculation() {
        Worker worker1 = new Worker("Мария", "Анализ", 10);
        Worker worker2 = new Worker("Дмитрий", "Документация", 10);

        worker1.setWorkHours(7);
        worker2.setWorkHours(5);

        if (worker1.getWorkHours() > 6) worker1.setTop(1);
        if (worker2.getWorkHours() > 3 && worker2.getWorkHours() <= 6) worker2.setTop(2);

        assertEquals(1, worker1.getTop());
        assertEquals(2, worker2.getTop());
    }
}
