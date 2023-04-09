DOCKER_CONFIG="-td --rm -v $PWD/./logs/sample:/logs -p 8083:8083 --log-opt max-size=10m --log-opt max-file=5"
IMAGE_CONFIG="--name sample-spring-boot mt-auth/mt-sample-spring-boot:latest -Xmx500m -Xms500m -jar sample-spring-boot.jar"
docker run $DOCKER_CONFIG $IMAGE_CONFIG