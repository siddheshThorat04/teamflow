package com.teamflow.intial.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    Optional<Task> findByProjectIdAndTaskNumber(Long projectId, Integer taskNumber);

    @Query("SELECT COALESCE(MAX(t.taskNumber), 0) FROM Task t WHERE t.project.id = :projectId")
    Integer findMaxTaskNumberForProject(@Param("projectId") Long projectId);
}