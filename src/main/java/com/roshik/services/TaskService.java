package com.roshik.services;

import com.roshik.domains.Task;
import com.roshik.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskService {
    @Autowired
    TaskRepository repository;

    public void add(Task task){
        repository.save(task);
    }

    public void update(Task task){
        repository.save(task);
    }

}
