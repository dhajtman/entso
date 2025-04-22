# This file contains the Terraform configuration for deploying the AWS Lambda function

# Create a Lambda function
resource "aws_lambda_function" "entsoe_scraper" {
  function_name = "entsoe-scraper"
  role          = aws_iam_role.lambda_role.arn
  handler       = "org.example.EntsoeDataHandler::handleRequest"
  runtime       = "java21"
  filename      = "../target/entso-1.0-SNAPSHOT.jar"
  timeout       = 60
  memory_size   = "256"

  snap_start {
    apply_on = var.snap_start_value
    # apply_on = "None" # Default value, can be changed to "PublishedVersions" if needed
  }

  vpc_config {
    subnet_ids         = [aws_subnet.private.id]
    security_group_ids = [aws_security_group.lambda_sg.id]
  }

  environment {
    variables = {
      API_URL       = var.entsoe_api_url
      API_URL_TOKEN = jsondecode(data.aws_secretsmanager_secret_version.api_token.secret_string).api_url_token
      DOCUMENT_TYPE = var.document_type
      PROCESS_TYPE  = var.process_type
      IN_DOMAIN     = var.in_domain
      PERIOD_START  = var.period_start
      PERIOD_END    = var.period_end
      TARGET_KEY    = var.target_key
      S3_BUCKET     = aws_s3_bucket.entsoe_data.bucket
      OUTPUT_PREFIX = var.output_prefix
    }
  }
}