CREATE DATABASE queue;

CREATE TABLE `updatequeue` (
`id` BIGINT NOT NULL AUTO_INCREMENT,
`imsi` varchar(15) NOT NULL,
`imei` varchar(15) DEFAULT NULL,
`active` BOOLEAN DEFAULT NULL,
`location` varchar(15) DEFAULT NULL,
`ue_ambr_ul` bigint(20) unsigned DEFAULT '50000000',
`addedtime` datetime NOT NULL,
`retrievedtime` datetime DEFAULT NULL,
PRIMARY KEY(`id`)
);