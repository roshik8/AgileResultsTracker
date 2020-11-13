package com.roshik.services;

import com.roshik.domains.TaskStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class FilterTaskQuery {
    private TaskStatus status;
    private boolean ownTasks;
    private LocalDate start_date;
    private LocalDate end_date;

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public boolean isOwnTasks() {
        return ownTasks;
    }

    public void setOwnTasks(boolean ownTasks) {
        this.ownTasks = ownTasks;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public void setStart_date(String start_date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.start_date= LocalDate.parse(start_date,formatter);
    }

    public void setEnd_date(String end_date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.end_date= LocalDate.parse(end_date,formatter);
    }
}
