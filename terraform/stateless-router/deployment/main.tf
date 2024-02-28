module "resource-mgmt" {
  source                         = "../resource-mgmt"
  confluent_cloud_api_key        = var.confluent_cloud_api_key
  confluent_cloud_api_secret     = var.confluent_cloud_api_secret
  confluent_cloud_environment    = var.confluent_cloud_environment
  connectors                     = var.connectors
  owner                          = var.owner
  resource_management_api_key    = var.resource_management_api_key
  resource_management_api_secret = var.resource_management_api_secret
  schemas                        = var.schemas
  topics                         = var.topics
  domain                         = var.domain
  env                            = var.env
}
