--liquibase formatted sql

--changeset Alice:1
--liquibasePercona:usePercona="false"
CREATE TABLE person (name VARCHAR(255) NOT NULL, CONSTRAINT PK_PERSON PRIMARY KEY (name));

--changeset Alice:2
ALTER TABLE person ADD address VARCHAR(255) NULL;

--changeset Alice:3
--liquibasePercona:usePercona="false"
ALTER TABLE person ADD email VARCHAR(255) NULL;
