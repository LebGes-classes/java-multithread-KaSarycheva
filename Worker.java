package org.example;

public class Worker {
    private String name;
    private String task;
    private int taskHours; // сколько часов требует задача
    private int workHours; // сколько часов работник выполняет задачу
    private int downtimeHours; // сколько часов работник не выполняет задачу
    private int top; // место в рейтинге

    public Worker(String name, String task, int taskHours) {
        this.name = name;
        this.task = task;
        this.taskHours = taskHours;
    }

    public String getName() {
        return name;
    }

    public String getTask() {
        return task;
    }

    public int getTaskHours() {
        return taskHours;
    }

    public int getWorkHours() {
        return workHours;
    }

    public int getDowntimeHours() {
        return downtimeHours;
    }

    public int getTop() {
        return top;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setTaskHours(int taskHours) {
        this.taskHours = taskHours;
    }

    public void setWorkHours(int workHours) {
        this.workHours = workHours;
    }

    public void setDowntimeHours(int downtimeHours) {
        this.downtimeHours = downtimeHours;
    }

    public void setTop(int top) {
        this.top = top;
    }
}
