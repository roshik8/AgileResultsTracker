package com.roshik.domains;

import javax.persistence.*;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int Id;

    private int id_owner;
    private int permission_owner;
    private int data_owner;

    private String permission_type;

    public Permission(){

    }

    public int getId() {
        return Id;
    }

    public int getId_owner() {
        return id_owner;
    }

    public void setId_owner(int id_owner) {
        this.id_owner = id_owner;
    }

    public int getPermission_owner() {
        return permission_owner;
    }

    public void setPermission_owner(int permission_owner) {
        this.permission_owner = permission_owner;
    }

    public int getData_owner() {
        return data_owner;
    }

    public void setData_owner(int data_owner) {
        this.data_owner = data_owner;
    }

    public String getPermission_type() {
        return permission_type;
    }

    public void setPermission_type(String permission_type) {
        this.permission_type = permission_type;
    }
}
