package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lambda handler for scraping ENTSO-E data and storing it in S3.
 */
public class EntsoeDataHandler implements RequestHandler<Object, String> {

    private static final S3Client S3_CLIENT = S3Client.builder().build();
    private LambdaLogger logger;

    @Override
    public String handleRequest(Object event, Context context) {
        logger = context.getLogger();
        logger.log("Starting the Lambda function...");
        logger.log("Event: " + event);
        logger.log("Environment Variables: " + System.getenv());
        logger.log("System Properties: " + System.getProperties());

        try {
            // Access environment variables
            String apiUrlTemplate = System.getenv().getOrDefault("API_URL",
                    "https://web-api.tp.entsoe.eu/api?documentType={document_type}&processType={process_type}&in_Domain={in_domain}&periodStart={period_start}&periodEnd={period_end}&securityToken={api_url_token}");
            String apiUrlToken = System.getenv().getOrDefault("API_URL_TOKEN", "xxxxxx");
            String documentType = System.getenv().getOrDefault("DOCUMENT_TYPE", "A71");
            String processType = System.getenv().getOrDefault("PROCESS_TYPE", "A01");
            String inDomain = System.getenv().getOrDefault("IN_DOMAIN", "10YBE----------2");
            String periodStart = System.getenv().getOrDefault("PERIOD_START", "202308152200");
            String periodEnd = System.getenv().getOrDefault("PERIOD_END", "202308162200");

            String bucketName = System.getenv().getOrDefault("S3_BUCKET", "entsoe-data-bucket");
            String outputPrefix = System.getenv().getOrDefault("OUTPUT_PREFIX", "entsoe_data_");

            String apiUrl = assembleApiUrl(apiUrlTemplate, documentType, processType, inDomain, periodStart, periodEnd, apiUrlToken);

            logger.log("Going to fetch data from API URL: " + apiUrl);

            // Fetch data from the API
            String responseData = fetchDataFromApi(apiUrl);
            logger.log("Got response: " + responseData);

            // Process the data dynamically
            List<String> processedData = processData(responseData);
            logger.log("Processed data: " + processedData);

            // Convert the data to CSV format
            String csvData = String.join(",", processedData);

            // Generate a unique file name
            String fileName = String.format("%s-%s.csv", outputPrefix, Instant.now().toString());

            // Upload the CSV to S3
            uploadToS3(bucketName, fileName, csvData);

            logger.log("Data successfully uploaded to S3: " + fileName);
            return "Success";
        } catch (Exception e) {
            logger.log("Error: " + e, LogLevel.ERROR);
            throw new RuntimeException(e);
        }
    }

    private String assembleApiUrl(String apiUrl, String documentType, String processType, String inDomain, String periodStart, String periodEnd, String apiUrlToken) {
        return apiUrl.replace("{document_type}", documentType)
                .replace("{process_type}", processType)
                .replace("{in_domain}", inDomain)
                .replace("{period_start}", periodStart)
                .replace("{period_end}", periodEnd)
                .replace("{api_url_token}", apiUrlToken);
    }

    private String fetchDataFromApi(String apiUrl) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            logger.log("Failed to fetch data from API: " + response.statusCode() + " " + response.body(), LogLevel.ERROR);
            throw new RuntimeException("Failed to fetch data from API: " + response.body());
        }
        return response.body();
    }

    private List<String> processData(String xmlData) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        Map<String, Object> root = xmlMapper.readValue(xmlData, Map.class);

        // Extract relevant data dynamically using streams
        return ((List<Map<String, String>>) ((Map<String, Object>) ((Map<String, Object>) root.get("TimeSeries"))
                .get("Period")).get("Point"))
                .stream()
                .map(point -> point.get("quantity"))
                .collect(Collectors.toList());
    }

    private void uploadToS3(String bucketName, String key, String data) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        S3_CLIENT.putObject(putObjectRequest, RequestBody.fromString(data));
    }
}