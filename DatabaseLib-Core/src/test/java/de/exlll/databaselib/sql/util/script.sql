CREATE TABLE IF NOT EXISTS `test`
(
    `id`   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(32)
) DEFAULT CHARACTER SET utf8;

SELECT * FROM `test` WHERE `id` = 10;
SELECT * FROM `test` WHERE `name` = ";";
SELECT * FROM `test` WHERE `name` = "\"";
SELECT * FROM `test` WHERE `name` = "\\";
SELECT * FROM `test` WHERE `name` = "\\\"";
SELECT * FROM `test` WHERE `name` = ';';
SELECT * FROM `test` WHERE `name` = '\'';
SELECT * FROM `test` WHERE `name` = '\\';
SELECT * FROM `test` WHERE `name` = '\\\'';
