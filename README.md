## 💳 Card Management System (CMS)

A backend service for managing users, payment cards, and internal transfers between cards with currency conversion.

The project is implemented as part of a technical assignment and demonstrates clean architecture, transactional business logic, security, and test coverage.

## 🚀 Features

- User registration & authentication (JWT)

- Card issuance (Visa / MasterCard / MIR)

- Unique card number generation (Luhn algorithm)

- Internal transfers between user cards

- Currency conversion with predefined exchange rates

- Role-based access control (ADMIN / USER)

- Optimistic locking for balance safety

- Pagination & filtering

- Swagger / OpenAPI documentation

- Dockerized setup

- Unit tests for business logic

## 🛠 Tech Stack

- Java 21

- Spring Boot

- Spring Security (JWT)

- Spring Data JPA (Hibernate)

- PostgreSQL

- Liquibase

- Gradle

- Docker

- Swagger (springdoc-openapi)

- JUnit 5 + Mockito

## 📦 Project Structure
```
├── config
├── controller
├── exception
├── mapper
├── model
    ├── dto
    ├── entity
    ├── request
    ├── response
    ├── type
├── repository
├── service
    ├── impl
├── specification
└── util
```

## 🔐 Authentication

The application uses JWT authentication.

### Auth Header
Authorization: Bearer <jwt-token>

## 📘 Swagger / API Documentation

After starting the application, Swagger UI is available at:

http://localhost:8080/swagger-ui.html

or

http://localhost:8080/swagger-ui/index.html

## ⚙️ Environment Variables

The application requires the following environment variables:
```env
DB_URL=jdbc:postgresql://localhost:5432/cms
DB_USERNAME=postgres
DB_PASSWORD=postgres

JWT_SECRET=SECRETKEYFORTESTTASKPLEASEWORKASFSAFSDFSDFSDFSDFSDFSDFSDFSDFSDFSDFS
JWT_EXPIRATION=3600000
```


#### ▶️ Running the Application
#### 1️⃣ Run locally (Gradle)
```bash
./gradlew bootRun
```
#### 2️⃣ Run with Docker
- Build image
```bash
docker build -t cms-app .
```

#### Run container
```bash
docker run -p 8080:8080 \
-e DB_URL=jdbc:postgresql://host.docker.internal:5432/cms \
-e DB_USERNAME=postgres \
-e DB_PASSWORD=postgres \
-e JWT_SECRET=CHANGE_ME_SECRET_KEY \
-e JWT_EXPIRATION=3600000 \
cms-app
```
_Note: JWT_SECRET must be at least 256 bits for HS256 algorithm._

#### 3️⃣ Recommended: Docker Compose
```yaml
version: "3.8"
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: cms_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://db:5432/cms_db
      DB_USERNAME: postgres
      DB_PASSWORD: postgres
      JWT_SECRET: SECRETKEYFORTESTTASK
      JWT_EXPIRATION: 3600000
      VISA_BIN: 411111
      MASTER_BIN: 521234
      MIR_BIN: 220000
    depends_on:
      - db
```
```bash
docker compose up --build
```

## 📌 API Examples
### 🔑 Login

__POST /v1/api/auth/login__
``` json 

{
    "email": "user@example.com",
    "password": "password"
}
```

### Response

``` json
{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 💳 Create Card (ADMIN only)
__POST /v1/api/cards__
```json
{
    "userId": 1,
    "type": "VISA",
    "currency": "USD"
}
```


### 🔄 Transfer Between Cards
__POST /v1/api/transfers__

```json
{
    "fromCard": 1,
    "toCard": 2,
    "amount": 100
}
```



### Success Response

```json
{
    "id": 10,
    "fromCard": 1,
    "toCard": 2,
    "rate": 0.92,
    "fromAmount": 100,
    "toAmount": 92,
    "createdAt": "2026-01-10T12:00:00"
}
```

### Error Example
```json
{
    "code": "insufficient_balance",
    "message": "Insufficient balance on card"
}
```


## 🧪 Tests

### Unit tests cover:

- Card creation

- Transfers

- Balance checks

- Error scenarios

- Repository interactions

### Run tests:

``` bash
./gradlew test
```

## 🧠 Design Decisions

* Optimistic locking (@Version) is used to prevent concurrent balance updates

* Card number uniqueness is enforced at DB level

* Exchange rates are initialized at application startup

* Transfers are transactional

* Business logic is isolated in services

* Security logic separated from domain logic

## 👤 Author

#### Ali Gasimzade
#### Backend Java Developer

#### GitHub: https://github.com/Gasimzade09

_Focus: Java, Spring Boot, Microservices, FinTech_

## 📄 License

#### This project is created for educational and demonstration purposes.