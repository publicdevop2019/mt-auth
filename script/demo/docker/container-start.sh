echo "running start script"
ACCESS_ARGS="
--mt.common.url.lock=redis://localhost:6379
--mt.common.url.message-queue=localhost:5672
--mt.common.instance-id=0
--mt.mgmt.email=admin@sample.com
--eureka.client.serviceUrl.defaultZone=http://localhost:8080/eureka
--eureka.instance.ip-address=localhost
--spring.redis.host=localhost
--spring.redis.port=6379
--spring.datasource.url=jdbc:h2:tcp://localhost:9092/./demo
--spring.datasource.username=sa
--spring.datasource.password=
--spring.rabbitmq.port=5672
"
PROXY_ARG="
--eureka.client.serviceUrl.defaultZone=http://localhost:8080/eureka
--eureka.instance.ip-address=localhost
--mt.common.url.message-queue=localhost:5672
--mt.common.domain-name=
--mt.common.instance-id=1
--spring.redis.host=localhost
--spring.redis.port=6379
--spring.rabbitmq.port=5672
"
VM_ARGS="-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
# prepare h2 db data
java -cp h2*.jar org.h2.tools.RunScript -url "jdbc:h2:file:./demo;MODE=MySQL;IGNORECASE=TRUE" -user sa -script ./init.sql
# start nginx, rabbitmq, redis, db, app
nginx &
rabbitmq-server &
redis-server &
java -cp h2*.jar org.h2.tools.Server -tcpAllowOthers -webAllowOthers &
java $VM_ARGS -jar Access.jar $ACCESS_ARGS &
echo "sleeping start" &
# must sleep 150
sleep 150
echo "sleeping end" &
java $VM_ARGS -jar Proxy.jar $PROXY_ARG &
tail -f /dev/null