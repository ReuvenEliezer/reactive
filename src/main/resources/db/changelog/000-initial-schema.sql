-- liquibase formatted sql
-- changeset liquibase:1111111-0::create-root-user
-- CREATE USER 'root'@'127.0.0.1' IDENTIFIED WITH mysql_native_password BY 'administrator';
-- GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;
-- FLUSH PRIVILEGES;
-- changeset liquibase:1111111-0::initial-schema
CREATE TABLE IF NOT EXISTS employee (
    emp_no SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    gender VARCHAR(40),
    birth_date DATE NULL,
    hire_date DATE NULL
    );
-- changeset liquibase:1111111-1::initial-schema
CREATE TABLE IF NOT EXISTS department (
    dept_no     CHAR(4)         NOT NULL,
    dept_name   VARCHAR(40)     NOT NULL,
    PRIMARY KEY (dept_no),
    UNIQUE (dept_name)
    );

-- changeset liquibase:1111111-2::initial-schema
CREATE TABLE IF NOT EXISTS dept_manager (
    emp_no       SERIAL          NOT NULL,
    dept_no      CHAR(4)         NOT NULL,
    from_date    DATE            NOT NULL,
    to_date      DATE            NOT NULL,
    FOREIGN KEY (emp_no)  REFERENCES employee (emp_no)    ON DELETE CASCADE,
    FOREIGN KEY (dept_no) REFERENCES department (dept_no) ON DELETE CASCADE,
    PRIMARY KEY (emp_no,dept_no)
    );

-- changeset liquibase:1111111-3::initial-schema
CREATE TABLE IF NOT EXISTS dept_emp (
    emp_no      SERIAL          NOT NULL,
    dept_no     CHAR(4)         NOT NULL,
    from_date   DATE            NOT NULL,
    to_date     DATE            NOT NULL,
    FOREIGN KEY (emp_no)  REFERENCES employee   (emp_no)  ON DELETE CASCADE,
    FOREIGN KEY (dept_no) REFERENCES department (dept_no) ON DELETE CASCADE,
    PRIMARY KEY (emp_no,dept_no)
    );

-- changeset liquibase:1111111-4::initial-schema
CREATE TABLE IF NOT EXISTS titles (
    emp_no      SERIAL          NOT NULL,
    title       VARCHAR(50)     NOT NULL,
    from_date   DATE            NOT NULL,
    to_date     DATE,
    FOREIGN KEY (emp_no) REFERENCES employee (emp_no) ON DELETE CASCADE,
    PRIMARY KEY (emp_no,title, from_date)
    );

-- changeset liquibase:1111111-5::initial-schema
CREATE TABLE IF NOT EXISTS salaries (
    emp_no      SERIAL          NOT NULL,
    salary      INT             NOT NULL,
    from_date   DATE            NOT NULL,
    to_date     DATE            NOT NULL,
    FOREIGN KEY (emp_no) REFERENCES employee (emp_no) ON DELETE CASCADE,
    PRIMARY KEY (emp_no, from_date)
    );
-- changeset liquibase:1111111-6::initial-schema
CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL
);
