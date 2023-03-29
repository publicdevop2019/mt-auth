# ---- start of properties-----
# instance id, every instance has one uniue id, max is 64
INSTANCE_ID=
# email which application send notification to
ADMIN_EMAIL=
# email username used for notification feature
EMAIL_ID=
# email password used for notification feature
EMAIL_PWD=
# mysql ip
DATABASE_IP=127.0.0.1
# mysql username
DATABASE_USERNAME=
# mysql password
DATABASE_PWD=
# current server ip
INSTANCE_IP=127.0.0.1
# redis and rabbitmq ip
INFRA_IP=127.0.0.1
# eureka registry address, pls add all mt-access instance ip address
REGISTRY_LIST="http://$INSTANCE_IP:8080/eureka"
# ---- end of properties-----
DB_CONFIG="--aws-instance-uri=$DATABASE_IP --spring.datasource.username=$DATABASE_USERNAME --spring.datasource.password=$DATABASE_PWD"
LOG_CONFIG='--logging.level.com.mt.common.domain.model.domain_event=DEBUG'
REGISTRY_CONFIG="--eureka.client.serviceUrl.defaultZone=$REGISTRY_LIST --eureka.instance.ip-address=$INSTANCE_IP"
INFRA_CONFIG="--mt.url.support.dis_lock=redis://$INFRA_IP:6378 --mt.url.support.mq=$INFRA_IP --spring.redis.host=$INFRA_IP --spring.redis.port=6379 --instanceId=$INSTANCE_ID"
MAIL_CONFIG="--spring.mail.username=$EMAIL_ID --spring.mail.password=$EMAIL_PWD --mt.email.admin=$ADMIN_EMAIL"
PROFILE=--spring.profiles.active=prod
VM_CONFIG="-Xmx2g -Xms2g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/logs/$(date +"%Y_%m_%d_%I_%M_%p%z")_heapdump.hprof -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
SERVER_CONFIG=--server.tomcat.accept-count=100\ --server.tomcat.max-connections=10000\ --server.tomcat.max-threads=10
DOCKER_CONFIG="-td --rm -v $PWD/./logs/access:/logs -p 8080:8080 --log-opt max-size=10m --log-opt max-file=5"
IMAGE_CONFIG="--name access mt-auth/mt-access:latest $VM_CONFIG -jar Access.jar"
docker run $DOCKER_CONFIG $IMAGE_CONFIG $SERVER_CONFIG $DB_CONFIG $REGISTRY_CONFIG $LOG_CONFIG $INFRA_CONFIG $PROFILE $MAIL_CONFIG