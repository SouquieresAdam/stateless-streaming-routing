
data "aws_vpc" "my-vpc" {
  tags = {
    Name = "asouquieres-vpc"
  }
}

data "aws_subnet" "private-subnet" {
  tags = {
    Name = "asouquieres-vpc-private-subnet"
  }
  vpc_id = data.aws_vpc.my-vpc.id
}

resource "aws_security_group" "ecs_tasks_sg" {
  vpc_id = data.aws_vpc.my-vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"  // Allow all outbound
    cidr_blocks = ["0.0.0.0/0"]
  }

}