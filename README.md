# 🏦 Online Bank Management System (OBMS)

A comprehensive, full-stack banking application built with Spring Boot that provides secure banking operations for customers, tellers, and administrators.

## 📋 Table of Contents

- [🏦 Online Bank Management System (OBMS)](#-online-bank-management-system-obms)
  - [📋 Table of Contents](#-table-of-contents)
  - [✨ Features](#-features)
    - [Customer Portal](#customer-portal)
    - [Teller Operations](#teller-operations)
    - [Admin Dashboard](#admin-dashboard)
    - [Security](#security)
  - [🛠 Tech Stack](#-tech-stack)
  - [📦 Prerequisites](#-prerequisites)
  - [🚀 Installation](#-installation)
  - [⚙️ Configuration](#️-configuration)
  - [▶️ Running the Application](#️-running-the-application)
  - [📁 Project Structure](#-project-structure)
  - [🧪 Testing](#-testing)
  - [🤝 Contributing](#-contributing)
  - [📄 License](#-license)

## ✨ Features

### Customer Portal

- Account management (Savings, Checking)
- Fund transfers and transactions
- Transaction history and statements
- Loan applications and tracking
- ATM locator and services

### Teller Operations

- Customer account management
- Transaction processing
- Cash handling operations

### Admin Dashboard

- User and role management
- Branch administration
- System monitoring and reports
- Loan approval workflow

### Security

- Role-based access control (RBAC)
- Spring Security integration
- Secure authentication and authorization
- Session management

## 🛠 Tech Stack

| Category       | Technology                     |
| -------------- | ------------------------------ |
| Backend        | Spring Boot 3.3.2, Java 17     |
| Security       | Spring Security 6              |
| Database       | MySQL 8.0, Spring Data JPA     |
| Frontend       | Thymeleaf, Bootstrap 5.3       |
| PDF Generation | OpenPDF, Flying Saucer         |
| Testing        | JUnit 5, Selenium 4, H2 (test) |
| Build Tool     | Maven                          |

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

## 🚀 Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/hena1ok/OBMS.git
   cd OBMS
   ```

2. **Create MySQL database**

   ```sql
   CREATE DATABASE obms_db;
   ```

3. **Configure database connection**

   Update `src/main/resources/application.properties` with your MySQL credentials.

4. **Build the project**
   ```bash
   ./mvnw clean install
   ```

## ⚙️ Configuration

Configure the following properties in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/obms_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## ▶️ Running the Application

**Using Maven Wrapper:**

```bash
./mvnw spring-boot:run
```

**Using JAR:**

```bash
./mvnw package
java -jar target/studCrud-0.0.1-SNAPSHOT.jar
```

Access the application at: `http://localhost:8080`

## 📁 Project Structure

```
OBMS/
├── src/
│   ├── main/
│   │   ├── java/com/example/bankmanagement/
│   │   │   ├── config/          # Application configurations
│   │   │   ├── controller/      # MVC Controllers
│   │   │   ├── model/           # Entity classes
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── security/        # Security configurations
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── static/          # CSS, JS, Images
│   │       ├── templates/       # Thymeleaf templates
│   │       └── application.properties
│   └── test/                    # Unit and integration tests
├── Documentation/               # Project documentation
├── pom.xml                      # Maven dependencies
└── README.md
```

## 🧪 Testing

**Run all tests:**

```bash
./mvnw test
```

**Run with test reports:**

```bash
./mvnw test -Dmaven.test.failure.ignore=true
```

Test reports are generated in `target/surefire-reports/`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Developed with ❤️ using Spring Boot**
