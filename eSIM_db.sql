-- MySQL dump 10.13  Distrib 5.7.27, for Linux (x86_64)
--
-- Host: localhost    Database: oai_db
-- ------------------------------------------------------
-- Server version	5.7.27-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `mmeidentity`
--

DROP TABLE IF EXISTS `mmeidentity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mmeidentity` (
  `idmmeidentity` int(11) NOT NULL AUTO_INCREMENT,
  `mmehost` varchar(255) DEFAULT NULL,
  `mmerealm` varchar(200) DEFAULT NULL,
  `UE-Reachability` tinyint(1) NOT NULL COMMENT 'Indicates whether the MME supports UE Reachability Notifcation',
  PRIMARY KEY (`idmmeidentity`)
) ENGINE=MyISAM AUTO_INCREMENT=46 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mmeidentity`
--

LOCK TABLES `mmeidentity` WRITE;
/*!40000 ALTER TABLE `mmeidentity` DISABLE KEYS */;
INSERT INTO `mmeidentity` 
(`idmmeidentity`, `mmehost`, `mmerealm`, `UE-Reachability`) VALUES 
(1,'mme.OpenAir5G.Alliance','OpenAir5G.Alliance',0);
/*!40000 ALTER TABLE `mmeidentity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pdn`
--

DROP TABLE IF EXISTS `pdn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pdn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `apn` varchar(60) NOT NULL,
  `pdn_type` enum('IPv4','IPv6','IPv4v6','IPv4_or_IPv6') NOT NULL DEFAULT 'IPv4',
  `pdn_ipv4` varchar(15) DEFAULT '0.0.0.0',
  `pdn_ipv6` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_ci DEFAULT '0:0:0:0:0:0:0:0',
  `aggregate_ambr_ul` int(10) unsigned DEFAULT '50000000',
  `aggregate_ambr_dl` int(10) unsigned DEFAULT '100000000',
  `pgw_id` int(11) NOT NULL,
  `users_imsi` varchar(15) NOT NULL,
  `qci` tinyint(3) unsigned NOT NULL DEFAULT '9',
  `priority_level` tinyint(3) unsigned NOT NULL DEFAULT '15',
  `pre_emp_cap` enum('ENABLED','DISABLED') DEFAULT 'DISABLED',
  `pre_emp_vul` enum('ENABLED','DISABLED') DEFAULT 'DISABLED',
  `LIPA-Permissions` enum('LIPA-prohibited','LIPA-only','LIPA-conditional') NOT NULL DEFAULT 'LIPA-only',
  PRIMARY KEY (`id`,`pgw_id`,`users_imsi`),
  KEY `fk_pdn_pgw1_idx` (`pgw_id`),
  KEY `fk_pdn_users1_idx` (`users_imsi`)
) ENGINE=MyISAM AUTO_INCREMENT=60 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pdn`
--

LOCK TABLES `pdn` WRITE;
/*!40000 ALTER TABLE `pdn` DISABLE KEYS */;
INSERT INTO `pdn` 
(`id`, `apn`, `pdn_type`, `pdn_ipv4`, `pdn_ipv6`, `aggregate_ambr_ul`, `aggregate_ambr_dl`, `pgw_id`, `users_imsi`, `qci`, `priority_level`, `pre_emp_cap`, `pre_emp_vul`, `LIPA-Permissions`) VALUES 
(45,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001111',9,15,'DISABLED','ENABLED','LIPA-only'),
(22,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001100',9,15,'DISABLED','ENABLED','LIPA-only'),
(23,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001101',9,15,'DISABLED','ENABLED','LIPA-only'),
(24,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001102',9,15,'DISABLED','ENABLED','LIPA-only'),
(25,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001103',9,15,'DISABLED','ENABLED','LIPA-only'),
(26,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001104',9,15,'DISABLED','ENABLED','LIPA-only'),
(27,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001105',9,15,'DISABLED','ENABLED','LIPA-only'),
(28,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001106',9,15,'DISABLED','ENABLED','LIPA-only'),
(29,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001107',9,15,'DISABLED','ENABLED','LIPA-only'),
(30,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001108',9,15,'DISABLED','ENABLED','LIPA-only'),
(31,'ltebox','IPv4','0.0.0.0','0:0:0:0:0:0:0:0',50000000,100000000,3,'208920100001109',9,15,'DISABLED','ENABLED','LIPA-only');
/*!40000 ALTER TABLE `pdn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `imsi` varchar(15) NOT NULL COMMENT 'IMSI is the main reference key.',
  `msisdn` varchar(46) DEFAULT NULL COMMENT 'The basic MSISDN of the UE (Presence of MSISDN is optional).',
  `imei` varchar(15) DEFAULT NULL COMMENT 'International Mobile Equipment Identity',
  `active` BOOLEAN NOT NULL DEFAULT 1 COMMENT 'Activeness of this IMSI',
  `location` varchar(15) DEFAULT '90024' COMMENT 'Location of this IMSI',
  `imei_sv` varchar(2) DEFAULT NULL COMMENT 'International Mobile Equipment Identity Software Version Number',
  `ms_ps_status` enum('PURGED','NOT_PURGED') DEFAULT 'PURGED' COMMENT 'Indicates that ESM and EMM status are purged from MME',
  `rau_tau_timer` int(10) unsigned DEFAULT '120',
  `ue_ambr_ul` bigint(20) unsigned DEFAULT '50000000' COMMENT 'The Maximum Aggregated uplink MBRs to be shared across all Non-GBR bearers according to the subscription of the user.',
  `ue_ambr_dl` bigint(20) unsigned DEFAULT '100000000' COMMENT 'The Maximum Aggregated downlink MBRs to be shared across all Non-GBR bearers according to the subscription of the user.',
  `access_restriction` int(10) unsigned DEFAULT '60' COMMENT 'Indicates the access restriction subscription information. 3GPP TS.29272 #7.3.31',
  `mme_cap` int(10) unsigned zerofill DEFAULT NULL COMMENT 'Indicates the capabilities of the MME with respect to core functionality e.g. regional access restrictions.',
  `mmeidentity_idmmeidentity` int(11) NOT NULL DEFAULT '0',
  `key` varbinary(16) NOT NULL DEFAULT '0' COMMENT 'UE security key',
  `RFSP-Index` smallint(5) unsigned NOT NULL DEFAULT '1' COMMENT 'An index to specific RRM configuration in the E-UTRAN. Possible values from 1 to 256',
  `urrp_mme` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'UE Reachability Request Parameter indicating that UE activity notification from MME has been requested by the HSS.',
  `sqn` bigint(20) unsigned zerofill NOT NULL,
  `rand` varbinary(16) NOT NULL,
  `OPc` varbinary(16) DEFAULT NULL COMMENT 'Can be computed by HSS',
  PRIMARY KEY (`imsi`,`mmeidentity_idmmeidentity`),
  KEY `fk_users_mmeidentity_idx1` (`mmeidentity_idmmeidentity`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` 
(`imsi`, `msisdn`, `imei`, `imei_sv`, `ms_ps_status`, `rau_tau_timer`, `ue_ambr_ul`, `ue_ambr_dl`, `access_restriction`, `mme_cap`, `mmeidentity_idmmeidentity`, `key`, `RFSP-Index`, `urrp_mme`, `sqn`, `rand`, `OPc`) VALUES 
('208920100001111','33600101789','35609204079298',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001100','33638020000','35609204079200',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001101','33638020001','35609204079201',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001102','33638020002','35609204079202',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001103','33638020003','35609204079203',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001104','33638020004','35609204079204',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001105','33638020005','35609204079205',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001106','33638020006','35609204079206',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001107','33638020007','35609204079207',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001108','33638020008','35609204079208',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F),
('208920100001109','33638020009','35609204079209',NULL,'PURGED',120,50000000,100000000,47,0000000000,1,0x6874736969202073796D4B2079650A73,1,0,00000000000000000351,0x00,0x504F20634F6320504F50206363500A4F);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-18  8:34:45
