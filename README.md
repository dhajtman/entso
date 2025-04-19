# ENTSOE Assignment with Terraform Deployment

## Prerequisites
- Create a free account on GitHub
- Create a free account on AWS (I recommend to set 0$ budget on your account to prevent misuse)
- Create a free account on transparency.entsoe.eu (https://transparencyplatform.zendesk.com/hc/en-us/articles/12845911031188-How-to-get-security-token)
- Install Terraform CLI locally

## Task Requirements

### Data Collection System
Develop an AWS Lambda function that scrapes data from the ENTSO-E Transparency Platform for selected countries and control areas of your choice, with infrastructure deployed using Terraform.

Download data source via REST API preferably: `https://transparency.entsoe.eu/generation/r2/dayAheadAggregatedGeneration/show`

### Core Requirements
1. **Data Storage**: Store the scraped data in AWS S3 as CSV files.

2. **Flexible Data Handling**:
    - Implement a generic data processing approach that adapts to changes in the response structure
    - Design the solution to be reusable for other endpoints, so I can add scrape new data from `https://transparency.entsoe.eu/generation/r2/actualGenerationPerProductionType/show` just by changing the configuration file

3. **Infrastructure as Code**:
    - Use Terraform to define and provision all required AWS resources:
        - Lambda function
        - IAM roles and permissions
        - S3 bucket
        - CloudWatch\Event bridge events for scheduling (if applicable)
        - Any other necessary resources

4. **Documentation & Code Quality**:
    - Add appropriate comments and documentation
    - Follow best practices for both AWS Lambda and Terraform development
    - Include instructions for deploying the infrastructure

### Submission Process
- Create a GitHub repository containing both application code and Terraform configuration
- Submit your completed assignment as a pull request
- Include a README with setup and deployment instructions

## Additional Information
Feel free to make reasonable assumptions about any aspects not explicitly defined in the requirements. You may choose any programming language supported by AWS Lambda. Document any assumptions or design decisions in your submission.
API documentation available here https://transparency.entsoe.eu/content/static_content/Static%20content/knowledge%20base/knowledge%20base.html

## Setup
1. **Create terraform.tfvars file with values:**:
    ```
   api_url       = "https://web-api.tp.entsoe.eu/api?documentType=A71&processType=A01&in_Domain=10YBE----------2&periodStart=202308152200&periodEnd=202308162200&securityToken="
   s3_bucket     = "entsoe-data-buckets"
   countries     = ["DE", "FR", "IT"]
   output_prefix = "entsoe-data"
   api_url_token = "entsoe web api token"    
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