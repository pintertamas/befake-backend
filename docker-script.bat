pwd
echo Cleaning up...
docker system prune --all -f
docker container prune -f
docker volume prune -f

echo Creating Docker containers from Dockerfiles...

echo --------------------api-gateway--------------------
cd api-gateway
call .\mvnw package clean install
docker build --tag=pintertamas/api-gateway:SNAPSHOT-0.0.1 .
docker push pintertamas/api-gateway:SNAPSHOT-0.0.1
cd ..

echo --------------------authorization-service--------------------
cd authorization-service
call .\mvnw package clean install
docker build --tag=pintertamas/authorization-service:SNAPSHOT-0.0.1 .
docker push pintertamas/authorization-service:SNAPSHOT-0.0.1
cd ..

echo --------------------friend-service--------------------
cd friend-service
call .\mvnw package clean install
docker build --tag=pintertamas/friend-service:SNAPSHOT-0.0.1 .
docker push pintertamas/friend-service:SNAPSHOT-0.0.1
cd ..

echo --------------------interaction-service--------------------
cd interaction-service
call .\mvnw package clean install
docker build --tag=pintertamas/interaction-service:SNAPSHOT-0.0.1 .
docker push pintertamas/interaction-service:SNAPSHOT-0.0.1
cd ..

::echo --------------------naming-server--------------------
::cd naming-server
::call .\mvnw package clean install
::docker build --tag=pintertamas/naming-server:SNAPSHOT-0.0.1 .
::docker push pintertamas/naming-server:SNAPSHOT-0.0.1
::cd ..

echo --------------------notification-service--------------------
cd notification-service
call .\mvnw package clean install
docker build --tag=pintertamas/notification-service:SNAPSHOT-0.0.1 .
docker push pintertamas/notification-service:SNAPSHOT-0.0.1
cd ..

echo --------------------post-service--------------------
cd post-service
call .\mvnw package clean install
docker build --tag=pintertamas/post-service:SNAPSHOT-0.0.1 .
docker push pintertamas/post-service:SNAPSHOT-0.0.1
cd ..

echo --------------------time-service--------------------
cd time-service
call .\mvnw package clean install
docker build --tag=pintertamas/time-service:SNAPSHOT-0.0.1 .
docker push pintertamas/time-service:SNAPSHOT-0.0.1
cd ..

echo --------------------user-service--------------------
cd user-service
call .\mvnw package clean install
docker build --tag=pintertamas/user-service:SNAPSHOT-0.0.1 .
docker push pintertamas/user-service:SNAPSHOT-0.0.1
cd ..

echo Docker containers created successfully

echo Running containers
::docker compose up