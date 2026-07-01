package com.taskmanager.task_management_system.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.taskmanager.task_management_system.model.Task;
import com.taskmanager.task_management_system.model.TaskStatus;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
	List<Task> findByUsername(String username);

	List<Task> findByUsernameAndTitleContainingIgnoreCaseAndStatus(String username, String keyword, TaskStatus status);

	List<Task> findByUsernameAndStatus(String username, TaskStatus status);

	List<Task> findByUsernameAndTitleContainingIgnoreCase(String username, String keyword);
}
