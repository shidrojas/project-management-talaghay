-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: talaghaydb
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
  `projectID` int NOT NULL AUTO_INCREMENT,
  `project_title` varchar(100) NOT NULL,
  `project_desc` varchar(100) NOT NULL,
  `project_pass` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `finished_at` timestamp(6) NULL DEFAULT NULL,
  `project_status` varchar(50) NOT NULL,
  PRIMARY KEY (`projectID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (1,'pet glamour','sample',NULL,'2025-08-18 10:40:32',NULL,'Active'),(2,'Aprtment Rental','apartments','$2b$12$6OYoG0FIBQbP.n56liS5kOtCH/ramfTK9r6Q5kh7Z0kgFNNmGj8Uu','2025-08-19 09:16:53',NULL,'Active'),(4,'bike rental','bikes','$2b$12$47Fe0Tm.WiNAoWjch5XKLektb9Fw.yE1z0tsidFmLUqq82HtF3mkK','2025-08-22 07:15:02',NULL,'Active');
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `taskID` int NOT NULL AUTO_INCREMENT,
  `projID` int DEFAULT NULL,
  `task_title` varchar(100) NOT NULL,
  `task_comment` varchar(100) DEFAULT NULL,
  `task_attachment` blob,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `passed_at` timestamp(6) NULL DEFAULT NULL,
  `assigned_to` varchar(100) NOT NULL,
  `task_status` varchar(100) NOT NULL,
  PRIMARY KEY (`taskID`),
  KEY `proj_ID_idx` (`projID`),
  CONSTRAINT `proj_ID` FOREIGN KEY (`projID`) REFERENCES `project` (`projectID`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` VALUES (1,4,'RULES',NULL,_binary 'e9713989fe104020878001f1a81dfe89.png','2025-07-24 04:42:25','2025-08-26 00:55:08.000000','Asher','Done'),(3,1,'homepage','',NULL,'2025-08-04 09:28:29',NULL,'Alrashid Rojas','To Do'),(5,1,'profile','',NULL,'2025-08-05 01:15:10',NULL,'Asher Laxa','In Progress'),(6,1,'settings','',NULL,'2025-08-06 03:23:46',NULL,'Asher Laxa','In Progress'),(7,1,'notification','system update',NULL,'2025-08-06 03:49:09','2025-08-06 08:10:51.000000','Alrashid Rojas','Done'),(27,1,'privacy',NULL,_binary '2578a64f3032498b9b785828c0c5b4ec','2025-08-19 09:06:14',NULL,'Alrashid Rojas','To Do'),(29,2,'rules',NULL,_binary '8dac9ac5bb2846bea995516e73bf7f32','2025-08-22 03:01:47',NULL,'Alrashid Rojas','To Do'),(30,4,'login',NULL,_binary '1586213a37cb4e20b2cd02d2ade1dffb','2025-08-26 00:54:41',NULL,'Alrashid Rojas','To Do');
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-28 11:17:16
