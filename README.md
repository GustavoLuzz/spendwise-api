# 💸 SpendWise - Personal Finance Manager

**SpendWise** is a RESTful API built with **Java Spring Boot** to help users manage their personal finances. It allows users to track income and expenses, organize them into categories, and gain insights through clean data structures and secure access.

---

## 🚀 Key Features

- 👤 **User Management**: Sign up, log in, and manage user details.
- 🗂️ **Category Management**: Create and manage custom income and expense categories.
- 💰 **Transaction Tracking**: Record financial transactions with flexible attributes.
- 🔐 **Authentication**: Secure authentication using JWT and Spring Security.
- 🗄️ **Data Persistence**: PostgreSQL integration with Spring Data JPA.
- 🐳 **Docker Support**: Ready-to-use PostgreSQL container via Docker Compose.
- 📄 **API Documentation**: Swagger UI for exploring and testing API endpoints.

---

## 🧩 Entities

### 🧑‍💼 `User`
> Represents registered users of the system.

| Field       | Type      | Description                       |
|-------------|-----------|-----------------------------------|
| `id`        | Long      | Primary key (auto-increment)      |
| `name`      | String    | Full name of the user             |
| `email`     | String    | Unique email address              |
| `password`  | String    | Encrypted user password           |
| `createdAt` | Timestamp | Account creation timestamp        |

---

### 🏷️ `Category`
> Represents a transaction category.

| Field     | Type   | Description                                      |
|-----------|--------|--------------------------------------------------|
| `id`      | Long   | Primary key (auto-increment)                     |
| `name`    | String | Category name                                    |
| `type`    | String | Either `"INCOME"` or `"EXPENSE"`                |
| `userId`  | Long   | Foreign key referring to the owner `User`        |

---

### 💳 `Transaction`
> Represents an individual financial transaction.

| Field        | Type     | Description                                      |
|--------------|----------|--------------------------------------------------|
| `id`         | Long     | Primary key (auto-increment)                     |
| `amount`     | Decimal  | Transaction amount (e.g., 199.99)                |
| `description`| String   | Optional description                             |
| `date`       | Date     | Date of the transaction                          |
| `type`       | String   | `"INCOME"` or `"EXPENSE"`                        |
| `categoryId` | Long     | Foreign key linking to a `Category`              |
| `userId`     | Long     | Foreign key linking to the transaction's `User`  |

---

## ⚙️ Database Configuration

Make sure your `application.properties` or `application.yml` contains:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/spendwise
spring.datasource.username=admin
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update
```

---

## ▶️ Running the Project

1. Make sure Docker is installed and running.
2. Start PostgreSQL using Docker Compose:

```bash
docker-compose up -d
```

3. Run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

4. Access the API at:  
   👉 `http://localhost:8080`

---

## 📘 API Documentation

Swagger UI is available at:  
👉 `http://localhost:8080/swagger-ui.html`

---


## 🪪 License

This project is licensed under the MIT License — see the `LICENSE` file for details.

---

## 📬 Contact

For questions or feedback, reach out at: [gustavo_lux@outlook.com]
