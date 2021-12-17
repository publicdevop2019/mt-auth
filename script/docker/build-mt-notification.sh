cd ../../
git checkout master
git fetch
git pull
docker build -f mt-notification/Dockerfile . -t publicdevop2019/mt-notification:latest --no-cache
docker push publicdevop2019/mt-notification:latest