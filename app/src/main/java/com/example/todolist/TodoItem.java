package com.example.todolist;

import java.util.ArrayList;
import java.util.UUID;

public class TodoItem {
    private String id; // ID duy nhất cho mỗi công việc
    private String title, description, deadline, status;
    private boolean isCompleted;
    private ArrayList<Contact> contacts;

    // Sửa constructor để bao gồm cả ID
    public TodoItem(String id, String title, String description, String deadline, String status, boolean isCompleted, ArrayList<Contact> contacts) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.isCompleted = isCompleted;
        this.contacts = contacts;
    }

    // Constructor để tạo mới với ID tự động
    public TodoItem(String title, String description, String deadline, String status, boolean isCompleted, ArrayList<Contact> contacts) {
        this(UUID.randomUUID().toString(), title, description, deadline, status, isCompleted, contacts);
    }

    // Thêm getter cho ID
    public String getId() { return id; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public ArrayList<Contact> getContacts() {
        return contacts != null ? contacts : new ArrayList<>();
    }
}
