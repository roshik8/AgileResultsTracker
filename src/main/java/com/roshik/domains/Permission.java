package com.roshik.domains;

import javax.persistence.*;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int Id;

    private Long id_owner;
    private Long permission_owner;

    @ManyToOne
    @JoinColumn(name = "task_id",nullable = false)
    private Task task;

    public Permission(){

    }

    public int getId() {
        return Id;
    }

    public Long getId_owner() {
        return id_owner;
    }

    public void setId_owner(Long id_owner) {
        this.id_owner = id_owner;
    }

    public Long getPermission_owner() {
        return permission_owner;
    }

    public void setPermission_owner(Long permission_owner) {
        this.permission_owner = permission_owner;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
