cd ../../
git checkout main
git fetch
git pull
docker build -f mt-access/Dockerfile . -t mt-auth/mt-access:latest --no-cache