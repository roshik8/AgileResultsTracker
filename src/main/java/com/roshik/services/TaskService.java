package com.roshik.services;

import com.roshik.domains.Task;
import com.roshik.repositories.PeriodRepository;
import com.roshik.repositories.PermissionRepository;
import com.roshik.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository repository;
    @Autowired
    PeriodRepository periodRepository;
    @Autowired
    PermissionRepository permissionRepository;

    public void add(Task task){
        periodRepository.save(task.getPeriod());
        repository.save(task);

    }

    public void update(Task task){
        repository.save(task);
    }

    public List<Task> get(){
        return repository.findAll();
    }

    public List<Task> get(Long userId){
       return null;
    }




}
