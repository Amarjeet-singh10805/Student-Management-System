# Student Management System — Java + Swing + JDBC + MySQL

A fully working application to manage student records using Java Swing for the GUI,
JDBC for database connectivity, and MySQL as the backend.

## Requirements

- **Java 8** or higher
- **MySQL 8.x** server
- **mysql-connector-j-8.0.x.jar** (JDBC driver) on your classpath

## Database Schema

```sql
CREATE TABLE sdata (
  Student_ID  VARCHAR(10)  NOT NULL,
  first_name  VARCHAR(30)  NOT NULL,
  last_name   VARCHAR(30)  NOT NULL,
  major       VARCHAR(50)  NOT NULL,
  Phone       VARCHAR(12)  DEFAULT NULL,
  GPA         VARCHAR(5)   NOT NULL,
  DOB         VARCHAR(30)  DEFAULT NULL,
  UNIQUE KEY Student_ID (Student_ID)
);
```
