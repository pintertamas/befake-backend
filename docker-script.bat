pwd
echo Cleaning up...
docker system prune --all -f
docker container prune -f
docker volume prune -f

echo Creating Docker containers from Dockerfiles...

echo --------------------api-gateway--------------------
cd api-gateway
call .\mvnw package clean install
docker build --tag=pintertamas/api-gateway:kubernetes .
docker push pintertamas/api-gateway:kubernetes
cd ..

echo --------------------authorization-service--------------------
cd authorization-service
call .\mvnw package clean install
docker build --tag=pintertamas/authorization-service:kubernetes .
docker push pintertamas/authorization-service:kubernetes
cd ..

echo --------------------friend-service--------------------
cd friend-service
call .\mvnw package clean install
docker build --tag=pintertamas/friend-service:kubernetes .
docker push pintertamas/friend-service:kubernetes
cd ..

echo --------------------interaction-service--------------------
cd interaction-service
call .\mvnw package clean install
docker build --tag=pintertamas/interaction-service:kubernetes .
docker push pintertamas/interaction-service:kubernetes
cd ..

::echo --------------------naming-server--------------------
::cd naming-server
::call .\mvnw package clean install
::docker build --tag=pintertamas/naming-server:kubernetes .
::docker push pintertamas/naming-server:kubernetes
::cd ..

echo --------------------notification-service--------------------
cd notification-service
call .\mvnw package clean install
docker build --tag=pintertamas/notification-service:kubernetes .
docker push pintertamas/notification-service:kubernetes
cd ..

echo --------------------post-service--------------------
cd post-service
call .\mvnw package clean install
docker build --tag=pintertamas/post-service:kubernetes .
docker push pintertamas/post-service:kubernetes
cd ..

echo --------------------time-service--------------------
cd time-service
call .\mvnw package clean install
docker build --tag=pintertamas/time-service:kubernetes .
docker push pintertamas/time-service:kubernetes
cd ..

echo --------------------user-service--------------------
cd user-service
call .\mvnw package clean install
docker build --tag=pintertamas/user-service:kubernetes .
docker push pintertamas/user-service:kubernetes
cd ..

echo Docker containers created successfully

echo Running containers
::docker compose up