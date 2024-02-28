package rules.mining.oracles;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import rules.mining.rules.IRule;

@Getter
@Setter
public class LinearOracle {
    private double[] weights;

    private static double[] generateRandomWeights(int size) {
        double[] randomArray = new double[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            randomArray[i] = random.nextDouble();
        }

        return randomArray;
    }

    public LinearOracle(int nbMeasures) {
        this.weights = generateRandomWeights(nbMeasures);
    }

    public double score(double[] measures) {
        double score = 0;
        
        for(int i=0; i< measures.length; i++) {
            score += measures[i]*this.weights[i];
        }

        return score;
    }

    public IRule[] rank(IRule[] rules, double[][] measures) {
        // Calculate scores for each rule
        double[] scores = new double[rules.length];
        for (int i = 0; i < rules.length; i++) {
            scores[i] = score(measures[i]);
        }

        // Create an array of indices to keep track of original positions
        Integer[] indices = new Integer[rules.length];
        for (int i = 0; i < rules.length; i++) {
            indices[i] = i;
        }

        // Sort indices based on scores in descending order
        Arrays.sort(indices, Comparator.comparingDouble((Integer i) -> scores[i]).reversed());

        // Create a new array of rules sorted based on scores
        IRule[] sortedRules = new IRule[rules.length];
        for (int i = 0; i < rules.length; i++) {
            sortedRules[i] = rules[indices[i]];
        }

        return sortedRules;
    }

    public String topK(IRule[] rules, double[][] measures, int K) {
        IRule[] rankedRules = rank(rules, measures);

        StringBuilder sb = new StringBuilder();
        sb.append("Rank\tRule\n");
        sb.append("----\t----\n");

        for (int i = 0; i < Math.min(rankedRules.length, K); i++) {
            sb.append(String.format("%-5d\t%s%n", i + 1, rankedRules[i].toString()));
        }

        return sb.toString();
    }
}
