cd ../../
echo 'sync git'
git checkout main
git fetch
git pull
echo 'prepare maven'
mkdir .m2
cp ./script/build/settings.xml .m2
echo 'create mt-common jar'
docker run -it --rm --name builder -v .m2:/root/.m2 -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven/mt-common maven:3.6.0-jdk-11 mvn clean install
echo 'create mt-access jar'
docker run -it --rm --name builder -v .m2:/root/.m2 -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven/mt-access maven:3.6.0-jdk-11 mvn clean package
echo 'create mt-access docker image'
# docker build -f mt-access/Dockerfile . -t mt-auth/mt-access:latest --no-cache