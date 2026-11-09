--liquibase formatted sql

--changeset Alice:1 stripComments:false
--liquibasePercona:usePercona="false"
CREATE TABLE person (id INT);

--changeset Alice:2
--liquibasePercona:usePercona="false"
CREATE TABLE address (id INT);
