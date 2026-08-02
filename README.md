# 💰 Expense Splitter REST API (Spring Boot 3.x Backend)

A production-ready, highly testable Java Spring Boot 3.x backend application for managing shared expense groups, equal & custom expense splitting, real-time group balance calculation, and minimum transaction debt simplification.

Designed for production standards, GitHub portfolio presentation, and technical interview demonstrations.

---

## 🚀 Features

- **JWT Authentication & Security**: Stateless authentication with BCrypt password hashing and Spring Security.
- **Group & Member Management**: Create groups, list user memberships, and invite/remove members.
- **Expense Creation**:
  - **Equal Split**: Automatically calculates per-person shares with exact remainder allocation.
  - **Custom Split**: Supports explicit per-user split amounts with strict validation ($sum = totalAmount$).
- **Expense History & Details**: View group expense feeds ordered newest first, and view detailed split breakdowns.
- **Group Net Balance Calculation**: Real-time calculation of `totalPaid`, `totalOwed`, and `netBalance` for all group members in $\mathcal{O}(M + E + S)$ linear time.
- **Debt Simplification (Settle Up)**: Read-only Greedy Two-Pointer algorithm computing the minimum number of transactions (at most $N-1$) required to settle all group debts.
- **OpenAPI 3.0 / Swagger UI**: Interactive API documentation configured with Bearer JWT token support.
- **Comprehensive Unit Test Suite**: 12+ JUnit 5 & Mockito test cases covering all business logic without external database dependencies.

---

## 🛠️ Tech Stack

- **Java Version**: 21
- **Framework**: Spring Boot 3.5.6 (Spring Web, Spring Security, Spring Data JPA, Validation)
- **Database**: MySQL 8.x
- **JWT**: `io.jsonwebtoken:jjwt-api:0.12.7`
- **API Documentation**: `springdoc-openapi-starter-webmvc-ui:2.8.5`
- **Testing**: JUnit 5 & Mockito
- **Build Tool**: Maven

---

## 📁 Package Architecture & Layered Structure

```text
com.mathan.expensesplitter
├── config             # SecurityConfig, OpenApiConfig
├── controller         # AuthController, GroupController, ExpenseController
├── dto                # Auth, Group, and Expense Request/Response DTOs
│   ├── auth
│   └── expense
├── entity             # User, ExpenseGroup, GroupMember, Expense, ExpenseSplit
├── enums              # SplitType (EQUAL, CUSTOM)
├── exception          # GlobalExceptionHandler, Custom Runtime Exceptions
├── repository         # UserRepository, ExpenseGroupRepository, GroupMemberRepository, etc.
├── security           # JwtService, JwtAuthenticationFilter, SecurityUtils, UserPrincipal
└── service            # UserService, GroupService, ExpenseService & Implementations
```

---

## 📜 API Endpoint Summary

### 🔑 Authentication (`/api/auth`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new user | ❌ No |
| `POST` | `/api/auth/login` | Authenticate user & return Bearer JWT token | ❌ No |

### 👥 Expense Groups (`/api/groups`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/groups` | Create an expense group (creator added automatically) | 🔒 Yes |
| `GET` | `/api/groups` | List all groups the user belongs to | 🔒 Yes |
| `GET` | `/api/groups/{id}` | Get group details | 🔒 Yes |
| `POST` | `/api/groups/{groupId}/members` | Add member to group by email | 🔒 Yes |
| `DELETE` | `/api/groups/{groupId}/members/{userId}` | Remove member from group | 🔒 Yes |

### 💸 Expenses, Balances & Settlement (`/api/expenses` & `/api/groups`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/expenses` | Add expense (`EQUAL` or `CUSTOM` split) | 🔒 Yes |
| `GET` | `/api/groups/{groupId}/expenses` | View group expense history (newest first) | 🔒 Yes |
| `GET` | `/api/expenses/{expenseId}` | View detailed expense and split breakdown | 🔒 Yes |
| `GET` | `/api/groups/{groupId}/balances` | Calculate member net balances (`totalPaid`, `totalOwed`, `netBalance`) | 🔒 Yes |
| `GET` | `/api/groups/{groupId}/settlements` | Calculate minimum Settle Up debt transactions | 🔒 Yes |

---

## 🌐 Swagger UI Documentation

Access interactive API documentation and test endpoints directly:
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`

---

## 🏃 How to Run Locally

### 1. Prerequisites
- JDK 21+
- MySQL Server running on `localhost:3306`

### 2. Database Setup
Create database in MySQL:
```sql
CREATE DATABASE expense_splitter_db;
```

### 3. Environment Configuration
Copy `application.properties.example` to `application.properties` inside `src/main/resources/` and set credentials:
```properties
spring.datasource.username=root
spring.datasource.password=your_password
application.security.jwt.secret-key=your_256_bit_secret_key_minimum_32_characters
```

### 4. Build and Run
```bash
# Compile and run unit tests
mvn clean test

# Run application
mvn spring-boot:run
```

---

## 🧠 Key Technical Concepts & Interview Q&A

### 1. Why DTOs over Entity Exposure?
Prevents over-posting/mass assignment vulnerabilities, hides internal entity representations, prevents circular JSON serialization loops (`@ManyToOne`/`@OneToMany`), and decouples database schemas from API contracts.

### 2. Why `BigDecimal` for Monetary Calculations?
Avoids IEEE 754 floating-point rounding errors (e.g., `0.1 + 0.2 != 0.3`). `BigDecimal` with explicit scale and `RoundingMode.DOWN` guarantees penny-exact precision across equal and custom splits.

### 3. Why `@Transactional` on Service Methods?
Ensures atomic execution. Creating an expense saves the `Expense` header and multiple `ExpenseSplit` records. If any split validation fails, the entire transaction rolls back cleanly without leaving orphaned records.

### 4. How does the Debt Simplification Algorithm work?
Uses a **Greedy Two-Pointer Algorithm**:
1. Computes net balances for all group members ($netBalance = totalPaid - totalOwed$).
2. Separates members into `Creditors` ($netBalance > 0$) and `Debtors` ($netBalance < 0$).
3. Iteratively matches the top creditor and top debtor with $payment = \min(creditor, debtor)$, producing at most $N-1$ optimal transactions in $\mathcal{O}(N)$ time.

---

## 👤 Author
- **Mathan** - *Expense Splitter Project*
