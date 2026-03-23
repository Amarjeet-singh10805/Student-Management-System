CREATE DATABASE studata;
USE studata;
CREATE TABLE sdata (
  `Student_ID` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_520_ci NOT NULL,
  `first_name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_520_ci NOT NULL,
  `last_name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_520_ci NOT NULL,
  `major` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_520_ci NOT NULL,
  `Phone` varchar(12) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_520_ci DEFAULT NULL,
  `CGPA` varchar(5) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_520_ci NOT NULL,
  `DOB` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_520_ci DEFAULT NULL,
  UNIQUE KEY `Student_ID` (`Student_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_520_ci;
INSERT INTO sdata VALUES
  ('001','Amarjeet','Singh','CSE','9090909090','8.0','2005-08-01'),
  ('002','Abhishek','Adhikari','CSE','9191919191','8.5','2006-05-25'),
  ('003','Aditi','Khanna','CSE','9292929292','8.0','2006-06-25');
