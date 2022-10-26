
pwd
echo 'Cleaning up...'
docker system prune --all
docker container prune
docker volume prune

echo 'Creating Docker containers from Dockerfiles...'
cd api-gateway
docker build --tag=api-gateway:latest .
cd ..
cd authorization-service
docker build --tag=authorization-service:latest .
cd ..
cd friend-service
docker build --tag=friend-service:latest .
cd ..
cd interaction-service
docker build --tag=interaction-service:latest .
cd ..
cd naming-server
docker build --tag=naming-server:latest .
cd ..
cd notification-service
docker build --tag=notification-service:latest .
cd ..
cd post-service
docker build --tag=post-service:latest .
cd ..
cd time-service
docker build --tag=time-service:latest .
cd ..
cd user-service
docker build --tag=user-service:latest .
cd ..
echo 'Docker containers created successfully'

echo 'Running containers'
docker compose up