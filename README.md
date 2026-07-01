## Task Management System

### Overview
A Spring Boot application for managing tasks with user authentication and authorization using JWT. This project allows users to register, log in, create, read, update, and delete tasks. It also supports filtering tasks by title and status.

### Features
- User Registration and Login with JWT authentication.
- CRUD operations for tasks.
- Task filtering by title and status.
- Secure authentication and authorization.

### Prerequisites
Ensure you have the following installed:
- Java JDK 17 or higher
- Maven 3.8 or higher
- MySQL Server
- An API testing tool like Postman

### Setup Instructions

#### 1. Import the Project
- Open an IDE like IntelliJ IDEA or Eclipse.
- Choose **Import Project** or **Open Project**, and select the folder containing the project files.
- Choose Maven as the build management option when prompted.
- Click **Finish** to import the project into your development environment.

#### 2. Create the Database in MySQL
- Open MySQL Workbench or connect to your MySQL server through the command line.
- Create a new database for the project:

***sql
CREATE DATABASE task_management;
***

- After creating the database, run the application to allow Spring Boot to auto-create the necessary tables using Hibernate.

#### 3. Configure the Database
- Open the 'src/main/resources/application.properties' file.
- Update the database properties with your MySQL credentials:

***properties
spring.datasource.url=jdbc:mysql://localhost:3306/task_management
spring.datasource.username=<your-database-username>
spring.datasource.password=<your-database-password>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
***

- Replace '<your-database-username>' and '<your-database-password>' with your actual MySQL credentials.

#### 4. Build the Project
- To install all dependencies and build the project, open a terminal inside the project folder and run the following command:

***
mvn clean install
***

#### 5. Run the Application
- To start the application, use the following command in the terminal:

***
mvn spring-boot:run
***

- The application will be running on [http://localhost:8080].

### API Endpoints

#### Authentication

**POST /api/auth/register**

Request Body:
***json
{
  "username": "example",
  "password": "password"
  "email": "testuser@example.com"

}
***

**POST /api/auth/login**

Request Body:
***json
{
  "username": "example",
  "password": "password"
}
***

Response:
***json
{
  "token": "<jwt-token>"
}
***

#### Tasks
Include the JWT token in the Authorization header as 'Bearer <token>' for all the endpoints below:

**GET /api/tasks**

- Fetch all tasks.

**POST /api/tasks**

- Create a new task.

Request Body:
***json
{
  "title": "New Task",
  "description": "Task description",
  "status": "IN_PROGRESS"
}
***

**PUT /api/tasks/{id}**

- Update an existing task.

**DELETE /api/tasks/{id}**

- Delete a task.

**GET /api/tasks/search?keyword={keyword}&status={status}**

- Search and filter tasks.

### Testing the Application

#### Register a User
- Use Postman to send a POST request to '/api/auth/register' with a JSON body containing username and password and email.

#### Log In
- Use Postman to send a POST request to '/api/auth/login' to get a JWT token.

#### Perform Task Operations
- Use the token from the login step in the Authorization header for subsequent requests.
- Test creating, updating, deleting, and fetching tasks.

#### Search and Filter Tasks
- Use '/api/tasks/search' with query parameters like 'keyword' and 'status' to test filtering functionality.
