cd ../../
git checkout main
git fetch
git pull
docker build -f mt-access/Dockerfile . -t publicdevop2019/mt-access:latest --no-cache
docker push publicdevop2019/mt-access:latest