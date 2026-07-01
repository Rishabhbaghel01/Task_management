package com.taskmanager.task_management_system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taskmanager.task_management_system.model.Task;
import com.taskmanager.task_management_system.repository.TaskRepository;

@Service
public class TaskService {

	private final TaskRepository taskRepository;

	// Constructor to initialize the service with TaskRepository
	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	// Method to fetch all tasks from the repository
	public List<Task> getAllTasks() {
		return taskRepository.findAll(); // Retrieves all tasks from the database
	}

	// Method to create a new task and save it to the repository
	public Task createTask(Task task) {
		return taskRepository.save(task);
	}

	// Method to update an existing task
	public Task updateTask(String id, Task updatedTask) {
		// Retrieve the task by ID, throw an exception if not found
		Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

		// Update the task fields with the updated task data
		task.setTitle(updatedTask.getTitle());
		task.setDescription(updatedTask.getDescription());
		task.setStatus(updatedTask.getStatus());
		task.setPriority(updatedTask.getPriority());
		task.setDueDate(updatedTask.getDueDate());

		// Save the updated task to the repository
		return taskRepository.save(task);
	}

	// Method to delete a task by ID
	public void deleteTask(String id) {
		taskRepository.deleteById(id); // Deletes the task from the database by ID
	}
}
