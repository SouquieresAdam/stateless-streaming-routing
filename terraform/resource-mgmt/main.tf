# Use existing environment
data "confluent_environment" "env" {
  id = var.confluent_cloud_environment
}

# Kafka cluster
data "confluent_kafka_cluster" "cluster" {
  display_name = "${var.owner}-cluster"
  environment {
    id = data.confluent_environment.env.id
  }
}