-- MySQL dump 10.13  Distrib 8.0.33, for Linux (x86_64)
--
-- Host: localhost    Database: auth_dev
-- ------------------------------------------------------
-- Server version	8.0.33

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
INSERT INTO `allowed_header_map` VALUES (857844656111616,'Accept'),(857844656111616,'Access-Control-Request-Method'),(857844656111616,'Authorization'),(857844656111616,'changeId'),(857844656111616,'Content-Type'),(857844656111616,'lastupdateat'),(857844656111616,'x-mt-request-id'),(857844656111616,'x-requested-with'),(857844656111616,'X-XSRF-TOKEN'),(881941651914966,'authorization'),(881941651914966,'changeId'),(881941651914966,'x-mt-request-id'),(881941651914966,'X-XSRF-TOKEN');
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
  `action_at` bigint NOT NULL,
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
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `allow_cache` bit(1) NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `etag` bit(1) DEFAULT NULL,
  `expires` bigint DEFAULT NULL,
  `max_age` bigint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `smax_age` bigint DEFAULT NULL,
  `vary` varchar(255) DEFAULT NULL,
  `weak_validation` bit(1) DEFAULT NULL,
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
INSERT INTO `cache_profile` VALUES (858322708725762,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',4,_binary '','0X8G900BJFGG',NULL,_binary '',NULL,10,'缓存10秒Etag',NULL,NULL,_binary '\0','0P8HE307W6IO'),(858360529289217,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '','0X8G9HDSXCZK',NULL,_binary '\0',NULL,10,'缓存10秒无Etag',NULL,NULL,_binary '\0','0P8HE307W6IO'),(858360594825216,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '\0','0X8G9HEVMSCH',NULL,_binary '\0',NULL,NULL,'无缓存',NULL,NULL,_binary '\0','0P8HE307W6IO'),(881933328318480,1689492851003,'0U8OMAGVFMS3',1689492851003,'0U8OMAGVFMS3',0,_binary '\0','0X8OMAKSU4UN',NULL,_binary '\0',NULL,NULL,'禁止缓存',NULL,NULL,_binary '\0','0P8OMAHSU0W4');
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
  `empty_opt` bit(1) DEFAULT NULL,
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
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `accessible_` bit(1) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `path` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) NOT NULL,
  `role_id` varchar(255) NOT NULL,
  `secret` varchar(255) NOT NULL,
  `access_token_validity_seconds` int NOT NULL,
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
INSERT INTO `client` VALUES (843498099048450,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '','0C8AZTODP4HT','负责管理应用与用户等信息','权限管理中心','auth-svc','0P8HE307W6IO','0Z8HHJ489SE0','97b29ceb-c445-4178-bb95-84755f14cba6',120,NULL,'http://localhost:8080'),(843498099048451,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '','0C8AZTODP4H0','暂未使用，一个简单的NoSQL存储服务','nosql store','object-svc','0P8HE307W6IO','0Z8HHJ489SE1','97b29ceb-c445-4178-bb95-84755f14cba6',122,NULL,'http://localhost:3000'),(843498099048459,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '\0','0C8AZTODP4H8','scripttestscript','rightRoleNotSufficientResourceId',NULL,'0P8HE307W6IO','0Z8HHJ489SE9','97b29ceb-c445-4178-bb95-84755f14cba6',120,NULL,'http://localhost:3000'),(843498099048553,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '','0C8AZTODP4HZ','仅供测试使用','测试后端服务','test-svc','0P8HE307W6IO','0Z8HHJ489SEA','97b29ceb-c445-4178-bb95-84755f14cba6',122,NULL,'http://localhost:9999'),(843509306228737,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '','0C8AZYTQ5W5C','反向代理服务，负责处理mt-auth的核心功能','反向代理服务',NULL,'0P8HE307W6IO','0Z8HHJ489SEB','97b29ceb-c445-4178-bb95-84755f14cba6',120,NULL,'http://localhost:3000'),(843509757116417,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '\0','0C8AZZ16LZB4','mt-auth前端','登陆客户端UI',NULL,'0P8HE307W6IO','0Z8HHJ489SEC','97b29ceb-c445-4178-bb95-84755f14cba6',120,1200,NULL),(843511877861378,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '\0','0C8B00098WLD','发送验证码时使用','未登录UI',NULL,'0P8HE307W6IO','0Z8HHJ489SED','97b29ceb-c445-4178-bb95-84755f14cba6',120,NULL,NULL),(843512635457539,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '\0','0C8B00CSATJ6','用户密码验证策略','测试应用服务',NULL,'0P8HE307W6IO','0Z8HHJ489SEF','97b29ceb-c445-4178-bb95-84755f14cba6',120,NULL,'http://localhost:3000'),(843512635457561,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '','0C8AZTODP4I0','仅供测试使用','测试用资源应用',NULL,'0P8HE307W6IO','0Z8HHJ489SEG','97b29ceb-c445-4178-bb95-84755f14cba6',120,NULL,'http://localhost:3000'),(862433369391105,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,_binary '','0C8HPGF4GBUP',NULL,'分布式事务服务','saga-svc','0P8HPG99R56P','0Z8HPGF4RKEA','97b29ceb-c445-4178-bb95-84755f14cba6',123,NULL,'http://localhost:3000'),(862433780433088,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,_binary '','0C8HPGLXHMET','商城后端','商城服务端','product-svc','0P8HPG99R56P','0Z8HPGLXHMF5','97b29ceb-c445-4178-bb95-84755f14cba6',123,NULL,'http://localhost:3000'),(862433826570240,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,_binary '\0','0C8HPGMON9J5',NULL,'商城前端UI',NULL,'0P8HPG99R56P','0Z8HPGMOYI2P','97b29ceb-c445-4178-bb95-84755f14cba6',1200,NULL,NULL),(862524186558475,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,_binary '\0','0C8HQM52YN7K',NULL,'管理员前端UI',NULL,'0P8HPG99R56P','0Z8HQM52YN7W','97b29ceb-c445-4178-bb95-84755f14cba6',1200,NULL,NULL),(863884654149898,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '','0C8I7Z4Q8N09',NULL,'resource client',NULL,'0P8HE307W6IO','0Z8I7Z4Q8N4B','97b29ceb-c445-4178-bb95-84755f14cba6',1800,NULL,'http://localhost:3000'),(863884655198262,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '\0','0C8I7Z4QJVNC',NULL,'non resource client',NULL,'0P8HE307W6IO','0Z8I7Z4QV41J','97b29ceb-c445-4178-bb95-84755f14cba6',1800,120,'http://localhost:3001'),(881933189382715,1689492851003,'0U8OMAGVFMS3',1689492851003,'0U8OMAGVFMS3',0,_binary '','0C8OMAII48XE',NULL,'演示后端应用','demo-svc','0P8OMAHSU0W4','0Z8OMAII49B6','97b29ceb-c445-4178-bb95-84755f14cba6',120,NULL,'http://localhost:8083'),(881933420068882,1689492851003,'0U8OMAGVFMS3',1689492851003,'0U8OMAGVFMS3',0,_binary '\0','0C8OMAMBGNWG',NULL,'演示前端应用',NULL,'0P8OMAHSU0W4','0Z8OMAMBGNWX','97b29ceb-c445-4178-bb95-84755f14cba6',120,NULL,NULL),(881933438418954,1689492851003,'0U8OMAGVFMS3',1689492851003,'0U8OMAGVFMS3',0,_binary '\0','0C8OMAMM2QDC',NULL,'演示单点登录应用',NULL,'0P8OMAHSU0W4','0Z8OMAMMDYX5','97b29ceb-c445-4178-bb95-84755f14cba6',120,NULL,NULL),(881933451001878,1689492851003,'0U8OMAGVFMS3',1689492851003,'0U8OMAGVFMS3',0,_binary '\0','0C8OMAMTVNY8',NULL,'演示用户名密码登录应用',NULL,'0P8OMAHSU0W4','0Z8OMAMTVNYT','97b29ceb-c445-4178-bb95-84755f14cba6',120,1200,NULL);
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
INSERT INTO `cors_origin_map` VALUES (857844656111616,'http://localhost:4300'),(857844656111616,'http://localhost:8083'),(857844656111616,'https://console.letsauth.cloud'),(857844656111616,'https://www.letsauth.cloud'),(881933318357304,'http://localhost:3000'),(881941651914966,'*');
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
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `allow_credentials` bit(1) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `max_age` bigint DEFAULT NULL,
  `name` varchar(255) NOT NULL,
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
INSERT INTO `cors_profile` VALUES (857844656111616,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '','0O8G2WE71L35','默认适用所有api',7200,'默认CORS配置','0P8HE307W6IO'),(881933318357304,1689492851003,'0U8OMAGVFMS3',1689492851003,'0U8OMAGVFMS3',0,_binary '','0O8OMAKMWMRB',NULL,1200,'允许本地跨域','0P8OMAHSU0W4'),(881941651914966,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,_binary '','0O8OMEEGHP05',NULL,7200,'Token跨域配置','0P8HE307W6IO');
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
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `cache_profile_id` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) NOT NULL,
  `cors_profile_id` varchar(255) DEFAULT NULL,
  `csrf_enabled` bit(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `websocket` bit(1) NOT NULL,
  `method` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  `permission_id` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) NOT NULL,
  `secured` bit(1) DEFAULT NULL,
  `shared` bit(1) DEFAULT NULL,
  `expire_reason` varchar(255) DEFAULT NULL,
  `expired` bit(1) NOT NULL,
  `external` bit(1) NOT NULL,
  `replenish_rate` int DEFAULT NULL,
  `burst_capacity` int DEFAULT NULL,
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
INSERT INTO `endpoint` VALUES (847539962118000,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP400',_binary '\0','GET','读取应用简略信息','projects/**/clients','0Y8HHJ47NBD4','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118001,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP401',_binary '\0','GET','查询目标应用是否可以自动批准第三方登录请求','projects/**/clients/**/authorize','0Y8HHJ47NBD5','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118002,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP402',_binary '\0','POST','创建应用','projects/**/clients','0Y8HHJ47NBD6','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118003,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP403',_binary '\0','PUT','更新应用','projects/**/clients/**','0Y8HHJ47NBD7','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118004,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP404',_binary '\0','DELETE','删除应用','projects/**/clients/**','0Y8HHJ47NBD8','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118005,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP405',_binary '\0','POST','注册用户','users','0Y8HHJ47NBD9','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118006,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP407',_binary '\0','PUT','更新单个用户信息','mgmt/users/**','0Y8HHJ47NBDB','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118008,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP409',_binary '\0','PUT','用户更改密码','users/pwd','0Y8HHJ47NBDD','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118009,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40A',_binary '\0','POST','OAuth2 authorize端口','authorize','0Y8HHJ47NBDE','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118010,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40C',_binary '\0','POST','发送验证码','verification-code','0Y8HHJ47NBDG','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118011,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40D',_binary '\0','POST','用户忘记密码','users/forgetPwd','0Y8HHJ47NBDH','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118012,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40E',_binary '\0','POST','重置用户密码','users/resetPwd','0Y8HHJ47NBDI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118013,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8OMEEGHP05',_binary '\0',NULL,'0E8AZTODP40F',_binary '\0','POST','申请身份令牌','oauth/token',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118014,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40G',_binary '\0','POST','将已签发的用户或应用的身份令牌回收','mgmt/revoke-tokens','0Y8HHJ47NBDK','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118015,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40I',_binary '\0','POST','创建api端口','projects/**/endpoints','0Y8HHJ47NBDL','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118016,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40J',_binary '\0','GET','读取当前所有api端口简略信息','projects/**/endpoints','0Y8HHJ47NBDM','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118017,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40K',_binary '\0','PUT','更新单个api端口信息','projects/**/endpoints/**','0Y8HHJ47NBDN','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118018,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP40L',_binary '\0','DELETE','删除单个api端口','projects/**/endpoints/**','0Y8HHJ47NBDO','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118019,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP432',_binary '\0','GET','读取单个应用详细信息','projects/**/clients/**','0Y8HHJ47NBDP','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118021,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP435',_binary '\0','GET','读取单个api端口的详细信息','projects/**/endpoints/**','0Y8HHJ47NBDS','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118024,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP439',_binary '\0','DELETE','批量删除多个api端口','projects/**/endpoints','0Y8HHJ47NBDV','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118027,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP43N',_binary '\0','GET','查看当前回收的身份令牌','mgmt/revoke-tokens','0Y8HHJ47NBDZ','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118028,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP450',_binary '\0','POST','发布重新载入代理缓存事件','mgmt/endpoints/event/reload','0Y8HHJ47NB00','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118029,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP451',_binary '\0','GET','测试延迟','delay/**',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(847539962118030,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8AZTODP452',_binary '\0','GET','测试Http返回码','status/**',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(847539962118031,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT',NULL,_binary '\0',NULL,'0E8BO3KAHURK',_binary '','GET','系统监控websocket','monitor','0Y8HHJ47NBE6','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118032,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8BPAEMD3B5',_binary '\0','POST','获取websocket链接使用的令牌','tickets/**','0Y8HHJ47NBE7','0P8HE307W6IO',_binary '',_binary '',NULL,_binary '\0',_binary '',10,60),(847539962118033,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8BPAEMD3B6',_binary '\0','GET','获取商城通知历史','notifications','0Y8HHJ47NBE8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(847539962118034,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8HPGLXHMET',NULL,_binary '\0',NULL,'0E8BPAEMD3B7',_binary '','GET','商城监控websocket','monitor','0Y8HHJ47NBE9','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(855088528097288,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8F3Q8VWB9C',_binary '\0','POST','重试系统事件','mgmt/events/**/retry','0Y8HHJ47NBEA','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(855088539107336,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8F3Q92GAO0',_binary '\0','GET','读取当前系统事件','mgmt/events','0Y8HHJ47NBEB','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(858486057992192,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZYTQ5W5C',NULL,_binary '\0',NULL,'0E8GB31T5VK2',_binary '\0','GET','查询代理缓存MD5值','/info/checkSum',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(858491679408138,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8GB5MSBKE8',_binary '\0','GET','检查代理缓存是否同步','mgmt/proxy/check','0Y8HHJ47NBES','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861497201262599,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HDICM4CU8',_binary '\0','POST','创建新项目','projects','0Y8HHJ47NBET','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861497574031368,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HDIISDBLS',_binary '\0','GET','查询已有项目','mgmt/projects','0Y8HHJ47NBEU','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861540772741129,1689492851003,'0U8AZTODP4H0',1716372487282,'0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HE2D7RLDT',_binary '\0','GET','查询权限','projects/**/permissions','0Y8HHJ47NBEV','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861541373050887,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HE2N4V2TD',_binary '\0','POST','创建权限配置','projects/**/permissions','0Y8HHJ47NBEW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861612640567304,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HEZDS0IYO',_binary '\0','GET','查询当前全部角色简要信息','projects/**/roles','0Y8HHJ47NBEX','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861612644761608,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HEZDUIFB4',_binary '\0','POST','创建角色','projects/**/roles','0Y8HHJ47NBEY','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(861809903403018,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HHI057P4W',_binary '\0','GET','查询租户项目','projects/tenant','0Y8HHJ47NBEZ','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862016664240135,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HK4ZKYRP1',_binary '\0','GET','查询租户用户','projects/**/users','0Y8HK4ZLA03Q','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862028321783818,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HKACDKDTT',_binary '\0','GET','查看角色','projects/**/roles/**','0Y8HKACDVMDL','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862036408401931,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HKE24FWU8',_binary '\0','PUT','更新角色','projects/**/roles/**','0Y8HKE24FWUI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862036445102092,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HKE2QAIV4',_binary '\0','DELETE','删除角色','projects/**/roles/**','0Y8HKE2QAIVF','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862037661450252,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '','查询当前用户角色信息','0E8HKEMUH340',_binary '\0','GET','查询项目用户详细信息','projects/**/users/**','0Y8HKEMUH34B','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862151446102030,1689492851003,'0U8AZTODP4H0',1716372438439,'0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HLUWMX2BL',_binary '\0','DELETE','删除权限','projects/**/permissions/**','0Y8HLUWMX2BX','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862151448723471,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HLUWOH91E',_binary '\0','GET','读取我的用户信息','users/profile','0Y8HLUWOH92P','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382466793485,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT1AO8OW',_binary '\0','GET','管理员查询任意应用简略信息','mgmt/clients','0Y8HOT1AO8P8','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382500872206,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT1UYO00',_binary '\0','GET','管理员查询任意应用详细信息','mgmt/clients/**','0Y8HOT1UYO0D','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382517125133,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT24N0U8',_binary '\0','GET','管理员查询任意API详细信息','mgmt/endpoints/**','0Y8HOT24N0UK','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382523940878,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT28P3WG',_binary '\0','GET','管理员查询任意API简略信息','mgmt/endpoints','0Y8HOT28P3WT','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382783463438,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT6J7KSH',_binary '\0','GET','管理员查询全部用户简略信息','mgmt/users','0Y8HOT6J7KST','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862382792900621,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HOT6OTUKG',_binary '\0','GET','管理员查询任意用户详细信息','mgmt/users/**','0Y8HOT6OTUKS','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862402490400776,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HP28FWEF5',_binary '\0','GET','获取所有系统通知','mgmt/notifications','0Y8HP28G7MYV','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862446240137233,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPMBZOBNL',_binary '\0','GET','获取购物车信息','cart/user','0Y8HPMBZOBO0','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469694685190,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX3VLG5D',_binary '\0','GET','管理员获取所有地址简要信息','addresses/admin','0Y8HPX3VWOP1','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469704646672,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX41U70G',_binary '\0','GET','管理员获取地址明细','addresses/admin/**','0Y8HPX41U70V','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469722996742,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX4CG9HD',_binary '\0','POST','用户添加地址','addresses/user','0Y8HPX4CRI11','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469746065633,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX4QHY35',_binary '\0','GET','用户获取所有地址','addresses/user','0Y8HPX4QHY3K','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469758648326,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX4XOEF5',_binary '\0','GET','用户获取地址明细','addresses/user/**','0Y8HPX4XZMYT','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469786435600,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX5EJ7R4',_binary '\0','DELETE','用户删除地址','addresses/user/**','0Y8HPX5EJ7RJ','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469789057041,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPX5G3EGX',_binary '\0','PUT','用户更新地址','addresses/user/**','0Y8HPX5G3EHC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469892341790,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX75L5HQ',_binary '\0','DELETE','管理员删除属性','attributes/admin','0Y8HPX75L5I5','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469943722001,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX806EPS',_binary '\0','POST','管理员创建属性','attributes/admin','0Y8HPX806EQ8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862469957353488,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX88AKU8',_binary '\0','GET','管理员获取当前所有属性简略信息','attributes/admin','0Y8HPX88AKUN','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470017122334,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX97VMRY',_binary '\0','GET','管理员获取单一属性明细','attributes/admin/**','0Y8HPX97VMSD','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470036520976,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX9JFEV4',_binary '\0','PATCH','管理员更改特定属性','attributes/admin/**','0Y8HPX9JFEVJ','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470049103888,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX9QX3WG',_binary '\0','PUT','管理员更新特定属性','attributes/admin/**','0Y8HPX9QX3WV','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470055919633,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPX9UZ6YO',_binary '\0','DELETE','管理员删除特定属性','attributes/admin/**','0Y8HPX9UZ6Z4','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470863847425,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXN7ONI8',_binary '\0','DELETE','系统清空购物车','cart/app','0Y8HPXN7ZW1S','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470881149153,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXNIAQ4W',_binary '\0','POST','用户添加商品到购物车','cart/user','0Y8HPXNIAQ5C','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470894256134,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXNPSF0H',_binary '\0','DELETE','用户删除购物车商品','cart/user/**','0Y8HPXNQ3NK5','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470929383440,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPXOB0JY8',_binary '\0','DELETE','管理员删除目录','catalogs/admin','0Y8HPXOB0JYN','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470952452112,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXOOQZUO',_binary '\0','GET','管理员获取商品目录','catalogs/admin','0Y8HPXOOQZV3','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470959267857,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXOST2WW',_binary '\0','POST','管理员创建商品目录','catalogs/admin','0Y8HPXOST2XC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470971850768,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXP0ARY8',_binary '\0','PATCH','管理员部分更新商品目录','catalogs/admin/**','0Y8HPXP0ARYN','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470983385285,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXP7601H',_binary '\0','GET','管理员获取商品目录明细','catalogs/admin/**','0Y8HPXP7601W','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862470998589456,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXPG7VNK',_binary '\0','DELETE','管理员删除特定商品目录','catalogs/admin/**','0Y8HPXPG7VNZ','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471005405200,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXPK9YPS',_binary '\0','PUT','管理员更新特定商品目录','catalogs/admin/**','0Y8HPXPK9YQ7','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471028998159,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '\0',NULL,'0E8HPXPYBN5S',_binary '\0','GET','获取商城商品目录信息','catalogs/public',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471117603041,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HPXRF2R4G',_binary '\0','GET','获取该系统改动','changes/root','0Y8HPXRF2R4W','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471126515728,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXRKDS74',_binary '\0','GET','获取该商城服务系统改动','changes/root','0Y8HPXRKDS7J','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471319978181,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXURKCS5',_binary '\0','POST','上传文件','files/app','0Y8HPXURKCSK','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471340425231,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '\0',NULL,'0E8HPXV3QLTS',_binary '\0','GET','获取文件公共端口','files/public/**',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471359823888,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXVFADXC',_binary '\0','GET','读取商城目录过滤器','filters/admin','0Y8HPXVFADXR','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471366115334,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXVIPZWH',_binary '\0','POST','创建商城目录过滤器','filters/admin','0Y8HPXVJ18G5','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471374503952,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXVO114W',_binary '\0','DELETE','删除商城目录过滤器','filters/admin','0Y8HPXVO115B','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471386038288,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXVUW934',_binary '\0','PATCH','更改部分商城目录过滤器','filters/admin/**','0Y8HPXVUW93J','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471397048336,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXW1G8HS',_binary '\0','GET','读取特定商城目录过滤器','filters/admin/**','0Y8HPXW1G8I7','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471405961231,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXW6G16P',_binary '\0','DELETE','删除特定商城目录过滤器','filters/admin/**','0Y8HPXW6R9QM','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471422738438,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPXWGFMQ9',_binary '\0','PUT','更新特定商城目录过滤器','filters/admin/**','0Y8HPXWGQV45','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471440564229,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '\0',NULL,'0E8HPXWR1P1D',_binary '\0','GET','获取商城过滤器配置','filters/public',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471786594421,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HPY2HDK6D',_binary '\0','GET','读取分布式事务服务系统事件','mgmt/events','0Y8HPY2HDK6S','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471789215761,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPY2IXQTD',_binary '\0','GET','读取商城服务系统事件','mgmt/events','0Y8HPY2IXQTS','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471831158801,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPY37WQ9T',_binary '\0','POST','重试商城服务某一系统事件','mgmt/events/**/retry','0Y8HPY37WQA8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862471833780230,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HPY395OG1',_binary '\0','POST','重试分布式事务服务某一系统事件','mgmt/events/**/retry','0Y8HPY39GWZP','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473202171921,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYPW6ARL',_binary '\0','GET','管理员获取当前全部订单','orders/admin','0Y8HPYPW6AS0','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473210036241,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYQ0UUWX',_binary '\0','GET','管理员获取特定订单','orders/admin/**','0Y8HPYQ0UUXC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473264562193,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYQXBJEP',_binary '\0','DELETE','管理员删除特定订单','orders/admin/**','0Y8HPYQXBJF4','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473281863878,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYR7MDH2',_binary '\0','POST','系统创建订单','orders/app','0Y8HPYR7MDHH','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473296019473,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYRG1S01',_binary '\0','PUT','系统更新订单','orders/app/**','0Y8HPYRG1S0G','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473310699537,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYROSF7L',_binary '\0','POST','系统验证订单','orders/app/validate','0Y8HPYROSF80','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473430761478,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYTNYJ81',_binary '\0','GET','用户查询我的订单','orders/user','0Y8HPYTO9RLX','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473436528657,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYTRPDKX',_binary '\0','POST','用户下订单','orders/user','0Y8HPYTRPDLC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473455927313,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYU395OH',_binary '\0','PUT','用户更新订单','orders/user/**','0Y8HPYU395OW','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473464840209,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYU8K6WW',_binary '\0','DELETE','用户删除订单','orders/user/**','0Y8HPYU8K6XC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473474277393,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYUE6GOW',_binary '\0','GET','用户读取订单详情','orders/user/**','0Y8HPYUE6GPC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473487384593,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYULZE9T',_binary '\0','PUT','用户确认支付订单','orders/user/**/confirm','0Y8HPYULZEA8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473496821777,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYURLO1S',_binary '\0','PUT','用户重新下订单','orders/user/**/reserve','0Y8HPYURLO28','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473513074695,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYV0YSCH',_binary '\0','POST','获取支付链接','paymentLink','0Y8HPYV1A0W6','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473523560465,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYV7IRR5',_binary '\0','GET','查询支付状态','paymentStatus/**','0Y8HPYV7IRRK','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473545580742,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYVKMQPI',_binary '\0','GET','管理员获取所有产品','products/admin','0Y8HPYVKMQPX','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473551347729,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYVO2CJL',_binary '\0','POST','管理员新建产品','products/admin','0Y8HPYVO2CK0','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473558687761,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYVSFO5D',_binary '\0','DELETE','管理员删除产品','products/admin','0Y8HPYVSFO5S','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473566027782,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYVWHR7L',_binary '\0','PATCH','管理员部分更新产品','products/admin','0Y8HPYVWSZR9','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473573367825,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYW16BCX',_binary '\0','PATCH','管理员部分更新某一产品','products/admin/**','0Y8HPYW16BDC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473581756433,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYW6641S',_binary '\0','GET','管理员读取某一产品','products/admin/**','0Y8HPYW66428','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473588047889,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYW9WYKH',_binary '\0','PUT','管理员更新某一产品','products/admin/**','0Y8HPYW9WYKW','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473595387921,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYWEAA69',_binary '\0','DELETE','管理员删除某一产品','products/admin/**','0Y8HPYWEAA6O','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473629466641,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYWYKPHC',_binary '\0','GET','系统读取产品信息','products/app','0Y8HPYWYKPHS','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473635233809,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPYX20BGH',_binary '\0','PATCH','系统更新产品信息','products/app','0Y8HPYX20BGW','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473660399813,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '\0',NULL,'0E8HPYXGZPO6',_binary '\0','GET','获取产品列表','products/public',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473669836805,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '\0',NULL,'0E8HPYXMAQRL',_binary '\0','GET','获取特定产品列表','products/public/**',NULL,'0P8HPG99R56P',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473720168455,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPYYG9IWX',_binary '\0','GET','获取订单信息','profiles/orders/id','0Y8HPYYGKRGM','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473737469969,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '','需要查看','0E8HPYYQVLDT',_binary '\0','GET','重新提交定时任务','profiles/orders/scheduler/resubmit','0Y8HPYYQVLE8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473909436423,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ1KY769',_binary '\0','GET','获取库存信息','skus/admin','0Y8HPZ1L9FK6','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473917825041,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ1Q988X',_binary '\0','POST','创建新库存','skus/admin','0Y8HPZ1Q989C','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473932505094,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ1YOMWX',_binary '\0','PATCH','更新库存','skus/admin','0Y8HPZ1YZVGL','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473939320849,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ231YIP',_binary '\0','DELETE','删除库存','skus/admin','0Y8HPZ231YJ4','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473949806609,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ29APDT',_binary '\0','PATCH','更新特定库存','skus/admin/**','0Y8HPZ29APE8','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473958719505,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ2ELQM9',_binary '\0','DELETE','删除指定库存','skus/admin/**','0Y8HPZ2ELQMO','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473970778118,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ2LGYKH',_binary '\0','PUT','更新替换指定库存','skus/admin/**','0Y8HPZ2LS745','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862473983885329,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET',NULL,_binary '',NULL,'0E8HPZ2TL4OX',_binary '\0','GET','查看指定库存','skus/admin/**','0Y8HPZ2TL4PC','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862670892826797,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8HSHJC34BK',_binary '\0','GET','租户查询自己的项目','projects/**','0Y8HSHJC34BW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(862944631455744,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '\0','只对顶级域名及其子域名有效','0E8HVZAGOPOH',_binary '\0','GET','获取csrf cookie','csrf',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863019434770449,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HWXNKYL8H',_binary '\0','GET','获取分布式事务列表','dtx','0Y8HWXNKYL8W','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863019442634758,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HWXNPBWU9',_binary '\0','GET','获取分布式事务详情','dtx/**','0Y8HWXNPN5DX','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863019449450513,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HWXNTP8G1',_binary '\0','POST','取消分布式事务','dtx/cancel','0Y8HWXNTP8GG','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863019457839121,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP',NULL,_binary '',NULL,'0E8HWXNYP14W',_binary '\0','POST','人工解决分布式事务','dtx/resolve','0Y8HWXNYP15C','0P8HPG99R56P',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863374103543820,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8I1GL5LA81',_binary '\0','GET','获取共享api列表','endpoints/shared','0Y8I1GL5LA8B','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863374103543821,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8I1GL5LA82',_binary '\0','GET','获取共享权限列表','projects/**/permissions/shared','0Y8I1GL5LA8C','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(863711891816468,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8I5RRK09HC',_binary '\0','GET','获取UI权限','projects/**/permissions/ui','0Y8I5RRK09HV','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(864558270906384,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8IGKL49IWX',_binary '\0','GET','测试Http返回值','get/**',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(864558282965158,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G900BJFGG','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8IGKLBFZHH',_binary '\0','GET','测试Http缓存','cache',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(864558338015248,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8IGKM87WG1',_binary '\0','POST','测试Post端口','post',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,60),(864879847669830,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8IKOBERLKW',_binary '\0','GET','查看当前定时任务状态','mgmt/jobs','0Y8IKOBERLMT','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(865515702583359,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G900BJFGG','0C8AZTODP4HT','0O8G2WE71L35',_binary '\0',NULL,'0E8ISSFAD4X7',_binary '\0','GET','读取我的用户头像','users/profile/avatar','0Y8ISSFAD4XQ','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(865516402507800,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8ISSQV2Y2O',_binary '\0','POST','创建或更新我的用户头像','users/profile/avatar','0Y8ISSQV2Y3B','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(865708046549002,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8IV8SAOM4K',_binary '\0','POST','重置数据校验任务','mgmt/job/validation/reset','0Y8IV8SAZUO9','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866012474900485,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8IZ4MZGPHG',_binary '\0','GET','检查当前会话是否过期','expire/check','0Y8IZ4MZRY10','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866070308061198,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8IZV7FU1VO',_binary '\0','PUT','更新单个用户信息','mgmt/test/**','0Y8IZV7G5AFH','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866101804662794,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8J09OC3JAF',_binary '\0','GET','读取当前系统审计事件','mgmt/events/audit','0Y8J09OCERRD','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866793667691291,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8J93IHSKUG',_binary '\0','GET','查询项目是否创建就绪','projects/**/ready','0Y8J93IHSL5M','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866794397499416,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8J93UKAWP0',_binary '\0','POST','确认站内信通知','mgmt/notifications/bell/**/ack','0Y8J93UKAWPJ','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(866863218688024,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8J9ZGQNTOK',_binary '\0','GET','获取站内信','mgmt/notifications/bell','0Y8J9ZGQNTP3','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584235573249,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IG77C3K',_binary '\0','POST','创建新的订阅请求','subscriptions/requests','0Y8M0IG8RITC','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584843747381,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '','包括待批准与已批准请求','0E8M0IQ9LUSY',_binary '\0','GET','获取项目订阅请求','subscriptions/requests','0Y8M0IQAUSZ8','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584869437441,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IQOWHG2',_binary '\0','PUT','更新我的订阅请求','subscriptions/requests/**','0Y8M0IQQ5FK0','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584889360549,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IR0RI0W',_binary '\0','POST','取消我的订阅请求','subscriptions/requests/**/cancel','0Y8M0IR20GBI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584908234808,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IRC01KW',_binary '\0','POST','批准订阅请求','subscriptions/requests/**/approve','0Y8M0IRD8ZSN','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874584925011969,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M0IRLZMYO',_binary '\0','POST','拒绝订阅请求','subscriptions/requests/**/reject','0Y8M0IRN8L4W','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874905630932993,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M4M3IY8ZK',_binary '\0','GET','获取我的订阅','subscriptions','0Y8M4M3J9HJ4','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(874924644761623,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8M4UTZLTKZ',_binary '\0','POST','标记当前API为过期','projects/**/endpoints/**/expire','0Y8M4UTZLTLI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(876266711941144,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8MLZDBR4SG',_binary '\0','GET','获取API报告','projects/**/endpoints/**/report','0Y8MLZDBR4T3','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(877686335471675,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8N43JAY7FR',_binary '\0','POST','重置任务','mgmt/jobs/**/reset','0Y8N43JAY7GA','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(878324922974216,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8NC8WD5DDT',_binary '\0','GET','测试-内部','internal',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '\0',10,20),(878324953382929,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '\0',NULL,'0E8NC8WVKDFK',_binary '\0','GET','测试-外部共享无验证','external/shared/no/auth',NULL,'0P8HE307W6IO',_binary '\0',_binary '',NULL,_binary '\0',_binary '',10,20),(878324962820112,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8NC8X16N7K',_binary '\0','GET','测试-外部共享需验证','external/shared/auth','0Y8NC8X16N81','0P8HE307W6IO',_binary '',_binary '',NULL,_binary '\0',_binary '',10,20),(878324978024542,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '',NULL,'0E8NC8XA8J0V',_binary '\0','GET','测试-外部非共享需验证','external/not/shared/auth','0Y8NC8XA8J1B','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,20),(878324991655951,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HZ','0O8G2WE71L35',_binary '\0',NULL,'0E8NC8XICP34',_binary '\0','GET','测试-外部非共享无验证','external/not/shared/no/auth',NULL,'0P8HE307W6IO',_binary '\0',_binary '\0',NULL,_binary '\0',_binary '',10,20),(878818637643797,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NIJPIFF28',_binary '\0','GET','获取用户站内信','user/notifications/bell','0Y8NIJPIFF2U','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(878818652848416,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NIJPRHATC',_binary '\0','POST','确认用户站内信通知','user/notifications/bell/**/ack','0Y8NIJPRHB1D','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(879037695131667,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT',NULL,_binary '\0',NULL,'0E8NLCCBLMGW',_binary '','GET','用户通知websocket','monitor/user','0Y8NLCCBLMHG','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(879716446765077,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NU05MSQO0',_binary '\0','GET','读取系统概述','mgmt/dashboard','0Y8NU05MSQOM','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(880043477696534,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NY6E4KKCH',_binary '\0','GET','获取管理员列表','projects/**/admins','0Y8NY6E4KKD3','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(880043499192592,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NY6EHDARV',_binary '\0','POST','添加管理员','projects/**/admins/**','0Y8NY6EHDATT','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(880043517542411,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8NY6ERZD37',_binary '\0','DELETE','移除管理员','projects/**/admins/**','0Y8NY6ESALN0','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881764363927575,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OK4YFSUF8',_binary '\0','POST','创建跨域配置','projects/**/cors','0Y8OK4YFSUFS','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881764374413335,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OK4YM1LAC',_binary '\0','PUT','编辑跨域配置','projects/**/cors/**','0Y8OK4YM1LAW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881764384374803,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OK4YRZ3LS',_binary '\0','GET','查看跨域配置','projects/**/cors','0Y8OK4YRZ3MC','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881764393811987,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OK4YXLDDS',_binary '\0','DELETE','删除跨域配置','projects/**/cors/**','0Y8OK4YXLDEC','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881811144572946,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OKQG3H6V8',_binary '\0','POST','创建缓存配置','projects/**/cache','0Y8OKQG3SFF7','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881811154534413,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OKQG9EP6O',_binary '\0','GET','查看缓存配置','projects/**/cache','0Y8OKQG9PXQM','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881811160301587,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OKQGD5JPC',_binary '\0','PUT','更新缓存配置','projects/**/cache/**','0Y8OKQGD5JPW','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881811164495891,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OKQGFNG1S',_binary '\0','DELETE','删除缓存配置','projects/**/cache/**','0Y8OKQGFNG2C','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(881933368164361,1689492851003,'0U8OMAGVFMS3',1689492851003,'0U8OMAGVFMS3',0,'0X8OMAKSU4UN','0C8OMAII48XE','0O8OMAKMWMRB',_binary '\0',NULL,'0E8OMALG8XKW',_binary '\0','GET','演示公共共享API','public',NULL,'0P8OMAHSU0W4',_binary '\0',_binary '',NULL,_binary '\0',_binary '',1,1),(881933380222985,1689492851003,'0U8OMAGVFMS3',1689492851003,'0U8OMAGVFMS3',0,'0X8OMAKSU4UN','0C8OMAII48XE','0O8OMAKMWMRB',_binary '',NULL,'0E8OMALNFE7X',_binary '\0','GET','演示保护共享API','protected','0Y8OMALNQMMI','0P8OMAHSU0W4',_binary '',_binary '',NULL,_binary '\0',_binary '',1,1),(882926490222611,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'0X8G9HEVMSCH','0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OYYTVSIKG',_binary '\0','GET','获取应用下拉列表','projects/**/clients/dropdown','0Y8OYY45NEVK','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(882926516961445,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8OYYUBPMCK',_binary '\0','GET','管理员获取应用下拉列表','mgmt/clients/dropdown','0Y8OYYUBPMEE','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(897248814170117,1711366300003,'0U8AZTODP4H0',1711366357863,'0U8AZTODP4H0',1,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8U1QEXOG03',_binary '\0','GET','获取role可以关联的API列表','projects/**/endpoints/protected','0Y8U1QEXOG06','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(903844398104579,1723946377350,'0U8AZTODP4H0',1723946377350,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8WDWDTQY2W',_binary '\0','POST','为账户添加Email','users/profile/email','0Y8WDWDU26MC','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(903844436377603,1723946450873,'0U8AZTODP4H0',1723946450873,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8WDWEGUI9T',_binary '\0','DELETE','移除账户绑定Email','users/profile/email','0Y8WDWEGUI9W','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(903844477272069,1723946528803,'0U8AZTODP4H0',1723946528803,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8WDWF570N5',_binary '\0','PUT','设置用户偏好语言','users/profile/language','0Y8WDWF570NA','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(903844591566854,1723946747152,'0U8AZTODP4H0',1723946747152,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8WDWH18R2C',_binary '\0','POST','为账户绑定Mobile','users/profile/mobile','0Y8WDWH1JZLS','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(903844620926981,1723946802541,'0U8AZTODP4H0',1723946802541,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8WDWHIQ1HD',_binary '\0','DELETE','移除账户绑定Mobile','users/profile/mobile','0Y8WDWHIQ1HI','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(903844663394316,1723946883552,'0U8AZTODP4H0',1723946883552,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8WDWI809HM',_binary '\0','POST','账户设置用户名','users/profile/username','0Y8WDWI809HP','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(903844691181569,1723946936360,'0U8AZTODP4H0',1723946936360,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8WDWIO8LQC',_binary '\0','DELETE','移除账户用户名','users/profile/username','0Y8WDWIOJU9U','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(903844789747715,1723947124431,'0U8AZTODP4H0',1723947124431,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8WDWKB8GE9',_binary '\0','DELETE','注销用户','users/remove-me','0Y8WDWKB8GEC','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(906294725705735,1728620007076,'0U8AZTODP4H0',1728620007076,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8X961RUK1X',_binary '\0','POST','赋予租户用户角色','projects/**/users/**/roles','0Y8X961RUK20','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60),(906294754017285,1728620061101,'0U8AZTODP4H0',1728620061101,'0U8AZTODP4H0',0,NULL,'0C8AZTODP4HT','0O8G2WE71L35',_binary '',NULL,'0E8X9628PDDV',_binary '\0','DELETE','移除租户用户角色','projects/**/users/**/roles/**','0Y8X9628PDDY','0P8HE307W6IO',_binary '',_binary '\0',NULL,_binary '\0',_binary '',10,60);
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
INSERT INTO `exposed_header_map` VALUES (857844656111616,'lastupdateat'),(857844656111616,'location'),(857844656111616,'x-mt-request-id');
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
  `request_at` bigint NOT NULL,
  `path` varchar(255) NOT NULL,
  `client_ip` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) DEFAULT NULL,
  `method` varchar(255) NOT NULL,
  `response_at` bigint NOT NULL,
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
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `content_type` varchar(255) NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `original_name` varchar(255) NOT NULL,
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
  `created_at` bigint DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `modified_at` bigint DEFAULT NULL,
  `modified_by` varchar(255) DEFAULT NULL,
  `version` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `last_status` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `failure_count` int NOT NULL,
  `failure_reason` varchar(255) DEFAULT NULL,
  `failure_allowed` int NOT NULL,
  `max_lock_acquire_failure_allowed` int DEFAULT NULL,
  `notified_admin` bit(1) NOT NULL,
  `last_execution` bigint DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `minimum_idle_time_milli` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_domainId_202409151059` (`domain_id`),
  UNIQUE KEY `UK_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `job_detail`
--

LOCK TABLES `job_detail` WRITE;
/*!40000 ALTER TABLE `job_detail` DISABLE KEYS */;
INSERT INTO `job_detail` VALUES (864879623798785,NULL,NULL,NULL,NULL,0,'KEEP_WS_CONNECTION',NULL,'SINGLE',0,NULL,3,5,_binary '\0',NULL,'0J8IKO7PH9MO',30000),(864879623798787,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'MISSED_EVENT_SCAN','SUCCESS','CLUSTER',0,NULL,3,5,_binary '\0',2023,'0J8IKO7PH9M2',360000),(864879623798788,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'DATA_VALIDATION','SUCCESS','CLUSTER',0,NULL,3,5,_binary '\0',2023,'0J8IKO7PH9M3',90000),(864879623798789,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'PROXY_VALIDATION','SUCCESS','CLUSTER',0,NULL,3,5,_binary '\0',2023,'0J8IKO7PH9M4',90000),(864879623798790,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'ACCESS_DATA_PROCESSING','SUCCESS','CLUSTER',0,NULL,2,5,_binary '\0',2023,'0J8IKO7PH9M5',90000),(877685800173569,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'KEEP_WS_CONNECTION_0','SUCCESS','SINGLE',0,NULL,3,5,_binary '\0',2023,'0J8N43AG8WSI',30000);
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
INSERT INTO `linked_permission_ids_map` VALUES (862170991034369,'0Y8HHJ47NBD4'),(862170991034369,'0Y8HHJ47NBD6'),(862170991034369,'0Y8HHJ47NBD7'),(862170991034369,'0Y8HHJ47NBD8'),(862170991034369,'0Y8HHJ47NBDP'),(862170994180097,'0Y8HHJ47NBDL'),(862170994180097,'0Y8HHJ47NBDM'),(862170994180097,'0Y8HHJ47NBDN'),(862170994180097,'0Y8HHJ47NBDO'),(862170994180097,'0Y8HHJ47NBDS'),(862170994180097,'0Y8HHJ47NBDV'),(862170994180097,'0Y8HHJ47NBEH'),(862170994180097,'0Y8HHJ47NBEM'),(862170994180097,'0Y8M4UTZLTLI'),(862170994180097,'0Y8MLZDBR4T3'),(862170994180097,'0Y8OK4YFSUFS'),(862170994180097,'0Y8OK4YM1LAW'),(862170994180097,'0Y8OK4YRZ3MC'),(862170994180097,'0Y8OK4YXLDEC'),(862170994180097,'0Y8OKQG3SFF7'),(862170994180097,'0Y8OKQG9PXQM'),(862170994180097,'0Y8OKQGD5JPW'),(862170994180097,'0Y8OKQGFNG2C'),(862170997325824,'0Y8HHJ47NBEX'),(862170997325824,'0Y8HHJ47NBEY'),(862170997325824,'0Y8HKACDVMDL'),(862170997325824,'0Y8HKE24FWUI'),(862170997325824,'0Y8HKE2QAIVF'),(862171001520128,'0Y8HHJ47NBEV'),(862171001520128,'0Y8HHJ47NBEW'),(862171001520128,'0Y8HLUWMX2BX'),(862171046084609,'0Y8HK4ZLA03Q'),(862171046084609,'0Y8HKEMUH34B'),(862433015496710,'0Y8HSHJC34BW'),(862433015496716,'0Y8HHJ47NBD4'),(862433015496716,'0Y8HHJ47NBD6'),(862433015496716,'0Y8HHJ47NBD7'),(862433015496716,'0Y8HHJ47NBD8'),(862433015496716,'0Y8HHJ47NBDP'),(862433015496730,'0Y8HHJ47NBDL'),(862433015496730,'0Y8HHJ47NBDM'),(862433015496730,'0Y8HHJ47NBDN'),(862433015496730,'0Y8HHJ47NBDO'),(862433015496730,'0Y8HHJ47NBDS'),(862433015496730,'0Y8HHJ47NBDV'),(862433015496730,'0Y8HHJ47NBEH'),(862433015496730,'0Y8HHJ47NBEM'),(862433015496730,'0Y8M4UTZLTLI'),(862433015496730,'0Y8MLZDBR4T3'),(862433015496730,'0Y8OK4YFSUFS'),(862433015496730,'0Y8OK4YM1LAW'),(862433015496730,'0Y8OK4YRZ3MC'),(862433015496730,'0Y8OK4YXLDEC'),(862433015496730,'0Y8OKQG3SFF7'),(862433015496730,'0Y8OKQG9PXQM'),(862433015496730,'0Y8OKQGD5JPW'),(862433015496730,'0Y8OKQGFNG2C'),(862433015496750,'0Y8HHJ47NBEX'),(862433015496750,'0Y8HHJ47NBEY'),(862433015496750,'0Y8HKACDVMDL'),(862433015496750,'0Y8HKE24FWUI'),(862433015496750,'0Y8HKE2QAIVF'),(862433015496762,'0Y8HHJ47NBEV'),(862433015496762,'0Y8HHJ47NBEW'),(862433015496762,'0Y8HLUWMX2BX'),(862433015496776,'0Y8HK4ZLA03Q'),(862433015496776,'0Y8HKEMUH34B'),(874584730501120,'0Y8M0IG8RITC'),(874584730501120,'0Y8M0IQAUSZ8'),(874584730501120,'0Y8M0IQQ5FK0'),(874584730501120,'0Y8M0IR20GBI'),(874584730501120,'0Y8M0IRD8ZSN'),(874584730501120,'0Y8M0IRN8L4W'),(874584730501120,'0Y8M4M3J9HJ4'),(874602266361904,'0Y8M0IG8RITC'),(874602266361904,'0Y8M0IQAUSZ8'),(874602266361904,'0Y8M0IQQ5FK0'),(874602266361904,'0Y8M0IR20GBI'),(874602266361904,'0Y8M0IRD8ZSN'),(874602266361904,'0Y8M0IRN8L4W'),(874602266361904,'0Y8M4M3J9HJ4'),(880043699994657,'0Y8NY6E4KKD3'),(880043699994657,'0Y8NY6EHDATT'),(880043699994657,'0Y8NY6ESALN0'),(880043708383267,'0Y8NY6E4KKD3'),(880043708383267,'0Y8NY6EHDATT'),(880043708383267,'0Y8NY6ESALN0'),(881933147963401,'0Y8HSHJC34BW'),(881933147963401,'0Y8OMAHTGHZC'),(881933147963401,'0Y8OMAHTGHZE'),(881933147963401,'0Y8OMAHTGHZG'),(881933147963407,'0Y8HHJ47NBD4'),(881933147963407,'0Y8HHJ47NBD6'),(881933147963407,'0Y8HHJ47NBD7'),(881933147963407,'0Y8HHJ47NBD8'),(881933147963407,'0Y8HHJ47NBDP'),(881933147963407,'0Y8OMAHTGHZI'),(881933147963407,'0Y8OMAHTGHZK'),(881933147963407,'0Y8OMAHTGHZM'),(881933147963407,'0Y8OMAHTGHZO'),(881933147963415,'0Y8HHJ47NBDL'),(881933147963415,'0Y8HHJ47NBDM'),(881933147963415,'0Y8HHJ47NBDN'),(881933147963415,'0Y8HHJ47NBDO'),(881933147963415,'0Y8HHJ47NBDS'),(881933147963415,'0Y8HHJ47NBDV'),(881933147963415,'0Y8M4UTZLTLI'),(881933147963415,'0Y8MLZDBR4T3'),(881933147963415,'0Y8OK4YFSUFS'),(881933147963415,'0Y8OK4YM1LAW'),(881933147963415,'0Y8OK4YRZ3MC'),(881933147963415,'0Y8OK4YXLDEC'),(881933147963415,'0Y8OKQG3SFF7'),(881933147963415,'0Y8OKQG9PXQM'),(881933147963415,'0Y8OKQGD5JPW'),(881933147963415,'0Y8OKQGFNG2C'),(881933147963415,'0Y8OMAHTGHZQ'),(881933147963415,'0Y8OMAHTGHZS'),(881933147963415,'0Y8OMAHTGHZU'),(881933147963415,'0Y8OMAHTGHZW'),(881933147963415,'0Y8OMAHTGI0O'),(881933147963415,'0Y8OMAHTGI0Q'),(881933147963415,'0Y8OMAHTGI0S'),(881933147963415,'0Y8OMAHTGI0U'),(881933147963415,'0Y8OMAHTGI0W'),(881933147963415,'0Y8OMAHTGI0Y'),(881933147963415,'0Y8OMAHTGI10'),(881933147963415,'0Y8OMAHTGI12'),(881933147963423,'0Y8HHJ47NBEX'),(881933147963423,'0Y8HHJ47NBEY'),(881933147963423,'0Y8HKACDVMDL'),(881933147963423,'0Y8HKE24FWUI'),(881933147963423,'0Y8HKE2QAIVF'),(881933147963423,'0Y8OMAHTGHZY'),(881933147963423,'0Y8OMAHTGI00'),(881933147963423,'0Y8OMAHTGI02'),(881933147963423,'0Y8OMAHTGI04'),(881933147963431,'0Y8HHJ47NBEV'),(881933147963431,'0Y8HHJ47NBEW'),(881933147963431,'0Y8HLUWMX2BX'),(881933147963431,'0Y8OMAHTGI06'),(881933147963431,'0Y8OMAHTGI08'),(881933147963431,'0Y8OMAHTGI0A'),(881933147963431,'0Y8OMAHTGI0C'),(881933147963439,'0Y8HK4ZLA03Q'),(881933147963439,'0Y8HKEMUH34B'),(881933147963439,'0Y8OMAHTGI0E'),(881933147963439,'0Y8OMAHTGI0G'),(881933147963439,'0Y8OMAHTGI0I'),(881933147963445,'0Y8M0IG8RITC'),(881933147963445,'0Y8M0IQAUSZ8'),(881933147963445,'0Y8M0IQQ5FK0'),(881933147963445,'0Y8M0IR20GBI'),(881933147963445,'0Y8M0IRD8ZSN'),(881933147963445,'0Y8M0IRN8L4W'),(881933147963445,'0Y8M4M3J9HJ4'),(881933147963445,'0Y8OMAHTGI0K'),(881933147963447,'0Y8NY6E4KKD3'),(881933147963447,'0Y8NY6EHDATT'),(881933147963447,'0Y8NY6ESALN0'),(881933147963447,'0Y8OMAHTGI0M');
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
  `login_at` bigint NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `ip_address` varchar(255) NOT NULL,
  `project_id` varchar(255) NOT NULL,
  `agent` varchar(255) NOT NULL,
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
  `login_at` bigint NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `ip_address` varchar(255) NOT NULL,
  `agent` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_domainId_202409151058` (`domain_id`)
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
  `descriptions` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `timestamp` bigint NOT NULL,
  `title` varchar(255) NOT NULL,
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
  `opt_type` varchar(255) NOT NULL,
  `executor` varchar(255) NOT NULL,
  `last_opt_at` bigint NOT NULL,
  PRIMARY KEY (`executor`,`opt_type`)
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
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` bigint NOT NULL,
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `project_id` varchar(255) NOT NULL,
  `shared` bit(1) NOT NULL,
  `system_create` bit(1) NOT NULL,
  `tenant_id` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_c1y16mv395nw4dev48m73a5h3` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (861812326137929,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP400','0Y8HHJ47NBD4','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137930,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP401','0Y8HHJ47NBD5','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137931,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP402','0Y8HHJ47NBD6','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137932,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP403','0Y8HHJ47NBD7','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137933,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP404','0Y8HHJ47NBD8','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137934,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP405','0Y8HHJ47NBD9','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137936,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP407','0Y8HHJ47NBDB','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137938,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP409','0Y8HHJ47NBDD','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137939,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP40A','0Y8HHJ47NBDE','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137941,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP40C','0Y8HHJ47NBDG','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137942,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP40D','0Y8HHJ47NBDH','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137943,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP40E','0Y8HHJ47NBDI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137945,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP40G','0Y8HHJ47NBDK','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137946,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP40I','0Y8HHJ47NBDL','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137947,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP40J','0Y8HHJ47NBDM','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137948,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP40K','0Y8HHJ47NBDN','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137949,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP40L','0Y8HHJ47NBDO','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137950,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP432','0Y8HHJ47NBDP','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137953,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP435','0Y8HHJ47NBDS','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137956,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP439','0Y8HHJ47NBDV','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137960,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP43N','0Y8HHJ47NBDZ','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137965,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8AZTODP450','0Y8HHJ47NB00','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137968,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8BO3KAHURK','0Y8HHJ47NBE6','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137969,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8BPAEMD3B5','0Y8HHJ47NBE7','0P8HE307W6IO',_binary '',_binary '',NULL,'API',NULL),(861812326137972,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8F3Q8VWB9C','0Y8HHJ47NBEA','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137973,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8F3Q92GAO0','0Y8HHJ47NBEB','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137990,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8GB5MSBKE8','0Y8HHJ47NBES','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137991,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HDICM4CU8','0Y8HHJ47NBET','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137992,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HDIISDBLS','0Y8HHJ47NBEU','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137993,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HE2D7RLDT','0Y8HHJ47NBEV','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137994,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HE2N4V2TD','0Y8HHJ47NBEW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137995,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HEZDS0IYO','0Y8HHJ47NBEX','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137996,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HEZDUIFB4','0Y8HHJ47NBEY','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(861812326137997,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HHI057P4W','0Y8HHJ47NBEZ','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862016664764426,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HK4ZKYRP1','0Y8HK4ZLA03Q','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862028321783905,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HKACDKDTT','0Y8HKACDVMDL','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862036409450502,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HKE24FWU8','0Y8HKE24FWUI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862036446150664,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HKE2QAIV4','0Y8HKE2QAIVF','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862037661974538,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HKEMUH340','0Y8HKEMUH34B','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862151446626385,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HLUWMX2BL','0Y8HLUWMX2BX','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862151449247826,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HLUWOH91E','0Y8HLUWOH92P','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862170896138242,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'PROJECT_INFO_MGMT','0Y8HM3UAYUBK','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON',NULL),(862170991034369,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'CLIENT_MGMT','0Y8HM3VVGSN4','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON',NULL),(862170994180097,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'API_MGMT','0Y8HM3VXC7WG','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON',NULL),(862170997325824,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'ROLE_MGMT','0Y8HM3VYWEM9','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON',NULL),(862171001520128,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'PERMISSION_MGMT','0Y8HM3W1EAYP','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON',NULL),(862171046084609,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'USER_MGMT','0Y8HM3WS8POG','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON',NULL),(862382467317778,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HOT1AO8OW','0Y8HOT1AO8P8','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862382501396561,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HOT1UYO00','0Y8HOT1UYO0D','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862382517649415,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HOT24N0U8','0Y8HOT24N0UK','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862382524465170,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HOT28P3WG','0Y8HOT28P3WT','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862382783987793,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HOT6J7KSH','0Y8HOT6J7KST','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862382793424903,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HOT6OTUKG','0Y8HOT6OTUKS','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862402490401254,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HP28FWEF5','0Y8HP28G7MYV','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(862433015496710,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'PROJECT_INFO_MGMT','0Y8HPG9A2DQD','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON',NULL),(862433015496716,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'CLIENT_MGMT','0Y8HPG9A2DQJ','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON',NULL),(862433015496730,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'API_MGMT','0Y8HPG9A2DQX','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON',NULL),(862433015496750,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'ROLE_MGMT','0Y8HPG9A2DRH','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON',NULL),(862433015496762,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'PERMISSION_MGMT','0Y8HPG9A2DRT','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON',NULL),(862433015496776,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'USER_MGMT','0Y8HPG9A2DS7','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON',NULL),(862446240661532,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPMBZOBNL','0Y8HPMBZOBO0','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469695209494,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX3VLG5D','0Y8HPX3VWOP1','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469705170971,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX41U70G','0Y8HPX41U70V','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469723521282,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX4CG9HD','0Y8HPX4CRI11','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469747114011,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX4QHY35','0Y8HPX4QHY3K','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469759172898,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX4XOEF5','0Y8HPX4XZMYT','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469786959899,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX5EJ7R4','0Y8HPX5EJ7RJ','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469790106430,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX5G3EGX','0Y8HPX5G3EHC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469893390369,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX75L5HQ','0Y8HPX75L5I5','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469944770935,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX806EPS','0Y8HPX806EQ8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862469958402064,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX88AKU8','0Y8HPX88AKUN','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470017646628,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX97VMRY','0Y8HPX97VMSD','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470037569568,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX9JFEV4','0Y8HPX9JFEVJ','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470049628187,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX9QX3WG','0Y8HPX9QX3WV','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470056968205,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPX9UZ6YO','0Y8HPX9UZ6Z4','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470864371746,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXN7ONI8','0Y8HPXN7ZW1S','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470882197538,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXNIAQ4W','0Y8HPXNIAQ5C','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470894780449,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXNPSF0H','0Y8HPXNQ3NK5','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470929907760,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXOB0JY8','0Y8HPXOB0JYN','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470953500941,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXOOQZUO','0Y8HPXOOQZV3','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470960316450,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXOST2WW','0Y8HPXOST2XC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470972899723,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXP0ARY8','0Y8HPXP0ARYN','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470984433698,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXP7601H','0Y8HPXP7601W','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862470999638326,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXPG7VNK','0Y8HPXPG7VNZ','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471005929733,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXPK9YPS','0Y8HPXPK9YQ7','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471118651787,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXRF2R4G','0Y8HPXRF2R4W','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471127040048,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXRKDS74','0Y8HPXRKDS7J','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471321026583,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXURKCS5','0Y8HPXURKCSK','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471360348188,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXVFADXC','0Y8HPXVFADXR','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471366640010,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXVIPZWH','0Y8HPXVJ18G5','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471375028266,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXVO114W','0Y8HPXVO115B','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471387087350,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXVUW934','0Y8HPXVUW93J','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471398097654,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXW1G8HS','0Y8HPXW1G8I7','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471406485537,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXW6G16P','0Y8HPXW6R9QM','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471423262752,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPXWGFMQ9','0Y8HPXWGQV45','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471787119009,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPY2HDK6D','0Y8HPY2HDK6S','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471789742108,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPY2IXQTD','0Y8HPY2IXQTS','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471831685160,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPY37WQ9T','0Y8HPY37WQA8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862471834304910,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPY395OG1','0Y8HPY39GWZP','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473203220878,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYPW6ARL','0Y8HPYPW6AS0','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473210561286,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYQ0UUWX','0Y8HPYQ0UUXC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473265087127,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYQXBJEP','0Y8HPYQXBJF4','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473282388444,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYR7MDH2','0Y8HPYR7MDHH','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473296544234,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYRG1S01','0Y8HPYRG1S0G','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473311224484,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYROSF7L','0Y8HPYROSF80','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473431286182,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYTNYJ81','0Y8HPYTO9RLX','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473437053403,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYTRPDKX','0Y8HPYTRPDLC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473456452059,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYU395OH','0Y8HPYU395OW','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473465365239,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYU8K6WW','0Y8HPYU8K6XC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473474802423,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYUE6GOW','0Y8HPYUE6GPC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473487909320,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYULZE9T','0Y8HPYULZEA8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473497346807,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYURLO1S','0Y8HPYURLO28','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473513599461,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYV0YSCH','0Y8HPYV1A0W6','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473524085168,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYV7IRR5','0Y8HPYV7IRRK','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473546105317,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYVKMQPI','0Y8HPYVKMQPX','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473552396675,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYVO2CJL','0Y8HPYVO2CK0','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473559736718,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYVSFO5D','0Y8HPYVSFO5S','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473566552451,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYVWHR7L','0Y8HPYVWSZR9','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473573892586,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYW16BCX','0Y8HPYW16BDC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473582281463,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYW6641S','0Y8HPYW66428','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473588572635,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYW9WYKH','0Y8HPYW9WYKW','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473596436847,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYWEAA69','0Y8HPYWEAA6O','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473629991613,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYWYKPHC','0Y8HPYWYKPHS','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473636283006,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYX20BGH','0Y8HPYX20BGW','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473720693505,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYYG9IWX','0Y8HPYYGKRGM','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473737995024,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPYYQVLDT','0Y8HPYYQVLE8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473909961102,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPZ1KY769','0Y8HPZ1L9FK6','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473918873998,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPZ1Q988X','0Y8HPZ1Q989C','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473933029774,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPZ1YOMWX','0Y8HPZ1YZVGL','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473939845601,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPZ231YIP','0Y8HPZ231YJ4','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473950331370,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPZ29APDT','0Y8HPZ29APE8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473959244256,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPZ2ELQM9','0Y8HPZ2ELQMO','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473971302798,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPZ2LGYKH','0Y8HPZ2LS745','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862473984410065,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HPZ2TL4OX','0Y8HPZ2TL4PC','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(862670893351306,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HSHJC34BK','0Y8HSHJC34BW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(863019435295462,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HWXNKYL8H','0Y8HWXNKYL8W','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(863019443159718,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HWXNPBWU9','0Y8HWXNPN5DX','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(863019449975539,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HWXNTP8G1','0Y8HWXNTP8GG','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(863019458364080,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8HWXNYP14W','0Y8HWXNYP15C','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(863019458364081,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8BPAEMD3B7','0Y8HHJ47NBE9','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(863019458364083,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8BPAEMD3B6','0Y8HHJ47NBE8','0P8HPG99R56P',_binary '\0',_binary '',NULL,'API',NULL),(863374104068935,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8I1GL5LA81','0Y8I1GL5LA8B','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(863374104068937,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8I1GL5LA82','0Y8I1GL5LA8C','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(863711892341119,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8I5RRK09HC','0Y8I5RRK09HV','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(864879848195042,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8IKOBERLKW','0Y8IKOBERLMT','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(865515703107956,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8ISSFAD4X7','0Y8ISSFAD4XQ','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(865516403032363,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8ISSQV2Y2O','0Y8ISSQV2Y3B','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(865708047073581,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8IV8SAOM4K','0Y8IV8SAZUO9','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(866012517892106,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8IZ4MZGPHG','0Y8IZ4MZRY10','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(866070308585771,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8IZV7FU1VO','0Y8IZV7G5AFH','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(866101805187427,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8J09OC3JAF','0Y8J09OCERRD','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(866793668739098,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8J93IHSKUG','0Y8J93IHSL5M','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(866794398024052,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8J93UKAWP0','0Y8J93UKAWPJ','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(866863787540493,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8J9ZGQNTOK','0Y8J9ZGQNTP3','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(874584270176308,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8M0IG77C3K','0Y8M0IG8RITC','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(874584730501120,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'SUB_REQ_MGMT','0Y8M0IOE6LFK','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON',NULL),(874584866291712,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8M0IQ9LUSY','0Y8M0IQAUSZ8','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(874584909807616,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8M0IQOWHG2','0Y8M0IQQ5FK0','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(874584915574790,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8M0IR0RI0W','0Y8M0IR20GBI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(874584956993536,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8M0IRC01KW','0Y8M0IRD8ZSN','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(874584966955013,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8M0IRLZMYO','0Y8M0IRN8L4W','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(874602266361904,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'SUB_REQ_MGMT','0Y8M0QQFTWD1','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON',NULL),(874905661866292,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8M4M3IY8ZK','0Y8M4M3J9HJ4','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(874924645810203,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8M4UTZLTKZ','0Y8M4UTZLTLI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(876266712990199,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8MLZDBR4SG','0Y8MLZDBR4T3','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(877686335996488,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8N43JAY7FR','0Y8N43JAY7GA','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(878324963868678,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8NC8X16N7K','0Y8NC8X16N81','0P8HE307W6IO',_binary '',_binary '',NULL,'API',NULL),(878324978548833,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8NC8XA8J0V','0Y8NC8XA8J1B','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(878818638692358,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8NIJPIFF28','0Y8NIJPIFF2U','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(878818653372424,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8NIJPRHATC','0Y8NIJPRHB1D','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(879037695656215,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8NLCCBLMGW','0Y8NLCCBLMHG','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(879716447813640,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8NU05MSQO0','0Y8NU05MSQOM','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(880043478745096,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8NY6E4KKCH','0Y8NY6E4KKD3','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(880043500240899,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8NY6EHDARV','0Y8NY6EHDATT','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(880043518066696,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8NY6ERZD37','0Y8NY6ESALN0','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(880043699994657,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'ADMIN_MGMT','0Y8NY6HSX6O0','0P8HE307W6IO',_binary '\0',_binary '','0P8HE307W6IO','COMMON',NULL),(880043708383267,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'ADMIN_MGMT','0Y8NY6HXWZCX','0P8HE307W6IO',_binary '\0',_binary '','0P8HPG99R56P','COMMON',NULL),(881764364976137,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OK4YFSUF8','0Y8OK4YFSUFS','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(881764375461897,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OK4YM1LAC','0Y8OK4YM1LAW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(881764384899158,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OK4YRZ3LS','0Y8OK4YRZ3MC','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(881764394336342,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OK4YXLDDS','0Y8OK4YXLDEC','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(881811145097230,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OKQG3H6V8','0Y8OKQG3SFF7','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(881811155582985,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OKQG9EP6O','0Y8OKQG9PXQM','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(881811160825927,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OKQGD5JPC','0Y8OKQGD5JPW','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(881811165020894,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OKQGFNG1S','0Y8OKQGFNG2C','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(881933147963401,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'PROJECT_INFO_MGMT','0Y8OMAHTGHZC','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON',NULL),(881933147963407,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'CLIENT_MGMT','0Y8OMAHTGHZI','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON',NULL),(881933147963415,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'API_MGMT','0Y8OMAHTGHZQ','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON',NULL),(881933147963423,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'ROLE_MGMT','0Y8OMAHTGHZY','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON',NULL),(881933147963431,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'PERMISSION_MGMT','0Y8OMAHTGI06','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON',NULL),(881933147963439,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'USER_MGMT','0Y8OMAHTGI0E','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON',NULL),(881933147963445,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'SUB_REQ_MGMT','0Y8OMAHTGI0K','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON',NULL),(881933147963447,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'ADMIN_MGMT','0Y8OMAHTGI0M','0P8HE307W6IO',_binary '\0',_binary '','0P8OMAHSU0W4','COMMON',NULL),(881933380747273,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OMALNFE7X','0Y8OMALNQMMI','0P8OMAHSU0W4',_binary '',_binary '',NULL,'API',NULL),(882926491271177,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OYYTVSIKG','0Y8OYY45NEVK','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(882926517485622,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0E8OYYUBPMCK','0Y8OYYUBPMEE','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(897248814694407,1711366300993,'SYSTEM',1711366300993,'SYSTEM',0,'0E8U1QEXOG03','0Y8U1QEXOG06','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(903844398628867,1723946378218,'SYSTEM',1723946378218,'SYSTEM',0,'0E8WDWDTQY2W','0Y8WDWDU26MC','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(903844436901893,1723946451692,'SYSTEM',1723946451692,'SYSTEM',0,'0E8WDWEGUI9T','0Y8WDWEGUI9W','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(903844477796369,1723946529680,'SYSTEM',1723946529680,'SYSTEM',0,'0E8WDWF570N5','0Y8WDWF570NA','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(903844592615424,1723946748006,'SYSTEM',1723946748006,'SYSTEM',0,'0E8WDWH18R2C','0Y8WDWH1JZLS','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(903844620926992,1723946802886,'SYSTEM',1723946802886,'SYSTEM',0,'0E8WDWHIQ1HD','0Y8WDWHIQ1HI','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(903844663394335,1723946883896,'SYSTEM',1723946883896,'SYSTEM',0,'0E8WDWI809HM','0Y8WDWI809HP','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(903844691181580,1723946936722,'SYSTEM',1723946936722,'SYSTEM',0,'0E8WDWIO8LQC','0Y8WDWIOJU9U','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(903844789747742,1723947124767,'SYSTEM',1723947124767,'SYSTEM',0,'0E8WDWKB8GE9','0Y8WDWKB8GEC','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(906294726230027,1728620007976,'SYSTEM',1728620007976,'SYSTEM',0,'0E8X961RUK1X','0Y8X961RUK20','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL),(906294754541577,1728620061951,'SYSTEM',1728620061951,'SYSTEM',0,'0E8X9628PDDV','0Y8X9628PDDY','0P8HE307W6IO',_binary '\0',_binary '',NULL,'API',NULL);
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
  `id` bigint NOT NULL,
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `name` varchar(255) NOT NULL,
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
INSERT INTO `project` VALUES (861542163677185,1689492851003,'0U8AZTODP4H0',1689492851003,'0U8AZTODP4H0',0,'main','0P8HE307W6IO'),(862433014972418,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,'MT-MALL','0P8HPG99R56P'),(881933146914821,1689492851003,'0U8OMAGVFMS3',1689492851003,'0U8OMAGVFMS3',0,'demo','0P8OMAHSU0W4');
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
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  `project_id` varchar(255) NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  `system_create` bit(1) NOT NULL,
  `tenant_id` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_113dky2ymmucbqsenqnaf6oxo` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (861812327186435,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0P8HE307W6IO',NULL,'0P8HE307W6IO','0Z8HHJ488SEC',_binary '','0P8HE307W6IO','PROJECT'),(861812327186436,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'CLIENT_ROOT',NULL,'0P8HE307W6IO','0Z8HHJ489SA0',_binary '',NULL,'CLIENT_ROOT'),(861812327186437,1689492851003,'NOT_HTTP',1689492851003,'0U8AZTODP4H0',0,NULL,'PROJECT_SUPER_ADMIN','0Z8HHJ488SEC','0P8HE307W6IO','0Z8HHJ489SEC',_binary '','0P8HE307W6IO','USER'),(861812327186439,1689492851003,'NOT_HTTP',1723947376509,'0U8AZTODP4H0',1,'DEFAULT_ROOT_USER','PROJECT_USER','0Z8HHJ488SEC','0P8HE307W6IO','0Z8HHJ489SEE',_binary '','0P8HE307W6IO','USER'),(861812327186440,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8AZTODP4HT','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SE0',_binary '',NULL,'CLIENT'),(861812327186441,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8AZTODP4H0','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SE1',_binary '',NULL,'CLIENT'),(861812327186443,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8AZTODP4H8','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SE9',_binary '',NULL,'CLIENT'),(861812327186444,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8AZTODP4HZ','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEA',_binary '',NULL,'CLIENT'),(861812327186445,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8AZYTQ5W5C','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEB',_binary '',NULL,'CLIENT'),(861812327186446,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8AZZ16LZB4','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEH',_binary '',NULL,'CLIENT'),(861812327186447,1689492851003,'NOT_HTTP',1689492851003,'0U8AZTODP4H0',0,NULL,'0C8B00098WLD','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SED',_binary '',NULL,'CLIENT'),(861812327186448,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8B00CSATJ6','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEF',_binary '',NULL,'CLIENT'),(861812327186449,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8AZTODP4I0','0Z8HHJ489SA0','0P8HE307W6IO','0Z8HHJ489SEG',_binary '',NULL,'CLIENT'),(862172156002538,1689492851003,'0U8AZTODP4H0',1728620095587,'0U8AZTODP4H0',2,'0P8HE307W6IO','PROJECT_ADMIN','0Z8HHJ488SEC','0P8HE307W6IO','0Z8HM4F4QV41',_binary '','0P8HE307W6IO','USER'),(862433016545286,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0P8HPG99R56P',NULL,'0P8HE307W6IO','0Z8HPG9AOUTG',_binary '','0P8HPG99R56P','PROJECT'),(862433016545288,1689492851003,'NOT_HTTP',1711366453237,'0U8AZTODP4H0',1,'0P8HPG99R56P','PROJECT_ADMIN','0Z8HPG9AOUTG','0P8HE307W6IO','0Z8HPG9AOUTJ',_binary '','0P8HPG99R56P','USER'),(862433016545290,1689492851003,'NOT_HTTP',1689492851003,'0U8HPG93IED3',0,NULL,'PROJECT_USER','0Z8HPG9AOUTH','0P8HPG99R56P','0Z8HPG9AOUTL',_binary '',NULL,'USER'),(862433016545292,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'CLIENT_ROOT',NULL,'0P8HPG99R56P','0Z8HPG9AOUTN',_binary '',NULL,'CLIENT_ROOT'),(862433016545293,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0P8HPG99R56P',NULL,'0P8HPG99R56P','0Z8HPG9AOUTH',_binary '',NULL,'PROJECT'),(862433369915464,1689492851003,'NOT_HTTP',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGF4GBUP','0Z8HPG9AOUTN','0P8HPG99R56P','0Z8HPGF4RKEA',_binary '',NULL,'CLIENT'),(862433781481554,1689492851003,'NOT_HTTP',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGLXHMET','0Z8HPG9AOUTN','0P8HPG99R56P','0Z8HPGLXHMF5',_binary '',NULL,'CLIENT'),(862433827094538,1689492851003,'NOT_HTTP',1689492851003,'0U8HPG93IED3',0,NULL,'0C8HPGMON9J5','0Z8HPG9AOUTN','0P8HPG99R56P','0Z8HPGMOYI2P',_binary '',NULL,'CLIENT'),(862486461939723,1689492851003,'0U8HPG93IED3',1689492851003,'0U8HPG93IED3',0,NULL,'商城管理员','0Z8HPG9AOUTH','0P8HPG99R56P','0Z8HQ4T6P535',_binary '\0',NULL,'USER'),(862524187082847,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8HQM52YN7K','0Z8HPG9AOUTN','0P8HPG99R56P','0Z8HQM52YN7W',_binary '',NULL,'CLIENT'),(863884656246846,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8I7Z4Q8N09','0Z8HHJ489SA0','0P8HE307W6IO','0Z8I7Z4Q8N4B',_binary '',NULL,'CLIENT'),(863884656771095,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8I7Z4QJVNC','0Z8HHJ489SA0','0P8HE307W6IO','0Z8I7Z4QV41J',_binary '',NULL,'CLIENT'),(881933150060553,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0P8OMAHSU0W4',NULL,'0P8HE307W6IO','0Z8OMAHUPG5J',_binary '','0P8OMAHSU0W4','PROJECT'),(881933151109427,1689492851003,'NOT_HTTP',1711366477910,'0U8AZTODP4H0',1,'0P8OMAHSU0W4','PROJECT_ADMIN','0Z8OMAHUPG5J','0P8HE307W6IO','0Z8OMAHUPG5M',_binary '','0P8OMAHSU0W4','USER'),(881933151109429,1689492851003,'NOT_HTTP',1689492851003,'0U8OMAGVFMS3',0,NULL,'PROJECT_USER','0Z8OMAHUPG5K','0P8OMAHSU0W4','0Z8OMAHVBXH0',_binary '',NULL,'USER'),(881933151109431,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'CLIENT_ROOT',NULL,'0P8OMAHSU0W4','0Z8OMAHVBXH2',_binary '',NULL,'CLIENT_ROOT'),(881933151109432,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0P8OMAHSU0W4',NULL,'0P8OMAHSU0W4','0Z8OMAHUPG5K',_binary '',NULL,'PROJECT'),(881933190430770,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8OMAII48XE','0Z8OMAHVBXH2','0P8OMAHSU0W4','0Z8OMAII49B6',_binary '',NULL,'CLIENT'),(881933421117440,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8OMAMBGNWG','0Z8OMAHVBXH2','0P8OMAHSU0W4','0Z8OMAMBGNWX',_binary '',NULL,'CLIENT'),(881933439468989,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8OMAMM2QDC','0Z8OMAHVBXH2','0P8OMAHSU0W4','0Z8OMAMMDYX5',_binary '',NULL,'CLIENT'),(881933452574720,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,NULL,'0C8OMAMTVNY8','0Z8OMAHVBXH2','0P8OMAHSU0W4','0Z8OMAMTVNYT',_binary '',NULL,'CLIENT');
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
INSERT INTO `role_api_permission_map` VALUES (861812327186437,'0Y8HHJ47NB00'),(861812327186437,'0Y8HHJ47NBDB'),(861812327186437,'0Y8HHJ47NBDK'),(861812327186437,'0Y8HHJ47NBDZ'),(861812327186437,'0Y8HHJ47NBE6'),(861812327186437,'0Y8HHJ47NBE7'),(861812327186437,'0Y8HHJ47NBEA'),(861812327186437,'0Y8HHJ47NBEB'),(861812327186437,'0Y8HHJ47NBES'),(861812327186437,'0Y8HHJ47NBEU'),(861812327186437,'0Y8HOT1AO8P8'),(861812327186437,'0Y8HOT1UYO0D'),(861812327186437,'0Y8HOT24N0UK'),(861812327186437,'0Y8HOT28P3WT'),(861812327186437,'0Y8HOT6J7KST'),(861812327186437,'0Y8HOT6OTUKS'),(861812327186437,'0Y8HP28G7MYV'),(861812327186437,'0Y8IKOBERLMT'),(861812327186437,'0Y8J09OCERRD'),(861812327186437,'0Y8J93UKAWPJ'),(861812327186437,'0Y8J9ZGQNTP3'),(861812327186437,'0Y8N43JAY7GA'),(861812327186437,'0Y8NU05MSQOM'),(861812327186437,'0Y8OYYUBPMEE'),(861812327186439,'0Y8HHJ47NBD5'),(861812327186439,'0Y8HHJ47NBDD'),(861812327186439,'0Y8HHJ47NBDE'),(861812327186439,'0Y8HHJ47NBE7'),(861812327186439,'0Y8HHJ47NBET'),(861812327186439,'0Y8HHJ47NBEZ'),(861812327186439,'0Y8HLUWOH92P'),(861812327186439,'0Y8I1GL5LA8B'),(861812327186439,'0Y8I1GL5LA8C'),(861812327186439,'0Y8I5RRK09HV'),(861812327186439,'0Y8ISSFAD4XQ'),(861812327186439,'0Y8ISSQV2Y3B'),(861812327186439,'0Y8IV8SAZUO9'),(861812327186439,'0Y8IZ4MZRY10'),(861812327186439,'0Y8J93IHSL5M'),(861812327186439,'0Y8NIJPIFF2U'),(861812327186439,'0Y8NIJPRHB1D'),(861812327186439,'0Y8NLCCBLMHG'),(861812327186439,'0Y8WDWDU26MC'),(861812327186439,'0Y8WDWEGUI9W'),(861812327186439,'0Y8WDWF570NA'),(861812327186439,'0Y8WDWH1JZLS'),(861812327186439,'0Y8WDWHIQ1HI'),(861812327186439,'0Y8WDWI809HP'),(861812327186439,'0Y8WDWIOJU9U'),(861812327186439,'0Y8WDWKB8GEC'),(861812327186447,'0Y8HHJ47NBD9'),(861812327186447,'0Y8HHJ47NBDG'),(861812327186447,'0Y8HHJ47NBDH'),(861812327186447,'0Y8HHJ47NBDI'),(862172156002538,'0Y8HHJ47NBD4'),(862172156002538,'0Y8HHJ47NBD6'),(862172156002538,'0Y8HHJ47NBD7'),(862172156002538,'0Y8HHJ47NBD8'),(862172156002538,'0Y8HHJ47NBDL'),(862172156002538,'0Y8HHJ47NBDM'),(862172156002538,'0Y8HHJ47NBDN'),(862172156002538,'0Y8HHJ47NBDO'),(862172156002538,'0Y8HHJ47NBDP'),(862172156002538,'0Y8HHJ47NBDS'),(862172156002538,'0Y8HHJ47NBDV'),(862172156002538,'0Y8HHJ47NBEV'),(862172156002538,'0Y8HHJ47NBEW'),(862172156002538,'0Y8HHJ47NBEX'),(862172156002538,'0Y8HHJ47NBEY'),(862172156002538,'0Y8HK4ZLA03Q'),(862172156002538,'0Y8HKACDVMDL'),(862172156002538,'0Y8HKE24FWUI'),(862172156002538,'0Y8HKE2QAIVF'),(862172156002538,'0Y8HKEMUH34B'),(862172156002538,'0Y8HLUWMX2BX'),(862172156002538,'0Y8HSHJC34BW'),(862172156002538,'0Y8M0IG8RITC'),(862172156002538,'0Y8M0IQAUSZ8'),(862172156002538,'0Y8M0IQQ5FK0'),(862172156002538,'0Y8M0IR20GBI'),(862172156002538,'0Y8M0IRD8ZSN'),(862172156002538,'0Y8M0IRN8L4W'),(862172156002538,'0Y8M4M3J9HJ4'),(862172156002538,'0Y8M4UTZLTLI'),(862172156002538,'0Y8MLZDBR4T3'),(862172156002538,'0Y8NY6E4KKD3'),(862172156002538,'0Y8NY6EHDATT'),(862172156002538,'0Y8NY6ESALN0'),(862172156002538,'0Y8OK4YFSUFS'),(862172156002538,'0Y8OK4YM1LAW'),(862172156002538,'0Y8OK4YRZ3MC'),(862172156002538,'0Y8OK4YXLDEC'),(862172156002538,'0Y8OKQG3SFF7'),(862172156002538,'0Y8OKQG9PXQM'),(862172156002538,'0Y8OKQGD5JPW'),(862172156002538,'0Y8OKQGFNG2C'),(862172156002538,'0Y8OYY45NEVK'),(862172156002538,'0Y8U1QEXOG06'),(862172156002538,'0Y8X961RUK20'),(862172156002538,'0Y8X9628PDDY'),(862433016545288,'0Y8HHJ47NBD4'),(862433016545288,'0Y8HHJ47NBD6'),(862433016545288,'0Y8HHJ47NBD7'),(862433016545288,'0Y8HHJ47NBD8'),(862433016545288,'0Y8HHJ47NBDL'),(862433016545288,'0Y8HHJ47NBDM'),(862433016545288,'0Y8HHJ47NBDN'),(862433016545288,'0Y8HHJ47NBDO'),(862433016545288,'0Y8HHJ47NBDP'),(862433016545288,'0Y8HHJ47NBDS'),(862433016545288,'0Y8HHJ47NBDV'),(862433016545288,'0Y8HHJ47NBEV'),(862433016545288,'0Y8HHJ47NBEW'),(862433016545288,'0Y8HHJ47NBEX'),(862433016545288,'0Y8HHJ47NBEY'),(862433016545288,'0Y8HK4ZLA03Q'),(862433016545288,'0Y8HKACDVMDL'),(862433016545288,'0Y8HKE24FWUI'),(862433016545288,'0Y8HKE2QAIVF'),(862433016545288,'0Y8HKEMUH34B'),(862433016545288,'0Y8HLUWMX2BX'),(862433016545288,'0Y8HSHJC34BW'),(862433016545288,'0Y8M0IG8RITC'),(862433016545288,'0Y8M0IQAUSZ8'),(862433016545288,'0Y8M0IQQ5FK0'),(862433016545288,'0Y8M0IR20GBI'),(862433016545288,'0Y8M0IRD8ZSN'),(862433016545288,'0Y8M0IRN8L4W'),(862433016545288,'0Y8M4M3J9HJ4'),(862433016545288,'0Y8M4UTZLTLI'),(862433016545288,'0Y8MLZDBR4T3'),(862433016545288,'0Y8NY6EHDATT'),(862433016545288,'0Y8NY6ESALN0'),(862433016545288,'0Y8OK4YFSUFS'),(862433016545288,'0Y8OK4YM1LAW'),(862433016545288,'0Y8OK4YRZ3MC'),(862433016545288,'0Y8OK4YXLDEC'),(862433016545288,'0Y8OKQG3SFF7'),(862433016545288,'0Y8OKQG9PXQM'),(862433016545288,'0Y8OKQGD5JPW'),(862433016545288,'0Y8OKQGFNG2C'),(862433016545288,'0Y8OYY45NEVK'),(862433016545288,'0Y8U1QEXOG06'),(862433016545288,'0Y8X961RUK20'),(862433016545288,'0Y8X9628PDDY'),(862433016545290,'0Y8HPMBZOBO0'),(862433016545290,'0Y8HPX4CRI11'),(862433016545290,'0Y8HPX4QHY3K'),(862433016545290,'0Y8HPX4XZMYT'),(862433016545290,'0Y8HPX5EJ7RJ'),(862433016545290,'0Y8HPX5G3EHC'),(862433016545290,'0Y8HPXNIAQ5C'),(862433016545290,'0Y8HPXNQ3NK5'),(862433016545290,'0Y8HPYTO9RLX'),(862433016545290,'0Y8HPYTRPDLC'),(862433016545290,'0Y8HPYU395OW'),(862433016545290,'0Y8HPYU8K6XC'),(862433016545290,'0Y8HPYUE6GPC'),(862433016545290,'0Y8HPYULZEA8'),(862433016545290,'0Y8HPYURLO28'),(862486461939723,'0Y8HHJ47NBE8'),(862486461939723,'0Y8HHJ47NBE9'),(862486461939723,'0Y8HPX3VWOP1'),(862486461939723,'0Y8HPX41U70V'),(862486461939723,'0Y8HPX75L5I5'),(862486461939723,'0Y8HPX806EQ8'),(862486461939723,'0Y8HPX88AKUN'),(862486461939723,'0Y8HPX97VMSD'),(862486461939723,'0Y8HPX9JFEVJ'),(862486461939723,'0Y8HPX9QX3WV'),(862486461939723,'0Y8HPX9UZ6Z4'),(862486461939723,'0Y8HPXOB0JYN'),(862486461939723,'0Y8HPXOOQZV3'),(862486461939723,'0Y8HPXOST2XC'),(862486461939723,'0Y8HPXP0ARYN'),(862486461939723,'0Y8HPXP7601W'),(862486461939723,'0Y8HPXPG7VNZ'),(862486461939723,'0Y8HPXPK9YQ7'),(862486461939723,'0Y8HPXURKCSK'),(862486461939723,'0Y8HPXVFADXR'),(862486461939723,'0Y8HPXVJ18G5'),(862486461939723,'0Y8HPXVO115B'),(862486461939723,'0Y8HPXVUW93J'),(862486461939723,'0Y8HPXW1G8I7'),(862486461939723,'0Y8HPXW6R9QM'),(862486461939723,'0Y8HPXWGQV45'),(862486461939723,'0Y8HPY2HDK6S'),(862486461939723,'0Y8HPY2IXQTS'),(862486461939723,'0Y8HPY37WQA8'),(862486461939723,'0Y8HPY39GWZP'),(862486461939723,'0Y8HPYPW6AS0'),(862486461939723,'0Y8HPYQ0UUXC'),(862486461939723,'0Y8HPYQXBJF4'),(862486461939723,'0Y8HPYVKMQPX'),(862486461939723,'0Y8HPYVO2CK0'),(862486461939723,'0Y8HPYVSFO5S'),(862486461939723,'0Y8HPYVWSZR9'),(862486461939723,'0Y8HPYW16BDC'),(862486461939723,'0Y8HPYW66428'),(862486461939723,'0Y8HPYW9WYKW'),(862486461939723,'0Y8HPYWEAA6O'),(862486461939723,'0Y8HPZ1L9FK6'),(862486461939723,'0Y8HPZ1Q989C'),(862486461939723,'0Y8HPZ1YZVGL'),(862486461939723,'0Y8HPZ231YJ4'),(862486461939723,'0Y8HPZ29APE8'),(862486461939723,'0Y8HPZ2ELQMO'),(862486461939723,'0Y8HPZ2LS745'),(862486461939723,'0Y8HPZ2TL4PC'),(862486461939723,'0Y8HWXNKYL8W'),(862486461939723,'0Y8HWXNPN5DX'),(862486461939723,'0Y8HWXNTP8GG'),(862486461939723,'0Y8HWXNYP15C'),(881933151109427,'0Y8HHJ47NBD4'),(881933151109427,'0Y8HHJ47NBD6'),(881933151109427,'0Y8HHJ47NBD7'),(881933151109427,'0Y8HHJ47NBD8'),(881933151109427,'0Y8HHJ47NBDL'),(881933151109427,'0Y8HHJ47NBDM'),(881933151109427,'0Y8HHJ47NBDN'),(881933151109427,'0Y8HHJ47NBDO'),(881933151109427,'0Y8HHJ47NBDP'),(881933151109427,'0Y8HHJ47NBDS'),(881933151109427,'0Y8HHJ47NBDV'),(881933151109427,'0Y8HHJ47NBEV'),(881933151109427,'0Y8HHJ47NBEW'),(881933151109427,'0Y8HHJ47NBEX'),(881933151109427,'0Y8HHJ47NBEY'),(881933151109427,'0Y8HK4ZLA03Q'),(881933151109427,'0Y8HKACDVMDL'),(881933151109427,'0Y8HKE24FWUI'),(881933151109427,'0Y8HKE2QAIVF'),(881933151109427,'0Y8HKEMUH34B'),(881933151109427,'0Y8HLUWMX2BX'),(881933151109427,'0Y8HSHJC34BW'),(881933151109427,'0Y8M0IG8RITC'),(881933151109427,'0Y8M0IQAUSZ8'),(881933151109427,'0Y8M0IQQ5FK0'),(881933151109427,'0Y8M0IR20GBI'),(881933151109427,'0Y8M0IRD8ZSN'),(881933151109427,'0Y8M0IRN8L4W'),(881933151109427,'0Y8M4M3J9HJ4'),(881933151109427,'0Y8M4UTZLTLI'),(881933151109427,'0Y8MLZDBR4T3'),(881933151109427,'0Y8NY6E4KKD3'),(881933151109427,'0Y8NY6EHDATT'),(881933151109427,'0Y8NY6ESALN0'),(881933151109427,'0Y8OK4YFSUFS'),(881933151109427,'0Y8OK4YM1LAW'),(881933151109427,'0Y8OK4YRZ3MC'),(881933151109427,'0Y8OK4YXLDEC'),(881933151109427,'0Y8OKQG3SFF7'),(881933151109427,'0Y8OKQG9PXQM'),(881933151109427,'0Y8OKQGD5JPW'),(881933151109427,'0Y8OKQGFNG2C'),(881933151109427,'0Y8OYY45NEVK'),(881933151109427,'0Y8U1QEXOG06'),(881933151109427,'0Y8X961RUK20'),(881933151109427,'0Y8X9628PDDY'),(881933151109429,'0Y8OMALNQMMI');
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
INSERT INTO `role_common_permission_map` VALUES (862172156002538,'0Y8HM3UAYUBK'),(862172156002538,'0Y8HM3VVGSN4'),(862172156002538,'0Y8HM3VXC7WG'),(862172156002538,'0Y8HM3VYWEM9'),(862172156002538,'0Y8HM3W1EAYP'),(862172156002538,'0Y8HM3WS8POG'),(862172156002538,'0Y8M0IOE6LFK'),(862172156002538,'0Y8NY6HSX6O0'),(862433016545288,'0Y8HPG9A2DQD'),(862433016545288,'0Y8HPG9A2DQJ'),(862433016545288,'0Y8HPG9A2DQX'),(862433016545288,'0Y8HPG9A2DRH'),(862433016545288,'0Y8HPG9A2DRT'),(862433016545288,'0Y8HPG9A2DS7'),(862433016545288,'0Y8M0QQFTWD1'),(862433016545288,'0Y8NY6HXWZCX'),(881933151109427,'0Y8OMAHTGHZC'),(881933151109427,'0Y8OMAHTGHZI'),(881933151109427,'0Y8OMAHTGHZQ'),(881933151109427,'0Y8OMAHTGHZY'),(881933151109427,'0Y8OMAHTGI06'),(881933151109427,'0Y8OMAHTGI0E'),(881933151109427,'0Y8OMAHTGI0K'),(881933151109427,'0Y8OMAHTGI0M');
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
  `domain_id` varchar(255) NOT NULL,
  `event_body` longtext,
  `internal` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `timestamp` bigint NOT NULL,
  `topic` varchar(255) NOT NULL,
  `send` bit(1) NOT NULL,
  `routable` bit(1) NOT NULL,
  `rejected` bit(1) NOT NULL,
  `application_id` varchar(255) NOT NULL,
  `trace_id` varchar(255) NOT NULL,
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
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
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
-- Table structure for table `temporary_code`
--

DROP TABLE IF EXISTS `temporary_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `temporary_code` (
  `id` bigint NOT NULL,
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `code` varchar(255) NOT NULL,
  `operation_type` varchar(255) NOT NULL,
  `domain_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjj1emkwcgmfwdbrftunxxaanh` (`domain_id`,`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `temporary_code`
--

LOCK TABLES `temporary_code` WRITE;
/*!40000 ALTER TABLE `temporary_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `temporary_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_`
--

DROP TABLE IF EXISTS `user_`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_` (
  `id` bigint NOT NULL,
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `locked` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `pwd_reset_code` varchar(255) DEFAULT NULL,
  `domain_id` varchar(255) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `country_code` varchar(255) DEFAULT NULL,
  `mobile_number` varchar(255) DEFAULT NULL,
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
INSERT INTO `user_` VALUES (838330249904129,1689492851003,'0U8AZTODP4H0',1689492851003,'ONBOARD_TENANT_USER',0,'admin@sample.com',_binary '\0','$2a$12$FJQlhRmF.ZXF5iv8fEPKD.wCwXJPEEhVLB3EAgX35KdMJc3UrkDh6',NULL,'0U8AZTODP4H0','超级管理员','86','12312312343',NULL,'ENGLISH','ca4f6ed2-8daa-4c31-a0b2-77d23e317f94','654321'),(862433005010948,1689492851003,'0C8B00098WLD',1689492851003,'ONBOARD_TENANT_USER',0,'mall@sample.com',_binary '\0','$2a$12$FJQlhRmF.ZXF5iv8fEPKD.wCwXJPEEhVLB3EAgX35KdMJc3UrkDh6',NULL,'0U8HPG93IED3',NULL,'86','12312312345',NULL,NULL,'848aefbc-b117-428f-967b-249c73f04f2a','654321'),(862531308486708,1689492851003,'0C8B00098WLD',1689492851003,'ONBOARD_TENANT_USER',0,'user1@sample.com',_binary '\0','$2a$12$FJQlhRmF.ZXF5iv8fEPKD.wCwXJPEEhVLB3EAgX35KdMJc3UrkDh6',NULL,'0U8HQPEV6A7K',NULL,'86','12312312345',NULL,NULL,'085e0f26-e376-44cd-91fa-8a64227308e4','654321'),(862531434315781,1689492851003,'0C8B00098WLD',1689492851003,'ONBOARD_TENANT_USER',0,'malluser@sample.com',_binary '\0','$2a$12$FJQlhRmF.ZXF5iv8fEPKD.wCwXJPEEhVLB3EAgX35KdMJc3UrkDh6',NULL,'0U8HQPGY38JL',NULL,'86','12312312345',NULL,NULL,'d05f0aa0-c19a-4047-b1d5-7ff750920feb','654321'),(881933091340295,1689492851003,'0C8B00098WLD',1689492851003,'ONBOARD_TENANT_USER',0,'demo@sample.com',_binary '\0','$2a$12$f1bMQikWEeoe2neA/xt7QOTmvCTUXfeBiOIu94byKaRsLIJQddSzK',NULL,'0U8OMAGVFMS3',NULL,'86','1231231234',NULL,NULL,'0672d1fc-8abb-4a0e-bf9a-fd9d246ec849','654321'),(903856535371779,1723969527361,'0U8WE1YK9XXC',1723969527361,'0U8WE1YK9XXC',0,NULL,_binary '\0',NULL,NULL,'0U8WE1YK9XXC',NULL,'86','12345678900',NULL,NULL,NULL,NULL),(903856546906122,1723969549687,'0U8WE1YR55VN',1723969549687,'0U8WE1YR55VN',0,'emailCode@sample.com',_binary '\0',NULL,NULL,'0U8WE1YR55VN',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(903856558964752,1723969572680,'0U8WE1YYBMDN',1723969572680,'0U8WE1YYBMDN',1,'mobileEmail@sample.com',_binary '\0',NULL,NULL,'0U8WE1YYBMDN',NULL,'86','12345678902',NULL,NULL,NULL,NULL),(903856582557702,1723969617833,'0U8WE1ZCDATF',1723969617833,'0U8WE1ZCDATF',1,NULL,_binary '\0',NULL,NULL,'0U8WE1ZCDATF','mobileUsername','86','12345678903',NULL,NULL,NULL,NULL),(903856600383494,1723969651898,'0U8WE1ZMZDA9',1723969651898,'0U8WE1ZMZDA9',0,NULL,_binary '\0','$2a$12$c0zJIAj2pqC9Ui2fAFTmw.TvgzC9fCIlKbUoATwFK2ycmsRhK2G96',NULL,'0U8WE1ZMZDA9',NULL,'86','12345678901',NULL,NULL,NULL,NULL),(903856614539269,1723969678407,'0U8WE1ZVERY8',1723969678407,'0U8WE1ZVERY8',1,'emailUsername@sample.com',_binary '\0',NULL,NULL,'0U8WE1ZVERY8','emailUsername',NULL,NULL,NULL,NULL,NULL,NULL),(903856630267907,1723969708246,'0U8WE204RW8W',1723969708246,'0U8WE204RW8W',0,'emailPwd@sample.com',_binary '\0','$2a$12$qFw84MoAvnM5Nq6kmxU85uiBNnJjjYfMYA9adKU9GaXrEni0hiA4u',NULL,'0U8WE204RW8W',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(903856641277957,1723969729577,'0U8WE20BBVNM',1723969729577,'0U8WE20BBVNM',0,NULL,_binary '\0','$2a$12$t.uNb58yXBD3YxQoBYbbPux0i.ubeC5ZhRBKDZ.hfAb7lmt2U538q',NULL,'0U8WE20BBVNM','usernamePwd',NULL,NULL,NULL,NULL,NULL,NULL),(903856651239429,1723969748479,'0U8WE20H9DZ4',1723969748479,'0U8WE20H9DZ4',2,'mobileEmailUsername@sample.com',_binary '\0',NULL,NULL,'0U8WE20H9DZ4','mobileEmailUsername','86','12345678905',NULL,NULL,NULL,NULL),(903856670638087,1723969785561,'0U8WE20ST62S',1723969785561,'0U8WE20ST62S',1,NULL,_binary '\0','$2a$12$sHIFPEwbcw1SNwtdwcZVTud9KBXUkmuvvWGJaIWHKRYiK.7X/IYqO',NULL,'0U8WE20ST62S','mobileUsernamePwd','86','12345678906',NULL,NULL,NULL,NULL),(903856685318156,1723969813247,'0U8WE2118KQR',1723969813247,'0U8WE2118KQR',2,'mobileEmailPwd@sample.com',_binary '\0','$2a$12$XLjMn1NjcmW0TkSbR2RlUuSPKUYZSfSLoSBrP7B9BIf0IDNeO/6oK',NULL,'0U8WE2118KQR',NULL,'86','12345678907',NULL,NULL,NULL,NULL),(903856735649795,1723969909536,'0U8WE21VILFK',1723969909536,'0U8WE21VILFK',1,'emailUsernamePwd@sample.com',_binary '\0','$2a$12$Ojc2vBTEvorYByH6dTEa9OueshDEaJiLVu.z3924SH8eZQVNClpwS',NULL,'0U8WE21VILFK','emailUsernamePwd',NULL,NULL,NULL,NULL,NULL,NULL),(903856756621318,1723969949904,'0U8WE228035V',1723969949904,'0U8WE228035V',2,'everything@sample.com',_binary '\0','$2a$12$CtJeBdAYWxSz/eMhM9fAEOR3U4PEfQNHs9ruSWAOc69JBqfCej.dm',NULL,'0U8WE228035V','everything','86','12345678908',NULL,NULL,NULL,NULL);
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
  `created_at` bigint NOT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_at` bigint NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `version` int NOT NULL,
  `project_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKaj74q84w36lmjs6ym17hjgdrm` (`user_id`,`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_relation`
--

LOCK TABLES `user_relation` WRITE;
/*!40000 ALTER TABLE `user_relation` DISABLE KEYS */;
INSERT INTO `user_relation` VALUES (861812327710759,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0P8HE307W6IO','0U8AZTODP4H0'),(862433005010945,1689492851003,'0C8B00098WLD',1689492851003,'NOT_HTTP',0,'0P8HE307W6IO','0U8HPG93IED3'),(862433017069645,1689492851003,'NOT_HTTP',1689492851003,'0U8HPG93IED3',0,'0P8HPG99R56P','0U8HPG93IED3'),(862525411819525,1689492851003,'ONBOARD_TENANT_USER',1689492851003,'ONBOARD_TENANT_USER',0,'0P8HPG99R56P','0U8AZTODP4H0'),(862531308486705,1689492851003,'0C8B00098WLD',1689492851003,'0C8B00098WLD',0,'0P8HE307W6IO','0U8HQPEV6A7K'),(862531311632420,1689492851003,'ONBOARD_TENANT_USER',1689492851003,'ONBOARD_TENANT_USER',0,'0P8HPG99R56P','0U8HQPEV6A7K'),(862531434315778,1689492851003,'0C8B00098WLD',1689492851003,'0C8B00098WLD',0,'0P8HE307W6IO','0U8HQPGY38JL'),(862531463151652,1689492851003,'ONBOARD_TENANT_USER',1689492851003,'0U8HPG93IED3',0,'0P8HPG99R56P','0U8HQPGY38JL'),(881933091340292,1689492851003,'0C8B00098WLD',1689492851003,'NOT_HTTP',0,'0P8HE307W6IO','0U8OMAGVFMS3'),(881933153206367,1689492851003,'NOT_HTTP',1689492851003,'NOT_HTTP',0,'0P8OMAHSU0W4','0U8OMAGVFMS3'),(881933801750540,1689492851003,'ONBOARD_TENANT_USER',1689492851003,'ONBOARD_TENANT_USER',0,'0P8OMAHSU0W4','0U8AZTODP4H0'),(903856535371778,1723969527253,'0U8WE1YK9XXC',1723969527253,'0U8WE1YK9XXC',0,'0P8HE307W6IO','0U8WE1YK9XXC'),(903856546906121,1723969549580,'0U8WE1YR55VN',1723969549580,'0U8WE1YR55VN',0,'0P8HE307W6IO','0U8WE1YR55VN'),(903856558964751,1723969572567,'0U8WE1YYBMDN',1723969572567,'0U8WE1YYBMDN',0,'0P8HE307W6IO','0U8WE1YYBMDN'),(903856582557701,1723969617719,'0U8WE1ZCDATF',1723969617719,'0U8WE1ZCDATF',2,'0P8HE307W6IO','0U8WE1ZCDATF'),(903856600383491,1723969651794,'0U8WE1ZMZDA9',1723969651794,'0U8WE1ZMZDA9',0,'0P8HE307W6IO','0U8WE1ZMZDA9'),(903856614539268,1723969678291,'0U8WE1ZVERY8',1723969678291,'0U8WE1ZVERY8',0,'0P8HE307W6IO','0U8WE1ZVERY8'),(903856630267906,1723969708130,'0U8WE204RW8W',1723969708130,'0U8WE204RW8W',0,'0P8HE307W6IO','0U8WE204RW8W'),(903856641277956,1723969729459,'0U8WE20BBVNM',1723969729459,'0U8WE20BBVNM',0,'0P8HE307W6IO','0U8WE20BBVNM'),(903856651239428,1723969748364,'0U8WE20H9DZ4',1723969748364,'0U8WE20H9DZ4',0,'0P8HE307W6IO','0U8WE20H9DZ4'),(903856670638086,1723969785450,'0U8WE20ST62S',1723969785450,'0U8WE20ST62S',0,'0P8HE307W6IO','0U8WE20ST62S'),(903856685318155,1723969813117,'0U8WE2118KQR',1723969813117,'0U8WE2118KQR',0,'0P8HE307W6IO','0U8WE2118KQR'),(903856735649794,1723969909410,'0U8WE21VILFK',1723969909410,'0U8WE21VILFK',0,'0P8HE307W6IO','0U8WE21VILFK'),(903856756621317,1723969949791,'0U8WE228035V',1723969949791,'0U8WE228035V',0,'0P8HE307W6IO','0U8WE228035V');
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
INSERT INTO `user_relation_role_map` VALUES (861812327710759,'0Z8HHJ489SEC'),(861812327710759,'0Z8HHJ489SEE'),(861812327710759,'0Z8HM4F4QV41'),(862433005010945,'0Z8HHJ489SEE'),(862433005010945,'0Z8HPG9AOUTJ'),(862433017069645,'0Z8HPG9AOUTL'),(862433017069645,'0Z8HQ4T6P535'),(862525411819525,'0Z8HPG9AOUTL'),(862531308486705,'0Z8HHJ489SEE'),(862531311632420,'0Z8HPG9AOUTL'),(862531434315778,'0Z8HHJ489SEE'),(862531463151652,'0Z8HPG9AOUTL'),(862531463151652,'0Z8HQ4T6P535'),(881933091340292,'0Z8HHJ489SEE'),(881933091340292,'0Z8OMAHUPG5M'),(881933153206367,'0Z8OMAHVBXH0'),(881933801750540,'0Z8OMAHVBXH0'),(903856535371778,'0Z8HHJ489SEE'),(903856546906121,'0Z8HHJ489SEE'),(903856558964751,'0Z8HHJ489SEE'),(903856582557701,'0Z8HHJ489SEE'),(903856600383491,'0Z8HHJ489SEE'),(903856614539268,'0Z8HHJ489SEE'),(903856630267906,'0Z8HHJ489SEE'),(903856641277956,'0Z8HHJ489SEE'),(903856651239428,'0Z8HHJ489SEE'),(903856670638086,'0Z8HHJ489SEE'),(903856685318155,'0Z8HHJ489SEE'),(903856735649794,'0Z8HHJ489SEE'),(903856756621317,'0Z8HHJ489SEE');
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
  `notify_admin` bit(1) DEFAULT NULL,
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

-- Dump completed on 2024-12-29 15:18:29
