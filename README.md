# imagestore-docker
image store application in docker

# setup minikube and kubectl
minikube start

# point to docker inside minikube
eval $(minikube docker-env)

# namespace: imagestore
    kubectl create -f imagestore-namespace.yaml

# persistent volume creation  -- this is mounted at /tmp/data in minikube host vm (see file for details)
	https://kubernetes.io/docs/tasks/configure-pod-container/configure-persistent-volume-storage/
##  create the mount directory in minikube 
	minikube ssh
	su
	mkdir /tmp/data

## run below commands to create the persistent volume
	kubectl create -f imagestore-volume.yaml
	kubectl get pv 

# persistent volume claim creation
	kubectl create -f imagestore-pv-claim.yaml
	kubectl get pvc --namespace=imagestore

# check pv again that it has been claimed
	kubectl get pv
	
	
# create app, create image and put in minikube as deployment, expose as service.
	cd image-store
	mvn clean install
	docker build -t imagestore:1.0 .
	kubectl create -f imagestore.yaml
	
# check
    kubectl get deployment --namespace=imagestore
    kubectl get pod --namespace=imagestore
    kubectl get service --namespace=imagestore
	
# delete deployment to clean up and rectifications
    kubectl delete deployment --namespace=imagestore imagestore
	
	
# minikube dashboard
  http://192.168.99.100:30000/#!/overview?namespace=default
  
# curl operations
  http://<minikubeip>:<LBPort>/<operation>?<param1=value1&param2=value2>
  http://192.168.99.100:32443/getImage?imageName=brigida1.jpg&albumName=prem
  http://192.168.99.100:32443/storeImage
     imgfile    <actual file>
	 imgname    filename.extension
	 albumName  prem
  http://192.168.99.100:32443/getAlbumImages?albumName=prem
  http://192.168.99.100:32443/deleteImage?imageName=brigida1.jpg&albumName=prem
  http://192.168.99.100:32443/deleteAlbum?albumName=brigida
  
  
# do delete pod and check that files are still present since files are stored in mounted volume
  kubectl get pod --namespace=imagestore
  kubectl delete pod --namespace=imagestore imagestore-7c68dd987-t8n8m
  
  http://192.168.99.100:32443/getAlbumImages?albumName=prem
  