package com.roshik.repositories;

import com.roshik.domains.Task;
import com.roshik.domains.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT u FROM Task u WHERE u.user_id=?1 and u.status = ?2")
    List<Task> findByUser_idAndStatus(Long user_id, TaskStatus taskStatus);

    @Query("select count(t) > 0 from Task t where t.Id=?1 and t.user_id=?2 and t.status='Created'")
    boolean isCanEditTask(Long id, Long user_id);

    @Query("SELECT u FROM Task u WHERE u.user_id=?1 and u.status = ?2 and u.period.start_date>=?3 and u.period.end_date<=?4")
    List<Task> findByUser_idAndStatusAndPeriod(Long user_id, TaskStatus taskStatus, Date start_date, Date end_date);

    @Query("select t.task from Permission t where t.permission_owner=?1 and t.task.status = ?2 and t.task.period.start_date>=?3 and t.task.period.end_date<=?4")
    List<Task> findByUser_idAndStatusAndPeriodAndPermissions(Long user_id, TaskStatus taskStatus, Date start_date, Date end_date);

    @Query("select t.task from Permission t where t.permission_owner=?1 and t.task.status = ?2 ")
    List<Task> findByUser_idAndStatusAndPermissions(Long user_id, TaskStatus taskStatus);

    @Query("select t from Task t where t.status='Created' and t.period.end_date<current_date")
    List<Task> findByExpiredPeriod();


}
