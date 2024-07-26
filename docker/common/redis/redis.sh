docker stop redis
docker rm redis
docker image rm redis
docker-compose -f docker-compose.yml up -d
