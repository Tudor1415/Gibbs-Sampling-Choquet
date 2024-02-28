package rules.mining.requests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChoquetCompute extends HttpRequestSender {
    private static String baseUrl = "http://127.0.0.1:8000";
    private static String endpoint = "/evaluate";

    public ChoquetCompute(String baseUrl) {
        super(baseUrl);
    }

    public ChoquetCompute() {
        super(baseUrl);
    }

    public double evaluate(double[] alternative, double[] capacities, int additivity, int[] subsets) throws IOException {
        // Convert arrays to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String alternativeString = objectMapper.writeValueAsString(alternative);
        String capacitiesString = objectMapper.writeValueAsString(capacities);
        String subsetsString = objectMapper.writeValueAsString(subsets);

        // Construct the URL-encoded request body
        String requestBody = String.format("alternative=%s&capacities=%s&subsets=%s&additivity=%d",
                URLEncoder.encode(alternativeString, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(capacitiesString, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(subsetsString, StandardCharsets.UTF_8.toString()),
                additivity);

        // Send the POST request with JSON body
        String response = sendGetRequest(endpoint, requestBody);

        // Parse the JSON response
        JsonNode jsonNode = objectMapper.readTree(response);

        // Get the Mobius.capacities array
        JsonNode scoreJson = jsonNode.path("score");

        // Convert the capacities to a double array
        List<Double> scores = objectMapper.readValue(scoreJson.toString(), new TypeReference<List<Double>>() {});

        return scores.get(0);
    }
}

