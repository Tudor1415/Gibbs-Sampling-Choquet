package rules.mining.sampling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import rules.mining.rules.BinaryRule;

@Getter
@Setter
public class GibbsSampling {

    // Variables regarding the data set
    private int[][] transactions;
    private int nbItems;

    // Variables regarding the samples
    public BinaryRule[] sample;

    private BinaryRule J;

    public GibbsSampling(int[][] transactions, int nbItems) {
        this.transactions = transactions;
        this.nbItems = nbItems;

        initializeSample();
    }

    private void initializeSample() {
        // Initialize the first sample randomly
        int[] X = new int[nbItems];
        int[] Y = new int[]{0};

        Random random = new Random();
        for (int i = 0; i < nbItems; i++) {
            X[i] = (int) random.nextInt(2);
        }

        // Setting the class to 0
        Y[0] = 0;
        J = new BinaryRule(X, Y, this.transactions);
    }

    public double probability_function(BinaryRule J) {
        return 0.5;
    }

    private static int Bernoulli(double probability) {
        Random random = new Random();
        if (random.nextDouble() < probability) {
            return 1;
        }

        return 0;
    }

    public void sample(int nbIterations) {
        // Mock sample as a Set
        Set<BinaryRule> sampleSet = new HashSet<>();
        sampleSet.add(this.J);
    
        double probability;
    
        for (int itt = 1; itt < nbIterations; itt++) {
            int[] X = Arrays.copyOf(sampleSet.iterator().next().getX(), nbItems);
    
            for (int item_no = 0; item_no < nbItems; item_no++) {
                X[item_no] = 1;
                BinaryRule J_0 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);
    
                X[item_no] = 0;
                BinaryRule J_1 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);
    
                probability = probability_function(J_1) / (probability_function(J_1) + probability_function(J_0));
    
                X[item_no] = Bernoulli(probability);
            }
    
            BinaryRule sampledRule = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);
            if (sampledRule.getSupport() > 0.0) {
                sampleSet.add(sampledRule);
            }
        }
    
        sampleSet.remove(this.J);
    
        // Sort the SampleSet using g_function values (product of support and confidence)
        Set<BinaryRule> sortedSampleSet = sampleSet.stream()
                .sorted(Comparator.comparingDouble(rule -> -rule.getSupport() * rule.getConfidence()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    
        this.sample = sortedSampleSet.toArray(new BinaryRule[0]);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-55s%-10s%-10s%n", "Rule", "Support", "Confidence"));
        sb.append(String.format("%-55s%-10s%-10s%n", "----", "-------", "----------"));

        // Print each rule in the sample
        for (BinaryRule rule : this.sample) {
            int[] X = rule.getX();
            int[] Y = rule.getY();

            double support = rule.getSupport();
            double confidence = rule.getConfidence();

            sb.append(String.format("%-55s%-10.4f%-10.4f%n", Arrays.toString(X) + " => " + Arrays.toString(Y), support,
                    confidence));
        }

        return sb.toString();
    }
}
