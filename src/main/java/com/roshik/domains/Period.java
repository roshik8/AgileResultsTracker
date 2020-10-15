package com.roshik.domains;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "period", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<Task> tasks;

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

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}
