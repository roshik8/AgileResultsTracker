package com.roshik.services;

import com.roshik.domains.TaskStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FilterTaskQuery {
    private TaskStatus status;
    private boolean ownTasks;
    private Date start_date;
    private Date end_date;

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

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }
    public void setStart_date(String start_date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try{
            this.start_date = dateFormat.parse(start_date.trim());
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }
    public void setEnd_date(String end_date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try{
            this.end_date = dateFormat.parse(end_date.trim());
        } catch (ParseException e){
            e.printStackTrace();
        }
    }
}
