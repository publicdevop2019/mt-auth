echo '--------sync git--------'
#cd ~/mt-auth
#git checkout main
#git fetch
#git pull
echo '--------prepare maven--------'
mkdir $HOME/.m2
cp ~/mt-auth/script/build/settings.xml $HOME/.m2
echo '--------create mt-proxy jar--------'
docker run -it --rm -v ~/mt-auth:/usr/src/temp -v "$HOME/.m2":/root/.m2 -w /usr/src/temp/mt-proxy maven:3.6.3-jdk-11 mvn dependency:go-offline -B clean package
echo '--------create mt-proxy docker image--------'
docker build -f ~/mt-auth/mt-proxy/Dockerfile . -t mt-auth/mt-proxy:latest --no-cache