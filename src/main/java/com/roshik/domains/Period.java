package com.roshik.domains;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "periods")
public class Period {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int Id;

    private Date start_date;
    private Date end_date;

    @OneToOne(mappedBy = "period")
    private Task task;

    public Period(){

    }

    public int getId() {
        return Id;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }
    public String getStringEnd_date() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(end_date);
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
