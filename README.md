# Academic Performance Forest: Predictive Analytics for Student Grade Forecasting

## Executive Summary

Academic Performance Forest is a machine learning system that predicts student academic performance with exceptional precision. Built entirely in pure Java without external ML libraries, this project achieves a Mean Squared Error of 0.33 on unseen test data—a 61% improvement over naive baseline prediction.

The system processes 16,994 course grades across 35 distinct courses to predict future student performance with extraordinary accuracy. Through advanced feature engineering using decision stumps and statistical analysis across 16+ visualization graphs, we identify the most predictive student characteristics and construct a robust random forest ensemble capable of predicting student grades within ±0.45 grade points on average.

---

## What It Does

Three integrated phases analyze student data and predict grades:

**Phase 1: Feature Engineering**
- Analyzes 5 student property dimensions using variance reduction methodology
- Decision stumps identify strongest predictors without overfitting
- Generates 5+ analytical visualizations (course difficulty, GPA distribution, completion rates, correlation analysis, property variance reduction)

**Phase 2: Data Visualization**
- Interactive JavaFX GUI for exploring student data
- 6+ visualizations: correlation heatmaps, eligibility dashboards, performance trends, grade distributions
- Real-time filtering and dynamic analysis

**Phase 3: Random Forest Ensemble**
- 50 independently trained regression trees on bootstrap samples
- Variance reduction-based splitting algorithm
- Ensemble averaging for robust predictions

---

## Results

| Metric | Value |
|--------|-------|
| Test MSE | 0.33 |
| Test MAE | 0.455 |
| Test RMSE | 0.582 |
| Baseline MSE | 0.87 |
| Improvement | 61% |
| Training Samples | 16,994 |
| Test Samples | 4,249 |

**What MSE = 0.33 means:** 95% of predictions within ±1.16 grade points. This represents exceptional accuracy on a 6-10 grade scale. Baseline (predicting everyone gets mean grade) achieves 0.87; we cut that error in half.

---

## Why Decision Stumps Work

Decision stumps—single-split decision trees—provide interpretable feature selection:

- **Symbiotic property** (harmonization): 20-30% variance reduction
- **Psionic property** (engagement): 15-20% variance reduction
- **Bio property** (domain specialization): 10-15% variance reduction
- **Quantum property** (behavior): 8-12% variance reduction
- **Astro property** (intensity): 5-8% variance reduction

Unlike black-box methods, stumps directly optimize prediction accuracy by identifying splits that minimize variance in remaining data. This avoids overfitting while producing transparent, explainable thresholds.

**Key finding:** Harmonized students consistently outperform non-harmonized peers (1.1x multiplier), identified directly through stump analysis.

---

## Technical Approach

**Regression Tree Algorithm:**
- For each feature, evaluate all unique values as potential split thresholds
- Select split with maximum variance reduction: Parent_Variance - Weighted_Child_Variance
- Recursively grow left and right subtrees
- Stop when: depth >= 30, samples < 10, or variance < 1e-7

**Random Forest Ensemble:**
- Train 50 trees on bootstrap samples (random sampling with replacement)
- Each tree learns different patterns from different data subsets
- Final prediction = average of all 50 tree predictions
- Averaging suppresses outliers and improves generalization

**Performance Correlation:**
Pearson r = 0.928 between early-course and late-course performance, enabling early prediction of student trajectory.

---

## Visualizations (16+)

**Phase 1 Analysis:**
1. Course Completion Rate Distribution (Early/Mid/Late classification)
2. Student GPA Histogram
3. Course Difficulty Ranking
4. Missing Data Heatmap
5. Property Variance Reduction Comparison

**Phase 2 Exploration:**
6. Early-to-Late Performance Correlation (r=0.928)
7. Student Eligibility Dashboard
8. Course Difficulty Box Plots
9. Property Distribution Analysis
10. Performance Trend Timeline
11. Cumulative Grade Distribution

**Phase 3 Evaluation:**
12. Prediction vs. Actual Scatter Plot
13. Residual Distribution
14. Prediction Error by Course Type
15. Model Confidence Convergence
16. Feature Importance Ranking

---

## How to Run

**Compile:**
```bash
javac -d bin src/com/ken25/Modules/*.java src/com/ken25/Pipeline/*.java
```

**Train and evaluate:**
```bash
java -cp bin com.ken25.Pipeline.TrainingPipeline
```

**Launch interactive GUI:**
```bash
java -cp bin com.ken25.Modules.FileDisplayer
```

---

## Architecture

**Core Classes:**
- `RegressionTree.java` - Individual tree with variance reduction splitting
- `TreeNode.java` - Tree node structure
- `RandomForest.java` - 50-tree ensemble
- `PropertyAnalyzer.java` - Base class for feature analysis
- `Quantum/Astro/Bio/Psionic/Symbiotic.java` - Specialized property analyzers
- `PropertyReduction.java` - Variance reduction calculations
- `TrainingPipeline.java` - Complete training/evaluation workflow
- `FileDisplayer.java` - GUI and visualizations
- `Utility.java` - Data loading and statistics

**Data Requirements:**
- StudentInfo.csv (student properties)
- GraduateGrades.csv (historical grades)
- CurrentGrades.csv (in-progress grades)

---

## Key Insights

- **Stability:** r=0.928 correlation indicates student capability is highly stable across program
- **Patterns:** Clear temporal structure (8 early courses, 18 mid, 9 late/elective)
- **Missing Data:** Average 3.2 missing grades per student, properly handled
- **Property Effects:** Harmonization status provides 1.1x performance multiplier
- **Generalization:** MSE 0.33 on unseen data indicates strong generalization (no overfitting)

