package rules.mining.sampling;

import lombok.Getter;
import lombok.Setter;
import rules.mining.requests.ChoquetCompute;
import rules.mining.requests.ChoquetLearn;
import rules.mining.rules.BinaryRule;
import rules.mining.rules.RuleMeasures;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Getter
@Setter
public class SoftmaxTempChoquet {

    private int[][] transactions;
    private int nbItems;
    public BinaryRule[] sample;
    private BinaryRule J;
    private double[] capacities;
    private int[] subsets;
    private int additivity;
    private ChoquetCompute Computer = new ChoquetCompute();
    private ChoquetLearn Trainer = new ChoquetLearn();
    private double temp;
    private String[] measureNames = new String[]{"support", "confidence", "lift", "cosine", "phi", "kruskal"};

    public SoftmaxTempChoquet(int[][] transactions, int nbItems, double temp, int additivity, String[] measureNames) {
        this.transactions = transactions;
        this.nbItems = nbItems;
        this.temp = temp;
        this.additivity = additivity;
        this.measureNames = measureNames;

        initializeSample();
    }

    public SoftmaxTempChoquet(int[][] transactions, int nbItems, double temp, int additivity) {
        this.transactions = transactions;
        this.nbItems = nbItems;
        this.temp = temp;
        this.additivity = additivity;

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
    
    public double importance_function(BinaryRule J) throws IOException {
		RuleMeasures measurer = new RuleMeasures(J, this.transactions.length, 0.0);
		double[] measures = measurer.computeMeasures(this.measureNames);

		if(this.capacities == null) {
			return Math.max(J.getSupport(), J.getConfidence());
		} else {
            // System.out.println("Measures: " + Arrays.toString(measures) + " Capacity length: " + this.capacities.length);
			return Computer.evaluate(measures, capacities, this.additivity, this.subsets);
		}
    }

	public double probability_function(BinaryRule J) throws IOException {
        return Math.exp(temp * importance_function(J));
    }
    
    private static int Bernoulli(double probability) {
        Random random = new Random();
        if (random.nextDouble() < probability) {
        	return 1;
        }

        return 0;
    }
    
    public void sample(int nbSamples) throws IOException {
        // Mock sample
        Set<BinaryRule> sampleSet = new HashSet<>();
        sampleSet.add(this.J);

        double probability;

        for (int itt = 1; itt < nbSamples; itt++) {
            int[] X = Arrays.copyOf(sampleSet.iterator().next().getX(), nbItems);

            for (int item_no = 0; item_no < nbItems; item_no++) {
                X[item_no] = 1;
                // System.out.println("Debug: X[" + item_no + "] set to 1: " + Arrays.toString(X));
                BinaryRule J_1 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);
                // System.out.println("Debug: J_1: " + J_1.toString());
                double probJ_1 = probability_function(J_1);
                // System.out.println("Debug: probJ_1: " + probJ_1);

                X[item_no] = 0;
                // System.out.println("Debug: X[" + item_no + "] set to 0: " + Arrays.toString(X));
                BinaryRule J_0 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);
                // System.out.println("Debug: J_0: " + J_0.toString());
                double probJ_0 = probability_function(J_0);
                // System.out.println("Debug: probJ_0: " + probJ_0);

                probability = probJ_1 / (probJ_1 + probJ_0);

                X[item_no] = Bernoulli(probability);

                // Debugging print statement
                // System.out.println("Debug: X after Bernoulli: " + Arrays.toString(X));

                // X[item_no] = 1;
                // BinaryRule J_0 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);

                // X[item_no] = 0;
                // BinaryRule J_1 = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);

                // probability = probability_function(J_1) / (probability_function(J_1) + probability_function(J_0));

                // X[item_no] = Bernoulli(probability);
            }

            BinaryRule sampledRule = new BinaryRule(Arrays.copyOf(X, X.length), J.getY(), this.transactions);
            if (sampledRule.getSupport() > 0.0) {
                sampleSet.add(sampledRule);
            }

            // Debugging print statement
            // System.out.println("Debug: sampledRule: " + sampledRule);
        }

        sampleSet.remove(this.J);

        this.sample = sampleSet.toArray(new BinaryRule[0]);;

    }

	public void train(double[][] measures, double[] overall) throws IOException {
		Trainer.train(measures, overall, additivity);
        
        double[] capacities = Trainer.getCapacities();
        int[] subsets = Trainer.getSubsets();
        
        this.capacities = capacities;
        this.subsets = subsets;
	}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
    
        // Header - Rule names
        sb.append(String.format("%-25s", "Rule"));
        for (String measureName : measureNames) {
            sb.append(String.format("%-15s", measureName));
        }

        sb.append("\n");
    
        // Separator
        sb.append(String.format("%-25s", "----"));
        for (int i = 0; i < measureNames.length; i++) {
            sb.append(String.format("%-15s", "-------"));
        }
    
        sb.append("\n");

        // Print each rule in the sample
        for (BinaryRule rule : this.sample) {
            RuleMeasures measurer = new RuleMeasures(rule, this.transactions.length, 0.0);
            double[] measures = measurer.computeMeasures(measureNames);
    
            // Rule details
            sb.append(String.format("%-25s", rule.toString()));
            for (double measure : measures) {
                sb.append(String.format("%-15.4f", measure));
            }
            sb.append("\n");

        }
    
        return sb.toString();
    }
    
}
