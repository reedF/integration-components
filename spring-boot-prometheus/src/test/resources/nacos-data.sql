# Host: localhost  (Version 5.7.21-log)
# Date: 2019-03-13 10:39:41
# Generator: MySQL-Front 6.1  (Build 1.21)


#
# Data for table "config_info"
#

INSERT INTO `config_info` (`id`,`data_id`,`group_id`,`content`,`md5`,`gmt_create`,`gmt_modified`,`src_user`,`src_ip`,`app_name`,`tenant_id`,`c_desc`,`c_use`,`effect`,`type`,`c_schema`) VALUES (1,'test','g1','test.nacos.item1=89466\r\ntest.nacos.item2=ssrr55r\r\ntest.nacos.test3=true\r\ntest.nacos.test4=a1,a2,a','8cdd6654ee73ea4bc2067c4a7e957b41','2019-03-12 14:41:21','2019-03-12 16:24:38',NULL,'127.0.0.1','','','',NULL,NULL,'properties',NULL),(7,'test','g1','test.nacos.item1=805\r\ntest.nacos.item2=sstyyyy\r\ntest.nacos.test3=true\r\ntest.nacos.test4=a1,a2,3','9f5e0c70a80a6c600bf80f2e83829648','2019-03-12 16:43:54','2019-03-13 10:19:37',NULL,'127.0.0.1','','84009fe1-0064-4fa5-9ac1-f214cf2338f6','',NULL,NULL,'properties',NULL);

#
# Data for table "tenant_info"
#

INSERT INTO `tenant_info` (`id`,`kp`,`tenant_id`,`tenant_name`,`tenant_desc`,`create_source`,`gmt_create`,`gmt_modified`) VALUES (1,'1','84009fe1-0064-4fa5-9ac1-f214cf2338f6','dev','dev','nacos',1552378786072,1552378786072);
