package rules.mining.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestSender {
    protected String baseUrl;

    public HttpRequestSender(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected String sendGetRequest(String endpoint, String queryString) throws IOException {
        // Construct the full URL
        String fullUrl = baseUrl + endpoint + "?" + queryString;

        // Create a URL object
        URL urlObject = new URL(fullUrl);

        // Open a connection
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        // Set the request method
        connection.setRequestMethod("GET");

        // Read the response body
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } finally {
            // Close the connection
            connection.disconnect();
        }
    }
}
