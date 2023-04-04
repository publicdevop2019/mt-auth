# ---- start of properties-----
# current server ip
INSTANCE_IP=
# redis and rabbitmq ip
INFRA_IP=
# domain name
DOMAIN_NAME=
# eureka registry address, pls add all mt-access instance ip address
REGISTRY_LIST="http://$INSTANCE_IP:8080/eureka,http://$OTHER_MT_ACCESS_IP:8080/eureka,http://$OTHER_MT_ACCESS_IP:8080/eureka"
# ---- end of properties-----
REGISTRY_CONFIG="--eureka.client.serviceUrl.defaultZone=$REGISTRY_LIST --eureka.instance.ip-address=$INSTANCE_IP"
INFRA_CONFIG="--mt.url.support.mq=$INFRA_IP --spring.redis.host=$INFRA_IP --spring.redis.port=6379"
DOMAIN_CONFIG="--manytree.domain-name=$DOMAIN_NAME"
DOCKER_CONFIG="-td --rm -v $PWD/./logs/proxy:/logs -p 8111:8111 --log-opt max-size=10m --log-opt max-file=5"
VM_CONFIG="-Xmx500m -Xms500m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/logs/$(date +"%Y_%m_%d_%I_%M_%p%z")_heapdump.hprof  -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
IMAGE_CONFIG="--name proxy mt-auth/mt-proxy:latest $VM_CONFIG -jar Proxy.jar"
docker run $DOCKER_CONFIG $IMAGE_CONFIG $REGISTRY_CONFIG $INFRA_CONFIG $DOMAIN_CONFIG