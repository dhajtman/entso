variable "api_url" {
  type        = string
  description = "The API URL for the ENTSOE scraper"
  default = "https://web-api.tp.entsoe.eu/api?documentType={document_type}&processType={process_type}&in_Domain={in_domain}&periodStart={period_start}&periodEnd={period_end}&securityToken={api_url_token}"
}

variable "document_type" {
  type        = string
  description = "Document type for the ENTSOE API (A71 = Generation forecast)"
  default = "A71" # Replace with your actual document type
}

variable "process_type" {
  type        = string
  description = "Process type for the ENTSOE API (A01 = Day ahead)"
  default = "A01" # Replace with your actual process type
}

variable "in_domain" {
  type        = string
  description = "Domain for the ENTSOE API (Control Area, Bidding Zone, Country)"
  default = "10YBE----------2" # Replace with your actual domain
}

variable "period_start" {
  type        = string
  description = "Start period for the ENTSOE API (Pattern yyyyMMddHHmm e.g. 201601010000)"
  default = "202308152200" # Replace with your actual start period
}

variable "period_end" {
  type        = string
  description = "End period for the ENTSOE API (Pattern yyyyMMddHHmm e.g. 201601010000)"
  default = "202308162200" # Replace with your actual end period
}

variable "schedule_expression" {
  description = "The schedule expression for the CloudWatch Event Rule"
  type        = string
  default     = "rate(1 day)" # Default value
}

variable "s3_bucket" {
  type        = string
  description = "The S3 bucket name for storing data"
  default = "entsoe-data-bucket" # Replace with your actual bucket name
}

variable "output_prefix" {
  type        = string
  description = "The prefix for the output files in S3"
  default = "entsoe-data" # Replace with your actual output prefix
}

variable "api_url_token" {
  type        = string
  description = "Token for accessing the ENTSOE API"
  sensitive   = true
}