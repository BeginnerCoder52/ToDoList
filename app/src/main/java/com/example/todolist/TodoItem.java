package com.example.todolist;

import java.util.ArrayList;

public class TodoItem {
    private String title, description, deadline, status;
    private boolean isCompleted;
    // Thay đổi từ contact đơn lẻ sang danh sách
    private ArrayList<Contact> contacts;

    public TodoItem(String title, String description, String deadline, String status, boolean isCompleted, ArrayList<Contact> contacts) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.isCompleted = isCompleted;
        this.contacts = contacts;
    }

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