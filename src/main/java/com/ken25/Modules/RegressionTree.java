package com.ken25.Modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class RegressionTree{

    private int minSamplesLeaf = 10;
    private int maxDepth = 30;
    TreeNode root;
    private double varTresh = 1e-7;

    public RegressionTree(int minSamplesLeaf, int maxDepth){
        this.minSamplesLeaf = minSamplesLeaf;
        this.maxDepth = maxDepth;
    }

    public void setMinSamplesLeaf(int x){
        this.minSamplesLeaf = x;
    }

    public void setMaxDepth(int x){
        this.maxDepth = x;
    }

    public void fit(double[][] grades, double[] course){
        int numberOfStudents = grades.length;
        int[] students = new int[numberOfStudents];
        for(int i = 0; i<numberOfStudents; i++){
            students[i] = i;
        }
        root = growTree(grades, course, students, 0);

    }

    private TreeNode growTree(double[][] grades, double[] course, int[] students, int depth){
        
        double currentMean = mean(course,students);
        double currentVar = calcVar(course, students);
        if (depth>=maxDepth || students.length<=minSamplesLeaf  || currentVar < varTresh ){
            return createLeafNode(course, students);
        }
        BestSplit bestSplit = findBestSplit(grades,course,students);
        if (bestSplit==null){
return new TreeNode(currentMean, currentVar, students.length);
        }


        int[][] splitStudents = splitData(grades, students, bestSplit.featureIndex, bestSplit.threshold);
        int[] leftStudents = splitStudents[0];
        int[] rightStudents = splitStudents[1];

        if (leftStudents.length < minSamplesLeaf || rightStudents.length < minSamplesLeaf) {
            return new TreeNode(currentMean, currentVar, students.length);
        }
        TreeNode leftChild = growTree(grades, course, leftStudents, depth + 1);
        TreeNode rightChild = growTree(grades, course, rightStudents, depth + 1);
        return new TreeNode(leftChild, rightChild, bestSplit.featureIndex, bestSplit.threshold);
    }
     /**
     * Finds the best feature and threshold to split on.
     * 
     * @return BestSplit object, or null if no valid split exists
     */
    private BestSplit findBestSplit(double[][] grades, double[] course, int[] students) {
        int numFeatures = grades[0].length;
        
        int bestFeature = -1;
        double bestThreshold = 0;
        double bestVarianceReduction = 0;
        
        double parentVariance = calcVar(course, students);
        
        for (int f = 0; f < numFeatures; f++) {
            Set<Double> uniqueValues = new TreeSet<>();
            for (int studentIdx : students) {
                uniqueValues.add(grades[studentIdx][f]);
            }
            
            for (double thresh : uniqueValues) {
                int[][] split = splitData(grades, students, f, thresh);
                int[] leftStudents = split[0];
                int[] rightStudents = split[1];
                
                if (leftStudents.length == 0 || rightStudents.length == 0) {
                    continue;
                }
                
                double leftVar = calcVar(course, leftStudents);
                double rightVar = calcVar(course, rightStudents);
                
                double leftWeight = (double) leftStudents.length / students.length;
                double rightWeight = (double) rightStudents.length / students.length;
                
                double weightedChildVariance = leftWeight * leftVar + rightWeight * rightVar;
                double vr = parentVariance - weightedChildVariance;
                
                if (vr > bestVarianceReduction) {
                    bestVarianceReduction = vr;
                    bestFeature = f;
                    bestThreshold = thresh;
                }
            }
        }
        
        if (bestFeature == -1 || bestVarianceReduction <= 0) {
            return null;
        }
        
        return new BestSplit(bestFeature, bestThreshold, bestVarianceReduction);
    }
    private TreeNode createLeafNode(double[] course, int[] students){
        double mean = mean(course, students);
        double var = calcVar(course, students);
        return new TreeNode(mean, var, students.length);
    }
    private double mean(double[] course, int[] students){
        double sum=0;
        int count=0;
        for (int studentIndex : students){
            sum+= course[studentIndex];
            count++;
        }
        return count >0 ? sum/count : 0;
    }


    private double calcVar(double[] course, int[] students){
        double mean = mean(course, students);
        double sumSqrdDif=0;
        double count=0;
        for(int studentIdx : students){
            double diff = course[studentIdx] -mean;
                sumSqrdDif+= diff * diff;
                count++;
        }
        return count > 0 ? sumSqrdDif/count : 0;
        }



    private int[][] splitData(double[][] grades, int[] students, int featureIndex, double threshold){
        List<Integer> leftList = new ArrayList<>();
        List<Integer> rightList = new ArrayList<>();

        for (int studentIdx : students) {
            if (grades[studentIdx][featureIndex] <= threshold) {
                leftList.add(studentIdx);
            } else {
                rightList.add(studentIdx);
            }
        }

        int[] leftStudents = new int[leftList.size()];
        int[] rightStudents = new int[rightList.size()];
        
        for (int i = 0; i < leftList.size(); i++) {
            leftStudents[i] = leftList.get(i);
        }
        for (int i = 0; i < rightList.size(); i++) {
            rightStudents[i] = rightList.get(i);
        }

        return new int[][]{leftStudents, rightStudents};
    }
    public double predict(double[] grades) {
        if (root == null) {
            throw new IllegalStateException("Tree has not been trained. Call fit() first.");
        }
        return predictRecursively(root, grades);
    }
    public double predictRecursively(TreeNode node, double[] grades){
        if (node.isLeaf){
            return node.mean;
        }
        if (grades[node.splitCourseIndex] <= node.threshold){
            return predictRecursively(node.left, grades);

        }
        else{
            return predictRecursively(node.right, grades);
        }
    }
    
    

    public double[] predict(double[][] features) {
        double[] predictions = new double[features.length];
        for (int i = 0; i < features.length; i++) {
            predictions[i] = predict(features[i]);
        }
        return predictions;
    }

    private static class BestSplit {
        final int featureIndex;
        final double threshold;
        final double varianceReduction;
        
        BestSplit(int featureIndex, double threshold, double varianceReduction) {
            this.featureIndex = featureIndex;
            this.threshold = threshold;
            this.varianceReduction = varianceReduction;
        }
    }
}