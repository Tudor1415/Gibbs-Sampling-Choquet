package rules.mining.requests;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SyntheticDataset extends HttpRequestSender {
    private static String baseUrl = "http://127.0.0.1:8000";
    private static String endpoint = "/sample_dataset";

    public SyntheticDataset(String baseUrl) {
        super(baseUrl);
    }

    public SyntheticDataset() {
        super(baseUrl);
    }

    public int[][] get_dataset(double[][] correlation_matrix, double[] marginal_probabilities, int number_rows) throws Exception {
        // Convert arrays to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String correlation_matrixJson = objectMapper.writeValueAsString(correlation_matrix);
        String marginal_probabilitiesJson = objectMapper.writeValueAsString(marginal_probabilities);

        // Construct the URL-encoded request body
        String requestBody = String.format("correlation_matrix=%s&marginal_probabilities=%s&number_rows=%d",
                URLEncoder.encode(correlation_matrixJson, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(marginal_probabilitiesJson, StandardCharsets.UTF_8.toString()),
                number_rows);

        // Send the GET request with URL-encoded body
        String response = sendGetRequest(endpoint, requestBody);

        // Parse the JSON response
        JsonNode jsonNode = objectMapper.readTree(response);

        // Get the Dataset array
        JsonNode datasetNode = jsonNode.path("Dataset");

        // Convert the Dataset to a double array
        int[][] dataset = objectMapper.treeToValue(datasetNode, int[][].class);

        return dataset;
    }

}
