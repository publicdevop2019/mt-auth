# Convert Mysql script to H2 compatible script
1. change table order (cors_profile, cache_profile, permission)
2. bit(1) to tinyint
3. _binary '' to 1
4. _binary '\0' to 0
5. remove LOCK TABLES `validation_result` WRITE; (^LOCK TABLES.*$)
6. remove UNLOCK TABLES;

