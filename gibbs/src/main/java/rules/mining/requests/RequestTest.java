package rules.mining.requests;

import java.io.IOException;
import java.util.Arrays;

public class RequestTest {
    public static void main(String[] args) {
        try {
            ChoquetLearn choquetLearn = new ChoquetLearn("http://localhost:8000");
            ChoquetCompute choquetCompute = new ChoquetCompute("http://localhost:8000");

            // Assuming that sendCustomRequest returns a String, you need to parse it into a double array
            choquetLearn.train(
                                new double[][]{{1, 2, 3}, {1, 2, 3}},
                                new double[]{1, 2},
                                2);
            double[] weights = choquetLearn.getCapacities();
            int[] subsets = choquetLearn.getSubsets();
            
            double score = choquetCompute.evaluate(new double[]{1, 2, 3}, weights, 2, subsets);

            System.out.println("Weights: " + Arrays.toString(weights));
            System.out.println("Score: " + score);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
