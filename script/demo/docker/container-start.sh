echo "running start script"
ACCESS_ARGS="
--mt.redis.url=redis://localhost:6379
--mt.rabbitmq.url=localhost:5672
--mt.jwt.password=localdev
--mt.misc.url.proxy=http://localhost:8111
--mt.misc.instance-id=0
--mt.misc.mgmt-email=admin@sample.com
--spring.datasource.url=jdbc:h2:tcp://localhost:9092/./demo
--spring.datasource.username=sa
--spring.datasource.password=
"
PROXY_ARG="
--mt.rabbitmq.url=localhost:5672
--mt.redis.url=redis://localhost:6379
--mt.misc.domain=
--mt.misc.instance-id=1
--mt.misc.url.access=http://localhost:8080
"
# prepare h2 db data
java -cp h2*.jar org.h2.tools.RunScript -url "jdbc:h2:file:./demo;MODE=MySQL;IGNORECASE=TRUE" -user sa -script ./init.sql
# start nginx, rabbitmq, redis, db, app
nginx &
rabbitmq-server &
redis-server &
java -cp h2*.jar org.h2.tools.Server -tcpAllowOthers -webAllowOthers &
sleep 10
# required to sleep to wait for app ready to connect
java -jar access.jar $ACCESS_ARGS &
echo "sleeping start"
# required to sleep to wait for app ready to connect
sleep 10
echo "sleeping end"
java -jar proxy.jar $PROXY_ARG &
tail -f /dev/null