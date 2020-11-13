package com.roshik.domains;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "periods")
public class Period {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int Id;

    private LocalDate start_date;
    private LocalDate end_date;

    @OneToMany(mappedBy = "period")
    private List<Task> tasks;

    public Period(){

    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getStringEnd_date() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return end_date.format(formatter);
    }
}
