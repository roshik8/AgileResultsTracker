package com.roshik.services;

import com.roshik.domains.Task;
import com.roshik.domains.TaskStatus;
import com.roshik.repositories.PeriodRepository;
import com.roshik.repositories.PermissionRepository;
import com.roshik.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {
    @Autowired
    TaskRepository repository;
    @Autowired
    PeriodRepository periodRepository;
    @Autowired
    PermissionRepository permissionRepository;

    public void add(Task task) {
        periodRepository.save(task.getPeriod());
        repository.save(task);

    }

    public void update(Task task) {
        repository.save(task);
    }

    public List<Task> getTasksByFilterTaskQuery(FilterTaskQuery filter, Long user_id) {
        List<Task> tasksList ;
        if (filter.isOwnTasks()) {
            if (filter.getStart_date()==null){
                tasksList = repository.findByUser_idAndStatus(user_id,filter.getStatus());
            }
            else {
                tasksList = repository.findByUser_idAndStatusAndPeriod(user_id, filter.getStatus(), filter.getStart_date(), filter.getEnd_date());
            }
        }
        else{
            if (filter.getStart_date()==null){
                tasksList = repository.findByUser_idAndStatusAndPermissions(user_id,filter.getStatus());
           }
            else {
                tasksList = repository.findByUser_idAndStatusAndPeriodAndPermissions(user_id,filter.getStatus(),filter.getStart_date(),filter.getEnd_date());
            }
        }
        return tasksList;

    }

    public boolean isCanEditTask(Long user_id, Long Id) {
        return repository.isCanEditTask(Id, user_id);
    }

    public Task getTaskById(Long Id){
        return repository.findById(Id).orElse(null);
    }



    public List<Task> updateExpiredTask(){
        var tasks = repository.findByExpiredPeriod();

        for(var task: tasks){
            task.setStatus(TaskStatus.Expired);
            repository.save(task);
        }
        return tasks;
    }

}






