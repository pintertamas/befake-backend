#!/bin/bash

pwd
echo 'Cleaning up...'
docker system prune --all -f
docker container prune -f
docker volume prune -f

echo 'Creating Docker containers from Dockerfiles...'

service_list=(
  "user-service"
  "post-service"
  "api-gateway"
  "authorization-service"
  "friend-service"
  "interaction-service"
  "notification-service"
  "time-service"
  #"naming-server"
)

for service_name in "${service_list[@]}"; do
  echo "--------------------$service_name--------------------"
  cd "$service_name" || break
  mvn clean install package
  docker build --tag="pintertamas/$service_name":SNAPSHOT-0.0.1 .
  docker push pintertamas/"$service_name":SNAPSHOT-0.0.1
  cd ..
done

echo 'Docker containers created successfully'

echo 'Running containers'
#docker compose up
