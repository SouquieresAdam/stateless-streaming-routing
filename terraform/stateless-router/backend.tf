terraform {
  backend "s3" {
    bucket         = "asouquieres-tf-state"
    key            = "simple-streaming-router-deployment"
    region         = "eu-west-1"
    encrypt        = true
  }
}