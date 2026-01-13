package com.ken25.Modules;

public class TreeNode{
    boolean isLeaf;
    TreeNode left;
    TreeNode right;
    int splitCourseIndex; // index of which course's grade we split on
    double threshold; // cutoff value in the decision tree
    double mean;
    double variance;
    int nrOfSamples;

    //constructor of leaf node

    TreeNode(double mean, double variance, int nrOfSamples){
        this.isLeaf = true;
        this.mean = mean;
        this.variance = variance;
        this.nrOfSamples = nrOfSamples;
    }

    //constructor of internal node
    TreeNode(TreeNode left, TreeNode right, int splitCourseIndex, double threshold){
        this.isLeaf = false;
        this.left = left;
        this.right = right;
        this.splitCourseIndex = splitCourseIndex;
        this.threshold = threshold;
    }
}