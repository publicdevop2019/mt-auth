-- MySQL dump 10.13  Distrib 8.0.25, for Linux (x86_64)
--
-- Host: localhost    Database: auth_dev
-- ------------------------------------------------------
-- Server version	8.0.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `allowed_header_map`
--

DROP TABLE IF EXISTS `allowed_header_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `allowed_header_map` (
  `id` bigint NOT NULL,
  `allowed_header` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`allowed_header`),
  CONSTRAINT `custom_constaint_4` FOREIGN KEY (`id`) REFERENCES `cors_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `allowed_header_map`
--

LOCK TABLES `allowed_header_map` WRITE;
/*!40000 ALTER TABLE `allowed_header_map` DISABLE KEYS */;
INSERT INTO `allowed_header_map` VALUES (857844656111616,'Accept'),(857844656111616,'Access-Control-Request-Method'),(857844656111616,'Authorization'),(857844656111616,'changeId'),(857844656111616,'Content-Type'),(857844656111616,'lastupdateat'),(857844656111616,'uuid'),(857844656111616,'x-requested-with'),(857844656111616,'X-XSRF-TOKEN'),(881941651914966,'authorization'),(881941651914966,'X-XSRF-TOKEN'),(881941651914966,'uuid');
/*!40000 ALTER TABLE `allowed_header_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_record`
--

DROP TABLE IF EXISTS `audit_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_record` (
  `id` bigint NOT NULL,
  `action_name` varchar(255) NOT NULL,
  `detail` text NOT NULL,
  `action_at` datetime NOT NULL,
  `action_by` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_record`
--

LOCK TABLES `audit_record` WRITE;
/*!40000 ALTER TABLE `audit_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cache_control_map`
--

DROP TABLE IF EXISTS `cache_control_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cache_control_map` (
  `id` bigint NOT NULL,
  `cache_control` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`cache_control`),
  CONSTRAINT `custom_constaint_2` FOREIGN KEY (`id`) REFERENCES `cache_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cache_control_map`
--

LOCK TABLES `cache_control_map` WRITE;
/*!40000 ALTER TABLE `cache_control_map` DISABLE KEYS */;
INSERT INTO `cache_control_map` VALUES (858322708725762,'MAX_AGE'),(858360529289217,'MAX_AGE');
/*!40000 ALTER TABLE `cache_control_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cache_profile`
--

DROP TABLE IF EXISTS `cache_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cache_profile` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `allow_cache` bit(1) NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `etag` bit(1) NOT NULL,
  `expires` bigint DEFAULT NULL,
  `max_age` bigint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `smax_age` bigint DEFAULT NULL,
  `vary` varchar(255) DEFAULT NULL,
  `weak_validation` bit(1) NOT NULL,
  `project_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_8t5bl3137yxhno0b5hndu6f4a` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cache_profile`
--

LOCK TABLES `cache_profile` WRITE;
/*!40000 ALTER TABLE `cache_profile` DISABLE KEYS */;
INSERT INTO `cache_profile` VALUES (858322708725762,'2021-11-17 03:44:06','0U8AZTODP4H0','2021-11-17 23:45:55','0U8AZTODP4H0',4,_binary '','0X8G900BJFGG',NULL,_binary '',NULL,10,'缓存10秒Etag',NULL,NULL,_binary '\0','0P8HE307W6IO'),(858360529289217,'2021-11-17 23:46:22','0U8AZTODP4H0','2021-11-17 23:46:22','0U8AZTODP4H0',0,_binary '','0X8G9HDSXCZK',NULL,_binary '\0',NULL,10,'缓存10秒无Etag',NULL,NULL,_binary '\0','0P8HE307W6IO'),(858360594825216,'2021-11-17 23:48:27','0U8AZTODP4H0','2021-11-17 23:48:27','0U8AZTODP4H0',0,_binary '\0','0X8G9HEVMSCH',NULL,_binary '\0',NULL,NULL,'无缓存',NULL,NULL,_binary '\0','0P8HE307W6IO'),(881933328318480,'2023-04-22 09:05:29','0U8OMAGVFMS3','2023-04-22 09:05:29','0U8OMAGVFMS3',0,_binary '\0','0X8OMAKSU4UN',NULL,_binary '\0',NULL,NULL,'禁止缓存',NULL,NULL,_binary '\0','0P8OMAHSU0W4');
/*!40000 ALTER TABLE `cache_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `change_record`
--

DROP TABLE IF EXISTS `change_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `change_record` (
  `id` bigint NOT NULL,
  `change_id` varchar(255) NOT NULL,
  `entity_type` varchar(255) NOT NULL,
  `return_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2lxn3cforhkp6bwr3a2tjrnbw` (`change_id`,`entity_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `change_record`
--

LOCK TABLES `change_record` WRITE;
/*!40000 ALTER TABLE `change_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `change_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `accessible_` bit(1) DEFAULT NULL,
  `auto_approve` bit(1) NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) DEFAULT NULL,
  `role_id` varchar(255) DEFAULT NULL,
  `secret` varchar(255) DEFAULT NULL,
  `access_token_validity_seconds` int DEFAULT NULL,
  `refresh_token_validity_seconds` int DEFAULT NULL,
  `external_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1jthx8yoaobfm9liox4t1dnis` (`domain_id`),
  UNIQUE KEY `UK96fgye6hf0y8iwvh3csi2ps2a` (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES (843498099048450,'2020-12-25 02:22:27','0','2022-01-05 00:50:53','0U8AZTODP4H0',4,_binary '',_binary '\0','0C8AZTODP4HT','负责管理应用与用户等信息','权限中心','auth-svc','0P8HE307W6IO','0Z8HHJ489SE0','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',120,0,'http://localhost:8080'),(843498099048451,'2020-12-25 02:22:27','0','2022-01-05 00:55:31','0U8AZTODP4H0',2,_binary '',_binary '\0','0C8AZTODP4H0','暂未使用，一个简单的NoSQL存储服务','store','object-svc','0P8HE307W6IO','0Z8HHJ489SE1','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',122,0,'http://localhost:3000'),(843498099048459,'2020-12-25 02:22:27','0','2022-01-05 00:52:59','0U8AZTODP4H0',2,_binary '\0',_binary '\0','0C8AZTODP4H8','<script>test</script>','rightRoleNotSufficientResourceId',NULL,'0P8HE307W6IO','0Z8HHJ489SE9','$2a$12$5PlVPPijEbr69oYIdC9e2uvDzW7NPqxnoxU9aKFiemB2vG7UmaC1S',120,0,'http://localhost:3000'),(843498099048553,'2020-12-25 02:22:27','0','2022-01-05 00:52:39','0U8AZTODP4H0',2,_binary '',_binary '\0','0C8AZTODP4HZ','仅供测试使用','测试后端','test-svc','0P8HE307W6IO','0Z8HHJ489SEA','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',122,0,'http://localhost:9999'),(843509306228737,'2020-12-25 08:18:43','0','2022-01-05 01:08:35','0U8AZTODP4H0',3,_binary '',_binary '\0','0C8AZYTQ5W5C','反向代理服务，负责处理mt-auth的核心功能','反向代理',NULL,'0P8HE307W6IO','0Z8HHJ489SEB','$2a$12$.HPmCVWM86g.OmWmWgsttuXB95onmQXhsNqwA.ugS9qdn/rxnLk/6',120,0,'http://localhost:3000'),(843509757116417,'2020-12-25 08:33:03','0','2022-05-07 21:13:52','0U8AZTODP4H0',7,_binary '\0',_binary '\0','0C8AZZ16LZB4','mt-auth前端','登陆客户端UI',NULL,'0P8HE307W6IO','0Z8HHJ489SEC','$2a$12$lBpm19kTPZserQCa72/67OAPAjPZWc0yJ9Kh6xPYPYOSVuFWS94IC',120,1200,NULL),(843511877861378,'2020-12-25 09:40:28','0','2022-01-05 00:53:31','0U8AZTODP4H0',4,_binary '\0',_binary '\0','0C8B00098WLD','用户注册时使用','注册客户端UI',NULL,'0P8HE307W6IO','0Z8HHJ489SED','$2a$12$lBpm19kTPZserQCa72/67OAPAjPZWc0yJ9Kh6xPYPYOSVuFWS94IC',120,0,NULL),(843512635457539,'2020-12-25 10:04:33','0','2022-01-05 01:09:22','0U8AZTODP4H0',4,_binary '\0',_binary '\0','0C8B00CSATJ6','用户密码验证策略','测试应用',NULL,'0P8HE307W6IO','0Z8HHJ489SEF','$2a$12$k73Ru5FaxyoX5NxQ775SxefRDY04HVvUNekQ.X/tBZJcsUuBUqiMS',120,0,'http://localhost:3000'),(843512635457561,'2020-12-25 10:04:33','0','2022-01-05 00:52:23','0U8AZTODP4H0',2,_binary '',_binary '\0','0C8AZTODP4I0','仅供测试使用','测试用资源应用',NULL,'0P8HE307W6IO','0Z8HHJ489SEG','$2a$12$k73Ru5FaxyoX5NxQ775SxefRDY04HVvUNekQ.X/tBZJcsUuBUqiMS',120,0,'http://localhost:3000'),(862433369391105,'2022-02-15 21:38:28','0U8HPG93IED3','2022-02-16 21:20:33','0U8HPG93IED3',2,_binary '',_binary '\0','0C8HPGF4GBUP',NULL,'分布式事务服务','saga-svc','0P8HPG99R56P','0Z8HPGF4RKEA','$2a$12$YQ7pI6YcDzbmbBYL1GXdEO3NT3hh34UBqKbY4XLt16XhFIMJ.h6c.',123,0,'http://localhost:3000'),(862433780433088,'2022-02-15 21:51:33','0U8HPG93IED3','2022-02-16 21:19:51','0U8HPG93IED3',1,_binary '',_binary '\0','0C8HPGLXHMET','商城后端','商城服务','product-svc','0P8HPG99R56P','0Z8HPGLXHMF5','$2a$12$bQOLGCybO.4EfjfvVdcCKOmUC3O9izskZisesUHcUzK2MsqPq.jX.',123,0,'http://localhost:3000'),(862433826570240,'2022-02-15 21:53:00','0U8HPG93IED3','2022-02-21 21:36:29','0U8HPG93IED3',5,_binary '\0',_binary '','0C8HPGMON9J5',NULL,'商城UI',NULL,'0P8HPG99R56P','0Z8HPGMOYI2P','$2a$12$Hk/uzmLBZt6JpIjQcFLedOnXZTHznBsBalKfPHjJDfRUVpt2vvjLK',1200,0,NULL),(862524186558475,'2022-02-17 21:45:29','0U8HPG93IED3','2022-02-21 21:37:00','0U8HPG93IED3',5,_binary '\0',_binary '','0C8HQM52YN7K',NULL,'管理员UI',NULL,'0P8HPG99R56P','0Z8HQM52YN7W','$2a$12$m/wOPj/pXiPcQ5LD1KSiturMbi7xSgD4hr.tkwT015zdwBs8a0CBu',1200,0,NULL),(863884654149898,'2022-03-19 22:33:35','0U8AZTODP4H0','2022-03-19 22:33:38','0U8AZTODP4H0',1,_binary '',_binary '\0','0C8I7Z4Q8N09',NULL,'resource client',NULL,'0P8HE307W6IO','0Z8I7Z4Q8N4B','$2a$12$MD1Jf7ffXb1saXmz0Ryohu1d1b0OFMoCq4P1Qd8U4G4yVou/0Y31m',1800,0,'http://localhost:3000'),(863884655198262,'2022-03-19 22:33:36','0U8AZTODP4H0','2023-03-23 16:39:08','0U8AZTODP4H0',2,_binary '\0',_binary '\0','0C8I7Z4QJVNC',NULL,'non resource client',NULL,'0P8HE307W6IO','0Z8I7Z4QV41J','$2a$12$v3MHrwQv6HbGR7.E9pHVEeaoODfw5sbF7MIAhazvv6tzT5zB3TCsS',1800,120,'http://localhost:3001'),(881933189382715,'2023-04-22 09:01:04','0U8OMAGVFMS3','2023-04-22 09:01:04','0U8OMAGVFMS3',0,_binary '',_binary '\0','0C8OMAII48XE',NULL,'演示后端应用','demo-svc','0P8OMAHSU0W4','0Z8OMAII49B6','$2a$12$HrnzsquGART7R73Y3Ol0FuF29twEuM2EbNl8D1dBTeMFBjsTJNJDq',120,0,'http://localhost:8111'),(881933420068882,'2023-04-22 09:08:24','0U8OMAGVFMS3','2023-04-22 09:08:24','0U8OMAGVFMS3',0,_binary '\0',_binary '\0','0C8OMAMBGNWG',NULL,'演示前端应用',NULL,'0P8OMAHSU0W4','0Z8OMAMBGNWX','$2a$12$b8K1fiWZNV7B5.6nz0IhAeEbdYDU1rAJyLM11HeUsg/r4NWaqYl8K',120,0,NULL),(881933438418954,'2023-04-22 09:08:59','0U8OMAGVFMS3','2023-04-22 09:08:59','0U8OMAGVFMS3',0,_binary '\0',_binary '','0C8OMAMM2QDC',NULL,'演示单点登录应用',NULL,'0P8OMAHSU0W4','0Z8OMAMMDYX5','$2a$12$YV.x/3qr5XO4K92pMBdZiuvCjVeEcGQbFyW9gLFWc3/5StJNIdKey',120,0,NULL),(881933451001878,'2023-04-22 09:09:23','0U8OMAGVFMS3','2023-04-22 09:09:23','0U8OMAGVFMS3',0,_binary '\0',_binary '\0','0C8OMAMTVNY8',NULL,'演示用户名密码登录应用',NULL,'0P8OMAHSU0W4','0Z8OMAMTVNYT','$2a$12$0BwD9acaMkLPYblIV5BmSe/4A2IF2UscrDNWsiQ68zAdagbnHx10m',120,1200,NULL);
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_grant_type_map`
--

DROP TABLE IF EXISTS `client_grant_type_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client_grant_type_map` (
  `id` bigint NOT NULL,
  `grant_type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`grant_type`),
  CONSTRAINT `custom_constaint_1` FOREIGN KEY (`id`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_grant_type_map`
--

LOCK TABLES `client_grant_type_map` WRITE;
/*!40000 ALTER TABLE `client_grant_type_map` DISABLE KEYS */;
INSERT INTO `client_grant_type_map` VALUES (843498099048450,'CLIENT_CREDENTIALS'),(843498099048451,'CLIENT_CREDENTIALS'),(843498099048459,'CLIENT_CREDENTIALS'),(843498099048553,'CLIENT_CREDENTIALS'),(843509306228737,'CLIENT_CREDENTIALS'),(843509757116417,'PASSWORD'),(843509757116417,'REFRESH_TOKEN'),(843511877861378,'CLIENT_CREDENTIALS'),(843512635457539,'PASSWORD'),(843512635457561,'PASSWORD'),(862433369391105,'CLIENT_CREDENTIALS'),(862433780433088,'CLIENT_CREDENTIALS'),(862433826570240,'AUTHORIZATION_CODE'),(862524186558475,'AUTHORIZATION_CODE'),(863884654149898,'PASSWORD'),(863884655198262,'PASSWORD'),(863884655198262,'REFRESH_TOKEN'),(881933189382715,'CLIENT_CREDENTIALS'),(881933420068882,'CLIENT_CREDENTIALS'),(881933438418954,'AUTHORIZATION_CODE'),(881933451001878,'PASSWORD'),(881933451001878,'REFRESH_TOKEN');
/*!40000 ALTER TABLE `client_grant_type_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_redirect_url_map`
--

DROP TABLE IF EXISTS `client_redirect_url_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client_redirect_url_map` (
  `id` bigint NOT NULL,
  `redirect_url` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`redirect_url`),
  CONSTRAINT `custom_constaint_12` FOREIGN KEY (`id`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_redirect_url_map`
--

LOCK TABLES `client_redirect_url_map` WRITE;
/*!40000 ALTER TABLE `client_redirect_url_map` DISABLE KEYS */;
INSERT INTO `client_redirect_url_map` VALUES (862433826570240,'http://localhost:4200/account'),(862433826570240,'https://www.letsauth.cloud/mall/account'),(862524186558475,'http://localhost:4400'),(862524186558475,'https://www.letsauth.cloud/admin'),(881933438418954,'http://localhost:3000'),(881933438418954,'http://localhost:8083');
/*!40000 ALTER TABLE `client_redirect_url_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_type_map`
--

DROP TABLE IF EXISTS `client_type_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client_type_map` (
  `id` bigint NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`type`),
  CONSTRAINT `custom_constaint_0` FOREIGN KEY (`id`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_type_map`
--

LOCK TABLES `client_type_map` WRITE;
/*!40000 ALTER TABLE `client_type_map` DISABLE KEYS */;
INSERT INTO `client_type_map` VALUES (843498099048450,'BACKEND_APP'),(843498099048451,'BACKEND_APP'),(843498099048459,'BACKEND_APP'),(843498099048553,'BACKEND_APP'),(843509306228737,'BACKEND_APP'),(843509757116417,'FRONTEND_APP'),(843511877861378,'FRONTEND_APP'),(843512635457539,'BACKEND_APP'),(843512635457561,'BACKEND_APP'),(862433369391105,'BACKEND_APP'),(862433780433088,'BACKEND_APP'),(862433826570240,'FRONTEND_APP'),(862524186558475,'FRONTEND_APP'),(863884654149898,'BACKEND_APP'),(863884655198262,'BACKEND_APP'),(881933189382715,'BACKEND_APP'),(881933420068882,'FRONTEND_APP'),(881933438418954,'FRONTEND_APP'),(881933451001878,'FRONTEND_APP');
/*!40000 ALTER TABLE `client_type_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cors_origin_map`
--

DROP TABLE IF EXISTS `cors_origin_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cors_origin_map` (
  `id` bigint NOT NULL,
  `allowed_origin` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`allowed_origin`),
  CONSTRAINT `custom_constaint_3` FOREIGN KEY (`id`) REFERENCES `cors_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cors_origin_map`
--

LOCK TABLES `cors_origin_map` WRITE;
/*!40000 ALTER TABLE `cors_origin_map` DISABLE KEYS */;
INSERT INTO `cors_origin_map` VALUES (857844656111616,'http://192.168.2.16'),(857844656111616,'http://192.168.2.16:3000'),(857844656111616,'http://192.168.2.16:4200'),(857844656111616,'http://192.168.2.23:3000'),(857844656111616,'http://192.168.2.23:4200'),(857844656111616,'http://192.168.2.23:4300'),(857844656111616,'http://localhost:3000'),(857844656111616,'http://localhost:4200'),(857844656111616,'http://localhost:4300'),(857844656111616,'http://localhost:4400'),(857844656111616,'http://localhost:8083'),(857844656111616,'https://auth.letsauth.cloud'),(857844656111616,'https://www.letsauth.cloud'),(881933318357304,'http://localhost:3000'),(881941651914966,'http://localhost:4300');
/*!40000 ALTER TABLE `cors_origin_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cors_profile`
--

DROP TABLE IF EXISTS `cors_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cors_profile` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `allow_credentials` bit(1) NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `max_age` bigint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hhw5hs6a0m8alutq6evi1k89x` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cors_profile`
--

LOCK TABLES `cors_profile` WRITE;
/*!40000 ALTER TABLE `cors_profile` DISABLE KEYS */;
INSERT INTO `cors_profile` VALUES (857844656111616,'2021-11-06 14:27:12','0U8AZTODP4H0','2023-04-22 09:17:54','0U8AZTODP4H0',3,_binary '','0O8G2WE71L35','默认适用所有api',86400,'默认CORS配置','0P8HE307W6IO'),(881933318357304,'2023-04-22 09:05:10','0U8OMAGVFMS3','2023-04-22 09:05:10','0U8OMAGVFMS3',0,_binary '','0O8OMAKMWMRB',NULL,1200,'允许本地跨域','0P8OMAHSU0W4'),(881941651914966,'2023-04-22 13:30:05','0U8AZTODP4H0','2023-04-22 13:39:13','0U8AZTODP4H0',3,_binary '','0O8OMEEGHP05',NULL,86400,'Token跨域配置','0P8HE307W6IO');
/*!40000 ALTER TABLE `cors_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `data_process_tracker`
--

DROP TABLE IF EXISTS `data_process_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `data_process_tracker` (
  `id` bigint NOT NULL,
  `last_processed_id` bigint NOT NULL,
  `version` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data_process_tracker`
--

LOCK TABLES `data_process_tracker` WRITE;
/*!40000 ALTER TABLE `data_process_tracker` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_process_tracker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `endpoint`
--

DROP TABLE IF EXISTS `endpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `endpoint` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `cache_profile_id` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) NOT NULL,
  `cors_profile_id` varchar(255) DEFAULT NULL,
  `csrf_enabled` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `is_websocket` bit(1) NOT NULL,
  `method` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `permission_id` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) NOT NULL,
  `secured` bit(1) NOT NULL,
  `shared` bit(1) NOT NULL,
  `expire_reason` varchar(255) DEFAULT NULL,
  `expired` bit(1) DEFAULT b'0',
  `external` bit(1) DEFAULT b'0',
  `replenish_rate` int DEFAULT '0',
  `burst_capacity` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_s6re5n04ncpcplwvj5pvn6uru` (`domain_id`),
  UNIQUE KEY `UKdjmpydp5oinihgfnvgqv9xm8a` (`client_id`,`path`,`method`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `endpoint`
--

LOCK TABLES `endpoint` WRITE;
/*!40000 ALTER TABLE `endpoint` DISABLE KEYS */;
INSERT INTO `endpoint` VALUES (0,NULL,NULL,'2022-01-05 20:18:31','0U8AZTODP4H0',5,'0X8G900BJFGG','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP400',_binary '\0','GET','读取应用简略信息','projects/**/clients','0Y8HHJ47NBD4','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(1,NULL,NULL,'2022-01-05 21:14:49','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP401',_binary '\0','GET','查询目标应用是否可以自动批准第三方登录请求','projects/**/clients/**/autoApprove','0Y8HHJ47NBD5','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(2,NULL,NULL,'2022-01-05 20:18:43','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP402',_binary '\0','POST','创建应用','projects/**/clients','0Y8HHJ47NBD6','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(3,NULL,NULL,'2022-01-05 20:18:56','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP403',_binary '\0','PUT','更新应用','projects/**/clients/**','0Y8HHJ47NBD7','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(4,NULL,NULL,'2022-01-05 20:19:03','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP404',_binary '\0','DELETE','删除应用','projects/**/clients/**','0Y8HHJ47NBD8','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(5,NULL,NULL,'2022-01-05 20:21:25','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP405',_binary '\0','POST','注册用户','users','0Y8HHJ47NBD9','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(7,NULL,NULL,'2022-01-05 20:28:19','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP407',_binary '\0','PUT','更新单个用户信息','mgmt/users/**','0Y8HHJ47NBDB','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(8,NULL,NULL,'2022-01-05 20:28:26','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP408',_binary '\0','DELETE','删除单个用户','mgmt/users/**','0Y8HHJ47NBDC','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(9,NULL,NULL,'2022-01-05 20:28:38','0U8AZTODP4H0',4,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP409',_binary '\0','PUT','用户更改密码','users/pwd','0Y8HHJ47NBDD','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(10,NULL,NULL,'2022-01-05 20:22:39','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40A',_binary '\0','POST','OAuth2 authorize端口','authorize','0Y8HHJ47NBDE','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(12,NULL,NULL,'2022-01-05 20:24:07','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40C',_binary '\0','POST','创建待注册用户','pending-users','0Y8HHJ47NBDG','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(13,NULL,NULL,'2022-01-05 20:28:50','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40D',_binary '\0','POST','用户忘记密码','users/forgetPwd','0Y8HHJ47NBDH','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(14,NULL,NULL,'2022-01-05 20:24:40','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40E',_binary '\0','POST','重置用户密码','users/resetPwd','0Y8HHJ47NBDI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(15,NULL,NULL,'2023-04-22 13:30:24','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8OMEEGHP05',_binary '\0',NULL,'0E8AZTODP40F',_binary '\0','POST','申请身份令牌','oauth/token',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(16,NULL,NULL,'2022-01-05 20:29:15','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40G',_binary '\0','POST','将已签发的用户或应用的身份令牌回收','mgmt/revoke-tokens','0Y8HHJ47NBDK','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(18,NULL,NULL,'2022-01-05 20:29:35','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40I',_binary '\0','POST','创建api端口','projects/**/endpoints','0Y8HHJ47NBDL','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(19,NULL,NULL,'2022-01-05 20:26:21','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40J',_binary '\0','GET','读取当前所有api端口简略信息','projects/**/endpoints','0Y8HHJ47NBDM','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(20,NULL,NULL,'2022-01-05 20:29:50','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40K',_binary '\0','PUT','更新单个api端口信息','projects/**/endpoints/**','0Y8HHJ47NBDN','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(21,NULL,NULL,'2022-01-05 20:30:02','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40L',_binary '\0','DELETE','删除单个api端口','projects/**/endpoints/**','0Y8HHJ47NBDO','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(125,NULL,NULL,'2022-01-05 20:31:06','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP432',_binary '\0','GET','读取单个应用详细信息','projects/**/clients/**','0Y8HHJ47NBDP','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(126,NULL,NULL,'2022-01-05 20:31:23','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP433',_binary '\0','PATCH','更改单个应用的部分信息','projects/**/clients/**','0Y8HHJ47NBDQ','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(128,NULL,NULL,'2022-01-05 20:31:49','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP435',_binary '\0','GET','读取单个api端口的详细信息','projects/**/endpoints/**','0Y8HHJ47NBDS','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(130,NULL,NULL,'2022-01-05 20:32:08','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP437',_binary '\0','PATCH','更改单个用户的部分信息','mgmt/users/**','0Y8HHJ47NBDT','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(131,NULL,NULL,'2022-01-05 20:33:30','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP438',_binary '\0','PATCH','批量更改多个用户的部分信息','mgmt/users','0Y8HHJ47NBDU','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(132,NULL,NULL,'2022-01-05 20:33:15','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP439',_binary '\0','DELETE','批量删除多个api端口','projects/**/endpoints','0Y8HHJ47NBDV','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(137,NULL,NULL,'2022-01-05 20:33:50','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP43E',_binary '\0','PATCH','更改单个api端口的部分信息','projects/**/endpoints/**','0Y8HHJ47NBDW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(143,NULL,NULL,'2022-01-05 20:35:02','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP43I',_binary '\0','GET','读取mt-auth中的改动','changes/root','0Y8HHJ47NBDY','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(149,NULL,NULL,'2022-01-05 20:35:26','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP43N',_binary '\0','GET','查看当前回收的身份令牌','mgmt/revoke-tokens','0Y8HHJ47NBDZ','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(180,NULL,NULL,'2022-01-05 20:35:53','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP450',_binary '\0','POST','发布重新载入代理缓存事件','mgmt/endpoints/event/reload','0Y8HHJ47NB00','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(181,NULL,NULL,NULL,NULL,0,NULL,'0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP451',_binary '\0','GET','测试延迟','delay/**',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(182,NULL,NULL,NULL,NULL,0,NULL,'0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP452',_binary '\0','GET','测试Http返回码','status/**',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(183,'2021-02-06 13:13:40','0U8AZTODP4H0','2021-02-06 13:13:40','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT',NULL,_binary '\0',NULL,'0E8BO3KAHURK',_binary '\0','GET','系统监控websocket get初始化请求','monitor','0Y8HHJ47NBE6','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(184,NULL,NULL,'2022-01-05 20:36:26','0U8AZTODP4H0',3,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8BPAEMD3B5',_binary '\0','POST','获取websocket链接使用的令牌','tickets/**','0Y8HHJ47NBE7','0P8HE307W6IO',_binary '',_binary '',NULL,_binary '\0',_binary '',10,60),(185,NULL,NULL,NULL,NULL,0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8BPAEMD3B6',_binary '\0','GET','获取商城通知历史','notifications','0Y8HHJ47NBE8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(186,'2021-02-06 13:13:40','0U8AZTODP4H0','2021-02-06 13:13:40','0U8AZTODP4H0',0,NULL,'0C8HPGLXHMET',NULL,_binary '\0',NULL,'0E8BPAEMD3B7',_binary '\0','GET','商城监控websocket get初始化请求','monitor','0Y8HHJ47NBE9','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118150,'2021-03-25 10:49:49','0U8AZTODP4H0','2021-03-25 10:49:49','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT',NULL,_binary '\0',NULL,'0E8CFEHEM0W0',_binary '','','系统监控ws接口','monitor','0Y8HVWH0K64P','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539979419653,'2021-03-25 10:50:22','0U8AZTODP4H0','2021-03-25 10:50:22','0U8AZTODP4H0',0,NULL,'0C8HPGLXHMET',NULL,_binary '\0',NULL,'0E8CFEHOWUTC',_binary '','','商城管理端WS接口','monitor','0Y8HYA2GSU80','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(855088528097288,'2021-09-07 22:12:16','0U8AZTODP4H0','2022-01-05 20:36:38','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8F3Q8VWB9C',_binary '\0','POST','重试系统事件','mgmt/events/**/retry','0Y8HHJ47NBEA','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(855088539107336,'2021-09-07 22:12:37','0U8AZTODP4H0','2022-01-05 20:37:44','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8F3Q92GAO0',_binary '\0','GET','读取当前系统事件','mgmt/events','0Y8HHJ47NBEB','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(858486057992192,'2021-11-21 19:16:49','0U8AZTODP4H0','2021-11-21 20:20:41','0U8AZTODP4H0',1,NULL,'0C8AZYTQ5W5C',NULL,_binary '\0',NULL,'0E8GB31T5VK2',_binary '\0','GET','查询代理缓存MD5值','/info/checkSum',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(858491679408138,'2021-11-21 22:15:32','0U8AZTODP4H0','2022-01-05 20:43:27','0U8AZTODP4H0',2,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8GB5MSBKE8',_binary '\0','GET','检查代理缓存是否同步','mgmt/proxy/check','0Y8HHJ47NBES','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861497201262599,'2022-01-27 01:38:29','0U8AZTODP4H0','2022-01-27 01:38:29','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HDICM4CU8',_binary '\0','POST','创建新项目','projects','0Y8HHJ47NBET','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861497574031368,'2022-01-27 01:50:21','0U8AZTODP4H0','2022-01-27 01:50:21','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HDIISDBLS',_binary '\0','GET','查询已有项目','mgmt/projects','0Y8HHJ47NBEU','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861540772741129,'2022-01-28 00:43:36','0U8AZTODP4H0','2022-01-28 00:43:36','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HE2D7RLDT',_binary '\0','GET','查询当前全部权限简要信息','projects/**/permissions','0Y8HHJ47NBEV','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861541373050887,'2022-01-28 01:02:40','0U8AZTODP4H0','2022-01-28 01:04:03','0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HE2N4V2TD',_binary '\0','POST','创建权限配置','projects/**/permissions','0Y8HHJ47NBEW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861612640567304,'2022-01-29 14:48:12','0U8AZTODP4H0','2022-01-29 14:48:12','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HEZDS0IYO',_binary '\0','GET','查询当前全部角色简要信息','projects/**/roles','0Y8HHJ47NBEX','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861612644761608,'2022-01-29 14:48:21','0U8AZTODP4H0','2022-01-29 14:48:21','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HEZDUIFB4',_binary '\0','POST','创建角色','projects/**/roles','0Y8HHJ47NBEY','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861809903403018,'2022-02-02 18:19:01','0U8AZTODP4H0','2022-02-02 18:19:01','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HHI057P4W',_binary '\0','GET','查询租户项目','projects/tenant','0Y8HHJ47NBEZ','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862016664240135,'2022-02-07 02:51:46','0U8AZTODP4H0','2022-02-07 02:51:46','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HK4ZKYRP1',_binary '\0','GET','查询租户用户','projects/**/users','0Y8HK4ZLA03Q','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862028321783818,'2022-02-07 09:02:21','0U8AZTODP4H0','2022-02-07 09:02:21','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HKACDKDTT',_binary '\0','GET','查看角色','projects/**/roles/**','0Y8HKACDVMDL','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862036408401931,'2022-02-07 13:19:26','0U8AZTODP4H0','2022-02-07 13:19:26','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HKE24FWU8',_binary '\0','PUT','更新角色','projects/**/roles/**','0Y8HKE24FWUI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862036445102092,'2022-02-07 13:20:36','0U8AZTODP4H0','2022-02-07 13:20:36','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HKE2QAIV4',_binary '\0','DELETE','删除角色','projects/**/roles/**','0Y8HKE2QAIVF','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862037661450252,'2022-02-07 13:59:15','0U8AZTODP4H0','2022-02-07 13:59:15','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '','查询当前用户角色信息','0E8HKEMUH340',_binary '\0','GET','查询项目用户详细信息','projects/**/users/**','0Y8HKEMUH34B','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862037665120268,'2022-02-07 13:59:22','0U8AZTODP4H0','2022-02-07 13:59:22','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '','更改用户角色信息','0E8HKEMWNQWX',_binary '\0','PUT','更改项目用户信息','projects/**/users/**','0Y8HKEMWNQX7','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862151434567893,'2022-02-09 21:16:01','0U8AZTODP4H0','2022-02-09 21:16:01','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HLUWG1UIW',_binary '\0','GET','查询单个权限信息','projects/**/permissions/**','0Y8HLUWG1UJ8','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862151442432014,'2022-02-09 21:16:16','0U8AZTODP4H0','2022-02-09 21:16:16','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HLUWKQEIP',_binary '\0','PUT','更改单个权限信息','projects/**/permissions/**','0Y8HLUWKQEJ1','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862151446102030,'2022-02-09 21:16:23','0U8AZTODP4H0','2022-02-09 21:16:23','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HLUWMX2BL',_binary '\0','DELETE','部分更改单个权限信息','projects/**/permissions/**','0Y8HLUWMX2BX','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862151448723470,'2022-02-09 21:16:28','0U8AZTODP4H0','2022-05-07 03:14:37','0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HLUWOH91D',_binary '\0','PATCH','部分更新单个权限信息','projects/**/permissions/**','0Y8HLUWOH91P','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862151448723471,'2022-02-09 21:16:28','0U8AZTODP4H0','2022-04-24 23:04:11','0U8AZTODP4H0',4,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HLUWOH91E',_binary '\0','GET','读取我的用户信息','users/profile','0Y8HLUWOH92P','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382466793485,'2022-02-14 23:40:20','0U8AZTODP4H0','2022-02-14 23:40:20','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT1AO8OW',_binary '\0','GET','管理员查询任意应用简略信息','mgmt/clients','0Y8HOT1AO8P8','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382500872206,'2022-02-14 23:41:25','0U8AZTODP4H0','2022-02-14 23:41:25','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT1UYO00',_binary '\0','GET','管理员查询任意应用详细信息','mgmt/clients/**','0Y8HOT1UYO0D','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382517125133,'2022-02-14 23:41:55','0U8AZTODP4H0','2022-02-14 23:41:55','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT24N0U8',_binary '\0','GET','管理员查询任意API详细信息','mgmt/endpoints/**','0Y8HOT24N0UK','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382523940878,'2022-02-14 23:42:09','0U8AZTODP4H0','2022-02-14 23:42:09','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT28P3WG',_binary '\0','GET','管理员查询任意API简略信息','mgmt/endpoints','0Y8HOT28P3WT','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382783463438,'2022-02-14 23:50:24','0U8AZTODP4H0','2022-02-14 23:50:24','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT6J7KSH',_binary '\0','GET','管理员查询全部用户简略信息','mgmt/users','0Y8HOT6J7KST','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382792900621,'2022-02-14 23:50:41','0U8AZTODP4H0','2022-02-14 23:50:41','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT6OTUKG',_binary '\0','GET','管理员查询任意用户详细信息','mgmt/users/**','0Y8HOT6OTUKS','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862402490400776,'2022-02-15 10:16:51','0U8AZTODP4H0','2022-05-24 16:39:51','0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HP28FWEF5',_binary '\0','GET','获取所有系统通知','mgmt/notifications','0Y8HP28G7MYV','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862446240137233,'2022-02-16 09:27:38','0U8HPG93IED3','2022-02-16 09:27:38','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPMBZOBNL',_binary '\0','GET','获取购物车信息','cart/user','0Y8HPMBZOBO0','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469694685190,'2022-02-16 21:53:13','0U8HPG93IED3','2022-02-16 21:53:13','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX3VLG5D',_binary '\0','GET','管理员获取所有地址简要信息','addresses/admin','0Y8HPX3VWOP1','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469704646672,'2022-02-16 21:53:32','0U8HPG93IED3','2022-02-16 21:53:32','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX41U70G',_binary '\0','GET','管理员获取地址明细','addresses/admin/**','0Y8HPX41U70V','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469722996742,'2022-02-16 21:54:07','0U8HPG93IED3','2022-02-16 21:54:07','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX4CG9HD',_binary '\0','POST','用户添加地址','addresses/user','0Y8HPX4CRI11','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469746065633,'2022-02-16 21:54:52','0U8HPG93IED3','2022-02-16 21:54:52','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX4QHY35',_binary '\0','GET','用户获取所有地址','addresses/user','0Y8HPX4QHY3K','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469758648326,'2022-02-16 21:55:15','0U8HPG93IED3','2022-02-16 21:55:15','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX4XOEF5',_binary '\0','GET','用户获取地址明细','addresses/user/**','0Y8HPX4XZMYT','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469786435600,'2022-02-16 21:56:08','0U8HPG93IED3','2022-02-16 21:56:08','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX5EJ7R4',_binary '\0','DELETE','用户删除地址','addresses/user/**','0Y8HPX5EJ7RJ','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469789057041,'2022-02-16 21:56:14','0U8HPG93IED3','2022-02-16 21:56:14','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX5G3EGX',_binary '\0','PUT','用户更新地址','addresses/user/**','0Y8HPX5G3EHC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469892341790,'2022-02-16 21:59:31','0U8HPG93IED3','2022-02-16 21:59:31','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX75L5HQ',_binary '\0','DELETE','管理员删除属性','attributes/admin','0Y8HPX75L5I5','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469943722001,'2022-02-16 22:01:09','0U8HPG93IED3','2022-02-16 22:01:09','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX806EPS',_binary '\0','POST','管理员创建属性','attributes/admin','0Y8HPX806EQ8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469957353488,'2022-02-16 22:01:35','0U8HPG93IED3','2022-02-16 22:01:35','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX88AKU8',_binary '\0','GET','管理员获取当前所有属性简略信息','attributes/admin','0Y8HPX88AKUN','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470017122334,'2022-02-16 22:03:28','0U8HPG93IED3','2022-02-16 22:03:28','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX97VMRY',_binary '\0','GET','管理员获取单一属性明细','attributes/admin/**','0Y8HPX97VMSD','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470036520976,'2022-02-16 22:04:06','0U8HPG93IED3','2022-02-16 22:04:06','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX9JFEV4',_binary '\0','PATCH','管理员更改特定属性','attributes/admin/**','0Y8HPX9JFEVJ','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470049103888,'2022-02-16 22:04:29','0U8HPG93IED3','2022-02-16 22:04:29','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX9QX3WG',_binary '\0','PUT','管理员更新特定属性','attributes/admin/**','0Y8HPX9QX3WV','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470055919633,'2022-02-16 22:04:43','0U8HPG93IED3','2022-02-16 22:04:43','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX9UZ6YO',_binary '\0','DELETE','管理员删除特定属性','attributes/admin/**','0Y8HPX9UZ6Z4','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470863847425,'2022-02-16 22:30:23','0U8HPG93IED3','2022-02-16 22:30:23','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXN7ONI8',_binary '\0','DELETE','系统清空购物车','cart/app','0Y8HPXN7ZW1S','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470881149153,'2022-02-16 22:30:57','0U8HPG93IED3','2022-02-16 22:30:57','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXNIAQ4W',_binary '\0','POST','用户添加商品到购物车','cart/user','0Y8HPXNIAQ5C','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470894256134,'2022-02-16 22:31:21','0U8HPG93IED3','2022-02-16 22:31:21','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXNPSF0H',_binary '\0','DELETE','用户删除购物车商品','cart/user/**','0Y8HPXNQ3NK5','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470929383440,'2022-02-16 22:32:28','0U8HPG93IED3','2022-02-16 22:32:28','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPXOB0JY8',_binary '\0','DELETE','管理员删除目录','catalogs/admin','0Y8HPXOB0JYN','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470952452112,'2022-02-16 22:33:13','0U8HPG93IED3','2022-02-16 22:33:13','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXOOQZUO',_binary '\0','GET','管理员获取商品目录','catalogs/admin','0Y8HPXOOQZV3','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470959267857,'2022-02-16 22:33:26','0U8HPG93IED3','2022-02-16 22:33:26','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXOST2WW',_binary '\0','POST','管理员创建商品目录','catalogs/admin','0Y8HPXOST2XC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470971850768,'2022-02-16 22:33:50','0U8HPG93IED3','2022-02-16 22:33:50','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXP0ARY8',_binary '\0','PATCH','管理员部分更新商品目录','catalogs/admin/**','0Y8HPXP0ARYN','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470983385285,'2022-02-16 22:34:12','0U8HPG93IED3','2022-02-16 22:34:12','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXP7601H',_binary '\0','GET','管理员获取商品目录明细','catalogs/admin/**','0Y8HPXP7601W','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470998589456,'2022-02-16 22:34:41','0U8HPG93IED3','2022-02-16 22:34:41','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXPG7VNK',_binary '\0','DELETE','管理员删除特定商品目录','catalogs/admin/**','0Y8HPXPG7VNZ','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471005405200,'2022-02-16 22:34:53','0U8HPG93IED3','2022-02-16 22:34:53','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXPK9YPS',_binary '\0','PUT','管理员更新特定商品目录','catalogs/admin/**','0Y8HPXPK9YQ7','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471028998159,'2022-02-16 22:35:39','0U8HPG93IED3','2022-02-17 02:39:37','0U8HPG93IED3',1,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXPYBN5S',_binary '\0','GET','获取商城商品目录信息','catalogs/public',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471117603041,'2022-02-16 22:38:28','0U8HPG93IED3','2022-02-16 22:38:28','0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HPXRF2R4G',_binary '\0','GET','获取该系统改动','changes/root','0Y8HPXRF2R4W','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471126515728,'2022-02-16 22:38:45','0U8HPG93IED3','2022-02-16 22:38:45','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXRKDS74',_binary '\0','GET','获取该商城服务系统改动','changes/root','0Y8HPXRKDS7J','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471319978181,'2022-02-16 22:44:54','0U8HPG93IED3','2022-02-16 22:44:54','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXURKCS5',_binary '\0','POST','上传文件','files/app','0Y8HPXURKCSK','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471340425231,'2022-02-16 22:45:32','0U8HPG93IED3','2022-02-16 22:45:32','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXV3QLTS',_binary '\0','GET','获取文件公共端口','files/public/**',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471359823888,'2022-02-16 22:46:09','0U8HPG93IED3','2022-02-16 22:46:09','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXVFADXC',_binary '\0','GET','读取商城目录过滤器','filters/admin','0Y8HPXVFADXR','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471366115334,'2022-02-16 22:46:21','0U8HPG93IED3','2022-02-16 22:46:21','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXVIPZWH',_binary '\0','POST','创建商城目录过滤器','filters/admin','0Y8HPXVJ18G5','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471374503952,'2022-02-16 22:46:37','0U8HPG93IED3','2022-02-16 22:46:37','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXVO114W',_binary '\0','DELETE','删除商城目录过滤器','filters/admin','0Y8HPXVO115B','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471386038288,'2022-02-16 22:47:00','0U8HPG93IED3','2022-02-16 22:47:00','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXVUW934',_binary '\0','PATCH','更改部分商城目录过滤器','filters/admin/**','0Y8HPXVUW93J','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471397048336,'2022-02-16 22:47:21','0U8HPG93IED3','2022-02-16 22:47:21','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXW1G8HS',_binary '\0','GET','读取特定商城目录过滤器','filters/admin/**','0Y8HPXW1G8I7','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471405961231,'2022-02-16 22:47:37','0U8HPG93IED3','2022-02-16 22:47:37','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXW6G16P',_binary '\0','DELETE','删除特定商城目录过滤器','filters/admin/**','0Y8HPXW6R9QM','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471422738438,'2022-02-16 22:48:09','0U8HPG93IED3','2022-02-16 22:48:09','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXWGFMQ9',_binary '\0','PUT','更新特定商城目录过滤器','filters/admin/**','0Y8HPXWGQV45','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471440564229,'2022-02-16 22:48:43','0U8HPG93IED3','2022-02-16 22:48:43','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXWR1P1D',_binary '\0','GET','获取商城过滤器配置','filters/public',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471786594421,'2022-02-16 22:59:43','0U8HPG93IED3','2022-02-16 23:03:23','0U8HPG93IED3',1,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HPY2HDK6D',_binary '\0','GET','读取分布式事务服务系统事件','mgmt/events','0Y8HPY2HDK6S','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471789215761,'2022-02-16 22:59:49','0U8HPG93IED3','2022-02-16 23:03:14','0U8HPG93IED3',1,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPY2IXQTD',_binary '\0','GET','读取商城服务系统事件','mgmt/events','0Y8HPY2IXQTS','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471831158801,'2022-02-16 23:01:09','0U8HPG93IED3','2022-02-16 23:02:00','0U8HPG93IED3',1,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPY37WQ9T',_binary '\0','POST','重试商城服务某一系统事件','mgmt/events/**/retry','0Y8HPY37WQA8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471833780230,'2022-02-16 23:01:13','0U8HPG93IED3','2022-02-16 23:01:48','0U8HPG93IED3',1,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HPY395OG1',_binary '\0','POST','重试分布式事务服务某一系统事件','mgmt/events/**/retry','0Y8HPY39GWZP','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473202171921,'2022-02-16 23:44:44','0U8HPG93IED3','2022-02-16 23:44:44','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYPW6ARL',_binary '\0','GET','管理员获取当前全部订单','orders/admin','0Y8HPYPW6AS0','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473210036241,'2022-02-16 23:44:58','0U8HPG93IED3','2022-02-16 23:44:58','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYQ0UUWX',_binary '\0','GET','管理员获取特定订单','orders/admin/**','0Y8HPYQ0UUXC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473264562193,'2022-02-16 23:46:42','0U8HPG93IED3','2022-02-16 23:46:42','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYQXBJEP',_binary '\0','DELETE','管理员删除特定订单','orders/admin/**','0Y8HPYQXBJF4','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473281863878,'2022-02-16 23:47:16','0U8HPG93IED3','2022-02-16 23:47:16','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYR7MDH2',_binary '\0','POST','系统创建订单','orders/app','0Y8HPYR7MDHH','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473296019473,'2022-02-16 23:47:43','0U8HPG93IED3','2022-02-16 23:47:43','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYRG1S01',_binary '\0','PUT','系统更新订单','orders/app/**','0Y8HPYRG1S0G','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473310699537,'2022-02-16 23:48:11','0U8HPG93IED3','2022-02-16 23:48:11','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYROSF7L',_binary '\0','POST','系统验证订单','orders/app/validate','0Y8HPYROSF80','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473430761478,'2022-02-16 23:51:59','0U8HPG93IED3','2022-02-16 23:51:59','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYTNYJ81',_binary '\0','GET','用户查询我的订单','orders/user','0Y8HPYTO9RLX','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473436528657,'2022-02-16 23:52:11','0U8HPG93IED3','2022-02-16 23:52:11','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYTRPDKX',_binary '\0','POST','用户下订单','orders/user','0Y8HPYTRPDLC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473455927313,'2022-02-16 23:52:48','0U8HPG93IED3','2022-02-16 23:52:48','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYU395OH',_binary '\0','PUT','用户更新订单','orders/user/**','0Y8HPYU395OW','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473464840209,'2022-02-16 23:53:04','0U8HPG93IED3','2022-02-16 23:53:04','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYU8K6WW',_binary '\0','DELETE','用户删除订单','orders/user/**','0Y8HPYU8K6XC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473474277393,'2022-02-16 23:53:22','0U8HPG93IED3','2022-02-16 23:53:22','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYUE6GOW',_binary '\0','GET','用户读取订单详情','orders/user/**','0Y8HPYUE6GPC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473487384593,'2022-02-16 23:53:47','0U8HPG93IED3','2022-02-16 23:53:47','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYULZE9T',_binary '\0','PUT','用户确认支付订单','orders/user/**/confirm','0Y8HPYULZEA8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473496821777,'2022-02-16 23:54:05','0U8HPG93IED3','2022-02-16 23:54:05','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYURLO1S',_binary '\0','PUT','用户重新下订单','orders/user/**/reserve','0Y8HPYURLO28','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473513074695,'2022-02-16 23:54:36','0U8HPG93IED3','2022-02-16 23:54:36','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYV0YSCH',_binary '\0','POST','获取支付链接','paymentLink','0Y8HPYV1A0W6','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473523560465,'2022-02-16 23:54:56','0U8HPG93IED3','2022-02-16 23:54:56','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYV7IRR5',_binary '\0','GET','查询支付状态','paymentStatus/**','0Y8HPYV7IRRK','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473545580742,'2022-02-16 23:55:39','0U8HPG93IED3','2022-02-16 23:55:39','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYVKMQPI',_binary '\0','GET','管理员获取所有产品','products/admin','0Y8HPYVKMQPX','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473551347729,'2022-02-16 23:55:50','0U8HPG93IED3','2022-02-16 23:55:50','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYVO2CJL',_binary '\0','POST','管理员新建产品','products/admin','0Y8HPYVO2CK0','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473558687761,'2022-02-16 23:56:04','0U8HPG93IED3','2022-02-16 23:56:04','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYVSFO5D',_binary '\0','DELETE','管理员删除产品','products/admin','0Y8HPYVSFO5S','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473566027782,'2022-02-16 23:56:17','0U8HPG93IED3','2022-02-16 23:56:17','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYVWHR7L',_binary '\0','PATCH','管理员部分更新产品','products/admin','0Y8HPYVWSZR9','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473573367825,'2022-02-16 23:56:31','0U8HPG93IED3','2022-02-16 23:56:31','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYW16BCX',_binary '\0','PATCH','管理员部分更新某一产品','products/admin/**','0Y8HPYW16BDC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473581756433,'2022-02-16 23:56:47','0U8HPG93IED3','2022-02-16 23:56:47','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYW6641S',_binary '\0','GET','管理员读取某一产品','products/admin/**','0Y8HPYW66428','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473588047889,'2022-02-16 23:57:00','0U8HPG93IED3','2022-02-16 23:57:00','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYW9WYKH',_binary '\0','PUT','管理员更新某一产品','products/admin/**','0Y8HPYW9WYKW','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473595387921,'2022-02-16 23:57:14','0U8HPG93IED3','2022-02-16 23:57:14','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYWEAA69',_binary '\0','DELETE','管理员删除某一产品','products/admin/**','0Y8HPYWEAA6O','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473629466641,'2022-02-16 23:58:18','0U8HPG93IED3','2022-02-16 23:58:18','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYWYKPHC',_binary '\0','GET','系统读取产品信息','products/app','0Y8HPYWYKPHS','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473635233809,'2022-02-16 23:58:30','0U8HPG93IED3','2022-02-16 23:58:30','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYX20BGH',_binary '\0','PATCH','系统更新产品信息','products/app','0Y8HPYX20BGW','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473660399813,'2022-02-16 23:59:18','0U8HPG93IED3','2022-02-16 23:59:18','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYXGZPO6',_binary '\0','GET','获取产品列表','products/public',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473669836805,'2022-02-16 23:59:35','0U8HPG93IED3','2022-02-16 23:59:35','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYXMAQRL',_binary '\0','GET','获取特定产品列表','products/public/**',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473720168455,'2022-02-17 00:01:11','0U8HPG93IED3','2022-02-17 00:01:11','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPYYG9IWX',_binary '\0','GET','获取订单信息','profiles/orders/id','0Y8HPYYGKRGM','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473737469969,'2022-02-17 00:01:44','0U8HPG93IED3','2022-02-17 00:01:44','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPYYQVLDT',_binary '\0','GET','重新提交定时任务','profiles/orders/scheduler/resubmit','0Y8HPYYQVLE8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473909436423,'2022-02-17 00:07:12','0U8HPG93IED3','2022-02-17 00:07:12','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ1KY769',_binary '\0','GET','获取库存信息','skus/admin','0Y8HPZ1L9FK6','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473917825041,'2022-02-17 00:07:29','0U8HPG93IED3','2022-02-17 00:07:29','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ1Q988X',_binary '\0','POST','创建新库存','skus/admin','0Y8HPZ1Q989C','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473932505094,'2022-02-17 00:07:56','0U8HPG93IED3','2022-02-17 00:07:56','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ1YOMWX',_binary '\0','PATCH','更新库存','skus/admin','0Y8HPZ1YZVGL','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473939320849,'2022-02-17 00:08:10','0U8HPG93IED3','2022-02-17 00:08:10','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ231YIP',_binary '\0','DELETE','删除库存','skus/admin','0Y8HPZ231YJ4','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473949806609,'2022-02-17 00:08:30','0U8HPG93IED3','2022-02-17 00:08:30','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ29APDT',_binary '\0','PATCH','更新特定库存','skus/admin/**','0Y8HPZ29APE8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473958719505,'2022-02-17 00:08:47','0U8HPG93IED3','2022-02-17 00:08:47','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ2ELQM9',_binary '\0','DELETE','删除指定库存','skus/admin/**','0Y8HPZ2ELQMO','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473970778118,'2022-02-17 00:09:09','0U8HPG93IED3','2022-02-17 00:09:09','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ2LGYKH',_binary '\0','PUT','更新替换指定库存','skus/admin/**','0Y8HPZ2LS745','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473983885329,'2022-02-17 00:09:35','0U8HPG93IED3','2022-02-17 00:09:35','0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ2TL4OX',_binary '\0','GET','查看指定库存','skus/admin/**','0Y8HPZ2TL4PC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862670892826797,'2022-02-21 08:29:09','0U8AZTODP4H0','2022-02-21 08:29:09','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HSHJC34BK',_binary '\0','GET','租户查询自己的项目','projects/**','0Y8HSHJC34BW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862944631455744,'2022-02-27 09:31:03','0U8AZTODP4H0','2022-02-27 09:31:03','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '\0','只对顶级域名及其子域名有效','0E8HVZAGOPOH',_binary '\0','GET','获取csrf cookie','csrf',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863019434770449,'2022-03-01 01:09:00','0U8HPG93IED3','2022-03-01 01:09:00','0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HWXNKYL8H',_binary '\0','GET','获取分布式事务列表','dtx','0Y8HWXNKYL8W','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863019442634758,'2022-03-01 01:09:14','0U8HPG93IED3','2022-03-01 01:09:14','0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HWXNPBWU9',_binary '\0','GET','获取分布式事务详情','dtx/**','0Y8HWXNPN5DX','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863019449450513,'2022-03-01 01:09:28','0U8HPG93IED3','2022-03-01 01:09:28','0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HWXNTP8G1',_binary '\0','POST','取消分布式事务','dtx/cancel','0Y8HWXNTP8GG','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863019457839121,'2022-03-01 01:09:43','0U8HPG93IED3','2022-03-01 01:09:43','0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HWXNYP14W',_binary '\0','POST','人工解决分布式事务','dtx/resolve','0Y8HWXNYP15C','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863374103543820,'2022-03-08 16:03:37','0U8AZTODP4H0','2022-03-08 16:03:37','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8I1GL5LA81',_binary '\0','GET','获取共享api列表','endpoints/shared','0Y8I1GL5LA8B','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863374103543821,'2022-03-08 16:03:37','0U8AZTODP4H0','2022-03-08 16:03:37','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8I1GL5LA82',_binary '\0','GET','获取共享权限列表','permissions/shared','0Y8I1GL5LA8C','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863439733915660,'2022-03-10 02:49:57','0U8AZTODP4H0','2022-03-10 02:49:57','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8I2AQK7X8G',_binary '\0','GET','查看当前注册服务','registry','0Y8I2AQK7X8R','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863711891816468,'2022-03-16 03:01:37','0U8AZTODP4H0','2022-03-16 03:01:37','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8I5RRK09HC',_binary '\0','GET','获取UI权限','permissions/ui','0Y8I5RRK09HV','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(864558270906384,'2022-04-03 19:27:16','0U8AZTODP4H0','2022-04-03 19:27:16','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8IGKL49IWX',_binary '\0','GET','测试Http返回值','get/**',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(864558282965158,'2022-04-03 19:27:40','0U8AZTODP4H0','2022-04-03 19:27:40','0U8AZTODP4H0',0,'0X8G900BJFGG','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8IGKLBFZHH',_binary '\0','GET','测试Http缓存','cache',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(864558338015248,'2022-04-03 19:29:25','0U8AZTODP4H0','2022-04-03 19:29:25','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8IGKM87WG1',_binary '\0','POST','测试Post端口','post',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(864879847669830,'2022-04-10 21:49:55','0U8AZTODP4H0','2022-04-10 21:49:55','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8IKOBERLKW',_binary '\0','GET','查看当前定时任务状态','mgmt/jobs','0Y8IKOBERLMT','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(865479232585852,'2022-04-24 03:23:52','0U8AZTODP4H0','2022-04-24 03:23:52','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8ISBO52IPI',_binary '\0','PUT','更新我的用户信息','users/profile','0Y8ISBO52IRF','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(865515702583359,'2022-04-24 22:43:13','0U8AZTODP4H0','2022-04-24 23:13:47','0U8AZTODP4H0',1,'0X8G900BJFGG','0C8AZTODP4HT','0O8G2WE71L35',_binary '\0',NULL,'0E8ISSFAD4X7',_binary '\0','GET','读取我的用户头像','users/profile/avatar','0Y8ISSFAD4XQ','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(865516402507800,'2022-04-24 23:05:28','0U8AZTODP4H0','2022-04-24 23:05:28','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8ISSQV2Y2O',_binary '\0','POST','创建或更新我的用户头像','users/profile/avatar','0Y8ISSQV2Y3B','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(865708046549002,'2022-04-29 04:37:39','0U8AZTODP4H0','2022-04-29 04:37:39','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8IV8SAOM4K',_binary '\0','POST','重置数据校验任务','mgmt/job/validation/reset','0Y8IV8SAZUO9','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866012474900485,'2022-05-05 21:55:10','0U8AZTODP4H0','2022-05-05 21:55:10','0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8IZ4MZGPHG',_binary '\0','GET','检查当前会话是否过期','expire/check','0Y8IZ4MZRY10','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866067833946122,'2022-05-07 03:14:59','0U8AZTODP4H0','2022-05-07 03:14:59','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8IZU2IT6GX',_binary '\0','PATCH','部分更新单个角色信息','projects/**/roles/**','0Y8IZU2J4F0P','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866070308061198,'2022-05-07 04:33:38','0U8AZTODP4H0','2022-05-07 04:33:38','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8IZV7FU1VO',_binary '\0','PUT','更新单个用户信息','mgmt/test/**','0Y8IZV7G5AFH','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866101804662794,'2022-05-07 21:14:53','0U8AZTODP4H0','2022-05-07 21:14:53','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8J09OC3JAF',_binary '\0','GET','读取当前系统审计事件','mgmt/events/audit','0Y8J09OCERRD','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866793667691291,'2022-05-23 03:48:38','0U8AZTODP4H0','2022-05-23 03:48:38','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8J93IHSKUG',_binary '\0','GET','查询项目是否创建就绪','projects/**/ready','0Y8J93IHSL5M','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866794397499416,'2022-05-23 04:11:50','0U8AZTODP4H0','2022-05-24 16:38:37','0U8AZTODP4H0',2,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8J93UKAWP0',_binary '\0','POST','确认站内信通知','mgmt/notifications/bell/**/ack','0Y8J93UKAWPJ','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866863218688024,'2022-05-24 16:39:36','0U8AZTODP4H0','2022-05-24 16:39:36','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8J9ZGQNTOK',_binary '\0','GET','获取站内信','mgmt/notifications/bell','0Y8J9ZGQNTP3','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584235573249,'2022-11-11 03:24:07','0U8AZTODP4H0','2022-11-11 03:24:07','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IG77C3K',_binary '\0','POST','创建新的订阅请求','subscriptions/requests','0Y8M0IG8RITC','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584843747381,'2022-11-11 03:43:28','0U8AZTODP4H0','2022-11-19 07:49:00','0U8AZTODP4H0',1,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '','包括待批准与已批准请求','0E8M0IQ9LUSY',_binary '\0','GET','获取项目订阅请求','subscriptions/requests','0Y8M0IQAUSZ8','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584869437441,'2022-11-11 03:44:16','0U8AZTODP4H0','2022-11-11 03:44:16','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IQOWHG2',_binary '\0','PUT','更新我的订阅请求','subscriptions/requests/**','0Y8M0IQQ5FK0','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584889360549,'2022-11-11 03:44:55','0U8AZTODP4H0','2022-11-11 03:44:55','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IR0RI0W',_binary '\0','POST','取消我的订阅请求','subscriptions/requests/**/cancel','0Y8M0IR20GBI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584908234808,'2022-11-11 03:45:30','0U8AZTODP4H0','2022-11-11 03:45:30','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IRC01KW',_binary '\0','POST','批准订阅请求','subscriptions/requests/**/approve','0Y8M0IRD8ZSN','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584925011969,'2022-11-11 03:46:02','0U8AZTODP4H0','2022-11-11 03:46:02','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IRLZMYO',_binary '\0','POST','拒绝订阅请求','subscriptions/requests/**/reject','0Y8M0IRN8L4W','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874905630932993,'2022-11-18 05:40:59','0U8AZTODP4H0','2022-11-19 07:48:52','0U8AZTODP4H0',1,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M4M3IY8ZK',_binary '\0','GET','获取我的订阅','subscriptions','0Y8M4M3J9HJ4','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874924644761623,'2022-11-18 15:45:26','0U8AZTODP4H0','2022-11-18 15:45:26','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M4UTZLTKZ',_binary '\0','POST','标记当前API为过期','projects/**/endpoints/**/expire','0Y8M4UTZLTLI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(876266711941144,'2022-12-18 06:48:36','0U8AZTODP4H0','2022-12-18 06:48:36','0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8MLZDBR4SG',_binary '\0','GET','获取API报告','projects/**/endpoints/**/report','0Y8MLZDBR4T3','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(877686335471675,'2023-01-18 14:57:13','0U8AZTODP4H0','2023-01-18 14:57:13','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8N43JAY7FR',_binary '\0','POST','重置任务','mgmt/jobs/**/reset','0Y8N43JAY7GA','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(878324922974216,'2023-02-01 17:17:21','0U8AZTODP4H0','2023-02-01 17:17:21','0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8NC8WD5DDT',_binary '\0','GET','测试-内部','internal',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,20),(878324953382929,'2023-02-01 17:18:20','0U8AZTODP4H0','2023-02-01 17:18:20','0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8NC8WVKDFK',_binary '\0','GET','测试-外部共享无验证','external/shared/no/auth',NULL,'0P8HE307W6IO',_binary '\0',_binary '',NULL,_binary '\0',_binary '',10,20),(878324962820112,'2023-02-01 17:18:38','0U8AZTODP4H0','2023-02-01 17:18:38','0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8NC8X16N7K',_binary '\0','GET','测试-外部共享需验证','external/shared/auth','0Y8NC8X16N81','0P8HE307W6IO',_binary '',_binary '',NULL,_binary '\0',_binary '',10,20),(878324978024542,'2023-02-01 17:19:07','0U8AZTODP4H0','2023-02-01 17:19:07','0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8NC8XA8J0V',_binary '\0','GET','测试-外部非共享需验证','external/not/shared/auth','0Y8NC8XA8J1B','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,20),(878324991655951,'2023-02-01 17:19:32','0U8AZTODP4H0','2023-02-01 17:19:32','0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8NC8XICP34',_binary '\0','GET','测试-外部非共享无验证','external/not/shared/no/auth',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,20),(878818637643797,'2023-02-12 14:52:08','0U8AZTODP4H0','2023-02-12 14:52:08','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NIJPIFF28',_binary '\0','GET','获取用户站内信','user/notifications/bell','0Y8NIJPIFF2U','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(878818652848416,'2023-02-12 14:52:36','0U8AZTODP4H0','2023-02-12 14:52:36','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NIJPRHATC',_binary '\0','POST','确认用户站内信通知','user/notifications/bell/**/ack','0Y8NIJPRHB1D','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(879037695131667,'2023-02-17 10:55:46','0U8AZTODP4H0','2023-02-17 10:55:46','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT',NULL,_binary '\0',NULL,'0E8NLCCBLMGW',_binary '\0','GET','用户通知websocket get初始化请求','monitor/user','0Y8NLCCBLMHG','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(879037957799945,'2023-02-17 11:04:07','0U8AZTODP4H0','2023-02-17 11:04:07','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT',NULL,_binary '\0',NULL,'0E8NLCGNOA2R',_binary '','','用户ws接口','monitor/user','0Y8NLCGNZIMI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(879716446765077,'2023-03-04 10:32:43','0U8AZTODP4H0','2023-03-04 10:32:43','0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NU05MSQO0',_binary '\0','GET','读取系统概述','mgmt/dashboard','0Y8NU05MSQOM','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(880043477696534,'2023-03-11 15:48:45','0U8AZTODP4H0','2023-03-11 15:48:45','0U8AZTODP4H0',0,'0X8G900BJFGG','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NY6E4KKCH',_binary '\0','GET','获取管理员列表','projects/**/admins','0Y8NY6E4KKD3','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(880043499192592,'2023-03-11 15:49:26','0U8AZTODP4H0','2023-03-11 15:49:26','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NY6EHDARV',_binary '\0','POST','添加管理员','projects/**/admins/**','0Y8NY6EHDATT','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(880043517542411,'2023-03-11 15:50:00','0U8AZTODP4H0','2023-03-11 15:50:00','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NY6ERZD37',_binary '\0','DELETE','移除管理员','projects/**/admins/**','0Y8NY6ESALN0','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881764363927575,'2023-04-18 15:34:15','0U8AZTODP4H0','2023-04-18 15:34:15','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OK4YFSUF8',_binary '\0','POST','创建跨域配置','projects/**/cors','0Y8OK4YFSUFS','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881764374413335,'2023-04-18 15:34:35','0U8AZTODP4H0','2023-04-18 15:34:35','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OK4YM1LAC',_binary '\0','PUT','编辑跨域配置','projects/**/cors/**','0Y8OK4YM1LAW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881764384374803,'2023-04-18 15:34:54','0U8AZTODP4H0','2023-04-18 15:34:54','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OK4YRZ3LS',_binary '\0','GET','查看跨域配置','projects/**/cors','0Y8OK4YRZ3MC','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881764393811987,'2023-04-18 15:35:12','0U8AZTODP4H0','2023-04-18 15:35:12','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OK4YXLDDS',_binary '\0','DELETE','删除跨域配置','projects/**/cors/**','0Y8OK4YXLDEC','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881764400103443,'2023-04-18 15:35:23','0U8AZTODP4H0','2023-04-18 15:35:50','0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OK4Z1C7WG',_binary '\0','PATCH','更改部分跨域配置','projects/**/cors/**','0Y8OK4Z1C7X0','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881811144572946,'2023-04-19 16:21:21','0U8AZTODP4H0','2023-04-22 10:11:06','0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OKQG3H6V8',_binary '\0','POST','创建缓存配置','projects/**/cache','0Y8OKQG3SFF7','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881811154534413,'2023-04-19 16:21:40','0U8AZTODP4H0','2023-04-22 10:10:23','0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OKQG9EP6O',_binary '\0','GET','查看缓存配置','projects/**/cache','0Y8OKQG9PXQM','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881811160301587,'2023-04-19 16:21:52','0U8AZTODP4H0','2023-04-22 10:10:33','0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OKQGD5JPC',_binary '\0','PUT','更新缓存配置','projects/**/cache/**','0Y8OKQGD5JPW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881811164495891,'2023-04-19 16:22:00','0U8AZTODP4H0','2023-04-22 10:10:45','0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OKQGFNG1S',_binary '\0','DELETE','删除缓存配置','projects/**/cache/**','0Y8OKQGFNG2C','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881811169738771,'2023-04-19 16:22:09','0U8AZTODP4H0','2023-04-22 10:10:55','0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OKQGIRTHC',_binary '\0','PATCH','部分更新缓存配置','projects/**/cache/**','0Y8OKQGIRTHW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881933368164361,'2023-04-22 09:06:44','0U8OMAGVFMS3','2023-04-22 09:06:44','0U8OMAGVFMS3',0,'0X8OMAKSU4UN','0C8OMAII48XE','0O8OMAKMWMRB',_binary '',NULL,'0E8OMALG8XKW',_binary '\0','GET','演示公共共享API','public',NULL,'0P8OMAHSU0W4',_binary '\0',_binary '',NULL,_binary '\0',_binary '',1,1),(881933380222985,'2023-04-22 09:07:07','0U8OMAGVFMS3','2023-04-22 09:07:07','0U8OMAGVFMS3',0,'0X8OMAKSU4UN','0C8OMAII48XE','0O8OMAKMWMRB',_binary '',NULL,'0E8OMALNFE7X',_binary '\0','GET','演示保护共享API','protected','0Y8OMALNQMMI','0P8OMAHSU0W4',_binary '',_binary '',NULL,_binary '\0',_binary '',1,1),(882926490222611,'2023-05-14 07:17:14','0U8AZTODP4H0','2023-05-14 07:17:14','0U8AZTODP4H0',0,'0X8G900BJFGG','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OYYTVSIKG',_binary '\0','GET','获取应用下拉列表','projects/**/clients/dropdown','0Y8OYY45NEVK','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(882926516961445,'2023-05-14 07:18:06','0U8AZTODP4H0','2023-05-14 07:18:06','0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OYYUBPMCK',_binary '\0','GET','管理员获取应用下拉列表','mgmt/clients/dropdown','0Y8OYYUBPMEE','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60);
/*!40000 ALTER TABLE `endpoint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exposed_header_map`
--

DROP TABLE IF EXISTS `exposed_header_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exposed_header_map` (
  `id` bigint NOT NULL,
  `exposed_header` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`exposed_header`),
  CONSTRAINT `custom_constaint_5` FOREIGN KEY (`id`) REFERENCES `cors_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exposed_header_map`
--

LOCK TABLES `exposed_header_map` WRITE;
/*!40000 ALTER TABLE `exposed_header_map` DISABLE KEYS */;
INSERT INTO `exposed_header_map` VALUES (857844656111616,'lastupdateat'),(857844656111616,'location'),(857844656111616,'uuid');
/*!40000 ALTER TABLE `exposed_header_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `external_resources_map`
--

DROP TABLE IF EXISTS `external_resources_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `external_resources_map` (
  `id` bigint NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`domain_id`),
  CONSTRAINT `FKact8s44rr8xockm24f2i0qts2` FOREIGN KEY (`id`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `external_resources_map`
--

LOCK TABLES `external_resources_map` WRITE;
/*!40000 ALTER TABLE `external_resources_map` DISABLE KEYS */;
INSERT INTO `external_resources_map` VALUES (862433369391105,'0C8AZTODP4HT'),(862433780433088,'0C8AZTODP4HT'),(862433826570240,'0C8AZTODP4HT'),(862524186558475,'0C8AZTODP4HT');
/*!40000 ALTER TABLE `external_resources_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `formatted_access_record`
--

DROP TABLE IF EXISTS `formatted_access_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `formatted_access_record` (
  `id` bigint NOT NULL,
  `endpoint_id` varchar(255) NOT NULL,
  `request_at` datetime(3) NOT NULL,
  `path` varchar(255) NOT NULL,
  `client_ip` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) DEFAULT NULL,
  `method` varchar(255) NOT NULL,
  `response_at` datetime(3) NOT NULL,
  `response_code` int NOT NULL,
  `response_content_size` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `formatted_access_record`
--

LOCK TABLES `formatted_access_record` WRITE;
/*!40000 ALTER TABLE `formatted_access_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `formatted_access_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `image` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `original_name` varchar(255) DEFAULT NULL,
  `source` longblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_3tq247u7bp4mi45ojkf40eiic` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
/*!40000 ALTER TABLE `image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `job_detail`
--

DROP TABLE IF EXISTS `job_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `job_detail` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `last_status` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `failure_count` int NOT NULL,
  `failure_reason` varchar(255) DEFAULT NULL,
  `failure_allowed` int NOT NULL,
  `max_lock_acquire_failure_allowed` int NOT NULL,
  `notified_admin` bit(1) NOT NULL,
  `last_execution` datetime DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `minimum_idle_time_milli` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_domainId` (`domain_id`),
  UNIQUE KEY `UK_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_detail`
--

LOCK TABLES `job_detail` WRITE;
/*!40000 ALTER TABLE `job_detail` DISABLE KEYS */;
INSERT INTO `job_detail` VALUES (864879623798785,NULL,NULL,NULL,NULL,0,'KEEP_WS_CONNECTION',NULL,'SINGLE',0,NULL,3,5,_binary '\0',NULL,'0J8IKO7PH9MO',30000),(864879623798786,NULL,NULL,'2023-04-04 16:25:38','NOT_HTTP',8482,'EVENT_SCAN','SUCCESS','CLUSTER',0,NULL,3,5,_binary '\0','2023-04-04 16:25:38','0J8IKO7PH9M1',3000),(864879623798787,NULL,NULL,'2023-04-04 16:21:41','NOT_HTTP',51,'MISSED_EVENT_SCAN','SUCCESS','CLUSTER',0,NULL,3,5,_binary '\0','2023-04-04 16:21:41','0J8IKO7PH9M2',360000),(864879623798788,NULL,NULL,'2023-04-04 16:24:50','NOT_HTTP',219,'DATA_VALIDATION','SUCCESS','CLUSTER',0,NULL,3,5,_binary '\0','2023-04-04 16:24:49','0J8IKO7PH9M3',90000),(864879623798789,NULL,NULL,'2023-04-04 16:24:42','NOT_HTTP',191,'PROXY_VALIDATION','SUCCESS','CLUSTER',0,NULL,3,5,_binary '\0','2023-04-04 16:24:42','0J8IKO7PH9M4',90000),(864879623798790,NULL,NULL,'2023-04-04 16:24:49','NOT_HTTP',213,'ACCESS_DATA_PROCESSING','SUCCESS','CLUSTER',0,NULL,2,5,_binary '\0','2023-04-04 16:24:49','0J8IKO7PH9M5',90000),(877685800173569,'2023-01-18 14:40:12','NOT_HTTP','2023-04-04 16:25:16','NOT_HTTP',553,'KEEP_WS_CONNECTION_0','SUCCESS','SINGLE',0,NULL,3,5,_binary '\0','2023-04-04 16:25:16','0J8N43AG8WSI',30000);
/*!40000 ALTER TABLE `job_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `linked_permission_ids_map`
--

DROP TABLE IF EXISTS `linked_permission_ids_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `linked_permission_ids_map` (
  `id` bigint NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`domain_id`),
  CONSTRAINT `FKsc2b879p2s4x7k1tn3kxh7jhf` FOREIGN KEY (`id`) REFERENCES `permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `linked_permission_ids_map`
--

LOCK TABLES `linked_permission_ids_map` WRITE;
/*!40000 ALTER TABLE `linked_permission_ids_map` DISABLE KEYS */;
INSERT INTO `linked_permission_ids_map` VALUES (862170928644096,'0Y8HSHJC34BW'),(862171060240394,'0Y8HHJ47NBD6'),(862171066007552,'0Y8HHJ47NBD7'),(862171066007552,'0Y8HHJ47NBD8'),(862171066007552,'0Y8HHJ47NBDQ'),(862171094319114,'0Y8HHJ47NBD4'),(862171094319114,'0Y8HHJ47NBDP'),(862171185020938,'0Y8HHJ47NBDM'),(862171185020938,'0Y8HHJ47NBDS'),(862171185020938,'0Y8HHJ47NBEH'),(862171185020938,'0Y8HHJ47NBEM'),(862171185020938,'0Y8MLZDBR4T3'),(862171196030985,'0Y8HHJ47NBDN'),(862171196030985,'0Y8HHJ47NBDO'),(862171196030985,'0Y8HHJ47NBDV'),(862171196030985,'0Y8HHJ47NBDW'),(862171196030985,'0Y8M4UTZLTLI'),(862171208613899,'0Y8HHJ47NBDL'),(862171208613900,'0Y8OK4YRZ3MC'),(862171208613901,'0Y8OK4YM1LAW'),(862171208613901,'0Y8OK4YXLDEC'),(862171208613901,'0Y8OK4Z1C7X0'),(862171208613902,'0Y8OK4YFSUFS'),(862171208613903,'0Y8OK4YRZ3MC'),(862171208613904,'0Y8OK4YM1LAW'),(862171208613904,'0Y8OK4YXLDEC'),(862171208613904,'0Y8OK4Z1C7X0'),(862171208613905,'0Y8OK4YFSUFS'),(862171299840010,'0Y8HKE24FWUI'),(862171299840010,'0Y8HKE2QAIVF'),(862171306655753,'0Y8HHJ47NBEX'),(862171306655753,'0Y8HKACDVMDL'),(862171316617227,'0Y8HHJ47NBEY'),(862171388968970,'0Y8HHJ47NBEW'),(862171406270472,'0Y8HHJ47NBEV'),(862171406270472,'0Y8HLUWG1UJ8'),(862171421474823,'0Y8HLUWKQEJ1'),(862171421474823,'0Y8HLUWMX2BX'),(862171421474823,'0Y8HLUWOH91P'),(862171494350859,'0Y8HK4ZLA03Q'),(862171494350859,'0Y8HKEMUH34B'),(862171502215178,'0Y8HKEMWNQX7'),(862433015496712,'0Y8HSHJC34BW'),(862433015496718,'0Y8HHJ47NBD6'),(862433015496720,'0Y8HHJ47NBD4'),(862433015496720,'0Y8HHJ47NBDP'),(862433015496722,'0Y8HHJ47NBD7'),(862433015496722,'0Y8HHJ47NBD8'),(862433015496722,'0Y8HHJ47NBDQ'),(862433015496734,'0Y8HHJ47NBDM'),(862433015496734,'0Y8HHJ47NBDS'),(862433015496734,'0Y8HHJ47NBEH'),(862433015496734,'0Y8HHJ47NBEM'),(862433015496734,'0Y8MLZDBR4T3'),(862433015496736,'0Y8HHJ47NBDN'),(862433015496736,'0Y8HHJ47NBDO'),(862433015496736,'0Y8HHJ47NBDV'),(862433015496736,'0Y8HHJ47NBDW'),(862433015496736,'0Y8M4UTZLTLI'),(862433015496740,'0Y8HHJ47NBDL'),(862433015496754,'0Y8HKE24FWUI'),(862433015496754,'0Y8HKE2QAIVF'),(862433015496756,'0Y8HHJ47NBEY'),(862433015496758,'0Y8HHJ47NBEX'),(862433015496758,'0Y8HKACDVMDL'),(862433015496764,'0Y8HHJ47NBEW'),(862433015496768,'0Y8HHJ47NBEV'),(862433015496768,'0Y8HLUWG1UJ8'),(862433015496770,'0Y8HLUWKQEJ1'),(862433015496770,'0Y8HLUWMX2BX'),(862433015496770,'0Y8HLUWOH91P'),(862433015496780,'0Y8HK4ZLA03Q'),(862433015496780,'0Y8HKEMUH34B'),(862433015496782,'0Y8HKEMWNQX7'),(874584730501120,'0Y8M0IG8RITC'),(874584730501120,'0Y8M0IQAUSZ8'),(874584730501120,'0Y8M0IQQ5FK0'),(874584730501120,'0Y8M0IR20GBI'),(874584730501120,'0Y8M0IRD8ZSN'),(874584730501120,'0Y8M0IRN8L4W'),(874584730501120,'0Y8M4M3J9HJ4'),(874602266361904,'0Y8M0IG8RITC'),(874602266361904,'0Y8M0IQAUSZ8'),(874602266361904,'0Y8M0IQQ5FK0'),(874602266361904,'0Y8M0IR20GBI'),(874602266361904,'0Y8M0IRD8ZSN'),(874602266361904,'0Y8M0IRN8L4W'),(874602266361904,'0Y8M4M3J9HJ4'),(880043699994657,'0Y8NY6E4KKD3'),(880043699994657,'0Y8NY6EHDATT'),(880043699994657,'0Y8NY6ESALN0'),(880043708383267,'0Y8NY6E4KKD3'),(880043708383267,'0Y8NY6EHDATT'),(880043708383267,'0Y8NY6ESALN0'),(881811433979919,'0Y8OKQG9PXQM'),(881811440271375,'0Y8OKQG3SFF7'),(881811447611464,'0Y8OKQGD5JPW'),(881811447611464,'0Y8OKQGFNG2C'),(881811447611464,'0Y8OKQGIRTHW'),(881811499515923,'0Y8OKQG9PXQM'),(881811504234521,'0Y8OKQGD5JPW'),(881811504234521,'0Y8OKQGFNG2C'),(881811504234521,'0Y8OKQGIRTHW'),(881811511050675,'0Y8OKQG3SFF7'),(881933147963399,'0Y8OMAHTGHZA'),(881933147963401,'0Y8OMAHTGHZC'),(881933147963403,'0Y8HSHJC34BW'),(881933147963403,'0Y8OMAHTGHZE'),(881933147963405,'0Y8OMAHTGHZG'),(881933147963407,'0Y8OMAHTGHZI'),(881933147963409,'0Y8HHJ47NBD6'),(881933147963409,'0Y8OMAHTGHZK'),(881933147963411,'0Y8HHJ47NBD4'),(881933147963411,'0Y8HHJ47NBDP'),(881933147963411,'0Y8OMAHTGHZM'),(881933147963413,'0Y8HHJ47NBD7'),(881933147963413,'0Y8HHJ47NBD8'),(881933147963413,'0Y8HHJ47NBDQ'),(881933147963413,'0Y8OMAHTGHZO'),(881933147963415,'0Y8OMAHTGHZQ'),(881933147963417,'0Y8HHJ47NBDM'),(881933147963417,'0Y8HHJ47NBDS'),(881933147963417,'0Y8MLZDBR4T3'),(881933147963417,'0Y8OMAHTGHZS'),(881933147963419,'0Y8HHJ47NBDN'),(881933147963419,'0Y8HHJ47NBDO'),(881933147963419,'0Y8HHJ47NBDV'),(881933147963419,'0Y8HHJ47NBDW'),(881933147963419,'0Y8M4UTZLTLI'),(881933147963419,'0Y8OMAHTGHZU'),(881933147963421,'0Y8HHJ47NBDL'),(881933147963421,'0Y8OMAHTGHZW'),(881933147963423,'0Y8OMAHTGHZY'),(881933147963425,'0Y8HKE24FWUI'),(881933147963425,'0Y8HKE2QAIVF'),(881933147963425,'0Y8OMAHTGI00'),(881933147963427,'0Y8HHJ47NBEY'),(881933147963427,'0Y8OMAHTGI02'),(881933147963429,'0Y8HHJ47NBEX'),(881933147963429,'0Y8HKACDVMDL'),(881933147963429,'0Y8OMAHTGI04'),(881933147963431,'0Y8OMAHTGI06'),(881933147963433,'0Y8HHJ47NBEW'),(881933147963433,'0Y8OMAHTGI08'),(881933147963435,'0Y8HHJ47NBEV'),(881933147963435,'0Y8HLUWG1UJ8'),(881933147963435,'0Y8OMAHTGI0A'),(881933147963437,'0Y8HLUWKQEJ1'),(881933147963437,'0Y8HLUWMX2BX'),(881933147963437,'0Y8HLUWOH91P'),(881933147963437,'0Y8OMAHTGI0C'),(881933147963439,'0Y8OMAHTGI0E'),(881933147963441,'0Y8HK4ZLA03Q'),(881933147963441,'0Y8HKEMUH34B'),(881933147963441,'0Y8OMAHTGI0G'),(881933147963443,'0Y8HKEMWNQX7'),(881933147963443,'0Y8OMAHTGI0I'),(881933147963445,'0Y8M0IG8RITC'),(881933147963445,'0Y8M0IQAUSZ8'),(881933147963445,'0Y8M0IQQ5FK0'),(881933147963445,'0Y8M0IR20GBI'),(881933147963445,'0Y8M0IRD8ZSN'),(881933147963445,'0Y8M0IRN8L4W'),(881933147963445,'0Y8M4M3J9HJ4'),(881933147963445,'0Y8OMAHTGI0K'),(881933147963447,'0Y8NY6E4KKD3'),(881933147963447,'0Y8NY6EHDATT'),(881933147963447,'0Y8NY6ESALN0'),(881933147963447,'0Y8OMAHTGI0M'),(881933147963449,'0Y8OMAHTGI0O'),(881933147963451,'0Y8OK4YM1LAW'),(881933147963451,'0Y8OK4YXLDEC'),(881933147963451,'0Y8OK4Z1C7X0'),(881933147963451,'0Y8OMAHTGI0Q'),(881933147963453,'0Y8OK4YFSUFS'),(881933147963453,'0Y8OMAHTGI0S'),(881933147963455,'0Y8OK4YRZ3MC'),(881933147963455,'0Y8OMAHTGI0U'),(881933147963457,'0Y8OMAHTGI0W'),(881933147963459,'0Y8OKQGD5JPW'),(881933147963459,'0Y8OKQGFNG2C'),(881933147963459,'0Y8OKQGIRTHW'),(881933147963459,'0Y8OMAHTGI0Y'),(881933147963461,'0Y8OKQG3SFF7'),(881933147963461,'0Y8OMAHTGI10'),(881933147963463,'0Y8OKQG9PXQM'),(881933147963463,'0Y8OMAHTGI12');
/*!40000 ALTER TABLE `linked_permission_ids_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_history`
--

DROP TABLE IF EXISTS `login_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_history` (
  `id` bigint NOT NULL,
  `login_at` datetime DEFAULT NULL,
  `domain_id` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `agent` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_history`
--

LOCK TABLES `login_history` WRITE;
/*!40000 ALTER TABLE `login_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_info`
--

DROP TABLE IF EXISTS `login_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_info` (
  `id` bigint NOT NULL,
  `login_at` datetime DEFAULT NULL,
  `domain_id` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `agent` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_domainId` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_info`
--

LOCK TABLES `login_info` WRITE;
/*!40000 ALTER TABLE `login_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `descriptions` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `timestamp` bigint DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `ack` bit(1) NOT NULL,
  `type` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_domain_id` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opt_cool_down`
--

DROP TABLE IF EXISTS `opt_cool_down`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `opt_cool_down` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `opt_type` varchar(255) NOT NULL,
  `executor` varchar(255) NOT NULL,
  `last_opt_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7t5atr8fdeocq2166eativjub` (`executor`,`opt_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opt_cool_down`
--

LOCK TABLES `opt_cool_down` WRITE;
/*!40000 ALTER TABLE `opt_cool_down` DISABLE KEYS */;
/*!40000 ALTER TABLE `opt_cool_down` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `project_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_9xan47a8d87y395189eaipit` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization`
--

LOCK TABLES `organization` WRITE;
/*!40000 ALTER TABLE `organization` DISABLE KEYS */;
/*!40000 ALTER TABLE `organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pending_user`
--

DROP TABLE IF EXISTS `pending_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pending_user` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `activation_code` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjj1emkwcgmfwdbrftunxxaanh` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pending_user`
--

LOCK TABLES `pending_user` WRITE;
/*!40000 ALTER TABLE `pending_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `pending_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `project_id` varchar(255) DEFAULT NULL,
  `shared` bit(1) NOT NULL,
  `system_create` bit(1) NOT NULL,
  `tenant_id` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_c1y16mv395nw4dev48m73a5h3` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (861812326137928,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'API_ACCESS',NULL,'0Y8HHJ47NBD3','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137929,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP400','0Y8HHJ47NBD3','0Y8HHJ47NBD4','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137930,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP401','0Y8HHJ47NBD3','0Y8HHJ47NBD5','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137931,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP402','0Y8HHJ47NBD3','0Y8HHJ47NBD6','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137932,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP403','0Y8HHJ47NBD3','0Y8HHJ47NBD7','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137933,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP404','0Y8HHJ47NBD3','0Y8HHJ47NBD8','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137934,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP405','0Y8HHJ47NBD3','0Y8HHJ47NBD9','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137936,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP407','0Y8HHJ47NBD3','0Y8HHJ47NBDB','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137937,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP408','0Y8HHJ47NBD3','0Y8HHJ47NBDC','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137938,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP409','0Y8HHJ47NBD3','0Y8HHJ47NBDD','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137939,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP40A','0Y8HHJ47NBD3','0Y8HHJ47NBDE','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137941,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP40C','0Y8HHJ47NBD3','0Y8HHJ47NBDG','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137942,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP40D','0Y8HHJ47NBD3','0Y8HHJ47NBDH','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137943,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP40E','0Y8HHJ47NBD3','0Y8HHJ47NBDI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137945,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP40G','0Y8HHJ47NBD3','0Y8HHJ47NBDK','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137946,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP40I','0Y8HHJ47NBD3','0Y8HHJ47NBDL','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137947,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP40J','0Y8HHJ47NBD3','0Y8HHJ47NBDM','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137948,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP40K','0Y8HHJ47NBD3','0Y8HHJ47NBDN','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137949,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP40L','0Y8HHJ47NBD3','0Y8HHJ47NBDO','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137950,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP432','0Y8HHJ47NBD3','0Y8HHJ47NBDP','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137951,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP433','0Y8HHJ47NBD3','0Y8HHJ47NBDQ','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137953,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP435','0Y8HHJ47NBD3','0Y8HHJ47NBDS','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137954,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP437','0Y8HHJ47NBD3','0Y8HHJ47NBDT','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137955,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP438','0Y8HHJ47NBD3','0Y8HHJ47NBDU','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137956,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP439','0Y8HHJ47NBD3','0Y8HHJ47NBDV','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137957,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP43E','0Y8HHJ47NBD3','0Y8HHJ47NBDW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137959,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP43I','0Y8HHJ47NBD3','0Y8HHJ47NBDY','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137960,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP43N','0Y8HHJ47NBD3','0Y8HHJ47NBDZ','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137965,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8AZTODP450','0Y8HHJ47NBD3','0Y8HHJ47NB00','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137968,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8BO3KAHURK','0Y8HHJ47NBD3','0Y8HHJ47NBE6','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137969,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8BPAEMD3B5','0Y8HHJ47NBD3','0Y8HHJ47NBE7','0P8HE307W6IO',_binary '',_binary '',NULL,'API'),(861812326137972,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8F3Q8VWB9C','0Y8HHJ47NBD3','0Y8HHJ47NBEA','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137973,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8F3Q92GAO0','0Y8HHJ47NBD3','0Y8HHJ47NBEB','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137990,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8GB5MSBKE8','0Y8HHJ47NBD3','0Y8HHJ47NBES','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137991,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8HDICM4CU8','0Y8HHJ47NBD3','0Y8HHJ47NBET','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137992,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8HDIISDBLS','0Y8HHJ47NBD3','0Y8HHJ47NBEU','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137993,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8HE2D7RLDT','0Y8HHJ47NBD3','0Y8HHJ47NBEV','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137994,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8HE2N4V2TD','0Y8HHJ47NBD3','0Y8HHJ47NBEW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137995,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8HEZDS0IYO','0Y8HHJ47NBD3','0Y8HHJ47NBEX','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137996,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8HEZDUIFB4','0Y8HHJ47NBD3','0Y8HHJ47NBEY','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(861812326137997,'2022-02-02 14:36:02','NOT_HTTP','2022-02-02 14:36:02','NOT_HTTP',0,'0E8HHI057P4W','0Y8HHJ47NBD3','0Y8HHJ47NBEZ','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862016664764426,'2022-02-07 02:51:48','NOT_HTTP','2022-02-07 02:51:48','NOT_HTTP',0,'0E8HK4ZKYRP1','0Y8HHJ47NBD3','0Y8HK4ZLA03Q','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862028321783905,'2022-02-07 09:02:22','NOT_HTTP','2022-02-07 09:02:22','NOT_HTTP',0,'0E8HKACDKDTT','0Y8HHJ47NBD3','0Y8HKACDVMDL','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862036409450502,'2022-02-07 13:19:27','NOT_HTTP','2022-02-07 13:19:27','NOT_HTTP',0,'0E8HKE24FWU8','0Y8HHJ47NBD3','0Y8HKE24FWUI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862036446150664,'2022-02-07 13:20:37','NOT_HTTP','2022-02-07 13:20:37','NOT_HTTP',0,'0E8HKE2QAIV4','0Y8HHJ47NBD3','0Y8HKE2QAIVF','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862037661974538,'2022-02-07 13:59:17','NOT_HTTP','2022-02-07 13:59:17','NOT_HTTP',0,'0E8HKEMUH340','0Y8HHJ47NBD3','0Y8HKEMUH34B','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862037665644553,'2022-02-07 13:59:24','NOT_HTTP','2022-02-07 13:59:24','NOT_HTTP',0,'0E8HKEMWNQWX','0Y8HHJ47NBD3','0Y8HKEMWNQX7','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862151435616263,'2022-02-10 02:16:02','NOT_HTTP','2022-02-10 02:16:02','NOT_HTTP',0,'0E8HLUWG1UIW','0Y8HHJ47NBD3','0Y8HLUWG1UJ8','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862151442956369,'2022-02-10 02:16:16','NOT_HTTP','2022-02-10 02:16:16','NOT_HTTP',0,'0E8HLUWKQEIP','0Y8HHJ47NBD3','0Y8HLUWKQEJ1','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862151446626385,'2022-02-10 02:16:23','NOT_HTTP','2022-02-10 02:16:23','NOT_HTTP',0,'0E8HLUWMX2BL','0Y8HHJ47NBD3','0Y8HLUWMX2BX','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862151449247825,'2022-02-10 02:16:28','NOT_HTTP','2022-02-10 02:16:28','NOT_HTTP',0,'0E8HLUWOH91D','0Y8HHJ47NBD3','0Y8HLUWOH91P','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862151449247826,'2022-02-10 02:16:28','NOT_HTTP','2022-02-10 02:16:28','NOT_HTTP',0,'0E8HLUWOH91E','0Y8HHJ47NBD3','0Y8HLUWOH92P','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862170844758016,'2022-02-10 12:33:02','0U8AZTODP4H0','2022-02-10 12:33:02','0U8AZTODP4H0',0,'0P8HE307W6IO',NULL,'0Y8HM3TG2CJL','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862170896138242,'2022-02-10 12:34:41','0U8AZTODP4H0','2022-05-07 03:48:57','0U8AZTODP4H0',2,'PROJECT_INFO_MGMT','0Y8HM3TG2CJL','0Y8HM3UAYUBK','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862170928644096,'2022-02-10 12:35:42','0U8AZTODP4H0','2022-02-10 12:35:42','0U8AZTODP4H0',0,'VIEW_PROJECT_INFO','0Y8HM3UAYUBK','0Y8HM3UU0BM0','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862170965868546,'2022-02-10 12:36:54','0U8AZTODP4H0','2022-02-10 12:36:54','0U8AZTODP4H0',0,'EDIT_PROJECT_INFO','0Y8HM3UAYUBK','0Y8HM3VGHEKG','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862170991034369,'2022-02-10 12:37:41','0U8AZTODP4H0','2022-02-10 12:37:41','0U8AZTODP4H0',0,'CLIENT_MGMT','0Y8HM3TG2CJL','0Y8HM3VVGSN4','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862170994180097,'2022-02-10 12:37:48','0U8AZTODP4H0','2022-02-10 12:37:48','0U8AZTODP4H0',0,'API_MGMT','0Y8HM3TG2CJL','0Y8HM3VXC7WG','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862170994180098,'2022-02-10 12:37:48','0U8AZTODP4H0','2022-02-10 12:37:48','0U8AZTODP4H0',0,'CORS_MGMT','0Y8HM3TG2CJL','0Y8HM3VXC7WH','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862170997325824,'2022-02-10 12:37:53','0U8AZTODP4H0','2022-02-10 12:37:53','0U8AZTODP4H0',0,'ROLE_MGMT','0Y8HM3TG2CJL','0Y8HM3VYWEM9','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171001520128,'2022-02-10 12:38:01','0U8AZTODP4H0','2022-02-10 12:38:01','0U8AZTODP4H0',0,'PERMISSION_MGMT','0Y8HM3TG2CJL','0Y8HM3W1EAYP','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171046084609,'2022-02-10 12:39:27','0U8AZTODP4H0','2022-02-10 12:39:27','0U8AZTODP4H0',0,'USER_MGMT','0Y8HM3TG2CJL','0Y8HM3WS8POG','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171060240394,'2022-02-10 12:39:53','0U8AZTODP4H0','2022-02-10 12:39:53','0U8AZTODP4H0',0,'CREATE_CLIENT','0Y8HM3VVGSN4','0Y8HM3X0O4CG','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171066007552,'2022-02-10 12:40:04','0U8AZTODP4H0','2022-02-10 12:40:04','0U8AZTODP4H0',0,'EDIT_CLIENT','0Y8HM3VVGSN4','0Y8HM3X3SHS1','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171094319114,'2022-02-10 12:40:59','0U8AZTODP4H0','2022-02-10 12:40:59','0U8AZTODP4H0',0,'VIEW_CLIENT','0Y8HM3VVGSN4','0Y8HM3XKYJNK','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171185020938,'2022-02-10 12:43:52','0U8AZTODP4H0','2022-02-10 12:43:52','0U8AZTODP4H0',0,'VIEW_API','0Y8HM3VXC7WG','0Y8HM3Z2YLMO','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171196030985,'2022-02-10 12:44:12','0U8AZTODP4H0','2022-02-10 12:44:12','0U8AZTODP4H0',0,'EDIT_API','0Y8HM3VXC7WG','0Y8HM3Z97CHT','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171208613899,'2022-02-10 12:44:37','0U8AZTODP4H0','2022-02-10 12:44:37','0U8AZTODP4H0',0,'CREATE_API','0Y8HM3VXC7WG','0Y8HM3ZH0A2O','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171208613900,'2022-02-10 12:43:52','0U8AZTODP4H0','2022-02-10 12:43:52','0U8AZTODP4H0',0,'VIEW_CORS','0Y8HM3VXC7WH','0Y8HM3VXC7WI','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171208613901,'2022-02-10 12:44:12','0U8AZTODP4H0','2022-02-10 12:44:12','0U8AZTODP4H0',0,'EDIT_CORS','0Y8HM3VXC7WH','0Y8HM3VXC7WJ','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171208613902,'2022-02-10 12:44:37','0U8AZTODP4H0','2022-02-10 12:44:37','0U8AZTODP4H0',0,'CREATE_CORS','0Y8HM3VXC7WH','0Y8HM3VXC7WK','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171208613903,'2022-02-10 12:43:52','0U8AZTODP4H0','2022-02-10 12:43:52','0U8AZTODP4H0',0,'VIEW_CORS','0Y8HM3VXC7WL','0Y8HM3VXC7WM','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862171208613904,'2022-02-10 12:44:12','0U8AZTODP4H0','2022-02-10 12:44:12','0U8AZTODP4H0',0,'EDIT_CORS','0Y8HM3VXC7WL','0Y8HM3VXC7WN','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862171208613905,'2022-02-10 12:44:37','0U8AZTODP4H0','2022-02-10 12:44:37','0U8AZTODP4H0',0,'CREATE_CORS','0Y8HM3VXC7WL','0Y8HM3VXC7WO','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862171299840010,'2022-02-10 12:47:30','0U8AZTODP4H0','2022-02-10 12:47:30','0U8AZTODP4H0',0,'EDIT_ROLE','0Y8HM3VYWEM9','0Y8HM40ZBKLC','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171306655753,'2022-02-10 12:47:43','0U8AZTODP4H0','2022-02-10 12:47:43','0U8AZTODP4H0',0,'VIEW_ROLE','0Y8HM3VYWEM9','0Y8HM4132F41','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171316617227,'2022-02-10 12:48:03','0U8AZTODP4H0','2022-02-10 12:48:03','0U8AZTODP4H0',0,'CREATE_ROLE','0Y8HM3VYWEM9','0Y8HM419B5Z5','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171388968970,'2022-02-10 12:50:20','0U8AZTODP4H0','2022-02-10 12:50:20','0U8AZTODP4H0',0,'CREATE_PERMISSION','0Y8HM3W1EAYP','0Y8HM42GDWXS','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171406270472,'2022-02-10 12:50:53','0U8AZTODP4H0','2022-02-10 12:50:53','0U8AZTODP4H0',0,'VIEW_PERMISSION','0Y8HM3W1EAYP','0Y8HM42QOQV4','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171421474823,'2022-02-10 12:51:22','0U8AZTODP4H0','2022-02-10 12:51:22','0U8AZTODP4H0',0,'EDIT_PERMISSION','0Y8HM3W1EAYP','0Y8HM42ZFE88','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171494350859,'2022-02-10 12:53:42','0U8AZTODP4H0','2022-02-10 12:53:42','0U8AZTODP4H0',0,'VIEW_TENANT_USER','0Y8HM3WS8POG','0Y8HM4474M4G','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862171502215178,'2022-02-10 12:53:57','0U8AZTODP4H0','2022-02-10 12:53:57','0U8AZTODP4H0',0,'EDIT_TENANT_USER','0Y8HM3WS8POG','0Y8HM44BT69S','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(862382467317778,'2022-02-15 04:40:21','NOT_HTTP','2022-02-15 04:40:21','NOT_HTTP',0,'0E8HOT1AO8OW','0Y8HHJ47NBD3','0Y8HOT1AO8P8','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862382501396561,'2022-02-15 04:41:26','NOT_HTTP','2022-02-15 04:41:26','NOT_HTTP',0,'0E8HOT1UYO00','0Y8HHJ47NBD3','0Y8HOT1UYO0D','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862382517649415,'2022-02-15 04:41:57','NOT_HTTP','2022-02-15 04:41:57','NOT_HTTP',0,'0E8HOT24N0U8','0Y8HHJ47NBD3','0Y8HOT24N0UK','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862382524465170,'2022-02-15 04:42:10','NOT_HTTP','2022-02-15 04:42:10','NOT_HTTP',0,'0E8HOT28P3WG','0Y8HHJ47NBD3','0Y8HOT28P3WT','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862382783987793,'2022-02-15 04:50:25','NOT_HTTP','2022-02-15 04:50:25','NOT_HTTP',0,'0E8HOT6J7KSH','0Y8HHJ47NBD3','0Y8HOT6J7KST','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862382793424903,'2022-02-15 04:50:43','NOT_HTTP','2022-02-15 04:50:43','NOT_HTTP',0,'0E8HOT6OTUKG','0Y8HHJ47NBD3','0Y8HOT6OTUKS','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862402490401254,'2022-02-15 15:16:52','NOT_HTTP','2022-02-15 15:16:52','NOT_HTTP',0,'0E8HP28FWEF5','0Y8HHJ47NBD3','0Y8HP28G7MYV','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862433015496708,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'0P8HPG99R56P',NULL,'0Y8HPG9A2DQB','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496710,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'PROJECT_INFO_MGMT','0Y8HPG9A2DQB','0Y8HPG9A2DQD','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496712,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'VIEW_PROJECT_INFO','0Y8HPG9A2DQD','0Y8HPG9A2DQF','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496714,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'EDIT_PROJECT_INFO','0Y8HPG9A2DQD','0Y8HPG9A2DQH','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496716,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'CLIENT_MGMT','0Y8HPG9A2DQB','0Y8HPG9A2DQJ','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496718,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'CREATE_CLIENT','0Y8HPG9A2DQJ','0Y8HPG9A2DQL','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496720,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'VIEW_CLIENT','0Y8HPG9A2DQJ','0Y8HPG9A2DQN','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496722,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'EDIT_CLIENT','0Y8HPG9A2DQJ','0Y8HPG9A2DQP','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496730,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'API_MGMT','0Y8HPG9A2DQB','0Y8HPG9A2DQX','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496731,'2022-02-10 12:37:48','0U8AZTODP4H0','2022-02-10 12:37:48','0U8AZTODP4H0',0,'CORS_MGMT','0Y8HPG9A2DQB','0Y8HM3VXC7WL','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496734,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'VIEW_API','0Y8HPG9A2DQX','0Y8HPG9A2DR1','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496736,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'EDIT_API','0Y8HPG9A2DQX','0Y8HPG9A2DR3','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496740,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'CREATE_API','0Y8HPG9A2DQX','0Y8HPG9A2DR7','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496750,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'ROLE_MGMT','0Y8HPG9A2DQB','0Y8HPG9A2DRH','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496754,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'EDIT_ROLE','0Y8HPG9A2DRH','0Y8HPG9A2DRL','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496756,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'CREATE_ROLE','0Y8HPG9A2DRH','0Y8HPG9A2DRN','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496758,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'VIEW_ROLE','0Y8HPG9A2DRH','0Y8HPG9A2DRP','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496762,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'PERMISSION_MGMT','0Y8HPG9A2DQB','0Y8HPG9A2DRT','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496764,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'CREATE_PERMISSION','0Y8HPG9A2DRT','0Y8HPG9A2DRV','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496768,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'VIEW_PERMISSION','0Y8HPG9A2DRT','0Y8HPG9A2DRZ','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496770,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'EDIT_PERMISSION','0Y8HPG9A2DRT','0Y8HPG9A2DS1','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496776,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'USER_MGMT','0Y8HPG9A2DQB','0Y8HPG9A2DS7','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496780,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'VIEW_TENANT_USER','0Y8HPG9A2DS7','0Y8HPG9A2DSB','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496782,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'EDIT_TENANT_USER','0Y8HPG9A2DS7','0Y8HPG9A2DSD','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(862433015496784,'2022-02-16 07:27:13','NOT_HTTP','2022-02-16 07:27:13','NOT_HTTP',0,'API_ACCESS',NULL,'0Y8HPG9A2DSF','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862446240661532,'2022-02-16 14:27:39','NOT_HTTP','2022-02-16 14:27:39','NOT_HTTP',0,'0E8HPMBZOBNL','0Y8HPG9A2DSF','0Y8HPMBZOBO0','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469695209494,'2022-02-17 02:53:14','NOT_HTTP','2022-02-17 02:53:14','NOT_HTTP',0,'0E8HPX3VLG5D','0Y8HPG9A2DSF','0Y8HPX3VWOP1','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469705170971,'2022-02-17 02:53:33','NOT_HTTP','2022-02-17 02:53:33','NOT_HTTP',0,'0E8HPX41U70G','0Y8HPG9A2DSF','0Y8HPX41U70V','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469723521282,'2022-02-17 02:54:08','NOT_HTTP','2022-02-17 02:54:08','NOT_HTTP',0,'0E8HPX4CG9HD','0Y8HPG9A2DSF','0Y8HPX4CRI11','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469747114011,'2022-02-17 02:54:53','NOT_HTTP','2022-02-17 02:54:53','NOT_HTTP',0,'0E8HPX4QHY35','0Y8HPG9A2DSF','0Y8HPX4QHY3K','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469759172898,'2022-02-17 02:55:16','NOT_HTTP','2022-02-17 02:55:16','NOT_HTTP',0,'0E8HPX4XOEF5','0Y8HPG9A2DSF','0Y8HPX4XZMYT','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469786959899,'2022-02-17 02:56:09','NOT_HTTP','2022-02-17 02:56:09','NOT_HTTP',0,'0E8HPX5EJ7R4','0Y8HPG9A2DSF','0Y8HPX5EJ7RJ','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469790106430,'2022-02-17 02:56:15','NOT_HTTP','2022-02-17 02:56:15','NOT_HTTP',0,'0E8HPX5G3EGX','0Y8HPG9A2DSF','0Y8HPX5G3EHC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469893390369,'2022-02-17 02:59:32','NOT_HTTP','2022-02-17 02:59:32','NOT_HTTP',0,'0E8HPX75L5HQ','0Y8HPG9A2DSF','0Y8HPX75L5I5','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469944770935,'2022-02-17 03:01:10','NOT_HTTP','2022-02-17 03:01:10','NOT_HTTP',0,'0E8HPX806EPS','0Y8HPG9A2DSF','0Y8HPX806EQ8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862469958402064,'2022-02-17 03:01:36','NOT_HTTP','2022-02-17 03:01:36','NOT_HTTP',0,'0E8HPX88AKU8','0Y8HPG9A2DSF','0Y8HPX88AKUN','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470017646628,'2022-02-17 03:03:29','NOT_HTTP','2022-02-17 03:03:29','NOT_HTTP',0,'0E8HPX97VMRY','0Y8HPG9A2DSF','0Y8HPX97VMSD','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470037569568,'2022-02-17 03:04:07','NOT_HTTP','2022-02-17 03:04:07','NOT_HTTP',0,'0E8HPX9JFEV4','0Y8HPG9A2DSF','0Y8HPX9JFEVJ','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470049628187,'2022-02-17 03:04:30','NOT_HTTP','2022-02-17 03:04:30','NOT_HTTP',0,'0E8HPX9QX3WG','0Y8HPG9A2DSF','0Y8HPX9QX3WV','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470056968205,'2022-02-17 03:04:44','NOT_HTTP','2022-02-17 03:04:44','NOT_HTTP',0,'0E8HPX9UZ6YO','0Y8HPG9A2DSF','0Y8HPX9UZ6Z4','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470864371746,'2022-02-17 03:30:24','NOT_HTTP','2022-02-17 03:30:24','NOT_HTTP',0,'0E8HPXN7ONI8','0Y8HPG9A2DSF','0Y8HPXN7ZW1S','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470882197538,'2022-02-17 03:30:58','NOT_HTTP','2022-02-17 03:30:58','NOT_HTTP',0,'0E8HPXNIAQ4W','0Y8HPG9A2DSF','0Y8HPXNIAQ5C','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470894780449,'2022-02-17 03:31:22','NOT_HTTP','2022-02-17 03:31:22','NOT_HTTP',0,'0E8HPXNPSF0H','0Y8HPG9A2DSF','0Y8HPXNQ3NK5','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470929907760,'2022-02-17 03:32:29','NOT_HTTP','2022-02-17 03:32:29','NOT_HTTP',0,'0E8HPXOB0JY8','0Y8HPG9A2DSF','0Y8HPXOB0JYN','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470953500941,'2022-02-17 03:33:14','NOT_HTTP','2022-02-17 03:33:14','NOT_HTTP',0,'0E8HPXOOQZUO','0Y8HPG9A2DSF','0Y8HPXOOQZV3','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470960316450,'2022-02-17 03:33:27','NOT_HTTP','2022-02-17 03:33:27','NOT_HTTP',0,'0E8HPXOST2WW','0Y8HPG9A2DSF','0Y8HPXOST2XC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470972899723,'2022-02-17 03:33:51','NOT_HTTP','2022-02-17 03:33:51','NOT_HTTP',0,'0E8HPXP0ARY8','0Y8HPG9A2DSF','0Y8HPXP0ARYN','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470984433698,'2022-02-17 03:34:13','NOT_HTTP','2022-02-17 03:34:13','NOT_HTTP',0,'0E8HPXP7601H','0Y8HPG9A2DSF','0Y8HPXP7601W','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862470999638326,'2022-02-17 03:34:42','NOT_HTTP','2022-02-17 03:34:42','NOT_HTTP',0,'0E8HPXPG7VNK','0Y8HPG9A2DSF','0Y8HPXPG7VNZ','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471005929733,'2022-02-17 03:34:54','NOT_HTTP','2022-02-17 03:34:54','NOT_HTTP',0,'0E8HPXPK9YPS','0Y8HPG9A2DSF','0Y8HPXPK9YQ7','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471118651787,'2022-02-17 03:38:29','NOT_HTTP','2022-02-17 03:38:29','NOT_HTTP',0,'0E8HPXRF2R4G','0Y8HPG9A2DSF','0Y8HPXRF2R4W','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471127040048,'2022-02-17 03:38:45','NOT_HTTP','2022-02-17 03:38:45','NOT_HTTP',0,'0E8HPXRKDS74','0Y8HPG9A2DSF','0Y8HPXRKDS7J','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471321026583,'2022-02-17 03:44:55','NOT_HTTP','2022-02-17 03:44:55','NOT_HTTP',0,'0E8HPXURKCS5','0Y8HPG9A2DSF','0Y8HPXURKCSK','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471360348188,'2022-02-17 03:46:10','NOT_HTTP','2022-02-17 03:46:10','NOT_HTTP',0,'0E8HPXVFADXC','0Y8HPG9A2DSF','0Y8HPXVFADXR','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471366640010,'2022-02-17 03:46:22','NOT_HTTP','2022-02-17 03:46:22','NOT_HTTP',0,'0E8HPXVIPZWH','0Y8HPG9A2DSF','0Y8HPXVJ18G5','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471375028266,'2022-02-17 03:46:38','NOT_HTTP','2022-02-17 03:46:38','NOT_HTTP',0,'0E8HPXVO114W','0Y8HPG9A2DSF','0Y8HPXVO115B','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471387087350,'2022-02-17 03:47:01','NOT_HTTP','2022-02-17 03:47:01','NOT_HTTP',0,'0E8HPXVUW934','0Y8HPG9A2DSF','0Y8HPXVUW93J','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471398097654,'2022-02-17 03:47:22','NOT_HTTP','2022-02-17 03:47:22','NOT_HTTP',0,'0E8HPXW1G8HS','0Y8HPG9A2DSF','0Y8HPXW1G8I7','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471406485537,'2022-02-17 03:47:38','NOT_HTTP','2022-02-17 03:47:38','NOT_HTTP',0,'0E8HPXW6G16P','0Y8HPG9A2DSF','0Y8HPXW6R9QM','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471423262752,'2022-02-17 03:48:10','NOT_HTTP','2022-02-17 03:48:10','NOT_HTTP',0,'0E8HPXWGFMQ9','0Y8HPG9A2DSF','0Y8HPXWGQV45','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471787119009,'2022-02-17 03:59:45','NOT_HTTP','2022-02-17 03:59:45','NOT_HTTP',0,'0E8HPY2HDK6D','0Y8HPG9A2DSF','0Y8HPY2HDK6S','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471789742108,'2022-02-17 03:59:49','NOT_HTTP','2022-02-17 03:59:49','NOT_HTTP',0,'0E8HPY2IXQTD','0Y8HPG9A2DSF','0Y8HPY2IXQTS','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471831685160,'2022-02-17 04:01:09','NOT_HTTP','2022-02-17 04:01:09','NOT_HTTP',0,'0E8HPY37WQ9T','0Y8HPG9A2DSF','0Y8HPY37WQA8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862471834304910,'2022-02-17 04:01:14','NOT_HTTP','2022-02-17 04:01:14','NOT_HTTP',0,'0E8HPY395OG1','0Y8HPG9A2DSF','0Y8HPY39GWZP','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473203220878,'2022-02-17 04:44:46','NOT_HTTP','2022-02-17 04:44:46','NOT_HTTP',0,'0E8HPYPW6ARL','0Y8HPG9A2DSF','0Y8HPYPW6AS0','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473210561286,'2022-02-17 04:44:59','NOT_HTTP','2022-02-17 04:44:59','NOT_HTTP',0,'0E8HPYQ0UUWX','0Y8HPG9A2DSF','0Y8HPYQ0UUXC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473265087127,'2022-02-17 04:46:43','NOT_HTTP','2022-02-17 04:46:43','NOT_HTTP',0,'0E8HPYQXBJEP','0Y8HPG9A2DSF','0Y8HPYQXBJF4','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473282388444,'2022-02-17 04:47:16','NOT_HTTP','2022-02-17 04:47:16','NOT_HTTP',0,'0E8HPYR7MDH2','0Y8HPG9A2DSF','0Y8HPYR7MDHH','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473296544234,'2022-02-17 04:47:43','NOT_HTTP','2022-02-17 04:47:43','NOT_HTTP',0,'0E8HPYRG1S01','0Y8HPG9A2DSF','0Y8HPYRG1S0G','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473311224484,'2022-02-17 04:48:12','NOT_HTTP','2022-02-17 04:48:12','NOT_HTTP',0,'0E8HPYROSF7L','0Y8HPG9A2DSF','0Y8HPYROSF80','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473431286182,'2022-02-17 04:52:00','NOT_HTTP','2022-02-17 04:52:00','NOT_HTTP',0,'0E8HPYTNYJ81','0Y8HPG9A2DSF','0Y8HPYTO9RLX','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473437053403,'2022-02-17 04:52:11','NOT_HTTP','2022-02-17 04:52:11','NOT_HTTP',0,'0E8HPYTRPDKX','0Y8HPG9A2DSF','0Y8HPYTRPDLC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473456452059,'2022-02-17 04:52:48','NOT_HTTP','2022-02-17 04:52:48','NOT_HTTP',0,'0E8HPYU395OH','0Y8HPG9A2DSF','0Y8HPYU395OW','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473465365239,'2022-02-17 04:53:05','NOT_HTTP','2022-02-17 04:53:05','NOT_HTTP',0,'0E8HPYU8K6WW','0Y8HPG9A2DSF','0Y8HPYU8K6XC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473474802423,'2022-02-17 04:53:23','NOT_HTTP','2022-02-17 04:53:23','NOT_HTTP',0,'0E8HPYUE6GOW','0Y8HPG9A2DSF','0Y8HPYUE6GPC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473487909320,'2022-02-17 04:53:48','NOT_HTTP','2022-02-17 04:53:48','NOT_HTTP',0,'0E8HPYULZE9T','0Y8HPG9A2DSF','0Y8HPYULZEA8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473497346807,'2022-02-17 04:54:06','NOT_HTTP','2022-02-17 04:54:06','NOT_HTTP',0,'0E8HPYURLO1S','0Y8HPG9A2DSF','0Y8HPYURLO28','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473513599461,'2022-02-17 04:54:37','NOT_HTTP','2022-02-17 04:54:37','NOT_HTTP',0,'0E8HPYV0YSCH','0Y8HPG9A2DSF','0Y8HPYV1A0W6','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473524085168,'2022-02-17 04:54:57','NOT_HTTP','2022-02-17 04:54:57','NOT_HTTP',0,'0E8HPYV7IRR5','0Y8HPG9A2DSF','0Y8HPYV7IRRK','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473546105317,'2022-02-17 04:55:39','NOT_HTTP','2022-02-17 04:55:39','NOT_HTTP',0,'0E8HPYVKMQPI','0Y8HPG9A2DSF','0Y8HPYVKMQPX','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473552396675,'2022-02-17 04:55:51','NOT_HTTP','2022-02-17 04:55:51','NOT_HTTP',0,'0E8HPYVO2CJL','0Y8HPG9A2DSF','0Y8HPYVO2CK0','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473559736718,'2022-02-17 04:56:05','NOT_HTTP','2022-02-17 04:56:05','NOT_HTTP',0,'0E8HPYVSFO5D','0Y8HPG9A2DSF','0Y8HPYVSFO5S','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473566552451,'2022-02-17 04:56:18','NOT_HTTP','2022-02-17 04:56:18','NOT_HTTP',0,'0E8HPYVWHR7L','0Y8HPG9A2DSF','0Y8HPYVWSZR9','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473573892586,'2022-02-17 04:56:32','NOT_HTTP','2022-02-17 04:56:32','NOT_HTTP',0,'0E8HPYW16BCX','0Y8HPG9A2DSF','0Y8HPYW16BDC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473582281463,'2022-02-17 04:56:48','NOT_HTTP','2022-02-17 04:56:48','NOT_HTTP',0,'0E8HPYW6641S','0Y8HPG9A2DSF','0Y8HPYW66428','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473588572635,'2022-02-17 04:57:00','NOT_HTTP','2022-02-17 04:57:00','NOT_HTTP',0,'0E8HPYW9WYKH','0Y8HPG9A2DSF','0Y8HPYW9WYKW','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473596436847,'2022-02-17 04:57:15','NOT_HTTP','2022-02-17 04:57:15','NOT_HTTP',0,'0E8HPYWEAA69','0Y8HPG9A2DSF','0Y8HPYWEAA6O','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473629991613,'2022-02-17 04:58:19','NOT_HTTP','2022-02-17 04:58:19','NOT_HTTP',0,'0E8HPYWYKPHC','0Y8HPG9A2DSF','0Y8HPYWYKPHS','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473636283006,'2022-02-17 04:58:31','NOT_HTTP','2022-02-17 04:58:31','NOT_HTTP',0,'0E8HPYX20BGH','0Y8HPG9A2DSF','0Y8HPYX20BGW','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473720693505,'2022-02-17 05:01:13','NOT_HTTP','2022-02-17 05:01:13','NOT_HTTP',0,'0E8HPYYG9IWX','0Y8HPG9A2DSF','0Y8HPYYGKRGM','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473737995024,'2022-02-17 05:01:46','NOT_HTTP','2022-02-17 05:01:46','NOT_HTTP',0,'0E8HPYYQVLDT','0Y8HPG9A2DSF','0Y8HPYYQVLE8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473909961102,'2022-02-17 05:07:14','NOT_HTTP','2022-02-17 05:07:14','NOT_HTTP',0,'0E8HPZ1KY769','0Y8HPG9A2DSF','0Y8HPZ1L9FK6','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473918873998,'2022-02-17 05:07:30','NOT_HTTP','2022-02-17 05:07:30','NOT_HTTP',0,'0E8HPZ1Q988X','0Y8HPG9A2DSF','0Y8HPZ1Q989C','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473933029774,'2022-02-17 05:07:57','NOT_HTTP','2022-02-17 05:07:57','NOT_HTTP',0,'0E8HPZ1YOMWX','0Y8HPG9A2DSF','0Y8HPZ1YZVGL','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473939845601,'2022-02-17 05:08:10','NOT_HTTP','2022-02-17 05:08:10','NOT_HTTP',0,'0E8HPZ231YIP','0Y8HPG9A2DSF','0Y8HPZ231YJ4','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473950331370,'2022-02-17 05:08:30','NOT_HTTP','2022-02-17 05:08:30','NOT_HTTP',0,'0E8HPZ29APDT','0Y8HPG9A2DSF','0Y8HPZ29APE8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473959244256,'2022-02-17 05:08:47','NOT_HTTP','2022-02-17 05:08:47','NOT_HTTP',0,'0E8HPZ2ELQM9','0Y8HPG9A2DSF','0Y8HPZ2ELQMO','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473971302798,'2022-02-17 05:09:10','NOT_HTTP','2022-02-17 05:09:10','NOT_HTTP',0,'0E8HPZ2LGYKH','0Y8HPG9A2DSF','0Y8HPZ2LS745','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862473984410065,'2022-02-17 05:09:35','NOT_HTTP','2022-02-17 05:09:35','NOT_HTTP',0,'0E8HPZ2TL4OX','0Y8HPG9A2DSF','0Y8HPZ2TL4PC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(862670893351306,'2022-02-21 13:29:09','NOT_HTTP','2022-02-21 13:29:09','NOT_HTTP',0,'0E8HSHJC34BK','0Y8HHJ47NBD3','0Y8HSHJC34BW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(862670893351407,'2022-02-21 13:29:09','NOT_HTTP','2022-02-21 13:29:09','NOT_HTTP',0,'0E8CFEHEM0W0','0Y8HHJ47NBD3','0Y8HVWH0K64P','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(863019435295462,'2022-03-01 06:09:01','NOT_HTTP','2022-03-01 06:09:01','NOT_HTTP',0,'0E8HWXNKYL8H','0Y8HPG9A2DSF','0Y8HWXNKYL8W','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(863019443159718,'2022-03-01 06:09:16','NOT_HTTP','2022-03-01 06:09:16','NOT_HTTP',0,'0E8HWXNPBWU9','0Y8HPG9A2DSF','0Y8HWXNPN5DX','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(863019449975539,'2022-03-01 06:09:29','NOT_HTTP','2022-03-01 06:09:29','NOT_HTTP',0,'0E8HWXNTP8G1','0Y8HPG9A2DSF','0Y8HWXNTP8GG','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(863019458364080,'2022-03-01 06:09:45','NOT_HTTP','2022-03-01 06:09:45','NOT_HTTP',0,'0E8HWXNYP14W','0Y8HPG9A2DSF','0Y8HWXNYP15C','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(863019458364081,'2022-03-01 06:09:45','NOT_HTTP','2022-03-01 06:09:45','NOT_HTTP',0,'0E8BPAEMD3B7','0Y8HPG9A2DSF','0Y8HHJ47NBE9','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(863019458364082,'2022-03-01 06:09:45','NOT_HTTP','2022-03-01 06:09:45','NOT_HTTP',0,'0E8CFEHOWUTC','0Y8HPG9A2DSF','0Y8HYA2GSU80','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(863019458364083,'2022-03-01 06:09:45','NOT_HTTP','2022-03-01 06:09:45','NOT_HTTP',0,'0E8BPAEMD3B6','0Y8HPG9A2DSF','0Y8HHJ47NBE8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API'),(863374104068935,'2022-03-09 02:03:38','NOT_HTTP','2022-03-09 02:03:38','NOT_HTTP',0,'0E8I1GL5LA81','0Y8HHJ47NBD3','0Y8I1GL5LA8B','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(863374104068937,'2022-03-09 02:03:38','NOT_HTTP','2022-03-09 02:03:38','NOT_HTTP',0,'0E8I1GL5LA82','0Y8HHJ47NBD3','0Y8I1GL5LA8C','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(863439734441125,'2022-03-10 07:49:58','NOT_HTTP','2022-03-10 07:49:58','NOT_HTTP',0,'0E8I2AQK7X8G','0Y8HHJ47NBD3','0Y8I2AQK7X8R','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(863711892341119,'2022-03-16 03:01:37','NOT_HTTP','2022-03-16 03:01:37','NOT_HTTP',0,'0E8I5RRK09HC','0Y8HHJ47NBD3','0Y8I5RRK09HV','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(864879848195042,'2022-04-10 21:49:56','NOT_HTTP','2022-04-10 21:49:56','NOT_HTTP',0,'0E8IKOBERLKW','0Y8HHJ47NBD3','0Y8IKOBERLMT','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(865479233110779,'2022-04-24 03:23:53','NOT_HTTP','2022-04-24 03:23:53','NOT_HTTP',0,'0E8ISBO52IPI','0Y8HHJ47NBD3','0Y8ISBO52IRF','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(865515703107956,'2022-04-24 22:43:14','NOT_HTTP','2022-04-24 22:43:14','NOT_HTTP',0,'0E8ISSFAD4X7','0Y8HHJ47NBD3','0Y8ISSFAD4XQ','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(865516403032363,'2022-04-24 23:05:29','NOT_HTTP','2022-04-24 23:05:29','NOT_HTTP',0,'0E8ISSQV2Y2O','0Y8HHJ47NBD3','0Y8ISSQV2Y3B','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(865708047073581,'2022-04-29 04:37:41','NOT_HTTP','2022-04-29 04:37:41','NOT_HTTP',0,'0E8IV8SAOM4K','0Y8HHJ47NBD3','0Y8IV8SAZUO9','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(866012517892106,'2022-05-05 21:56:33','NOT_HTTP','2022-05-05 21:56:33','NOT_HTTP',0,'0E8IZ4MZGPHG','0Y8HHJ47NBD3','0Y8IZ4MZRY10','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(866067875366317,'2022-05-07 03:16:19','NOT_HTTP','2022-05-07 03:16:19','NOT_HTTP',0,'0E8IZU2IT6GX','0Y8HHJ47NBD3','0Y8IZU2J4F0P','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(866070308585771,'2022-05-07 04:33:39','NOT_HTTP','2022-05-07 04:33:39','NOT_HTTP',0,'0E8IZV7FU1VO','0Y8HHJ47NBD3','0Y8IZV7G5AFH','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(866101805187427,'2022-05-07 21:14:54','NOT_HTTP','2022-05-07 21:14:54','NOT_HTTP',0,'0E8J09OC3JAF','0Y8HHJ47NBD3','0Y8J09OCERRD','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(866793668739098,'2022-05-23 03:48:39','NOT_HTTP','2022-05-23 03:48:39','NOT_HTTP',0,'0E8J93IHSKUG','0Y8HHJ47NBD3','0Y8J93IHSL5M','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(866794398024052,'2022-05-23 04:11:51','NOT_HTTP','2022-05-23 04:11:51','NOT_HTTP',0,'0E8J93UKAWP0','0Y8HHJ47NBD3','0Y8J93UKAWPJ','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(866863787540493,'2022-05-24 16:57:41','NOT_HTTP','2022-05-24 16:57:41','NOT_HTTP',0,'0E8J9ZGQNTOK','0Y8HHJ47NBD3','0Y8J9ZGQNTP3','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(874584270176308,'2022-11-11 03:25:12','NOT_HTTP','2022-11-11 03:25:12','NOT_HTTP',0,'0E8M0IG77C3K','0Y8HHJ47NBD3','0Y8M0IG8RITC','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(874584730501120,'2022-11-11 03:39:50','NOT_HTTP','2022-11-11 03:48:43','NOT_HTTP',1,'SUB_REQ_MGMT','0Y8HM3TG2CJL','0Y8M0IOE6LFK','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(874584866291712,'2022-11-11 03:44:09','NOT_HTTP','2022-11-11 03:44:09','NOT_HTTP',0,'0E8M0IQ9LUSY','0Y8HHJ47NBD3','0Y8M0IQAUSZ8','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(874584909807616,'2022-11-11 03:45:32','NOT_HTTP','2022-11-11 03:45:32','NOT_HTTP',0,'0E8M0IQOWHG2','0Y8HHJ47NBD3','0Y8M0IQQ5FK0','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(874584915574790,'2022-11-11 03:45:44','NOT_HTTP','2022-11-11 03:45:44','NOT_HTTP',0,'0E8M0IR0RI0W','0Y8HHJ47NBD3','0Y8M0IR20GBI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(874584956993536,'2022-11-11 03:47:02','NOT_HTTP','2022-11-11 03:47:02','NOT_HTTP',0,'0E8M0IRC01KW','0Y8HHJ47NBD3','0Y8M0IRD8ZSN','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(874584966955013,'2022-11-11 03:47:21','NOT_HTTP','2022-11-11 03:47:21','NOT_HTTP',0,'0E8M0IRLZMYO','0Y8HHJ47NBD3','0Y8M0IRN8L4W','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(874602266361904,'2022-11-11 12:57:17','NOT_HTTP','2022-11-11 12:57:17','NOT_HTTP',0,'SUB_REQ_MGMT','0Y8HPG9A2DQB','0Y8M0QQFTWD1','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(874905661866292,'2022-11-18 05:41:59','NOT_HTTP','2022-11-18 05:41:59','NOT_HTTP',0,'0E8M4M3IY8ZK','0Y8HHJ47NBD3','0Y8M4M3J9HJ4','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(874924645810203,'2022-11-18 15:45:27','NOT_HTTP','2022-11-18 15:45:27','NOT_HTTP',0,'0E8M4UTZLTKZ','0Y8HHJ47NBD3','0Y8M4UTZLTLI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(876266712990199,'2022-12-18 06:48:38','NOT_HTTP','2022-12-18 06:48:38','NOT_HTTP',0,'0E8MLZDBR4SG','0Y8HHJ47NBD3','0Y8MLZDBR4T3','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(877686335996488,'2023-01-18 14:57:14','NOT_HTTP','2023-01-18 14:57:14','NOT_HTTP',0,'0E8N43JAY7FR','0Y8HHJ47NBD3','0Y8N43JAY7GA','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(878324963868678,'2023-02-01 17:18:39','NOT_HTTP','2023-02-01 17:18:39','NOT_HTTP',0,'0E8NC8X16N7K','0Y8HHJ47NBD3','0Y8NC8X16N81','0P8HE307W6IO',_binary '',_binary '',NULL,'API'),(878324978548833,'2023-02-01 17:19:08','NOT_HTTP','2023-02-01 17:19:08','NOT_HTTP',0,'0E8NC8XA8J0V','0Y8HHJ47NBD3','0Y8NC8XA8J1B','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(878818638692358,'2023-02-12 14:52:09','NOT_HTTP','2023-02-12 14:52:09','NOT_HTTP',0,'0E8NIJPIFF28','0Y8HHJ47NBD3','0Y8NIJPIFF2U','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(878818653372424,'2023-02-12 14:52:38','NOT_HTTP','2023-02-12 14:52:38','NOT_HTTP',0,'0E8NIJPRHATC','0Y8HHJ47NBD3','0Y8NIJPRHB1D','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(879037695656215,'2023-02-17 10:55:48','NOT_HTTP','2023-02-17 10:55:48','NOT_HTTP',0,'0E8NLCCBLMGW','0Y8HHJ47NBD3','0Y8NLCCBLMHG','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(879037958324231,'2023-02-17 11:04:08','NOT_HTTP','2023-02-17 11:04:08','NOT_HTTP',0,'0E8NLCGNOA2R','0Y8HHJ47NBD3','0Y8NLCGNZIMI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(879716447813640,'2023-03-04 10:32:45','NOT_HTTP','2023-03-04 10:32:45','NOT_HTTP',0,'0E8NU05MSQO0','0Y8HHJ47NBD3','0Y8NU05MSQOM','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(880043478745096,'2023-03-11 15:48:47','NOT_HTTP','2023-03-11 15:48:47','NOT_HTTP',0,'0E8NY6E4KKCH','0Y8HHJ47NBD3','0Y8NY6E4KKD3','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(880043500240899,'2023-03-11 15:49:27','NOT_HTTP','2023-03-11 15:49:27','NOT_HTTP',0,'0E8NY6EHDARV','0Y8HHJ47NBD3','0Y8NY6EHDATT','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(880043518066696,'2023-03-11 15:50:02','NOT_HTTP','2023-03-11 15:50:02','NOT_HTTP',0,'0E8NY6ERZD37','0Y8HHJ47NBD3','0Y8NY6ESALN0','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(880043699994657,'2023-03-11 15:55:49','0U8AZTODP4H0','2023-03-11 15:55:49','0U8AZTODP4H0',0,'ADMIN_MGMT','0Y8HM3TG2CJL','0Y8NY6HSX6O0','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(880043708383267,'2023-03-11 15:56:05','0U8AZTODP4H0','2023-03-11 15:56:05','0U8AZTODP4H0',0,'ADMIN_MGMT','0Y8HPG9A2DQB','0Y8NY6HXWZCX','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(881764364976137,'2023-04-18 15:34:17','NOT_HTTP','2023-04-18 15:34:17','NOT_HTTP',0,'0E8OK4YFSUF8','0Y8HHJ47NBD3','0Y8OK4YFSUFS','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881764375461897,'2023-04-18 15:34:37','NOT_HTTP','2023-04-18 15:34:37','NOT_HTTP',0,'0E8OK4YM1LAC','0Y8HHJ47NBD3','0Y8OK4YM1LAW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881764384899158,'2023-04-18 15:34:55','NOT_HTTP','2023-04-18 15:34:55','NOT_HTTP',0,'0E8OK4YRZ3LS','0Y8HHJ47NBD3','0Y8OK4YRZ3MC','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881764394336342,'2023-04-18 15:35:13','NOT_HTTP','2023-04-18 15:35:13','NOT_HTTP',0,'0E8OK4YXLDDS','0Y8HHJ47NBD3','0Y8OK4YXLDEC','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881764400627721,'2023-04-18 15:35:25','NOT_HTTP','2023-04-18 15:35:25','NOT_HTTP',0,'0E8OK4Z1C7WG','0Y8HHJ47NBD3','0Y8OK4Z1C7X0','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881811145097230,'2023-04-19 16:21:23','NOT_HTTP','2023-04-19 16:21:23','NOT_HTTP',0,'0E8OKQG3H6V8','0Y8HHJ47NBD3','0Y8OKQG3SFF7','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881811155582985,'2023-04-19 16:21:43','NOT_HTTP','2023-04-19 16:21:43','NOT_HTTP',0,'0E8OKQG9EP6O','0Y8HHJ47NBD3','0Y8OKQG9PXQM','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881811160825927,'2023-04-19 16:21:53','NOT_HTTP','2023-04-19 16:21:53','NOT_HTTP',0,'0E8OKQGD5JPC','0Y8HHJ47NBD3','0Y8OKQGD5JPW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881811165020894,'2023-04-19 16:22:01','NOT_HTTP','2023-04-19 16:22:01','NOT_HTTP',0,'0E8OKQGFNG1S','0Y8HHJ47NBD3','0Y8OKQGFNG2C','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881811170263053,'2023-04-19 16:22:11','NOT_HTTP','2023-04-19 16:22:11','NOT_HTTP',0,'0E8OKQGIRTHC','0Y8HHJ47NBD3','0Y8OKQGIRTHW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(881811414581262,'2023-04-19 16:29:57','0U8AZTODP4H0','2023-04-19 16:33:40','0U8AZTODP4H0',1,'CACHE_MGMT','0Y8HM3TG2CJL','0Y8OKQKKJN5W','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(881811433979919,'2023-04-19 16:30:34','0U8AZTODP4H0','2023-04-19 16:33:36','0U8AZTODP4H0',1,'VIEW_CACHE','0Y8OKQKKJN5W','0Y8OKQKW3F9C','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(881811440271375,'2023-04-19 16:30:46','0U8AZTODP4H0','2023-04-19 16:33:30','0U8AZTODP4H0',1,'CREATE_CACHE','0Y8OKQKKJN5W','0Y8OKQKZU9S0','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(881811447611464,'2023-04-19 16:30:59','0U8AZTODP4H0','2023-04-19 16:33:26','0U8AZTODP4H0',1,'EDIT_CACHE','0Y8OKQKKJN5W','0Y8OKQL47LF3','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON'),(881811463340057,'2023-04-19 16:31:30','0U8AZTODP4H0','2023-04-19 16:38:06','0U8AZTODP4H0',3,'CACHE_MGMT','0Y8HPG9A2DQB','0Y8OKQLDKPOG','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(881811499515923,'2023-04-19 16:32:39','0U8AZTODP4H0','2023-04-19 16:34:16','0U8AZTODP4H0',1,'VIEW_CACHE','0Y8OKQLDKPOG','0Y8OKQLZ435W','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(881811504234521,'2023-04-19 16:32:48','0U8AZTODP4H0','2023-04-19 16:34:20','0U8AZTODP4H0',1,'EDIT_CACHE','0Y8OKQLDKPOG','0Y8OKQM1X81S','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(881811511050675,'2023-04-19 16:33:00','0U8AZTODP4H0','2023-04-19 16:34:25','0U8AZTODP4H0',1,'CREATE_CACHE','0Y8OKQLDKPOG','0Y8OKQM5ZB4A','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON'),(881933147963399,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'0P8OMAHSU0W4',NULL,'0Y8OMAHTGHZA','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963401,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'PROJECT_INFO_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGHZC','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963403,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'VIEW_PROJECT_INFO','0Y8OMAHTGHZC','0Y8OMAHTGHZE','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963405,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'EDIT_PROJECT_INFO','0Y8OMAHTGHZC','0Y8OMAHTGHZG','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963407,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'CLIENT_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGHZI','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963409,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'CREATE_CLIENT','0Y8OMAHTGHZI','0Y8OMAHTGHZK','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963411,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'VIEW_CLIENT','0Y8OMAHTGHZI','0Y8OMAHTGHZM','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963413,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'EDIT_CLIENT','0Y8OMAHTGHZI','0Y8OMAHTGHZO','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963415,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'API_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGHZQ','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963417,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'VIEW_API','0Y8OMAHTGHZQ','0Y8OMAHTGHZS','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963419,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'EDIT_API','0Y8OMAHTGHZQ','0Y8OMAHTGHZU','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963421,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'CREATE_API','0Y8OMAHTGHZQ','0Y8OMAHTGHZW','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963423,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'ROLE_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGHZY','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963425,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'EDIT_ROLE','0Y8OMAHTGHZY','0Y8OMAHTGI00','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963427,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'CREATE_ROLE','0Y8OMAHTGHZY','0Y8OMAHTGI02','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963429,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'VIEW_ROLE','0Y8OMAHTGHZY','0Y8OMAHTGI04','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963431,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'PERMISSION_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGI06','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963433,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'CREATE_PERMISSION','0Y8OMAHTGI06','0Y8OMAHTGI08','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963435,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'VIEW_PERMISSION','0Y8OMAHTGI06','0Y8OMAHTGI0A','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963437,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'EDIT_PERMISSION','0Y8OMAHTGI06','0Y8OMAHTGI0C','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963439,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'USER_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGI0E','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963441,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'VIEW_TENANT_USER','0Y8OMAHTGI0E','0Y8OMAHTGI0G','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963443,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'EDIT_TENANT_USER','0Y8OMAHTGI0E','0Y8OMAHTGI0I','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963445,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'SUB_REQ_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGI0K','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963447,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'ADMIN_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGI0M','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963449,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'CORS_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGI0O','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963451,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'EDIT_CORS','0Y8OMAHTGI0O','0Y8OMAHTGI0Q','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963453,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'CREATE_CORS','0Y8OMAHTGI0O','0Y8OMAHTGI0S','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963455,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'VIEW_CORS','0Y8OMAHTGI0O','0Y8OMAHTGI0U','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963457,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'CACHE_MGMT','0Y8OMAHTGHZA','0Y8OMAHTGI0W','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963459,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'EDIT_CACHE','0Y8OMAHTGI0W','0Y8OMAHTGI0Y','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963461,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'CREATE_CACHE','0Y8OMAHTGI0W','0Y8OMAHTGI10','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963463,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'VIEW_CACHE','0Y8OMAHTGI0W','0Y8OMAHTGI12','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON'),(881933147963465,'2023-04-22 08:59:45','NOT_HTTP','2023-04-22 08:59:45','NOT_HTTP',0,'API_ACCESS',NULL,'0Y8OMAHTGI14','0P8OMAHSU0W4',_binary '\0',_binary '',NULL,'API'),(881933380747273,'2023-04-22 09:07:09','NOT_HTTP','2023-04-22 09:07:09','NOT_HTTP',0,'0E8OMALNFE7X','0Y8OMAHTGI14','0Y8OMALNQMMI','0P8OMAHSU0W4',_binary '',_binary '',NULL,'API'),(882926491271177,'2023-05-14 07:17:17','NOT_HTTP','2023-05-14 07:17:17','NOT_HTTP',0,'0E8OYYTVSIKG','0Y8HHJ47NBD3','0Y8OYY45NEVK','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API'),(882926517485622,'2023-05-14 07:18:06','NOT_HTTP','2023-05-14 07:18:06','NOT_HTTP',0,'0E8OYYUBPMCK','0Y8HHJ47NBD3','0Y8OYYUBPMEE','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API');
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `position`
--

DROP TABLE IF EXISTS `position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `position` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_p1hq19frkx7qlt9rr1sp1ftbp` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `position`
--

LOCK TABLES `position` WRITE;
/*!40000 ALTER TABLE `position` DISABLE KEYS */;
/*!40000 ALTER TABLE `position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_8l1cccuisamgod349kdqw52e6` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (861542163677185,'2022-01-27 05:27:48','0U8AZTODP4H0','2022-01-27 05:27:48','0U8AZTODP4H0',0,'MT-AUTH','0P8HE307W6IO'),(862433014972418,'2022-02-15 21:27:13','0U8HPG93IED3','2022-02-15 21:27:13','0U8HPG93IED3',0,'MT-MALL','0P8HPG99R56P'),(881933146914821,'2023-04-22 08:59:43','0U8OMAGVFMS3','2023-04-22 08:59:43','0U8OMAGVFMS3',0,'演示项目','0P8OMAHSU0W4');
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `published_event_tracker`
--

DROP TABLE IF EXISTS `published_event_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `published_event_tracker` (
  `id` bigint NOT NULL,
  `last_published_id` bigint NOT NULL,
  `version` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `published_event_tracker`
--

LOCK TABLES `published_event_tracker` WRITE;
/*!40000 ALTER TABLE `published_event_tracker` DISABLE KEYS */;
INSERT INTO `published_event_tracker` VALUES (864064908558366,0,0);
/*!40000 ALTER TABLE `published_event_tracker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `raw_access_record`
--

DROP TABLE IF EXISTS `raw_access_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `raw_access_record` (
  `id` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  `instance_id` varchar(255) NOT NULL,
  `record_id` varchar(255) NOT NULL,
  `record` varchar(255) NOT NULL,
  `is_request` bit(1) NOT NULL,
  `processed` bit(1) NOT NULL,
  `is_response` bit(1) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_combined_record` (`name`,`instance_id`,`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `raw_access_record`
--

LOCK TABLES `raw_access_record` WRITE;
/*!40000 ALTER TABLE `raw_access_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `raw_access_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resources_map`
--

DROP TABLE IF EXISTS `resources_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resources_map` (
  `id` bigint NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`domain_id`),
  CONSTRAINT `FKt1ali769oqq35mcithj8xrvl8` FOREIGN KEY (`id`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resources_map`
--

LOCK TABLES `resources_map` WRITE;
/*!40000 ALTER TABLE `resources_map` DISABLE KEYS */;
INSERT INTO `resources_map` VALUES (843498099048450,'0C8AZTODP4HT'),(843498099048450,'0C8AZYTQ5W5C'),(843498099048459,'0C8AZTODP4HT'),(843509306228737,'0C8AZTODP4HT'),(843509757116417,'0C8AZTODP4H0'),(843509757116417,'0C8AZTODP4HT'),(843509757116417,'0C8AZTODP4HZ'),(843509757116417,'0C8AZYTQ5W5C'),(843511877861378,'0C8AZTODP4HT'),(843512635457539,'0C8AZTODP4HT'),(843512635457539,'0C8AZYTQ5W5C'),(843512635457561,'0C8AZTODP4HT'),(862433826570240,'0C8HPGLXHMET'),(862524186558475,'0C8HPGF4GBUP'),(862524186558475,'0C8HPGLXHMET'),(863884655198262,'0C8AZTODP4HT'),(881933420068882,'0C8OMAII48XE'),(881933438418954,'0C8OMAII48XE'),(881933451001878,'0C8OMAII48XE');
/*!40000 ALTER TABLE `resources_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `system_create` bit(1) NOT NULL,
  `tenant_id` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_113dky2ymmucbqsenqnaf6oxo` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (861812327186435,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:04','NOT_HTTP',0,NULL,'0P8HE307W6IO',NULL,'0P8HE307W6IO','0Z8HHJ488SEC',_binary '','0P8HE307W6IO','PROJECT'),(861812327186436,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:05','NOT_HTTP',0,NULL,'CLIENT_ROOT',NULL,'0P8HE307W6IO','0Z8HHJ489SA0',_binary '',NULL,'CLIENT_ROOT'),(861812327186437,'2022-02-02 09:36:04','NOT_HTTP','2023-04-20 16:06:28','0U8AZTODP4H0',11,NULL,'PROJECT_SUPER_ADMIN','0Z8HHJ488SEC','0P8HE307W6IO','0Z8HHJ489SEC',_binary '','0P8HE307W6IO','USER'),(861812327186439,'2022-02-02 09:36:04','NOT_HTTP','2023-02-17 11:04:32','0U8AZTODP4H0',15,NULL,'PROJECT_USER','0Z8HHJ488SEC','0P8HE307W6IO','0Z8HHJ489SEE',_binary '','0P8HE307W6IO','USER'),(861812327186440,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:05','NOT_HTTP',0,NULL,'0C8AZTODP4HT','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SE0',_binary '',NULL,'CLIENT'),(861812327186441,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:05','NOT_HTTP',0,NULL,'0C8AZTODP4H0','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SE1',_binary '',NULL,'CLIENT'),(861812327186443,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:05','NOT_HTTP',0,NULL,'0C8AZTODP4H8','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SE9',_binary '',NULL,'CLIENT'),(861812327186444,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:05','NOT_HTTP',0,NULL,'0C8AZTODP4HZ','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEA',_binary '',NULL,'CLIENT'),(861812327186445,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:05','NOT_HTTP',0,NULL,'0C8AZYTQ5W5C','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEB',_binary '',NULL,'CLIENT'),(861812327186446,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:05','NOT_HTTP',0,NULL,'0C8AZZ16LZB4','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEH',_binary '',NULL,'CLIENT'),(861812327186447,'2022-02-02 09:36:04','NOT_HTTP','2022-04-09 20:18:18','0U8AZTODP4H0',1,NULL,'0C8B00098WLD','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SED',_binary '',NULL,'CLIENT'),(861812327186448,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:05','NOT_HTTP',0,NULL,'0C8B00CSATJ6','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEF',_binary '',NULL,'CLIENT'),(861812327186449,'2022-02-02 09:36:04','NOT_HTTP','2022-02-02 09:36:05','NOT_HTTP',0,NULL,'0C8AZTODP4I0','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEG',_binary '',NULL,'CLIENT'),(862172156002538,'2022-02-10 08:14:43','0U8AZTODP4H0','2023-04-20 16:11:18','0U8AZTODP4H0',6,NULL,'PROJECT_ADMIN','0Z8HHJ488SEC','0P8HE307W6IO','0Z8HM4F4QV41',_binary '','0P8HE307W6IO','USER'),(862433016545286,'2022-02-16 02:27:15','NOT_HTTP','2022-02-16 02:27:15','NOT_HTTP',0,NULL,'0P8HPG99R56P',NULL,'0P8HE307W6IO','0Z8HPG9AOUTG',_binary '','0P8HPG99R56P','PROJECT'),(862433016545288,'2022-02-16 02:27:15','NOT_HTTP','2023-04-20 16:11:32','0U8AZTODP4H0',6,NULL,'PROJECT_ADMIN','0Z8HPG9AOUTG','0P8HE307W6IO','0Z8HPG9AOUTJ',_binary '','0P8HPG99R56P','USER'),(862433016545290,'2022-02-16 02:27:15','NOT_HTTP','2022-04-09 20:14:41','0U8HPG93IED3',6,NULL,'PROJECT_USER','0Z8HPG9AOUTH','0P8HPG99R56P','0Z8HPG9AOUTL',_binary '',NULL,'USER'),(862433016545292,'2022-02-16 02:27:15','NOT_HTTP','2022-02-16 02:27:16','NOT_HTTP',1,NULL,'CLIENT_ROOT',NULL,'0P8HPG99R56P','0Z8HPG9AOUTN',_binary '',NULL,'CLIENT_ROOT'),(862433016545293,'2022-02-16 02:27:15','NOT_HTTP','2022-02-16 02:27:16','NOT_HTTP',1,NULL,'0P8HPG99R56P',NULL,'0P8HPG99R56P','0Z8HPG9AOUTH',_binary '',NULL,'PROJECT'),(862433369915464,'2022-02-16 02:38:29','NOT_HTTP','2022-02-16 10:08:06','0U8HPG93IED3',2,NULL,'0C8HPGF4GBUP','0Z8HPG9AOUTN','0P8HPG99R56P','0Z8HPGF4RKEA',_binary '',NULL,'CLIENT'),(862433781481554,'2022-02-16 02:51:34','NOT_HTTP','2022-02-16 10:08:03','0U8HPG93IED3',2,NULL,'0C8HPGLXHMET','0Z8HPG9AOUTN','0P8HPG99R56P','0Z8HPGLXHMF5',_binary '',NULL,'CLIENT'),(862433827094538,'2022-02-16 02:53:01','NOT_HTTP','2022-02-16 10:08:00','0U8HPG93IED3',3,NULL,'0C8HPGMON9J5','0Z8HPG9AOUTN','0P8HPG99R56P','0Z8HPGMOYI2P',_binary '',NULL,'CLIENT'),(862486461939723,'2022-02-17 06:46:15','0U8HPG93IED3','2022-04-09 20:14:52','0U8HPG93IED3',4,NULL,'商城管理员','0Z8HPG9AOUTH','0P8HPG99R56P','0Z8HQ4T6P535',_binary '\0',NULL,'USER'),(862524187082847,'2022-02-18 02:45:30','NOT_HTTP','2022-02-18 02:45:30','NOT_HTTP',1,NULL,'0C8HQM52YN7K','0Z8HPG9AOUTN','0P8HPG99R56P','0Z8HQM52YN7W',_binary '',NULL,'CLIENT'),(863884656246846,'2022-03-19 22:33:39','NOT_HTTP','2022-03-19 22:33:39','NOT_HTTP',1,NULL,'0C8I7Z4Q8N09','0Z8HHJ489SA0','0P8HE307W6IO','0Z8I7Z4Q8N4B',_binary '',NULL,'CLIENT'),(863884656771095,'2022-03-19 22:33:39','NOT_HTTP','2022-03-19 22:33:39','NOT_HTTP',1,NULL,'0C8I7Z4QJVNC','0Z8HHJ489SA0','0P8HE307W6IO','0Z8I7Z4QV41J',_binary '',NULL,'CLIENT'),(881933150060553,'2023-04-22 08:59:51','NOT_HTTP','2023-04-22 08:59:51','NOT_HTTP',0,NULL,'0P8OMAHSU0W4',NULL,'0P8HE307W6IO','0Z8OMAHUPG5J',_binary '','0P8OMAHSU0W4','PROJECT'),(881933151109427,'2023-04-22 08:59:51','NOT_HTTP','2023-04-22 08:59:51','NOT_HTTP',0,NULL,'PROJECT_ADMIN','0Z8OMAHUPG5J','0P8HE307W6IO','0Z8OMAHUPG5M',_binary '','0P8OMAHSU0W4','USER'),(881933151109429,'2023-04-22 08:59:51','NOT_HTTP','2023-04-22 09:21:06','0U8OMAGVFMS3',1,NULL,'PROJECT_USER','0Z8OMAHUPG5K','0P8OMAHSU0W4','0Z8OMAHVBXH0',_binary '',NULL,'USER'),(881933151109431,'2023-04-22 08:59:51','NOT_HTTP','2023-04-22 08:59:51','NOT_HTTP',0,NULL,'CLIENT_ROOT',NULL,'0P8OMAHSU0W4','0Z8OMAHVBXH2',_binary '',NULL,'CLIENT_ROOT'),(881933151109432,'2023-04-22 08:59:51','NOT_HTTP','2023-04-22 08:59:51','NOT_HTTP',0,NULL,'0P8OMAHSU0W4',NULL,'0P8OMAHSU0W4','0Z8OMAHUPG5K',_binary '',NULL,'PROJECT'),(881933190430770,'2023-04-22 09:01:05','NOT_HTTP','2023-04-22 09:01:05','NOT_HTTP',0,NULL,'0C8OMAII48XE','0Z8HHJ489SA0','0P8OMAHSU0W4','0Z8OMAII49B6',_binary '',NULL,'CLIENT'),(881933421117440,'2023-04-22 09:08:25','NOT_HTTP','2023-04-22 09:08:25','NOT_HTTP',0,NULL,'0C8OMAMBGNWG','0Z8HHJ489SA0','0P8OMAHSU0W4','0Z8OMAMBGNWX',_binary '',NULL,'CLIENT'),(881933439468989,'2023-04-22 09:09:01','NOT_HTTP','2023-04-22 09:09:01','NOT_HTTP',0,NULL,'0C8OMAMM2QDC','0Z8HHJ489SA0','0P8OMAHSU0W4','0Z8OMAMMDYX5',_binary '',NULL,'CLIENT'),(881933452574720,'2023-04-22 09:09:25','NOT_HTTP','2023-04-22 09:09:25','NOT_HTTP',0,NULL,'0C8OMAMTVNY8','0Z8HHJ489SA0','0P8OMAHSU0W4','0Z8OMAMTVNYT',_binary '',NULL,'CLIENT');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_api_permission_map`
--

DROP TABLE IF EXISTS `role_api_permission_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_api_permission_map` (
  `id` bigint NOT NULL,
  `permission` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`permission`),
  CONSTRAINT `custom_constaint_7` FOREIGN KEY (`id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_api_permission_map`
--

LOCK TABLES `role_api_permission_map` WRITE;
/*!40000 ALTER TABLE `role_api_permission_map` DISABLE KEYS */;
INSERT INTO `role_api_permission_map` VALUES (861812327186437,'0Y8HHJ47NB00'),(861812327186437,'0Y8HHJ47NBDB'),(861812327186437,'0Y8HHJ47NBDC'),(861812327186437,'0Y8HHJ47NBDK'),(861812327186437,'0Y8HHJ47NBDT'),(861812327186437,'0Y8HHJ47NBDU'),(861812327186437,'0Y8HHJ47NBDY'),(861812327186437,'0Y8HHJ47NBDZ'),(861812327186437,'0Y8HHJ47NBE6'),(861812327186437,'0Y8HHJ47NBE7'),(861812327186437,'0Y8HHJ47NBEA'),(861812327186437,'0Y8HHJ47NBEB'),(861812327186437,'0Y8HHJ47NBES'),(861812327186437,'0Y8HHJ47NBEU'),(861812327186437,'0Y8HOT1AO8P8'),(861812327186437,'0Y8HOT1UYO0D'),(861812327186437,'0Y8HOT24N0UK'),(861812327186437,'0Y8HOT28P3WT'),(861812327186437,'0Y8HOT6J7KST'),(861812327186437,'0Y8HOT6OTUKS'),(861812327186437,'0Y8HP28G7MYV'),(861812327186437,'0Y8HVWH0K64P'),(861812327186437,'0Y8I2AQK7X8R'),(861812327186437,'0Y8IKOBERLMT'),(861812327186437,'0Y8IZU2J4F0P'),(861812327186437,'0Y8J09OCERRD'),(861812327186437,'0Y8J93UKAWPJ'),(861812327186437,'0Y8J9ZGQNTP3'),(861812327186437,'0Y8N43JAY7GA'),(861812327186437,'0Y8NU05MSQOM'),(861812327186437,'0Y8OYYUBPMEE'),(861812327186439,'0Y8HHJ47NBD5'),(861812327186439,'0Y8HHJ47NBDD'),(861812327186439,'0Y8HHJ47NBDE'),(861812327186439,'0Y8HHJ47NBE7'),(861812327186439,'0Y8HHJ47NBET'),(861812327186439,'0Y8HHJ47NBEZ'),(861812327186439,'0Y8HLUWOH92P'),(861812327186439,'0Y8I1GL5LA8B'),(861812327186439,'0Y8I1GL5LA8C'),(861812327186439,'0Y8I5RRK09HV'),(861812327186439,'0Y8ISBO52IRF'),(861812327186439,'0Y8ISSFAD4XQ'),(861812327186439,'0Y8ISSQV2Y3B'),(861812327186439,'0Y8IV8SAZUO9'),(861812327186439,'0Y8IZ4MZRY10'),(861812327186439,'0Y8J93IHSL5M'),(861812327186439,'0Y8NIJPIFF2U'),(861812327186439,'0Y8NIJPRHB1D'),(861812327186439,'0Y8NLCCBLMHG'),(861812327186439,'0Y8NLCGNZIMI'),(861812327186447,'0Y8HHJ47NBD9'),(861812327186447,'0Y8HHJ47NBDG'),(861812327186447,'0Y8HHJ47NBDH'),(861812327186447,'0Y8HHJ47NBDI'),(862172156002538,'0Y8HHJ47NBD4'),(862172156002538,'0Y8HHJ47NBD6'),(862172156002538,'0Y8HHJ47NBD7'),(862172156002538,'0Y8HHJ47NBD8'),(862172156002538,'0Y8HHJ47NBDL'),(862172156002538,'0Y8HHJ47NBDM'),(862172156002538,'0Y8HHJ47NBDN'),(862172156002538,'0Y8HHJ47NBDO'),(862172156002538,'0Y8HHJ47NBDP'),(862172156002538,'0Y8HHJ47NBDQ'),(862172156002538,'0Y8HHJ47NBDS'),(862172156002538,'0Y8HHJ47NBDV'),(862172156002538,'0Y8HHJ47NBDW'),(862172156002538,'0Y8HHJ47NBEV'),(862172156002538,'0Y8HHJ47NBEW'),(862172156002538,'0Y8HHJ47NBEX'),(862172156002538,'0Y8HHJ47NBEY'),(862172156002538,'0Y8HK4ZLA03Q'),(862172156002538,'0Y8HKACDVMDL'),(862172156002538,'0Y8HKE24FWUI'),(862172156002538,'0Y8HKE2QAIVF'),(862172156002538,'0Y8HKEMUH34B'),(862172156002538,'0Y8HKEMWNQX7'),(862172156002538,'0Y8HLUWG1UJ8'),(862172156002538,'0Y8HLUWKQEJ1'),(862172156002538,'0Y8HLUWMX2BX'),(862172156002538,'0Y8HLUWOH91P'),(862172156002538,'0Y8HSHJC34BW'),(862172156002538,'0Y8OYY45NEVK'),(862433016545288,'0Y8HHJ47NBD4'),(862433016545288,'0Y8HHJ47NBD6'),(862433016545288,'0Y8HHJ47NBD7'),(862433016545288,'0Y8HHJ47NBD8'),(862433016545288,'0Y8HHJ47NBDL'),(862433016545288,'0Y8HHJ47NBDM'),(862433016545288,'0Y8HHJ47NBDN'),(862433016545288,'0Y8HHJ47NBDO'),(862433016545288,'0Y8HHJ47NBDP'),(862433016545288,'0Y8HHJ47NBDQ'),(862433016545288,'0Y8HHJ47NBDS'),(862433016545288,'0Y8HHJ47NBDV'),(862433016545288,'0Y8HHJ47NBDW'),(862433016545288,'0Y8HHJ47NBEV'),(862433016545288,'0Y8HHJ47NBEW'),(862433016545288,'0Y8HHJ47NBEX'),(862433016545288,'0Y8HHJ47NBEY'),(862433016545288,'0Y8HK4ZLA03Q'),(862433016545288,'0Y8HKACDVMDL'),(862433016545288,'0Y8HKE24FWUI'),(862433016545288,'0Y8HKE2QAIVF'),(862433016545288,'0Y8HKEMUH34B'),(862433016545288,'0Y8HKEMWNQX7'),(862433016545288,'0Y8HLUWG1UJ8'),(862433016545288,'0Y8HLUWKQEJ1'),(862433016545288,'0Y8HLUWMX2BX'),(862433016545288,'0Y8HLUWOH91P'),(862433016545288,'0Y8HSHJC34BW'),(862433016545288,'0Y8OYY45NEVK'),(862433016545290,'0Y8HPMBZOBO0'),(862433016545290,'0Y8HPX4CRI11'),(862433016545290,'0Y8HPX4QHY3K'),(862433016545290,'0Y8HPX4XZMYT'),(862433016545290,'0Y8HPX5EJ7RJ'),(862433016545290,'0Y8HPX5G3EHC'),(862433016545290,'0Y8HPXNIAQ5C'),(862433016545290,'0Y8HPXNQ3NK5'),(862433016545290,'0Y8HPYTO9RLX'),(862433016545290,'0Y8HPYTRPDLC'),(862433016545290,'0Y8HPYU395OW'),(862433016545290,'0Y8HPYU8K6XC'),(862433016545290,'0Y8HPYUE6GPC'),(862433016545290,'0Y8HPYULZEA8'),(862433016545290,'0Y8HPYURLO28'),(862486461939723,'0Y8HHJ47NBE8'),(862486461939723,'0Y8HHJ47NBE9'),(862486461939723,'0Y8HPX3VWOP1'),(862486461939723,'0Y8HPX41U70V'),(862486461939723,'0Y8HPX75L5I5'),(862486461939723,'0Y8HPX806EQ8'),(862486461939723,'0Y8HPX88AKUN'),(862486461939723,'0Y8HPX97VMSD'),(862486461939723,'0Y8HPX9JFEVJ'),(862486461939723,'0Y8HPX9QX3WV'),(862486461939723,'0Y8HPX9UZ6Z4'),(862486461939723,'0Y8HPXOB0JYN'),(862486461939723,'0Y8HPXOOQZV3'),(862486461939723,'0Y8HPXOST2XC'),(862486461939723,'0Y8HPXP0ARYN'),(862486461939723,'0Y8HPXP7601W'),(862486461939723,'0Y8HPXPG7VNZ'),(862486461939723,'0Y8HPXPK9YQ7'),(862486461939723,'0Y8HPXURKCSK'),(862486461939723,'0Y8HPXVFADXR'),(862486461939723,'0Y8HPXVJ18G5'),(862486461939723,'0Y8HPXVO115B'),(862486461939723,'0Y8HPXVUW93J'),(862486461939723,'0Y8HPXW1G8I7'),(862486461939723,'0Y8HPXW6R9QM'),(862486461939723,'0Y8HPXWGQV45'),(862486461939723,'0Y8HPY2HDK6S'),(862486461939723,'0Y8HPY2IXQTS'),(862486461939723,'0Y8HPY37WQA8'),(862486461939723,'0Y8HPY39GWZP'),(862486461939723,'0Y8HPYPW6AS0'),(862486461939723,'0Y8HPYQ0UUXC'),(862486461939723,'0Y8HPYQXBJF4'),(862486461939723,'0Y8HPYVKMQPX'),(862486461939723,'0Y8HPYVO2CK0'),(862486461939723,'0Y8HPYVSFO5S'),(862486461939723,'0Y8HPYVWSZR9'),(862486461939723,'0Y8HPYW16BDC'),(862486461939723,'0Y8HPYW66428'),(862486461939723,'0Y8HPYW9WYKW'),(862486461939723,'0Y8HPYWEAA6O'),(862486461939723,'0Y8HPZ1L9FK6'),(862486461939723,'0Y8HPZ1Q989C'),(862486461939723,'0Y8HPZ1YZVGL'),(862486461939723,'0Y8HPZ231YJ4'),(862486461939723,'0Y8HPZ29APE8'),(862486461939723,'0Y8HPZ2ELQMO'),(862486461939723,'0Y8HPZ2LS745'),(862486461939723,'0Y8HPZ2TL4PC'),(862486461939723,'0Y8HWXNKYL8W'),(862486461939723,'0Y8HWXNPN5DX'),(862486461939723,'0Y8HWXNTP8GG'),(862486461939723,'0Y8HWXNYP15C'),(862486461939723,'0Y8HYA2GSU80'),(881933151109427,'0Y8HHJ47NBD4'),(881933151109427,'0Y8HHJ47NBD6'),(881933151109427,'0Y8HHJ47NBD7'),(881933151109427,'0Y8HHJ47NBD8'),(881933151109427,'0Y8HHJ47NBDL'),(881933151109427,'0Y8HHJ47NBDM'),(881933151109427,'0Y8HHJ47NBDN'),(881933151109427,'0Y8HHJ47NBDO'),(881933151109427,'0Y8HHJ47NBDP'),(881933151109427,'0Y8HHJ47NBDQ'),(881933151109427,'0Y8HHJ47NBDS'),(881933151109427,'0Y8HHJ47NBDV'),(881933151109427,'0Y8HHJ47NBDW'),(881933151109427,'0Y8HHJ47NBEV'),(881933151109427,'0Y8HHJ47NBEW'),(881933151109427,'0Y8HHJ47NBEX'),(881933151109427,'0Y8HHJ47NBEY'),(881933151109427,'0Y8HK4ZLA03Q'),(881933151109427,'0Y8HKACDVMDL'),(881933151109427,'0Y8HKE24FWUI'),(881933151109427,'0Y8HKE2QAIVF'),(881933151109427,'0Y8HKEMUH34B'),(881933151109427,'0Y8HKEMWNQX7'),(881933151109427,'0Y8HLUWG1UJ8'),(881933151109427,'0Y8HLUWKQEJ1'),(881933151109427,'0Y8HLUWMX2BX'),(881933151109427,'0Y8HLUWOH91P'),(881933151109427,'0Y8HSHJC34BW'),(881933151109427,'0Y8M0IG8RITC'),(881933151109427,'0Y8M0IQAUSZ8'),(881933151109427,'0Y8M0IQQ5FK0'),(881933151109427,'0Y8M0IR20GBI'),(881933151109427,'0Y8M0IRD8ZSN'),(881933151109427,'0Y8M0IRN8L4W'),(881933151109427,'0Y8M4M3J9HJ4'),(881933151109427,'0Y8M4UTZLTLI'),(881933151109427,'0Y8MLZDBR4T3'),(881933151109427,'0Y8NY6E4KKD3'),(881933151109427,'0Y8NY6EHDATT'),(881933151109427,'0Y8NY6ESALN0'),(881933151109427,'0Y8OK4YFSUFS'),(881933151109427,'0Y8OK4YM1LAW'),(881933151109427,'0Y8OK4YRZ3MC'),(881933151109427,'0Y8OK4YXLDEC'),(881933151109427,'0Y8OK4Z1C7X0'),(881933151109427,'0Y8OKQG3SFF7'),(881933151109427,'0Y8OKQG9PXQM'),(881933151109427,'0Y8OKQGD5JPW'),(881933151109427,'0Y8OKQGFNG2C'),(881933151109427,'0Y8OKQGIRTHW'),(881933151109427,'0Y8OYY45NEVK'),(881933151109429,'0Y8OMAHTGI14'),(881933151109429,'0Y8OMALNQMMI');
/*!40000 ALTER TABLE `role_api_permission_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_common_permission_map`
--

DROP TABLE IF EXISTS `role_common_permission_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_common_permission_map` (
  `id` bigint NOT NULL,
  `permission` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`permission`),
  CONSTRAINT `custom_constaint_8` FOREIGN KEY (`id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_common_permission_map`
--

LOCK TABLES `role_common_permission_map` WRITE;
/*!40000 ALTER TABLE `role_common_permission_map` DISABLE KEYS */;
INSERT INTO `role_common_permission_map` VALUES (862172156002538,'0Y8HM3UAYUBK'),(862172156002538,'0Y8HM3UU0BM0'),(862172156002538,'0Y8HM3VGHEKG'),(862172156002538,'0Y8HM3VVGSN4'),(862172156002538,'0Y8HM3VXC7WG'),(862172156002538,'0Y8HM3VXC7WH'),(862172156002538,'0Y8HM3VXC7WI'),(862172156002538,'0Y8HM3VXC7WJ'),(862172156002538,'0Y8HM3VXC7WK'),(862172156002538,'0Y8HM3VYWEM9'),(862172156002538,'0Y8HM3W1EAYP'),(862172156002538,'0Y8HM3WS8POG'),(862172156002538,'0Y8HM3X0O4CG'),(862172156002538,'0Y8HM3X3SHS1'),(862172156002538,'0Y8HM3XKYJNK'),(862172156002538,'0Y8HM3Z2YLMO'),(862172156002538,'0Y8HM3Z97CHT'),(862172156002538,'0Y8HM3ZH0A2O'),(862172156002538,'0Y8HM40ZBKLC'),(862172156002538,'0Y8HM4132F41'),(862172156002538,'0Y8HM419B5Z5'),(862172156002538,'0Y8HM42GDWXS'),(862172156002538,'0Y8HM42QOQV4'),(862172156002538,'0Y8HM42ZFE88'),(862172156002538,'0Y8HM4474M4G'),(862172156002538,'0Y8HM44BT69S'),(862172156002538,'0Y8M0IOE6LFK'),(862172156002538,'0Y8NY6HSX6O0'),(862172156002538,'0Y8OKQKKJN5W'),(862172156002538,'0Y8OKQKW3F9C'),(862172156002538,'0Y8OKQKZU9S0'),(862172156002538,'0Y8OKQL47LF3'),(862433016545288,'0Y8HM3VXC7WL'),(862433016545288,'0Y8HM3VXC7WM'),(862433016545288,'0Y8HM3VXC7WN'),(862433016545288,'0Y8HM3VXC7WO'),(862433016545288,'0Y8HPG9A2DQD'),(862433016545288,'0Y8HPG9A2DQF'),(862433016545288,'0Y8HPG9A2DQH'),(862433016545288,'0Y8HPG9A2DQJ'),(862433016545288,'0Y8HPG9A2DQL'),(862433016545288,'0Y8HPG9A2DQN'),(862433016545288,'0Y8HPG9A2DQP'),(862433016545288,'0Y8HPG9A2DQX'),(862433016545288,'0Y8HPG9A2DR1'),(862433016545288,'0Y8HPG9A2DR3'),(862433016545288,'0Y8HPG9A2DR7'),(862433016545288,'0Y8HPG9A2DRH'),(862433016545288,'0Y8HPG9A2DRL'),(862433016545288,'0Y8HPG9A2DRN'),(862433016545288,'0Y8HPG9A2DRP'),(862433016545288,'0Y8HPG9A2DRT'),(862433016545288,'0Y8HPG9A2DRV'),(862433016545288,'0Y8HPG9A2DRZ'),(862433016545288,'0Y8HPG9A2DS1'),(862433016545288,'0Y8HPG9A2DS7'),(862433016545288,'0Y8HPG9A2DSB'),(862433016545288,'0Y8HPG9A2DSD'),(862433016545288,'0Y8M0QQFTWD1'),(862433016545288,'0Y8NY6HXWZCX'),(862433016545288,'0Y8OKQLDKPOG'),(862433016545288,'0Y8OKQLZ435W'),(862433016545288,'0Y8OKQM1X81S'),(862433016545288,'0Y8OKQM5ZB4A'),(881933151109427,'0Y8OMAHTGHZA'),(881933151109427,'0Y8OMAHTGHZC'),(881933151109427,'0Y8OMAHTGHZE'),(881933151109427,'0Y8OMAHTGHZG'),(881933151109427,'0Y8OMAHTGHZI'),(881933151109427,'0Y8OMAHTGHZK'),(881933151109427,'0Y8OMAHTGHZM'),(881933151109427,'0Y8OMAHTGHZO'),(881933151109427,'0Y8OMAHTGHZQ'),(881933151109427,'0Y8OMAHTGHZS'),(881933151109427,'0Y8OMAHTGHZU'),(881933151109427,'0Y8OMAHTGHZW'),(881933151109427,'0Y8OMAHTGHZY'),(881933151109427,'0Y8OMAHTGI00'),(881933151109427,'0Y8OMAHTGI02'),(881933151109427,'0Y8OMAHTGI04'),(881933151109427,'0Y8OMAHTGI06'),(881933151109427,'0Y8OMAHTGI08'),(881933151109427,'0Y8OMAHTGI0A'),(881933151109427,'0Y8OMAHTGI0C'),(881933151109427,'0Y8OMAHTGI0E'),(881933151109427,'0Y8OMAHTGI0G'),(881933151109427,'0Y8OMAHTGI0I'),(881933151109427,'0Y8OMAHTGI0K'),(881933151109427,'0Y8OMAHTGI0M'),(881933151109427,'0Y8OMAHTGI0O'),(881933151109427,'0Y8OMAHTGI0Q'),(881933151109427,'0Y8OMAHTGI0S'),(881933151109427,'0Y8OMAHTGI0U'),(881933151109427,'0Y8OMAHTGI0W'),(881933151109427,'0Y8OMAHTGI0Y'),(881933151109427,'0Y8OMAHTGI10'),(881933151109427,'0Y8OMAHTGI12');
/*!40000 ALTER TABLE `role_common_permission_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_external_permission_map`
--

DROP TABLE IF EXISTS `role_external_permission_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_external_permission_map` (
  `id` bigint NOT NULL,
  `permission` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`permission`),
  CONSTRAINT `custom_constaint_6` FOREIGN KEY (`id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_external_permission_map`
--

LOCK TABLES `role_external_permission_map` WRITE;
/*!40000 ALTER TABLE `role_external_permission_map` DISABLE KEYS */;
INSERT INTO `role_external_permission_map` VALUES (862486461939723,'0Y8HHJ47NBE7');
/*!40000 ALTER TABLE `role_external_permission_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stored_event`
--

DROP TABLE IF EXISTS `stored_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stored_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `domain_id` varchar(255) DEFAULT NULL,
  `event_body` longtext,
  `internal` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `timestamp` bigint DEFAULT NULL,
  `topic` varchar(255) DEFAULT NULL,
  `send` bit(1) NOT NULL,
  `routable` bit(1) DEFAULT b'1',
  `rejected` bit(1) NOT NULL,
  `application_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stored_event`
--

LOCK TABLES `stored_event` WRITE;
/*!40000 ALTER TABLE `stored_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `stored_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sub_request`
--

DROP TABLE IF EXISTS `sub_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sub_request` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `project_id` varchar(255) NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `endpoint_id` varchar(255) NOT NULL,
  `endpoint_project_id` varchar(255) NOT NULL,
  `replenish_rate` int DEFAULT NULL,
  `burst_capacity` int DEFAULT NULL,
  `rejected_by` varchar(255) DEFAULT NULL,
  `approved_by` varchar(255) DEFAULT NULL,
  `rejection_reason` varchar(255) DEFAULT NULL,
  `sub_request_status` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_sub_req_domain` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_request`
--

LOCK TABLES `sub_request` WRITE;
/*!40000 ALTER TABLE `sub_request` DISABLE KEYS */;
/*!40000 ALTER TABLE `sub_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_`
--

DROP TABLE IF EXISTS `user_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `locked` bit(1) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `country_code` varchar(255) NOT NULL,
  `mobile_number` varchar(255) NOT NULL,
  `avatar_link` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `mfa_id` varchar(255) DEFAULT NULL,
  `mfa_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_r112j0oma4shssn2avwqqa7tv` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_`
--

LOCK TABLES `user_` WRITE;
/*!40000 ALTER TABLE `user_` DISABLE KEYS */;
INSERT INTO `user_` VALUES (838330249904129,'2021-12-10 02:10:06','script','2023-04-22 09:16:22','ONBOARD_TENANT_USER',32,'superadmin@sample.com',_binary '\0','$2a$12$FJQlhRmF.ZXF5iv8fEPKD.wCwXJPEEhVLB3EAgX35KdMJc3UrkDh6',NULL,'0U8AZTODP4H0','超级管理员','86','12312312343',NULL,'ENGLISH','ca4f6ed2-8daa-4c31-a0b2-77d23e317f94','654321'),(862433005010948,'2022-02-15 21:26:53','0C8B00098WLD','2022-11-18 05:52:08','ONBOARD_TENANT_USER',2,'mall@sample.com',_binary '\0','$2a$12$FJQlhRmF.ZXF5iv8fEPKD.wCwXJPEEhVLB3EAgX35KdMJc3UrkDh6',NULL,'0U8HPG93IED3',NULL,'86','12312312345',NULL,NULL,'848aefbc-b117-428f-967b-249c73f04f2a','654321'),(862531308486708,'2022-02-18 01:31:53','0C8B00098WLD','2022-12-25 12:23:23','ONBOARD_TENANT_USER',1,'testuser1@sample.com',_binary '\0','$2a$12$FJQlhRmF.ZXF5iv8fEPKD.wCwXJPEEhVLB3EAgX35KdMJc3UrkDh6',NULL,'0U8HQPEV6A7K',NULL,'86','12312312345',NULL,NULL,'085e0f26-e376-44cd-91fa-8a64227308e4','654321'),(862531434315781,'2022-02-18 01:35:53','0C8B00098WLD','2022-11-11 13:28:36','ONBOARD_TENANT_USER',1,'mallUser@sample.com',_binary '\0','$2a$12$FJQlhRmF.ZXF5iv8fEPKD.wCwXJPEEhVLB3EAgX35KdMJc3UrkDh6',NULL,'0U8HQPGY38JL',NULL,'86','12312312345',NULL,NULL,'d05f0aa0-c19a-4047-b1d5-7ff750920feb','654321'),(881933091340295,'2023-04-22 08:57:56','0C8B00098WLD','2023-04-22 09:21:54','ONBOARD_TENANT_USER',2,'demo@sample.com',_binary '\0','$2a$12$f1bMQikWEeoe2neA/xt7QOTmvCTUXfeBiOIu94byKaRsLIJQddSzK',NULL,'0U8OMAGVFMS3',NULL,'86','1231231234',NULL,NULL,'0672d1fc-8abb-4a0e-bf9a-fd9d246ec849','654321');
/*!40000 ALTER TABLE `user_` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_relation`
--

DROP TABLE IF EXISTS `user_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_relation` (
  `id` bigint NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` datetime DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `organization_id` varchar(255) DEFAULT NULL,
  `position_id` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) DEFAULT NULL,
  `standalone_roles` longtext,
  `tenant_ids` longtext,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKaj74q84w36lmjs6ym17hjgdrm` (`user_id`,`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_relation`
--

LOCK TABLES `user_relation` WRITE;
/*!40000 ALTER TABLE `user_relation` DISABLE KEYS */;
INSERT INTO `user_relation` VALUES (861812327710759,'2022-02-02 04:36:05','NOT_HTTP','2022-02-02 04:36:05','NOT_HTTP',0,NULL,NULL,'0P8HE307W6IO','0Z8HHJ489SEE,0Z8HHJ489SEC,0Z8HM4F4QV41','0P8HE307W6IO','0U8AZTODP4H0'),(862433005010945,'2022-02-15 21:26:53','0C8B00098WLD','2022-02-15 21:27:16','NOT_HTTP',1,NULL,NULL,'0P8HE307W6IO','0Z8HHJ489SEE,0Z8HPG9AOUTJ','0P8HPG99R56P','0U8HPG93IED3'),(862433017069645,'2022-02-15 21:27:16','NOT_HTTP','2022-02-18 01:38:08','0U8HPG93IED3',1,NULL,NULL,'0P8HPG99R56P','0Z8HQ4T6P535,0Z8HPG9AOUTL',NULL,'0U8HPG93IED3'),(862525411819525,'2022-02-17 22:24:25','ONBOARD_TENANT_USER','2022-02-17 22:24:25','ONBOARD_TENANT_USER',0,NULL,NULL,'0P8HPG99R56P','0Z8HPG9AOUTL',NULL,'0U8AZTODP4H0'),(862531308486705,'2022-02-18 01:31:53','0C8B00098WLD','2022-02-18 01:31:53','0C8B00098WLD',0,NULL,NULL,'0P8HE307W6IO','0Z8HHJ489SEE',NULL,'0U8HQPEV6A7K'),(862531311632420,'2022-02-18 01:31:59','ONBOARD_TENANT_USER','2022-02-18 01:31:59','ONBOARD_TENANT_USER',0,NULL,NULL,'0P8HPG99R56P','0Z8HPG9AOUTL',NULL,'0U8HQPEV6A7K'),(862531434315778,'2022-02-18 01:35:53','0C8B00098WLD','2022-02-18 01:35:53','0C8B00098WLD',0,NULL,NULL,'0P8HE307W6IO','0Z8HHJ489SEE',NULL,'0U8HQPGY38JL'),(862531463151652,'2022-02-18 01:36:48','ONBOARD_TENANT_USER','2022-02-18 01:38:14','0U8HPG93IED3',1,NULL,NULL,'0P8HPG99R56P','0Z8HQ4T6P535,0Z8HPG9AOUTL',NULL,'0U8HQPGY38JL'),(881933091340292,'2023-04-22 08:57:56','0C8B00098WLD','2023-04-22 08:59:55','NOT_HTTP',1,NULL,NULL,'0P8HE307W6IO',NULL,NULL,'0U8OMAGVFMS3'),(881933153206367,'2023-04-22 08:59:55','NOT_HTTP','2023-04-22 08:59:55','NOT_HTTP',0,NULL,NULL,'0P8OMAHSU0W4',NULL,NULL,'0U8OMAGVFMS3'),(881933801750540,'2023-04-22 09:20:32','ONBOARD_TENANT_USER','2023-04-22 09:20:32','ONBOARD_TENANT_USER',0,NULL,NULL,'0P8OMAHSU0W4',NULL,NULL,'0U8AZTODP4H0');
/*!40000 ALTER TABLE `user_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_relation_role_map`
--

DROP TABLE IF EXISTS `user_relation_role_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_relation_role_map` (
  `id` bigint NOT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`role`),
  CONSTRAINT `custom_constaint_9` FOREIGN KEY (`id`) REFERENCES `user_relation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_relation_role_map`
--

LOCK TABLES `user_relation_role_map` WRITE;
/*!40000 ALTER TABLE `user_relation_role_map` DISABLE KEYS */;
INSERT INTO `user_relation_role_map` VALUES (861812327710759,'0Z8HHJ489SEC'),(861812327710759,'0Z8HHJ489SEE'),(861812327710759,'0Z8HM4F4QV41'),(862433005010945,'0Z8HHJ489SEE'),(862433005010945,'0Z8HPG9AOUTJ'),(862433017069645,'0Z8HPG9AOUTL'),(862433017069645,'0Z8HQ4T6P535'),(862525411819525,'0Z8HPG9AOUTL'),(862531308486705,'0Z8HHJ489SEE'),(862531311632420,'0Z8HPG9AOUTL'),(862531434315778,'0Z8HHJ489SEE'),(862531463151652,'0Z8HPG9AOUTL'),(862531463151652,'0Z8HQ4T6P535'),(881933091340292,'0Z8HHJ489SEE'),(881933091340292,'0Z8OMAHUPG5M'),(881933153206367,'0Z8OMAHVBXH0'),(881933801750540,'0Z8OMAHVBXH0');
/*!40000 ALTER TABLE `user_relation_role_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_relation_tenant_map`
--

DROP TABLE IF EXISTS `user_relation_tenant_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_relation_tenant_map` (
  `id` bigint NOT NULL,
  `tenant` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`tenant`),
  CONSTRAINT `custom_constaint_10` FOREIGN KEY (`id`) REFERENCES `user_relation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_relation_tenant_map`
--

LOCK TABLES `user_relation_tenant_map` WRITE;
/*!40000 ALTER TABLE `user_relation_tenant_map` DISABLE KEYS */;
INSERT INTO `user_relation_tenant_map` VALUES (861812327710759,'0P8HE307W6IO'),(862433005010945,'0P8HPG99R56P'),(881933091340292,'0P8OMAHSU0W4');
/*!40000 ALTER TABLE `user_relation_tenant_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `validation_result`
--

DROP TABLE IF EXISTS `validation_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `validation_result` (
  `id` bigint NOT NULL,
  `failure_count` tinyint NOT NULL,
  `notify_admin` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `validation_result`
--

LOCK TABLES `validation_result` WRITE;
/*!40000 ALTER TABLE `validation_result` DISABLE KEYS */;
INSERT INTO `validation_result` VALUES (865884807626753,0,_binary '\0');
/*!40000 ALTER TABLE `validation_result` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-04-22 21:43:00
