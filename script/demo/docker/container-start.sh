echo "running start script"
ACCESS_ARGS="
--mt.common.url.lock=redis://localhost:6379
--mt.common.url.proxy=http://localhost:8111
--mt.common.url.message-queue=localhost:5672
--mt.common.instance-id=0
--mt.mgmt.email=admin@sample.com
--mt.feature.oauth.jwt.password=localdev
--spring.redis.host=localhost
--spring.redis.port=6379
--spring.datasource.url=jdbc:h2:tcp://localhost:9092/./demo
--spring.datasource.username=sa
--spring.datasource.password=
"
PROXY_ARG="
--mt.common.url.access=http://localhost:8080
--mt.common.url.message-queue=localhost:5672
--mt.common.domain-name=
--mt.common.instance-id=1
--spring.redis.host=localhost
--spring.redis.port=6379
"
VM_ARGS="-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
# prepare h2 db data
java -cp h2*.jar org.h2.tools.RunScript -url "jdbc:h2:file:./demo;MODE=MySQL;IGNORECASE=TRUE" -user sa -script ./init.sql
# start nginx, rabbitmq, redis, db, app
nginx &
rabbitmq-server &
redis-server &
java -cp h2*.jar org.h2.tools.Server -tcpAllowOthers -webAllowOthers &
java $VM_ARGS -jar access.jar $ACCESS_ARGS &
echo "sleeping start" &
# required to sleep to wait for app ready to connect
sleep 10 &
echo "sleeping end" &
java $VM_ARGS -jar proxy.jar $PROXY_ARG &
tail -f /dev/null