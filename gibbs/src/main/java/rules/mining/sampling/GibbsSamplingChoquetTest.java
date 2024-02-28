package rules.mining.sampling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rules.mining.oracles.LinearOracle;
import rules.mining.requests.SyntheticDataset;
import rules.mining.rules.BinaryRule;
import rules.mining.rules.RuleMeasures;

public class GibbsSamplingChoquetTest extends SamplingTest {

    public static void main(String[] args) throws Exception {
        SyntheticDataset datasetBuilder = new SyntheticDataset();

        // int size = 100;
        // double[][] correlation_matrix = generateSymmetricCorrelationMatrix(size);
        // double[] marginal_probabilities = generateMarginalProbabilities(size);
        // printMatrix(correlation_matrix);

        // General parameters
        int nbItems = 3;
        int nbSamples = 10;
        int nbIterations = 10;
        int number_rows = 100;
        String[] measureNames = new String[] { "support", "confidence", "lift", "cosine" };

        List<double[]> allRulesMeasures = new ArrayList<>();
        List<Double> overalls = new ArrayList<>();

        // Create a data set of BinaryRule objects
        // double[] marginal_probabilities = generateMarginalProbabilities(nbItems+2);
        int[][] transactions = datasetBuilder.get_dataset(correlation_matrix, marginal_probabilities, number_rows);

        SoftmaxTempChoquet Sampler = new SoftmaxTempChoquet(transactions, nbItems, 3, 2, measureNames);
        LinearOracle Oracle = new LinearOracle(measureNames.length);

        Sampler.sample(nbSamples * 100);
        BinaryRule[] allRules = Sampler.getSample();

        for (BinaryRule rule : allRules) {
            RuleMeasures measurer = new RuleMeasures(rule, number_rows, 0.0);
            double[] measures = measurer.computeMeasures(measureNames);
            double score = Oracle.score(measures);

            allRulesMeasures.add(measures);
            overalls.add(score);
        }
        // Convert to arrays
        double[][] allRulesMeasuresArray = allRulesMeasures.toArray(new double[0][]);
        double[] overallsArray = overalls.stream().mapToDouble(Double::doubleValue).toArray();

        System.out.println("The oracle's preferences: ");
        System.out.println(Oracle.topK(allRules, allRulesMeasuresArray, 5));

        for (int itt = 0; itt < nbIterations; itt++) {
            Sampler.sample(nbSamples);

            BinaryRule[] sample = Sampler.getSample();

            for (BinaryRule rule : sample) {
                RuleMeasures measurer = new RuleMeasures(rule, number_rows, 0.0);
                double[] measures = measurer.computeMeasures(measureNames);
                double score = Oracle.score(measures);

                allRulesMeasures.add(measures);
                overalls.add(score);
            }

            // Convert to arrays
            // double[][] allRulesMeasuresArray = allRulesMeasures.toArray(new double[0][]);
            // double[] overallsArray =
            // overalls.stream().mapToDouble(Double::doubleValue).toArray();

            System.out.println("The capacities: " + Arrays.toString(Sampler.getCapacities()));

            System.out.println("The sampled rules: ");
            System.out.println(Sampler.toString());

            Sampler.train(allRulesMeasuresArray, overallsArray);
        }

    }
}
