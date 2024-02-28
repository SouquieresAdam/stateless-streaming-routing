
data "aws_ecr_repository" "container-repository" {
  name                 = "streaming-simple-routing"
}

data "aws_ecs_cluster" "my_cluster" {
  cluster_name = "kafka-streams-ecs"
}

resource "aws_cloudwatch_log_group" "my_log_group" {
  name              = "/ecs/asouquieres-simple-stream-router"  # Replace with your desired log group name
  retention_in_days = 1
}

// Task Definition
resource "aws_ecs_task_definition" "simple-routing-task" {
  family                   = "simple-routing-task-definition"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 512
  memory                   = 1024
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn

  container_definitions = jsonencode([
    {
      name      = "simple-stream-router"
      image     = "${data.aws_ecr_repository.container-repository.repository_url}:${var.app_version}"
      cpu       = 512
      memory    = 1024
      essential = true
      portMappings = []
      logConfiguration = {
        "logDriver" = "awslogs",
        "options" = {
          "awslogs-group" = "/ecs/asouquieres-simple-stream-router",
          "awslogs-region" = "eu-west-1",
          "awslogs-stream-prefix" = "simple-stream-router"
        }
      },
    },
  ])
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name = "asouquieres-ecsTaskExecutionRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      },
    ]
  })

  inline_policy {
    name = "requirements-for-log-driver"

    policy = jsonencode({
      Version : "2012-10-17",
      Statement : [
        {
          "Effect" : "Allow",
          "Action" : [
            "logs:CreateLogStream",
            "logs:PutLogEvents"
          ],
          "Resource" : "*"
        }
      ]
    })
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}


resource "aws_ecs_service" "my_service" {
  name            = "my-service"
  cluster         = data.aws_ecs_cluster.my_cluster.id
  task_definition = aws_ecs_task_definition.simple-routing-task.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = [data.aws_subnet.private-subnet.id]
    security_groups = [aws_security_group.ecs_tasks_sg.id]
    assign_public_ip = false  // Tasks in private subnet don't get a public IP
  }
}