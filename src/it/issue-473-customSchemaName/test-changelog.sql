-- liquibase formatted sql

-- changeset test:1
--comment: This is the default schema of liquibase
CREATE DATABASE IF NOT EXISTS `liquibase` DEFAULT CHARACTER SET utf8mb4;

-- changeset test:2
CREATE DATABASE `test_database` DEFAULT CHARACTER SET utf8mb4;

-- changeset test:3
CREATE TABLE `test_database`.`test_table` (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- changeset test:4
-- liquibasePercona:usePercona="true"
ALTER TABLE `test_database`.`test_table`
 ADD COLUMN `test_column` DATETIME NULL DEFAULT NULL,
 ADD INDEX `test_column_idx` (`test_column` ASC);

