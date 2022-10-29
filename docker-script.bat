
pwd
echo 'Cleaning up...'
docker system prune --all -f
docker container prune -f
docker volume prune -f

echo 'Creating Docker containers from Dockerfiles...'

echo '--------------------api-gateway--------------------'
cd api-gateway
call .\mvnw package clean install
docker build --tag=api-gateway:latest .
cd ..

echo '--------------------authorization-service--------------------'
cd authorization-service
call .\mvnw package clean install
docker build --tag=authorization-service:latest .
cd ..

echo '--------------------friend-service--------------------'
cd friend-service
call .\mvnw package clean install
docker build --tag=friend-service:latest .
cd ..

echo '--------------------interaction-service--------------------'
cd interaction-service
call .\mvnw package clean install
docker build --tag=interaction-service:latest .
cd ..

echo '--------------------naming-server--------------------'
cd naming-server
call .\mvnw package clean install
docker build --tag=naming-server:latest .
cd ..

echo '--------------------notification-service--------------------'
cd notification-service
call .\mvnw package clean install
docker build --tag=notification-service:latest .
cd ..

echo '--------------------post-service--------------------'
cd post-service
call .\mvnw package clean install
docker build --tag=post-service:latest .
cd ..

echo '--------------------time-service--------------------'
cd time-service
call .\mvnw package clean install
docker build --tag=time-service:latest .
cd ..

echo '--------------------user-service--------------------'
cd user-service
call .\mvnw package clean install
docker build --tag=user-service:latest .
cd ..

echo 'Docker containers created successfully'

echo 'Running containers'
#docker compose up