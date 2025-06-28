package com.k99.todolist;

public class Task {
    private String title;
    private String description;
    private String time;
    private boolean isCompleted;

    public Task(String title, String description, String time, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.isCompleted = isCompleted;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    // Setters
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
