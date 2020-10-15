package com.roshik.domains;

public enum TaskStatus {

    Created("Создана"),
    Done("Выполнена");

    private String title;

    TaskStatus(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

}
