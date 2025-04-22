## Building Lambda functions with Java
https://docs.aws.amazon.com/lambda/latest/dg/lambda-java.html

## ENTSOE API parameter list
https://transparencyplatform.zendesk.com/hc/en-us/articles/15692855254548-Sitemap-for-Restful-API-Integration

## Setup
1. **Create terraform.tfvars file with values:**:
    ```
   api_url       = "https://web-api.tp.entsoe.eu/api?documentType={document_type}&processType={process_type}&in_Domain={in_domain}&periodStart={period_start}&periodEnd={period_end}&securityToken={api_url_token}"
   document_type = "A71" # Generation forecast
   process_type  = "A01" # Day ahead
   in_domain     = "10YBE----------2" # Control Area, Bidding Zone, Country
   period_start  = "202308152200" # Start period (Pattern yyyyMMddHHmm e.g. 201601010000)
   period_end    = "202308162200" # End period (Pattern yyyyMMddHHmm e.g. 201601010000)
   schedule_expression = "rate(1 day)" # Default value
   s3_bucket     = "entsoe-data-buckets"
   output_prefix = "entsoe-data"
   api_url_token = "your_api_token" # Token from transparency.entsoe.eu
   ```

## Terraform  
1. **Check AWS region**: 
    ```bash
    aws configure get region
    ```
2. **Set AWS region**: 
    ```bash
    export AWS_REGION=us-east-1
    ```
3. **cd to terraform directory**:
    ```bash
    cd terraform
    ```
4. **Initialize Terraform**:
    ```bash
    terraform init
    ```
5. **Apply Terraform configuration**:
    ```bash
    terraform apply
    terraform apply -var-file=terraform_A71.tfvars # specific .tfvars file
    ```
6. **List all deployed resources**:
    ```bash
    terraform state list
    ```
7. **Destroy all resources**:
    ```bash
    terraform destroy
    ```
8. **Update only Lambda jar**:
    ```bash
    aws lambda update-function-code --function-name entsoe-scraper --zip-file fileb://../target/entso-1.0-SNAPSHOT.jar
    ```