package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EntsoeDataHandlerTest {

    private EntsoeDataHandler handler;
    private S3Client mockS3Client;

    @BeforeEach
    void setUp() {
        handler = new EntsoeDataHandler();
        mockS3Client = mock(S3Client.class);

//         Mock S3Client static builder
        MockedStatic<S3Client> s3ClientMockedStatic = mockStatic(S3Client.class);
        S3ClientBuilder mockBuilder = mock(S3Client.builder());

        s3ClientMockedStatic.when(S3Client::builder).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockS3Client);

        // Set environment variables
        System.setProperty("API_URL", "https://web-api.tp.entsoe.eu/api?documentType=A71&processType=A01&in_Domain=10YBE----------2&periodStart=202308152200&periodEnd=202308162200&securityToken=90765852-0497-41e0-b46f-8b0f49c57ca0");
        System.setProperty("S3_BUCKET", "entsoe-data-bucket");
        System.setProperty("COUNTRIES", "DE,FR,IT");
        System.setProperty("OUTPUT_PREFIX", "test-data");
    }

//    @Test
    void testReal() {
        // Invoke the handler
        Context mockContext = mock(Context.class);
        LambdaLogger loggerMock = mock(LambdaLogger.class);
        when(mockContext.getLogger()).thenReturn(loggerMock);

        doAnswer(call -> {
            System.out.println((String)call.getArgument(0));//print to the console
            return null;
        }).when(loggerMock).log(anyString());
        String result = handler.handleRequest(Map.of(), mockContext);
    }

    @Test
    void testHandleRequest() throws Exception {
        // Mock API response
        String mockApiResponse = "<items><item><timestamp>2023-01-01T00:00:00Z</timestamp><value>100</value><country>DE</country></item></items>";
        MockedStatic<HttpClient> httpClientMockedStatic = mockStatic(HttpClient.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockApiResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
        httpClientMockedStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

        // Mock S3 upload
        doNothing().when(mockS3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        // Invoke the handler
        Context mockContext = mock(Context.class);
        String result = handler.handleRequest(Map.of(), mockContext);

        // Verify S3 upload
        verify(mockS3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        // Assert result
        assertEquals("Success", result);
    }
}
