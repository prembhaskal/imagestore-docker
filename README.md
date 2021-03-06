# imagestore-docker
image store application in docker.
The image store application consists of 3 components here

- Image Store app
- Rabbit MQ Event Server
- Image Store Event app

## Image store app
The images store app lets user manage images into albums. It lets user store,delete,retreive images. It allows to list images in albums and delete album.
It publishes the different events on the Rabbit MQ Event server.
The images are stored in mounted file system thus allowing persistence.
Images are stored as files. Albums are the directories.
It exposes metrics for prometheus using spring boot actuator and micrometer libraries

## Rabbit MQ Event server
rabbit mq server which acts as event pipe.

## Image Store Event app
A consumer of the image store events. It keeps a count (non-persistent) of the different kind of image store events.


# Deployment steps
## setup minikube and kubectl
	minikube start

## point to docker inside minikube
	eval $(minikube docker-env)

## create namespace: imagestore
    kubectl create -f imagestore-namespace.yaml

## rabbit mq deployment
### deploy rabbit mq, configuration and service files
	kubectl create -f rmq_configmap.yaml
    kubectl create -f rabbitmq.yaml
	kubectl get deployment --namespace=imagestore
	kubectl get pods --namespace=imagestore
	kubectl get service --namespace=imagestore
	
### clean up rabbit mq (for redeployment/error checks etc)
    kubectl delete deployment rabbitmq --namespace=imagestore
    kubectl delete service rabbitmq-ser --namespace=imagestore
	
## persistent volume creation for image storage
###  create the mount directory in minikube 
	minikube ssh
	su
	mkdir /tmp/data

### run below commands to create the persistent volume
	kubectl create -f imagestore-volume.yaml
	kubectl get pv 

### persistent volume claim creation
	kubectl create -f imagestore-pv-claim.yaml
	kubectl get pvc --namespace=imagestore

### check pv again that it has been claimed
	kubectl get pv

## Deploy Image Store App	
### create image store app, create image and put in minikube as deployment, expose as service.
	cd image-store
	mvn clean install
	docker build -t imagestore:1.0 .
	kubectl create -f imagestore.yaml
	
### check
    kubectl get deployment --namespace=imagestore
    kubectl get pod --namespace=imagestore
    kubectl get service --namespace=imagestore
	
### delete deployment to clean up and rectifications
    kubectl delete deployment --namespace=imagestore imagestore
    kubectl delete service --namespace=imagestore imagestore-ser
	
## Deploy Image Store Event App
### create image store events app, create image and put in minikube as deployment, expose as service.
	cd imagestore-events
	mvn clean install
	docker build -t imagestore-events:1.0 .
	kubectl create -f imagestore-events.yaml
	
### check
    kubectl get deployment --namespace=imagestore
    kubectl get pod --namespace=imagestore
    kubectl get service --namespace=imagestore
	
### delete deployment to clean up and rectifications
    kubectl delete deployment --namespace=imagestore imagestore-events
    kubectl delete service --namespace=imagestore imagestoreevents-ser
	
## Deploy prometheus
   cd prometheus
   kubectl create -f monitoring-namespace.yaml
   kubectl create -f prometheus-config.yaml
   kubectl create -f prometheus-deployment.yaml
   kubectl create -f prometheus-service.yaml
   
### get prometheus port from the service
   access the custom metrics from here
   http://192.168.99.100:30911/graph?g0.range_input=1h&g0.expr=http_server_requests_seconds_count&g0.tab=1
	
## minikube dashboard
  running below command will open dashboard in browser. url will similar to given below
  minikube dashboard 
  eg.http://192.168.99.100:30000/#!/overview?namespace=default
  
## stop minikube
	minikube stop

# API usage

## curl operations for image store
  
### Swagger UI and docs for the image store are available at below endpoints
	http://192.168.99.100:31984/swagger-ui.html

### endpoints are also listed here. the albumName is optional, default album with no name will be used in that case.
  http://192.168.99.100:32443/storeImage
     imgfile    <actual file>
	 imgname    filename.extension
	 albumName  prem
  http://192.168.99.100:32443/getImage?imageName=brigida1.jpg&albumName=prem
  http://192.168.99.100:32443/getAlbumImages?albumName=prem
  http://192.168.99.100:32443/deleteImage?imageName=brigida1.jpg&albumName=prem
  http://192.168.99.100:32443/deleteAlbum?albumName=brigida
  
## curl operations for image event store

### Swagger UI and docs for the image store are available at below endpoints
	http://192.168.99.100:31084/swagger-ui.html
	
  At the home page, the event counters will be displayed for each of the event type.
  The endpoints are also listed here.
 
  http://localhost:8082/getEventDetails?evtType=IMAGE_STORE
  http://localhost:8082/getEventDetails?evtType=IMAGE_DELETE
  http://localhost:8082/getEventDetails?evtType=ALBUM_CREATE
  http://localhost:8082/getEventDetails?evtType=ALBUM_DELETE
  http://localhost:8082/getEventDetails?evtType=IMAGE_RETREIVE
  
  