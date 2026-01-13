# Card Management & Transfer Service

Test assignment: backend service for card management and transfers between user cards.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL
- Liquibase
- Gradle
- JUnit 5 + Mockito

---

## 📌 Features

- User registration & authentication (JWT)
- Card creation (Visa / MasterCard / MIR)
- Card listing with pagination & filtering
- Transfers between user's own cards
- Currency conversion with predefined exchange rates
- Role-based access control (ADMIN / USER)
- Optimistic locking for balance updates

---

## 🔐 Authentication

Authentication is implemented using **JWT**.

### Login
POST /v1/api/auth/login


Request:
```json
{
  "email": "user@test.com",
  "password": "password"
}

```
Response:
```
{
  "token": "jwt-token"
}
```

### Use the token in requests:

#### Authorization: Bearer < token >

## 💳 Cards
### Create card
POST /v1/api/cards


Available only for ADMIN

### Request:

```
{
  "userId": 1,
  "type": "VISA",
  "currency": "USD"
}
```

### Get cards
``` 
GET /v1/api/cards
```

* ADMIN can request cards of any user

* USER can access only own cards

## 🔄 Transfers
### Create transfer between own cards
```
POST /v1/api/transfers
Body:

{
  "fromCard": 1,
  "toCard": 2,
  "amount": 100
}
```

### Rules:

* Cards must belong to the same user

* Only ACTIVE cards are allowed

* Balance is checked before transfer

* Currency conversion is applied automatically

## 💱 Currency Exchange

### Exchange rates are initialized on application startup.

### Supported currencies:

* AZN

* USD

* EUR

* RUB

Rates are stored in-memory and used for transfer calculations.

## 🧪 Tests

* Unit tests for services

* Mockito is used for dependencies

* Business logic is fully covered

### Run tests:

```
./gradlew test
```

## 🗄 Database

* PostgreSQL

* Schema managed via Liquibase

* Unique constraint on card number

* Optimistic locking using @Version

▶️ Local Setup & Run Guide
## ▶️ Local Setup & Run Guide

This section describes how to run the application locally.

---

### ✅ Prerequisites

Make sure the following tools are installed:

- Java **21**
- Gradle **8+**
- PostgreSQL **14+**
- Git

---

## 1️⃣ Clone repository

```bash
git clone <repository-url>
cd <project-folder>
```
## 2️⃣ Environment variables

The application uses environment variables for sensitive configuration.

Create .env file

In the project root directory create a file:

.env


Example:
```
DB_URL=jdbc:postgresql://localhost:5432/card_service
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=SECRETKEYFORTESTTASK
JWT_EXPIRATION=3600000
```

## 3️⃣ Database setup

### Create PostgreSQL database:

``` sql 
CREATE DATABASE card_service;
```

## 4️⃣ Application configuration
_application.yml reads values from environment variables:_

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

application:
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION}
```
## 5️⃣ Run application
Using Gradle
```bash
./gradlew bootRun
```

or (Windows):

```bash
gradlew.bat bootRun
```

## 6️⃣ Verify application

#### After startup application will be available at:

<a href="http://localhost:8080">http://localhost:8080</a>


#### Health check example:

``` 
GET /actuator/health
```

## 7️⃣ Running tests
```
./gradlew test
```

## 🐳 Optional: Run with Docker
```bash 
docker-compose up -d
```

## ℹ Notes

* Transfers are transactional

* Card number generation handles collisions

* Security is stateless (JWT, no sessions)

## 👤 Author

### Ali Gasimzade  
__Java Backend Developer__

_This project was implemented as a test assignment to demonstrate backend development skills,
including Spring Boot, security, transactional logic, and clean architecture principles._