package com.roshik.services;

import com.roshik.domains.Permission;
import com.roshik.domains.Task;
import com.roshik.repositories.PeriodRepository;
import com.roshik.repositories.PermissionRepository;
import com.roshik.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    @Autowired
    PermissionRepository repository;


    public void add(Permission permission){
        repository.save(permission);
    }

    public void update(Permission permission){
        repository.save(permission);
    }

    public List<Permission> get(){
        return repository.findAll();
    }

    public List<Permission> get(Long userId){
       return null;
    }




}
