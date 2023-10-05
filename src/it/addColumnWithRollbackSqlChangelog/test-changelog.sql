--liquibase formatted sql

--changeset Alice:1
--rollback DROP TABLE person;
CREATE TABLE person (name VARCHAR(255) NOT NULL, CONSTRAINT PK_PERSON PRIMARY KEY (name));

--changeset Alice:2
--rollback ALTER TABLE person DROP COLUMN address;
ALTER TABLE person ADD address VARCHAR(255) NULL;

--changeset Alice:3
--liquibasePercona:usePercona="false"
--rollback ALTER TABLE person DROP COLUMN email;
ALTER TABLE person ADD email VARCHAR(255) NULL;
