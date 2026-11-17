--liquibase formatted sql
--testInclude file:test-changelog-delegated-included.sql

--changeset Bob:root
--liquibasePercona:usePercona="true"
--rollback ALTER TABLE person DROP COLUMN city;
ALTER TABLE person ADD city VARCHAR(255) NULL;

--changeset Bob:tagged
--liquibasePercona:usePercona="true"
ALTER TABLE person ADD zip VARCHAR(10) NULL;
