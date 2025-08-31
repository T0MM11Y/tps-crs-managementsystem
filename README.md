# TPS CRS Management System

## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Development](#development)
- [Troubleshooting](#troubleshooting)

## Overview

The TPS CRS Management System is a Spring Boot-based web application designed for managing customer reward systems and regional reward alliances for Taiwan Mobile (TWM). The system provides comprehensive functionality for user account management, reward campaign administration, and system reporting.

### Key Features

- Single Sign-On (SSO) authentication with NT SSO integration
- User account and role-based access control management
- Serial campaign management with approval workflows
- Comprehensive reporting and analytics
- MoMo ID change request processing
- Email notification system
- Scheduled task management

### System Screenshots

#### Account Management

![Account Management](<Image/新增使用者帳號(Xīnzēng%20Shǐyòngzhě%20Zhànghào).png>)

#### User Sub ID Query

![User Sub ID Query](<Image/查詢需求單(Cháxún%20Xūqiú%20Dān).png>)

### Technology Stack

- **Framework**: Spring Boot 2.7.8
- **Language**: Java 11
- **Database**: Oracle Database 11g+
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Security**: OWASP ESAPI, Jasypt encryption
- **Additional Libraries**: Lombok, Apache POI, Bouncy Castle

## Prerequisites

### Software Requirements

- Java 11 or higher
- Maven 3.6+
- Oracle Database 11g or higher
- IDE (IntelliJ IDEA or Eclipse recommended)

### Environment Setup

1. Install Java 11 JDK
2. Install Maven
3. Configure Oracle Database connection
4. Set up SSL certificates (if required)

## Installation & Setup

### 1. Clone and Build

```bash
# Clone the repository
git clone <repository-url>
cd tps-crs-mgmt

# Build the project
mvn clean install

# Skip tests during build (if needed)
mvn clean install -DskipTests
```

### 2. Environment Configuration

**Note**: The `application-dev.properties` file is included as a template. Copy and modify it for your specific environment:

```bash
# Copy the template for development
cp src/main/resources/application-dev.properties src/main/resources/application-dev-local.properties

# Edit the copied file with your configuration
# Then use: -Dspring.profiles.active=dev-local
```

### 3. Run the Application

```bash
# Development mode
mvn spring-boot:run -Dspring.profiles.active=dev

# Or with JAR file
java -jar target/crs-mgmt-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 4. Access the Application

- Local Development: http://localhost:8080
- Development Environment: https://web.crsdev.taiwanmobile.com

## Configuration

### Environment Profiles

The application supports three environment profiles:

- **dev**: Development environment
- **uat**: User Acceptance Testing environment
- **prod**: Production environment

### Database Configuration

```properties
# Database Configuration (application-dev.properties)
spring.modb.jdbc-url=jdbc:oracle:thin:@//localhost:1521/xepdb1
spring.modb.username=momoapi
spring.modb.password=12345678
spring.modb.max-active=5
spring.modb.initial-size=3
spring.modb.min-idle=3
```

### SSO Configuration

```properties
# NT SSO Configuration
nt.sso.url=https://ssouat.taiwanmobile.com/SSOUAT/SSOAPI.aspx
nt.sso.sid=200348
nt.sso.auth.type=Form
```

### Security Configuration

```properties
# Jasypt Encryption
jasypt.encryptor.algorithm=PBEWithMD5AndDES
jasypt.encryptor.password=your_master_key

# Session Management
server.servlet.session.timeout=120m
```

## Running the Application

### Development Mode

```bash
# Using Maven
mvn spring-boot:run -Dspring.profiles.active=dev

# Using JAR file
java -jar target/crs-mgmt-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### Production Mode

```bash
# Build for production
mvn clean package -Dspring.profiles.active=prod

# Run production build
java -jar target/crs-mgmt-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Health Check

Verify application status:

```bash
curl http://localhost:8080/actuator/health
```

## Development

### Project Structure

```
src/main/java/com/twm/mgmt/
├── controller/          # REST controllers
├── service/            # Business logic
├── persistence/        # Data access layer
│   ├── entity/        # JPA entities
│   ├── repository/    # Data repositories
│   └── dto/           # Data transfer objects
├── config/            # Configuration classes
├── utils/             # Utility classes
├── enums/             # Enum definitions
└── validator/         # Input validation
```

### Code Style Guidelines

- Follow Spring Boot conventions
- Use Lombok annotations to reduce boilerplate code
- Implement proper error handling and logging
- Add comprehensive input validation
- Follow security best practices

### Database Schema

#### Core Tables

| Table Name                   | Description                                       |
| ---------------------------- | ------------------------------------------------- |
| `ACCOUNT`                    | User account information with role and department |
| `ROLE`                       | User roles and responsibilities (ROLE_XXX)        |
| `DEPARTMENT`                 | Organizational departments with BU tags           |
| `MENU`                       | System menu structure (parent titles)             |
| `PROGRAM`                    | System programs and functions (child items)       |
| `ACCOUNT_PERMISSION_PROGRAM` | User-specific permission overrides                |
| `ROLE_PERMISSION_PROGRAM`    | Role-based permission assignments                 |
| `ACCOUNT_APPROVAL_SETTING`   | Two-level approval workflow configuration         |
| `ACCOUNT_ACTION_HISTORY`     | Account modification audit trail                  |
| `LOGIN_HISTORY`              | User login tracking and session history           |

#### AP (Application Program) Tables

| Table Name               | Description                                  |
| ------------------------ | -------------------------------------------- |
| `AP_ACCOUNT`             | External AP account management by department |
| `AP_ACCOUNT_MAP_ACCOUNT` | Mapping between AP accounts and system users |
| `AP_KEY_IV`              | Encryption key and IV management             |
| `AP_KEY_TABLE`           | Additional key storage table                 |

#### Database Sequences

- `ACCOUNT_SEQ` - Account ID sequence
- `ROLE_SEQ` - Role ID sequence
- `MENU_SEQ` - Menu ID sequence
- `PROGRAM_SEQ` - Program ID sequence
- `ACCOUNT_PERMISSION_PROGRAM_SEQ` - Account permission sequence
- `ROLE_PERMISSION_PROGRAM_SEQ` - Role permission sequence
- `ACCOUNT_ACTION_HISTORY_SEQ` - Action history sequence
- `AP_KEY_IV_SEQ` - AP key sequence
- `LOGIN_HISTORY_ID` - Login history sequence

#### Key Relationships

- **ACCOUNT** → **ROLE**: Each account has one role
- **ACCOUNT** → **DEPARTMENT**: Each account belongs to one department
- **PROGRAM** → **MENU**: Programs are grouped under menus
- **ROLE_PERMISSION_PROGRAM**: Links roles to specific programs
- **ACCOUNT_PERMISSION_PROGRAM**: Provides user-specific permission overrides
- **ACCOUNT_APPROVAL_SETTING**: Configures L1/L2 approval chains per account

#### Authentication Process

1. User accesses `/sso/login`
2. System redirects to NT SSO service
3. SSO service validates credentials and returns token
4. Application validates token and retrieves user information
5. Session is created with role-based permissions
6. User gains access to authorized system functions

#### Session Management

- Session timeout: 120 minutes
- Server-side session storage
- Real-time session validation via interceptors

## Troubleshooting

### Common Issues

#### Database Connection Problems

```bash
# Test database connectivity
telnet <database-host> <database-port>

# Verify JDBC URL format
jdbc:oracle:thin:@//hostname:port/service_name
```

#### SSO Authentication Issues

1. Verify SSO URL configuration in properties file
2. Check SSL certificate validity
3. Validate SID and authentication type settings
4. Review network firewall configurations

#### Session Management Problems

1. Check session timeout configuration
2. Verify cookie settings and domain
3. Review interceptor logic implementation
4. Monitor session storage mechanism

### Log Analysis

```bash
# View application logs
tail -f logs/application.log

# Check access logs
tail -f logs/access_log.yyyy-MM-dd.log

# Filter error messages
grep ERROR logs/application.log
```

### Performance Monitoring

- Application health: `GET /actuator/health`
- Application metrics: `GET /actuator/metrics`
- Environment info: `GET /actuator/env`

---

**Version**: 0.0.1-SNAPSHOT  
**Spring Boot**: 2.7.8  
**Java**: 11  
**Last Updated**: 2024
