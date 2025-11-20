package com.example.todolist;

public class TodoItem {
    private String title;
    private String description;
    private String deadline;
    private String status;
    private boolean isCompleted;

    public TodoItem(String title, String description, String deadline, String status, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.isCompleted = isCompleted;
    }

    // Getter và Setter
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }
    public boolean isCompleted() { return isCompleted; }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}