--liquibase formatted sql

--changeset Bob:included
--liquibasePercona:usePercona="true"
--rollback ALTER TABLE person DROP COLUMN country;
ALTER TABLE person ADD country VARCHAR(255) NULL;
