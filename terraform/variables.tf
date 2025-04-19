variable "api_url" {
  type        = string
  description = "The API URL for the ENTSOE scraper"
  default = "https://web-api.tp.entsoe.eu/api?documentType=A71&processType=A01&in_Domain=10YBE----------2&periodStart=202308152200&periodEnd=202308162200&securityToken="
}

variable "s3_bucket" {
  type        = string
  description = "The S3 bucket name for storing data"
  default = "entsoe-data-bucket" # Replace with your actual bucket name
}

variable "countries" {
  type        = list(string)
  description = "List of countries to scrape data for"
  default = ["DE", "FR", "IT"] # Replace with your actual countries
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