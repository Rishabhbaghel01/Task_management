package com.taskmanager.task_management_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.task_management_system.model.Task;
import com.taskmanager.task_management_system.model.TaskStatus;
import com.taskmanager.task_management_system.repository.TaskRepository;
import com.taskmanager.task_management_system.service.EmailService;
import com.taskmanager.task_management_system.service.TaskService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

	private final TaskService taskService;
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	EmailService emailService;

	// Constructor for initializing TaskController with TaskService
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	// Endpoint to get all tasks
	@GetMapping
	public List<Task> getAllTasks() {
		return taskService.getAllTasks();
	}

	// Endpoint to create a new task
	@PostMapping
	public ResponseEntity<?> createTask(@Valid @RequestBody Task task) throws MessagingException {
		// Sending an email notification when a new task is created
		// emailService.sendEmail("ea2584006@gmail.com", "New Task Created", "A new task has been added.");
		return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task));
	}

	// Endpoint to update an existing task
	@PutMapping("/{id}")
	public ResponseEntity<?> updateTask(@PathVariable String id, @Valid @RequestBody Task updatedTask) {
		return ResponseEntity.ok(taskService.updateTask(id, updatedTask));
	}

	// Endpoint to delete a task
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTask(@PathVariable String id) {
		taskService.deleteTask(id);
		return ResponseEntity.noContent().build();
	}

	// Endpoint to search tasks based on keyword and status
	@GetMapping("/search")
	public List<Task> searchTasks(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) TaskStatus status) {
		if (keyword != null && status != null) {
			return taskRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, status);
		} else if (keyword != null) {
			return taskRepository.findByTitleContainingIgnoreCase(keyword);
		} else if (status != null) {
			return taskRepository.findByStatus(status);
		}
		return taskRepository.findAll();
	}
}
