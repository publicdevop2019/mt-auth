echo '--------sync git--------'
#cd ~/mt-auth
#git checkout main
#git fetch
#git pull
echo '--------prepare maven--------'
mkdir -p $HOME/.m2
cp ~/mt-auth/script/build/settings.xml $HOME/.m2
echo '--------create sample-spring-boot jar--------'
docker run -it --rm -v ~/mt-auth:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-sample/spring-boot maven:3.6.0-jdk-11 mvn dependency:go-offline -B clean package
echo '--------create mt-sample/spring-boot docker image--------'
cd ~/mt-auth # required for docker build to work
docker build -f ~/mt-auth/mt-sample/spring-boot/Dockerfile . -t mt-auth/mt-sample-spring-boot:latest --no-cache