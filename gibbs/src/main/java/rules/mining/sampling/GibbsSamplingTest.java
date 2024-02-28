package rules.mining.sampling;

import rules.mining.requests.SyntheticDataset;

public class GibbsSamplingTest extends SamplingTest {
 public void main(String[] args) throws Exception {
        SyntheticDataset datasetBuilder = new SyntheticDataset();

        // int size = 100;
        // double[][] correlation_matrix = generateSymmetricCorrelationMatrix(size);
        // double[] marginal_probabilities = generateMarginalProbabilities(size);
        // printMatrix(correlation_matrix);
        int number_rows = 1000;

        // Create a data set of BinaryRule objects
        int[][] transactions = datasetBuilder.get_dataset(correlation_matrix, marginal_probabilities, number_rows);

        // Instantiate GibbsSampling with the data set and other parameters
        int nbItems = 3; 
        
        GibbsSampling gibbsSampling = new GibbsSampling(transactions, nbItems);
        SoftmaxTemp softmaxTemp = new SoftmaxTemp(transactions, nbItems, "prod", 3);
        
        // Run Gibbs Sampling algorithm for a certain number of iterations
        int numIterations = 50;
        
        // Print the results
        System.out.println("Results for fixed Gibbs Sampling");
        gibbsSampling.sample(numIterations);
        System.out.println(gibbsSampling.toString());
        
        System.out.println("Results for Softmax with Temp 3");
        softmaxTemp.sample(numIterations);
        System.out.println(softmaxTemp.toString());
        
        System.out.println("Results for Softmax with Temp 6");
        softmaxTemp.setTemp(6);
        softmaxTemp.sample(numIterations);
        System.out.println(softmaxTemp.toString());
        
        System.out.println("Results for Softmax with Temp 10");
        softmaxTemp.setTemp(10);
        softmaxTemp.sample(numIterations);
        System.out.println(softmaxTemp.toString());
    }
}

