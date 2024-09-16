APP_ROOT=~/mt-auth
# APP_ROOT=~/Documents/Apps/Public/mt-auth
cd $APP_ROOT
# build access and proxy jar
# update POM to include h2
sed -i.bak 's|<!-- h2_placeholder -->|<dependency><groupId>com.h2database</groupId><artifactId>h2</artifactId><version>2.3.232</version><scope>runtime</scope></dependency>|' ./mt-access/pom.xml
rm ./mt-access/pom.xml.bak
docker run -it --rm -v $APP_ROOT:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-common maven:3.6.3-jdk-11 mvn dependency:go-offline -B clean install
docker run -it --rm -v $APP_ROOT:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-access maven:3.6.3-jdk-11 mvn dependency:go-offline -B clean package
docker run -it --rm -v $APP_ROOT:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-proxy maven:3.6.3-jdk-11 mvn dependency:go-offline -B clean package
# build ui
docker run -it --rm -v $APP_ROOT:/usr/src/temp -w /usr/src/temp/mt-ui node:14.15.1-alpine3.12 sh -c "npm install -g npm@8.1.1; npm ci; npm run demo"
# build docker image
docker build -f ./script/demo/docker/Dockerfile . -t publicdevop2019/mt-auth:latest --no-cache
# docker login then run below
# docker push publicdevop2019/mt-auth:latest
