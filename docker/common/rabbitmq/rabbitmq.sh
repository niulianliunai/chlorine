docker stop rabbitmq
docker rm rabbitmq
docker image rm rabbitmq:management
docker-compose -f docker-compose.yml up -d
