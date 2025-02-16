# enter username and password for docker hub
docker login
# run below to use different builder
if docker buildx ls | grep -q "my-builder"; then
    docker buildx rm my-builder
fi
docker buildx create --name my-builder
docker buildx use my-builder
# build access and proxy jar
APP_ROOT=~/mt-auth
cd $APP_ROOT
# update POM to include h2
sed -i.bak 's|<!-- h2_placeholder -->|<dependency><groupId>com.h2database</groupId><artifactId>h2</artifactId><version>2.3.232</version><scope>runtime</scope></dependency>|' ./mt-access/pom.xml
rm ./mt-access/pom.xml.bak
# generate jks file
keytool -genkeypair -alias svcAsyKey -keyalg RSA -keysize 2048 -keystore svcAsyKey.jks -keypass localdev -storepass localdev -dname "CN=localdev, OU=localdev, O=localdev, L=localdev, S=localdev, C=localdev"
mv svcAsyKey.jks ./mt-access/src/main/resources/keystore/svcAsyKey.jks
# build jar
docker run -it --rm -v $APP_ROOT:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-common maven:3.6.3-jdk-11 mvn dependency:go-offline -B clean install
docker run -it --rm -v $APP_ROOT:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-access maven:3.6.3-jdk-11 mvn dependency:go-offline -B clean package
docker run -it --rm -v $APP_ROOT:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-proxy maven:3.6.3-jdk-11 mvn dependency:go-offline -B clean package
# build ui
docker run -it --rm -v $APP_ROOT:/usr/src/temp -w /usr/src/temp/mt-ui node:14.15.1-alpine3.12 sh -c "npm install -g npm@8.1.1; npm ci; npm run build:demo"
# use docker buildx to multi arch images
docker buildx build -f ./script/demo/docker/Dockerfile . --platform linux/arm64/v8,linux/amd64 --tag publicdevop2019/mt-auth:latest --no-cache --push