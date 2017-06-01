CREATE TABLE IF NOT EXISTS `test_table` (
  `user_id`   INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50)
);

INSERT INTO `test_table` (`name`) VALUES ('John');
INSERT INTO `test_table` (`name`) VALUES ('Jane');