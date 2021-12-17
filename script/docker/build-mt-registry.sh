cd ../../
git checkout master
git fetch
git pull
docker build -f mt-registry/Dockerfile . -t publicdevop2019/mt-registry:latest --no-cache
docker push publicdevop2019/mt-registry:latest