cd ../../
git checkout main
git fetch
git pull
docker build -f mt-proxy/Dockerfile . -t mt-auth/mt-proxy:latest --no-cache