# Description

Some Kafka Streams topology examples with the
library [kstreamplify](https://github.com/michelin/kstreamplify/tree/main).

### Prerequisites

- Java 17
- Maven 3
- Terraform (resource provisioning on Confluent Cloud)
- An Apache Kafka or Confluent Cloud Cluster
- A container based deployment target to deploy the generated docker image

### Setup your Apache Kafka / Confluent Cloud Environment

In order to run that project againt a real Kafka cluster, you can leverage the .tf files available in terraform/ folder.

```bash
cd terraform/papi
terraform init
terraform plan -var-file=assets/resources.json -var-file=../secret-variables.tfvars
terraform apply -var-file=assets/resources.json -var-file=../secret-variables.tfvars
```

- assets/resources.json declare all assets required to be deployed on your cluster : topic & connectors
- secret-variables.tfvars contains the **specific** values for your env

Example secret-variables.tfvars :

```terraform 
owner                          = "demo"
resource_management_api_key    = "api_key_with_manage_resource_permissions"
resource_management_api_secret = "api_key_secret"
confluent_cloud_environment    = "your confluent cloud env id"
```

### Build and deploy application images

```bash
mvn clean package
docker buildx build --build-arg APP=papi-reconciliation --platform linux/amd64 -t gcr.io/solutionsarchitect-01/papi-reconciliation:1.0.0-SNAPSHOT . --push
docker buildx build --build-arg APP=word-count --platform linux/amd64 -t gcr.io/solutionsarchitect-01/word-count:1.0.0-SNAPSHOT . --push
```

### Deploy your app on kubernetes using a stateful set w/ persistent storage

- Create config map

```bash
kubectl create configmap papi-reconciliation-cm --from-file=application.properties=./papi-reconciliation/deployment/config/stream-cm.properties -n demo
kubectl create configmap word-count-cm --from-file=application.properties=./word-count/deployment/config/stream-cm.properties -n demo
```

- Deploy the app

```bash
kubectl apply -f papi-reconciliation/deployment/streams.yml -n demo
kubectl apply -f word-count/deployment/streams.yml -n demo
```

## Word count dsl app

### Produce avro messages using console producer

```bash
kafka-avro-console-producer --bootstrap-server XX.gcp.confluent.cloud:9092 \
--producer.config cloud_sasl.properties \
--topic input-topic \
--property schema.registry.url="https://XX.gcp.confluent.cloud" \
--property schema.registry.basic.auth.user.info="XX:YY" \
--property basic.auth.credentials.source="USER_INFO" \
--property value.schema='{"type":"record","name":"Line","namespace":"com.example.kstreamplifydemo.model","fields":[{"name":"content","type":{"type":"string"}}],"connect.name":"com.example.kstreamplifydemo.model.Line"}'
```

### Then type some messages

Correct messages:

```
{"content":"a z e r t"}
{"content":"a z e r t"}
{"content":"a z e r t"}
```

Empty messages:

```
{"content":""}
{"content":""}
{"content":""}
```

### Produce avro messages using console producer with a wrong schema

```bash
kafka-console-producer --bootstrap-server XX.confluent.cloud:9092 \
--producer.config cloud_sasl.properties \
--topic input-topic 
```

### Then type some messages

Incorrect messages:

```
serialization error
```

# TODOs

- Deployment
    - Secret management
    - Readyness/Liveness Management
- Monitoring
    - Prometheus
    - Grafana
- CI/CD
    - Github actions





## Connect Docker to GCP GCR
```bash
gcloud auth login
gcloud auth configure-docker
docker build . -t gcr.io/solutionsarchitect-01/papi-reconciliation:1.0.0-SNAPSHOT
docker push gcr.io/solutionsarchitect-01/papi-reconciliation:1.0.0-SNAPSHOT
```

## Connect docker to AWS ECR
```bash
aws sso login
aws ecr get-login-password --region eu-west-1 | docker login --username AWS --password-stdin 492737776546.dkr.ecr.eu-west-1.amazonaws.com/
docker buildx build --build-arg APP=stateless-router --platform linux/amd64 -t 492737776546.dkr.ecr.eu-west-1.amazonaws.com/streaming-simple-routing:1.0.0-SNAPSHOT . --push
```




## Run in AWS

Prerequisites in AWS :

- SSO login
- a VPC & a private subnet
- a security group allowing traffic from the VPC to the internet
- an ecs cluster
- an ecr repository

```bash
aws sso login

cd terraform/stateless-router
terraform init
terraform plan -var-file=../secret-variables.tfvars
terraform apply -var-file=../secret-variables.tfvars
```








### TODO
- Secret in AWS KMS (Done)
- Backend in S3 (backend.tf, Done)
- One environment only per execution (Done)


- deploy an ECS container using terraform
- Add environment variables from KMS for authentification
- integration in Jenkhins (with team)