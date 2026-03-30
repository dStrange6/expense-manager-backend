This repository contains the backend code for expense manager.

Please find the frontend code here: https://github.com/dStrange6/expense-manager-ui

Tech stack used:
-
- Backend : Java 21, Spring Boot, Spring Data JPA, Flyway
- Frontend : React, TypeScript, Tailwind CSS, Vite
- Database : PostgreSQL

Pre-requisites:
- Java 21, Node.js, and Docker installed on your machine.

To run the application, follow these steps:
1. Clone the repository and navigate to the project directory.
2. Start the PostgreSQL database using Docker: docker compose -f deployments/docker-compose/infra.yml up -d
3. Postrges starts on port 15432, database name is expense_db, username and password are postgres
4. Run the backend application: ./mvnw spring-boot:run
5. backend application runs on localhost:8080

API Endpoints:

