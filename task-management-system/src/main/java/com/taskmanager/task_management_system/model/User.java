package com.taskmanager.task_management_system.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "users")
@Setter
@Getter
public class User {

	@Id
	private String id;

	private String username;

	private String password;

	private String email;

}
