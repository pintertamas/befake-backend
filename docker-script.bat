pwd
echo Cleaning up...
docker system prune --all -f
docker container prune -f
docker volume prune -f

echo Creating Docker containers from Dockerfiles...

echo --------------------api-gateway--------------------
cd api-gateway
call .\mvnw package clean install
docker build --tag=pintertamas/api-gateway:docker .
docker push pintertamas/api-gateway:docker
cd ..

echo --------------------authorization-service--------------------
cd authorization-service
call .\mvnw package clean install
docker build --tag=pintertamas/authorization-service:docker .
docker push pintertamas/authorization-service:docker
cd ..

echo --------------------friend-service--------------------
cd friend-service
call .\mvnw package clean install
docker build --tag=pintertamas/friend-service:docker .
docker push pintertamas/friend-service:docker
cd ..

echo --------------------interaction-service--------------------
cd interaction-service
call .\mvnw package clean install
docker build --tag=pintertamas/interaction-service:docker .
docker push pintertamas/interaction-service:docker
cd ..

echo --------------------naming-server--------------------
cd naming-server
call .\mvnw package clean install
docker build --tag=pintertamas/naming-server:docker .
docker push pintertamas/naming-server:docker
cd ..

echo --------------------notification-service--------------------
cd notification-service
call .\mvnw package clean install
docker build --tag=pintertamas/notification-service:docker .
docker push pintertamas/notification-service:docker
cd ..

echo --------------------post-service--------------------
cd post-service
call .\mvnw package clean install
docker build --tag=pintertamas/post-service:docker .
docker push pintertamas/post-service:docker
cd ..

echo --------------------time-service--------------------
cd time-service
call .\mvnw package clean install
docker build --tag=pintertamas/time-service:docker .
docker push pintertamas/time-service:docker
cd ..

echo --------------------user-service--------------------
cd user-service
call .\mvnw package clean install
docker build --tag=pintertamas/user-service:docker .
docker push pintertamas/user-service:docker
cd ..

echo Docker containers created successfully

echo Running containers
::docker compose up