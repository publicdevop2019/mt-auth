echo '--------sync git--------'
#cd ~/mt-auth
#git checkout main
#git fetch
#git pull
echo '--------prepare maven--------'
mkdir -p $HOME/.m2
cp ~/mt-auth/script/build/settings.xml $HOME/.m2
echo '--------create mt-common jar--------'
docker run -it --rm -v ~/mt-auth:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-common maven:3.6.0-jdk-11 mvn dependency:go-offline -B clean install
echo '--------create mt-access jar--------'
docker run -it --rm -v ~/mt-auth:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-access maven:3.6.0-jdk-11 mvn dependency:go-offline -B clean package
echo '--------create mt-access docker image--------'
cd ~/mt-auth # required for docker build to work
docker build -f ./mt-access/Dockerfile . -t mt-auth/mt-access:latest --no-cache