--liquibase formatted sql

--changeset Alice:1 labels:keep context:ctx
--liquibasePercona:usePercona="true"
--rollback ALTER TABLE person DROP COLUMN age;
ALTER TABLE person ADD age INT NULL;
