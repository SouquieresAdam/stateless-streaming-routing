# Topics
# Convert topics list in resource.json to a map
locals {
  topics_map = {
    for obj in var.topics : replace(obj.topic_name, ".", "_") => obj
  }
}

# Create a topic resource for each topic in the topic map with appropriate configs
resource "confluent_kafka_topic" "topics" {
  for_each = local.topics_map
  kafka_cluster {
    id = data.confluent_kafka_cluster.cluster.id
  }
  topic_name       = "${var.env}.${var.domain}.${each.value.topic_name}"
  partitions_count = each.value.partitions_count
  rest_endpoint    = data.confluent_kafka_cluster.cluster.rest_endpoint
  config           = each.value.config
  credentials {
    key    = var.resource_management_api_key
    secret = var.resource_management_api_secret
  }
  lifecycle {
    ignore_changes = [config]
  }
  # Hopefully a temporary fix
  # if terraform tries to remove the config block of a topic
  # you get this error
  # Error: error updating Kafka Topic "lkc-xxxxx/global.supply.fr.v1": reset to topic setting's default value operation
  # (in other words, removing topic settings from 'configs' block) is not supported at the moment.
  # Instead, find its default value at https://registry.terraform.io/providers/confluentinc/confluent/latest/docs/resources/confluent_kafka_topic and set its current value to the default value.
}


locals {
  connectors_map = {
    for obj in var.connectors : replace(obj.connector_config.name, ".", "_") => obj
  }
}

resource "confluent_connector" "connectors" {
  for_each = local.connectors_map

  environment {
    id = data.confluent_environment.env.id
  }

  kafka_cluster {
    id = data.confluent_kafka_cluster.cluster.id
  }

  config_sensitive = {
    "kafka.api.key"    = var.resource_management_api_key
    "kafka.api.secret" = var.resource_management_api_secret
  }

  config_nonsensitive = each.value.connector_config

  #  lifecycle {
  #    prevent_destroy = true
  #  }

  depends_on = [confluent_kafka_topic.topics]
}