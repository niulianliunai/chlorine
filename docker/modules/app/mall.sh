#!/usr/bin/env bash
app_name='mall'
docker_id='niulianliunai'
version=1.0.0
docker stop ${app_name} 
docker rm ${app_name}
docker image rm ${docker_id}/${app_name}
docker build -t ${docker_id}/${app_name}  .
docker-compose -f docker-compose.yml up -d
 #docker push ${docker_id}/${app_name}
