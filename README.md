# BrainCampus Backend

BrainCampus is a modern, scalable, multi-tenant School Management System built with Spring Boot. It is designed to manage the complete lifecycle of educational institutions, including student management, attendance, academics, examinations, fees, transport, hostel, communication, and administration.

This repository contains the backend REST API for the BrainCampus platform.

---

## 🚀 Features

- Multi-Tenant Architecture
- JWT Authentication & Authorization
- Role-Based Access Control (RBAC)
- School Administration
- Student Information Management
- Teacher Management
- Attendance Management
- Examination & Result Management
- Fee Management
- Timetable Management
- Library Management
- Transport Management
- Hostel Management
- Notifications & Communication
- Reports & Analytics
- RESTful APIs
- Swagger/OpenAPI Documentation

---

## 🛠 Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA (Hibernate)
- PostgreSQL
- JWT Authentication
- Maven
- Lombok
- Bean Validation
- Swagger / OpenAPI

---

## 📁 Project Structure

```text
src
└── main
    ├── java
    │   └── com.braincampus
    │       ├── auth
    │       ├── config
    │       ├── security
    │       ├── tenant
    │       ├── user
    │       ├── common
    │       ├── exception
    │       ├── dto
    │       ├── entity
    │       ├── repository
    │       ├── service
    │       ├── controller
    │       └── util
    │
    └── resources
        ├── application.properties
        └── static
```

---

## ⚙️ Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 16+
- Git

---

## 🗄 Database Setup

Create a PostgreSQL database.

```sql
CREATE DATABASE braincampus;
```

Configure `application.properties`.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/braincampus
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## ▶️ Running the Project

Clone the repository.

```bash
git clone https://github.com/<your-username>/braincampus-backend.git
```

Navigate to the project.

```bash
cd braincampus-backend
```

Run the application.

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

---

## 🔐 Authentication

BrainCampus uses JWT-based authentication.

Planned APIs:

```
POST /api/v1/auth/register

POST /api/v1/auth/login

GET /api/v1/auth/me
```

---

## 🏫 Multi-Tenant Architecture

BrainCampus follows a multi-tenant architecture where each school operates in its own isolated environment while sharing the same application instance.

Example:

```
abc.braincampus.in

xyz.braincampus.in

greenvalley.braincampus.in
```

Each request is resolved to the appropriate tenant before processing.

---

## 📚 Modules

- Authentication
- User Management
- School Management
- Student Management
- Teacher Management
- Academic Management
- Attendance
- Fees
- Examinations
- Timetable
- Library
- Transport
- Hostel
- Communication
- Reports
- Settings

---

## 🧪 Testing

Run all tests.

```bash
mvn test
```

---

## 📖 API Documentation

Swagger/OpenAPI documentation will be available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📌 Roadmap

- [x] Project Setup
- [ ] Security (JWT)
- [ ] User Management
- [ ] Multi-Tenant Architecture
- [ ] Role-Based Access Control
- [ ] School Management
- [ ] Student Module
- [ ] Teacher Module
- [ ] Attendance Module
- [ ] Examination Module
- [ ] Fee Module
- [ ] Timetable Module
- [ ] Transport Module
- [ ] Library Module
- [ ] Hostel Module
- [ ] Reports & Analytics
- [ ] Notification Service
- [ ] Audit Logs
- [ ] Docker Support
- [ ] CI/CD Pipeline

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome. Please fork the repository, create a feature branch, and submit a pull request.

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

**Harsh Khare**

BrainCampus is being developed as a modern SaaS platform to simplify and digitize school administration with a scalable, secure, and production-ready architecture.
