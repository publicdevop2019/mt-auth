cd ../../
git checkout main
git fetch
git pull
docker build -f mt-proxy/Dockerfile . -t publicdevop2019/mt-proxy:latest --no-cache
docker push publicdevop2019/mt-proxy:latest