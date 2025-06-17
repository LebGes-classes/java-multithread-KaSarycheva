package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static List<Worker> workers = new ArrayList<>();
    static final String FILE_PATH = "multithread.xlsx";
    static ExecutorService executor = Executors.newFixedThreadPool(3); // Пул потоков для обработки задач

    public static void main(String[] args) {
        loadDataFromExcel(); // Загрузка данных при старте

        boolean flag = true;
        while (flag) {
            System.out.println("Управление:");
            System.out.println("1. Нанять работника");
            System.out.println("2. Уволить работника");
            System.out.println("3. Отчет работника");
            System.out.println("4. Рейтинг сотрудников");
            System.out.println("5. Список работников");
            System.out.println("0. Сохранение данных и выход");
            System.out.print("\nВыберите: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addWorker();
                case 2 -> removeWorker();
                case 3 -> report();
                case 4 -> rating();
                case 5 -> showWorkers();
                case 0 -> {
                    saveAllData();
                    flag = false;
                }
                default -> System.out.println("Неверный ввод!");
            }
        }
        executor.shutdown(); // Завершаем работу пула потоков
    }

    public static void addWorker() {
        System.out.println("Введите ФИО работника: ");
        String FIO = scanner.nextLine();

        System.out.println("Введите задачу работника: ");
        String task = scanner.nextLine();

        System.out.println("Введите количество часов для задачи");
        int hours = scanner.nextInt();

        Worker worker = new Worker(FIO, task, hours);
        workers.add(worker);

        // Добавляем задачу в пул потоков
        executor.execute(() -> {
            System.out.println("Работник " + FIO + " добавлен и начал выполнять задачу: " + task);
        });
    }

    public static void removeWorker() {
        showWorkers();
        System.out.println("Введите номер работника в списке: ");
        int id = scanner.nextInt();
        if (id > 0 && id <= workers.size()) {
            Worker removed = workers.remove(id - 1);
            System.out.println("Работник " + removed.getName() + " уволен");
        } else {
            System.out.println("Неверный номер работника");
        }
    }

    public static void report() {
        showWorkers();
        System.out.println("Введите номер работника: ");
        int id = scanner.nextInt();

        System.out.println("Введите количество отработанных часов: ");
        int hours = scanner.nextInt();

        if (id > 0 && id <= workers.size()) {
            Worker worker = workers.get(id - 1);
            worker.setWorkHours(worker.getWorkHours() + hours);
            worker.setDowntimeHours(8 - hours);
            worker.setTaskHours(worker.getTaskHours() - hours);

            // Обработка отчета в отдельном потоке
            executor.execute(() -> {
                System.out.println("Отчет по работнику " + worker.getName() + " обработан");
            });
        } else {
            System.out.println("Неверный номер работника");
        }
    }

    public static void rating() {
        for (Worker worker : workers) {
            int workHours = worker.getWorkHours();
            if (workHours <= 3) {
                worker.setTop(3);
            } else if (workHours <= 6) {
                worker.setTop(2);
            } else {
                worker.setTop(1);
            }
        }

        int c = 1;
        for (Worker worker : workers) {
            System.out.println(c + ". " + worker.getName() + " - топ " + worker.getTop());
            c++;
        }
    }

    public static void showWorkers() {
        if (workers.isEmpty()) {
            System.out.println("Нет работников");
            return;
        }

        int n = 1;
        for (Worker worker : workers) {
            System.out.println(n + ". " + worker.getName() +
                    " | Задача: " + worker.getTask() +
                    " | Осталось часов: " + worker.getTaskHours());
            n++;
        }
        System.out.println();
    }

    public static void loadDataFromExcel() {
        try (FileInputStream file = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(file)) {

            // Чтение данных работников
            Sheet workersSheet = workbook.getSheet("Workers");
            for (int i = 1; i <= workersSheet.getLastRowNum(); i++) {
                Row row = workersSheet.getRow(i);
                if (row != null) {
                    String name = row.getCell(0).getStringCellValue();
                    String task = row.getCell(1).getStringCellValue();
                    int taskHours = (int) row.getCell(2).getNumericCellValue();
                    workers.add(new Worker(name, task, taskHours));
                }
            }

            // Чтение результатов работы
            Sheet resultsSheet = workbook.getSheet("Results");
            for (int i = 1; i <= resultsSheet.getLastRowNum(); i++) {
                Row row = resultsSheet.getRow(i);
                if (row != null) {
                    String name = row.getCell(0).getStringCellValue();
                    for (Worker worker : workers) {
                        if (worker.getName().equals(name)) {
                            worker.setWorkHours((int) row.getCell(1).getNumericCellValue());
                            worker.setDowntimeHours((int) row.getCell(2).getNumericCellValue());
                            worker.setTaskHours((int) row.getCell(3).getNumericCellValue());
                            break;
                        }
                    }
                }
            }
            System.out.println("Данные успешно загружены из Excel");
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке данных: " + e.getMessage());
        }
    }

    public static void saveAllData() {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Лист с работниками
            Sheet workersSheet = workbook.createSheet("Workers");
            createHeader(workersSheet, new String[]{"Name", "Task", "taskHours"});

            // Лист с результатами
            Sheet resultsSheet = workbook.createSheet("Results");
            createHeader(resultsSheet, new String[]{"Name", "workHours", "downtimeHours", "taskHours", "top"});

            // Заполняем данные
            for (Worker worker : workers) {
                // Добавляем в лист Workers
                Row workerRow = workersSheet.createRow(workersSheet.getLastRowNum() + 1);
                workerRow.createCell(0).setCellValue(worker.getName());
                workerRow.createCell(1).setCellValue(worker.getTask());
                workerRow.createCell(2).setCellValue(worker.getTaskHours());

                // Добавляем в лист Results
                Row resultRow = resultsSheet.createRow(resultsSheet.getLastRowNum() + 1);
                resultRow.createCell(0).setCellValue(worker.getName());
                resultRow.createCell(1).setCellValue(worker.getWorkHours());
                resultRow.createCell(2).setCellValue(worker.getDowntimeHours());
                resultRow.createCell(3).setCellValue(worker.getTaskHours());
                resultRow.createCell(4).setCellValue(worker.getTop());
            }

            // Сохраняем файл
            try (FileOutputStream out = new FileOutputStream(FILE_PATH)) {
                workbook.write(out);
                System.out.println("Данные успешно сохранены в Excel");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    private static void createHeader(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }
}
