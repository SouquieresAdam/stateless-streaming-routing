
data "aws_vpc" "my-vpc" {
  tags = {
    Name = "asouquieres-vpc"
  }
}

resource "aws_internet_gateway" "my_igw" {
  vpc_id = data.aws_vpc.my-vpc.id
}

resource "aws_nat_gateway" "asouquieres-nat-gateway" {
  allocation_id = aws_eip.my_eip.id
  subnet_id     = data.aws_subnet.public-subnet.id
}

resource "aws_eip" "my_eip" {
  vpc = true
}

resource "aws_route_table_association" "private_subnet_association" {
  subnet_id      = data.aws_subnet.private-subnet.id
  route_table_id = aws_route_table.private_route_table.id
}

resource "aws_route_table" "private_route_table" {
  vpc_id = data.aws_vpc.my-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_nat_gateway.asouquieres-nat-gateway.id
  }

  // Add other route table configurations as needed
}

data "aws_subnet" "private-subnet" {
  tags = {
    Name = "asouquieres-vpc-private-subnet"
  }
  vpc_id = data.aws_vpc.my-vpc.id
}

data "aws_subnet" "public-subnet" {
  tags = {
    Name = "asouquieres-vpc-public-subnet"
  }
  vpc_id = data.aws_vpc.my-vpc.id
}

resource "aws_route_table" "public_route_table" {
  vpc_id = data.aws_vpc.my-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.my_igw.id
  }

  tags = {
    Name = "Public Route Table"
  }
}

resource "aws_route_table_association" "public_subnet_association" {
  subnet_id      = data.aws_subnet.public-subnet.id
  route_table_id = aws_route_table.public_route_table.id
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