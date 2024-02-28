package rules.mining.requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class ChoquetLearn extends HttpRequestSender {

    private static String baseUrl = "http://127.0.0.1:8000";
    private static String endpoint = "/mobius_capacities";

    private double[] capacities;
    private int[] subsets;

    public ChoquetLearn(String baseUrl) {
        super(baseUrl);
    }

    public ChoquetLearn() {
        super(baseUrl);
    }

    public void train(double[][] alternatives, double[] overall, int additivity) throws IOException {
        // Convert arrays to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String alternativesJson = objectMapper.writeValueAsString(alternatives);
        String overallJson = objectMapper.writeValueAsString(overall);

        // Construct the URL-encoded request body
        String requestBody = String.format("alternatives=%s&overall=%s&additivity=%d",
                URLEncoder.encode(alternativesJson, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(overallJson, StandardCharsets.UTF_8.toString()),
                additivity);

        // Send the POST request with JSON body
        String response = sendGetRequest(endpoint, requestBody);

        // Parse the JSON response
        JsonNode jsonNode = objectMapper.readTree(response);

        // Get the Mobius.capacities array
        JsonNode capacitiesNode = jsonNode.path("Mobius.capacities");
        JsonNode subsetsNode = jsonNode.path("Mobius.subsets");

        // Convert the capacities to a double array
        this.capacities = objectMapper.treeToValue(capacitiesNode, double[].class);
        this.subsets = objectMapper.treeToValue(subsetsNode, int[].class);
    }
}
