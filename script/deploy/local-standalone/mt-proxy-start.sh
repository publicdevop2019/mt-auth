# ----配置 开始-----
# 当前节点IP
INSTANCE_IP=127.0.0.1
# mt-access IP
MT_ACCESS_IP=127.0.0.1
# Redis与RabbitMQ IP
INFRA_IP=127.0.0.1
# ----配置 结束-----
REGISTRY_LIST="http://$MT_ACCESS_IP:8080/eureka"
LOG_CONFIG=-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
REGISTRY_CONFIG="--eureka.client.serviceUrl.defaultZone=$REGISTRY_LIST --eureka.instance.ip-address=$INSTANCE_IP"
INFRA_CONFIG="--mt.url.support.dis_lock=redis://$INFRA_IP:6378 --mt.url.support.mq=$INFRA_IP --spring.redis.host=$INFRA_IP --spring.redis.port=6379"
DOCKER_CONFIG='-td --rm -v ~/logs/proxy:/logs -p 8111:8111 --log-opt max-size=10m --log-opt max-file=5'
IMAGE_CONFIG='--name proxy publicdevop2019/mt-proxy:latest -jar Proxy.jar'
VM_CONFIG='-Xmx500m -Xms500m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/logs/$(date +"%Y_%m_%d_%I_%M_%p%z")_heapdump.hprof'
docker run $DOCKER_CONFIG $IMAGE_CONFIG $LOG_CONFIG $REGISTRY_CONFIG $INFRA_CONFIG $VM_CONFIG