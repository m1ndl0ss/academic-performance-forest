package com.ken25.Modules;
import java.util.*;

public class RandomForest {
    private List<RegressionTree> trees;
    private int numtrees;
    private Random random;
    private int minSamplesLeaf;
    private int maxDepth;

    public RandomForest(int numtrees, int minSamplesLeaf, int maxDepth){
        this.numtrees = numtrees;
        this.minSamplesLeaf = minSamplesLeaf;
        this.maxDepth = maxDepth;
        this.trees = new ArrayList<>();
        this.random = new Random();
    }

    public int[] boostrapSampleIndices(int numRows){
        int[] indices = new int[numRows];
        
        for(int i = 0; i<numRows; i++){
            indices[i] = random.nextInt(numRows);
        }
        return indices;
    }

    public void train(double[][] grades, double[] course){
        trees.clear();

        for(int i = 0; i<numtrees; i++){

            int[] boostrapIndices = boostrapSampleIndices(grades.length);

            double[][] bootstrapGrades = new double[boostrapIndices.length][];
            double[] bootstrapCourse = new double[boostrapIndices.length];

            for(int j = 0; j<boostrapIndices.length; j++){
                int originalIndex = boostrapIndices[j];
                bootstrapGrades[j] = grades[originalIndex];
                bootstrapCourse[j] = course[originalIndex];
            }

            RegressionTree tree = new RegressionTree(minSamplesLeaf, maxDepth);
            tree.fit(bootstrapGrades, bootstrapCourse);

            trees.add(tree);

            
            
        }
    }


    public double predict(double[] studentGrades) {
        double sum = 0.0;

        for(RegressionTree tree : trees){
            double prediction = tree.predict(studentGrades);
            sum += prediction;
        }
        return sum / trees.size();
    }

}
