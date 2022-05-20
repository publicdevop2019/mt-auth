# ----配置 开始-----
# 节点ID，每一个节点应有唯一ID，最大值为64
INSTANCE_ID=
# 管理员邮箱
ADMIN_EMAIL=
# 邮件通知账户名
EMAIL_ID=
# 邮件通知密码
EMAIL_PWD=
# MySQL IP
DATABASE_IP=
# MySQL 用户名
DATABASE_USERNAME=
# MySQL 密码
DATABASE_PWD=
# 当前节点IP
INSTANCE_IP=
# Redis与RabbitMQ IP
INFRA_IP=
# Eureka注册地址, 请添加所有mt-access节点IP地址
REGISTRY_LIST="http://$INSTANCE_IP:8080/eureka,http://$OTHER_MT_ACCESS_IP:8080/eureka,http://$OTHER_MT_ACCESS_IP:8080/eureka"
# ----配置 结束-----
DB_CONFIG="--aws-instance-uri=$DATABASE_IP --spring.datasource.username=$DATABASE_USERNAME --spring.datasource.password=$DATABASE_PWD"
LOG_CONFIG='--logging.level.com.mt.common.domain.model.domain_event=DEBUG -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector'
REGISTRY_CONFIG="--eureka.client.serviceUrl.defaultZone=$REGISTRY_LIST --eureka.instance.ip-address=$INSTANCE_IP"
INFRA_CONFIG="--mt.url.support.dis_lock=redis://$INFRA_IP:6378 --mt.url.support.mq=$INFRA_IP --spring.redis.host=$INFRA_IP --spring.redis.port=6379 --instanceId=$INSTANCE_ID"
MAIL_CONFIG="--spring.mail.username=$EMAIL_ID --spring.mail.password=$EMAIL_PWD --mt.email.admin=$ADMIN_EMAIL"
PROFILE=--spring.profiles.active=prod
VM_CONFIG='-Xmx500m -Xms500m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/logs/$(date +"%Y_%m_%d_%I_%M_%p%z")_heapdump.hprof'
SERVER_CONFIG=--server.tomcat.accept-count=100\ --server.tomcat.max-connections=10000\ --server.tomcat.max-threads=10
DOCKER_CONFIG="-td --rm -v $PWD/./logs/access:/logs -p 8080:8080 --log-opt max-size=10m --log-opt max-file=5"
IMAGE_CONFIG='--name access publicdevop2019/mt-access:latest -jar Access.jar'
docker run $DOCKER_CONFIG $IMAGE_CONFIG $SERVER_CONFIG $DB_CONFIG $REGISTRY_CONFIG $LOG_CONFIG $INFRA_CONFIG $PROFILE $MAIL_CONFIG $VM_CONFIG