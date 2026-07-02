# Interview Preparation Guide: Task Management System

This guide is tailored for your **Task Management System** project built with Spring Boot. Use this document to review the architecture, dataflow, key technologies, and potential interview questions.

---

Tech Stack:
1)Backend Framework: Spring Boot 3.4.1 (Java 17)
2)Database: MongoDB (hosted on MongoDB Atlas)
3)Security: Spring Security with JWT (JSON Web Tokens)
4)Validation: Jakarta Validation API (Hibernate Validator)
5)Other Tools: Lombok, Spring Boot Mail (JavaMailSender)

## 1. Project Overview & Architecture

### The Core Concept
Your project is a **Full-Stack Task Management System**. It follows a standard Client-Server architecture.
* **The Client (Frontend):** A Single Page Application (SPA) built with vanilla HTML, CSS, and JavaScript. It provides an interactive user interface without requiring the page to reload.
* **The Server (Backend):** A RESTful API built with Java and Spring Boot 3.4.1. It handles business logic, security (authentication/authorization), and talks to the database.
* **The Database:** MongoDB, a NoSQL database used to store users and their tasks flexibly (hosted on MongoDB Atlas).

### Key Features
- **User Authentication:** Secure registration and login using JWT (JSON Web Tokens) and BCrypt password hashing.
- **Task CRUD:** Create, read, update, and delete tasks.
- **Search & Filtering:** Filter tasks by title/keyword and status.
- **Validation:** Jakarta Validation API ensures clean data is saved to the database.

---

## 2. The Dataflow (End-to-End)

In an interview, they will want to know how a piece of data gets from the user's screen all the way to the database and back. Here is the step-by-step flow for the two most important processes in your app:

### Flow A: Authentication (Registration & Login)
*How does a user securely access the system?*

1. **User Action (Frontend):** The user enters their email and password into the login form and clicks "Submit".
2. **Client Request (`app.js`):** JavaScript intercepts the form submission, packages the credentials into a JSON object, and makes an HTTP `POST` request to `/api/auth/login` using the `fetch()` API.
3. **Server Intercept (`SecurityConfig`):** The Spring Boot server receives the request. Because the `/api/auth/**` endpoints are explicitly permitted in your `SecurityConfig`, the request is routed directly to your `AuthController`.
4. **Validation & Token Generation:** The backend verifies the credentials against the hashed password stored in MongoDB. If successful, it generates a JSON Web Token (JWT).
5. **Response:** The server sends a `200 OK` response containing the JWT back to the client.
6. **Client Storage:** `app.js` receives the token and saves it in the browser's `localStorage`. The UI switches from the Auth Screen to the Dashboard.

### Flow B: Task Management (e.g., Creating a Task)
*How does an authenticated user create a new task?*

1. **User Action (Frontend):** The user fills out the "New Task" form and clicks "Create".
2. **Client Request (`app.js`):** JavaScript captures the input and creates a JSON payload. Crucially, it retrieves the JWT from `localStorage` and attaches it to the request's `Authorization` header as `Bearer <token>`. It sends a `POST` request to `/api/tasks`.
3. **Security Filter (`JwtAuthenticationFilter`):** Spring Security intercepts the request. The filter extracts the JWT, verifies its signature, and extracts the username to authenticate the request.
4. **Controller & Validation (`TaskController`):** The request reaches the `createTask` method. The `@Valid` annotation ensures the task meets the constraints (e.g., title is not empty, due date is in the future).
5. **Business Logic & Persistence:** The controller passes the valid task to the `TaskService`, which assigns it to the current user. `TaskRepository` then saves the document into the MongoDB database.
6. **Response & UI Update:** The server returns the created task to the frontend. `app.js` receives it and dynamically injects the new task into the HTML DOM without refreshing the page.

---

Security (Spring Security & JWT)
How it works:

You have a SecurityConfig class that disables CSRF (since it's a stateless REST API) and configures CORS.
It secures all endpoints except /api/auth/** (registration and login).
JwtAuthenticationFilter intercepts incoming requests, extracts the Bearer token from the Authorization header, and validates it. If valid, it sets the Authentication object in the Spring Security context.
Passwords are never stored in plain text; you use BCryptPasswordEncoder to hash them before saving to MongoDB.
Data Layer (MongoDB)
How it works:

You use Spring Data MongoDB (@Document, @Id).
Repositories (TaskRepository, UserRepository) extend MongoRepository (presumably), giving you instant access to CRUD operations.
You use custom query methods in TaskRepository like findByUsernameAndTitleContainingIgnoreCaseAndStatus for the search endpoint.
Validation
How it works:

The @Valid annotation is used in your controllers to trigger validation before the method body executes.
Entity constraints like @NotEmpty, @Size(max=255), and @FutureOrPresent ensure bad data (e.g., tasks with past due dates) cannot be saved.



## 3. Likely Interview Questions & How to Answer Them

### General / Architecture
**Q: "Walk me through your Task Management System project."**
> **Answer Strategy:** Start with the high-level architecture. "It's a full-stack application. The backend is a RESTful API built with Java 17 and Spring Boot, utilizing MongoDB for NoSQL data storage. For security, I implemented stateless authentication using Spring Security and JWT. The frontend is a Single Page Application built with Vanilla JS that communicates with the API."

**Q: "Why did you choose MongoDB over a relational database like MySQL?"**
> **Answer Strategy:** Mention flexibility. NoSQL databases like MongoDB allow flexible schemas, which is great if task models might evolve (e.g., adding sub-tasks or attachments later) without needing complex migrations.

**Q: "Why did you use Vanilla JavaScript instead of React or Angular?"**
> **Answer Strategy:** "I wanted to solidify my fundamental understanding of the DOM (Document Object Model) and the Fetch API before abstracting those concepts away with a framework. It allowed me to deeply understand how state and HTTP requests are managed natively in the browser."

### Security
**Q: "How does JWT authentication work in your application?"**
> **Answer Strategy:** "When a user logs in, the server generates a JWT containing their username and an expiration time. For subsequent requests, the client sends this token in the `Authorization` header. My `JwtAuthenticationFilter` validates the signature; if valid, it allows the request through without needing to check the database for a session."

**Q: "Why disable CSRF in your `SecurityConfig`?"**
> **Answer Strategy:** "CSRF (Cross-Site Request Forgery) attacks exploit cookie-based sessions where browsers automatically send cookies. Since my API is stateless and uses a JWT sent via the Authorization header (not a cookie), CSRF protection isn't strictly necessary."

### Best Practices & Challenges
**Q: "How do you handle data validation?"**
> **Answer Strategy:** "I use Jakarta Validation annotations on my models (like `@FutureOrPresent` for due dates). In the controller, I use `@Valid` to enforce these constraints before any business logic is executed."

**Q: "If you were to scale this application, what would you improve?"**
> **Answer Strategy:** 
> 1. Add pagination for the `GET /api/tasks` endpoint so it doesn't return massive arrays as users get more tasks.
> 2. Move email sending to an asynchronous process (like RabbitMQ) so the `/api/tasks` POST request isn't slowed down by the SMTP server.
> 3. Implement a global exception handler (`@ControllerAdvice`) for unified API error responses.
