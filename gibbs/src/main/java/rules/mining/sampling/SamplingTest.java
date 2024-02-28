package rules.mining.sampling;

import java.util.Random;

public class SamplingTest {

    protected static double[] generateMarginalProbabilities(int size) {
        double[] probabilities = new double[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            probabilities[i] = 0.5; // Generate random marginal probabilities
        }

        return probabilities;
    }

    protected static void printMatrix(int[][] transactions) {
        for (int[] row : transactions) {
            printArray(row);
        }
    }

    protected static void printArray(int[] row) {
        for (int value : row) {
            System.out.printf("%d\t", value);
        }
        System.out.println();
    }
    
    protected static double[][] correlation_matrix = {
        {1.0, 0.0, 0.0, 0.8},
        {0.0, 1.0, 0.0, 0.0},
        {0.0, 0.0, 1.0, 0.2},
        {0.8, 0.0, 0.2, 1.0}
    };        

    protected static double[] marginal_probabilities = {0.5, 0.5, 0.5, 0.5};
}
