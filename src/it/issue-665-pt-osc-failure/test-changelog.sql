--liquibase formatted sql

--changeset init:1
CREATE TABLE test_table (
                            id bigint NOT NULL AUTO_INCREMENT,
                            name varchar(50) DEFAULT NULL,
                            PRIMARY KEY (id)
) ENGINE=InnoDB;
INSERT INTO test_table (name) VALUES ('alice'), (NULL), ('charlie'), (NULL), ('eve');

--changeset repro:001-alter-name-not-null
--liquibasePercona:usePercona="true"
ALTER TABLE test_table MODIFY COLUMN name varchar(50) NOT NULL;
--rollback ALTER TABLE test_table MODIFY COLUMN name varchar(50) NULL;
