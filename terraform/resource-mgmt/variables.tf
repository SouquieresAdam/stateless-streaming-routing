#### Confluent Cloud
variable "owner" {
  description = "Owner of the project"
  type        = string
}

variable "confluent_cloud_api_key" {
  description = "Confluent Cloud API Key (also referred as Cloud API ID)"
  type        = string
}
variable "confluent_cloud_api_secret" {
  description = "Confluent Cloud API Secret"
  type        = string
  sensitive   = true
}


variable "resource_management_api_key" {
  description = "Resource Management API Key"
  type        = string
}
variable "resource_management_api_secret" {
  description = "Resource Management API Secret"
  type        = string
  sensitive   = true
}

variable "confluent_cloud_environment" {
  description = "Confluent Cloud Environment"
  type        = string
}

variable "env" {
  description = "Env name"
  type        = string
}

variable "domain" {
  description = "Domain name"
  type        = string
}

variable "topics" {
  type = list(
    object({
      topic_name       = string
      partitions_count = number
      config           = map(any)
    })
  )
  description = "List of topics with specifications"
}

variable "connectors" {
  type = list(
    object({
      connector_config = map(any)
    })
  )
  description = "List of connectors with specifications"
}

variable "schemas" {
  type = list(
    object({
      topic_name = string
      schema     = string
      format     = string
    })
  )
  description = "List of schemas with specifications"
}