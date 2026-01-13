package com.ken25.Modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileDisplayer {

    public static void main(String[] args) {
		// String[][] studentInfo = Utility.fillArray("data/StudentInfo.csv");
		// String[][] currentGrades = Utility.fillArray("data/CurrentGrades.csv");
		// String[][] graduateGrades = Utility.fillArray("data/GraduateGrades.csv");
		// // System.out.print(similarCourses(currentGrades));
		// // System.out.println(cumLaude(currentGrades, 1494));
		// System.out.println(1);
		// System.out.println(worstGpa(currentGrades));
		// System.out.println(bestGpa(currentGrades));

		// propertyAnalyzer(studentInfo, currentGrades);

		// System.out.println("r = " + performanceCorrelation(currentGrades));
		double[][] grades = {
            {10, 12, 14},   // student 0
            {8,  9,  11},   // student 1
            {16, 17, 18},   // student 2
            {13, 14, 15},   // student 3
            {9,  10, 12},   // student 4
            {15, 16, 17}    // student 5
        };

        double[] course = {
            15,  // student 0
            10,  // student 1
            19,  // student 2
            16,  // student 3
            11,  // student 4
            18   // student 5
        };

        // -------------------------------
        // 2. Create and train the tree
        // -------------------------------
        RegressionTree tree = new RegressionTree(
            2,   // minSamplesLeaf
            5    // maxDepth
        );

        tree.fit(grades, course);

        // -------------------------------
        // 3. Predict for a new student
        // -------------------------------
        double[] newStudentGrades = {14, 15, 16};

        double prediction = tree.predict(newStudentGrades);

        // -------------------------------
        // 4. Print result
        // -------------------------------
        System.out.println("Predicted course grade: " + prediction);
    
    }

	public static void propertyAnalyzer(String[][] studentInfo, String[][] currentGrades) {

		int courseNumber = 10;
		int[] studentId = new int[studentInfo.length];

		for(int i = 1; i<studentInfo.length; i++){
			studentId[i]=Integer.parseInt(studentInfo[i][0]);
		}

		PropertyAnalyzer analyzer = new Psionic();
    	double[][] result = analyzer.analyze(studentInfo, currentGrades, courseNumber, studentId);

		PropertyAnalyzer quantumAnalyzer = new Quantum();
		PropertyAnalyzer astroAnalyzer = new Astro();
		PropertyAnalyzer bioAnalyzer = new Bio();
		PropertyAnalyzer psionicAnalyzer = new Psionic();
		PropertyAnalyzer symbioticAnalyzer = new Symbiotic();

		double[][] quantumResult = quantumAnalyzer.analyze(studentInfo, currentGrades, courseNumber, studentId);
		double[][] astroResult = astroAnalyzer.analyze(studentInfo, currentGrades, courseNumber, studentId);
		double[][] bioResult = bioAnalyzer.analyze(studentInfo, currentGrades, courseNumber, studentId);
		double[][] psionicResult = psionicAnalyzer.analyze(studentInfo, currentGrades, courseNumber, studentId);
		double[][] symbioticResult = symbioticAnalyzer.analyze(studentInfo, currentGrades, courseNumber, studentId);

		// for(int i = 0;i<astroResult.length;i++){
		// 	System.out.println("PROPERTY"+ i);
		// 	for(int j = 0;j<astroResult[0].length;j++){
		// 		System.out.print(astroResult[i][j]+" ");
		// 	}
		// 	System.out.println();
		// }
		// System.out.println("Bio result");
		// for(int i = 0;i<bioResult.length;i++){
		// 	System.out.println("PROPERTY"+ i);
		// 	for(int j = 0;j<bioResult[0].length;j++){
		// 		System.out.print(bioResult[i][j]+" ");
		// 	}
		// 	System.out.println();
		// }

		double[] currentGradesScores = new double[currentGrades.length];
		java.util.Arrays.fill(currentGradesScores, -1.0);
		for(int i = 1; i < studentInfo.length; i++){
   
        int sID = Integer.parseInt(studentInfo[i][0]);
        
		int gradeRow = PropertyAnalyzer.findRowById(currentGrades, sID);
        
        if(gradeRow != -1 && currentGrades[gradeRow][courseNumber] != null){
             double g = Double.parseDouble(currentGrades[gradeRow][courseNumber]);
             if(g > 0) {
                 currentGradesScores[i] = g;
             }
        }
    
}
		PropertyReduction verify = new PropertyReduction();
		
    	String best = verify.bestProperty(quantumResult, astroResult, bioResult, psionicResult, symbioticResult, currentGradesScores);
		
		System.out.println("Best property for this course: " + best);

		int targetStudentId = 312804;
		double predictedGrade = verify.PredictGradeStudent(best, currentGradesScores, targetStudentId, studentInfo);

		System.out.println("Predicted grade for student " + targetStudentId + ": " + predictedGrade);

	}
	/**
	 * checks if a student is harmonized
	 * @param StudentRow row of the student we are checking
	 * @param data array with student data
	 * @return true if harmonized
	 */
	public static boolean  HarmonizedCheck(int StudentRow, String data[][]){
		if (data[StudentRow][2] == null){return false;}
		if (data[StudentRow][2]=="Harmonized"){return true;}
		return false;
	}
	public static double StudentGPA(String[][] grades, int StudentRow){
		int cols= grades[0].length;
		double sum=0;
		int count=0;
		for (int i=1; i<cols;i++){
			String v= grades[StudentRow][cols];
			if (v==null|| v=="NG"){continue;}
			sum+=Double.parseDouble(v);
			count++;
			if (count==0){return 0;}
		}
		return sum/count;
	}
  /**
	 * method calculates the average for each course, for use in most easy and most dificult methods.  In future make sure to look for min/max elements starting from index 1, as index 0 is ==0.
	 * @param array with grades
	 * @return array with averages
	 * @author Matvei
	 */
	public static double[] averages(String[][] array){
		int columns = array[0].length; // number of courses + 1 (student id)
		int rows = array.length; 

		double[] averages = new double[columns - 1];

		for (int col = 1; col < columns; col++){
			double totalForCourse = 0;
			int countValidGrades = 0; // count only non NG grades

			for (int row = 1; row < rows - 1; row++){
				String value = array[row][col];
				if (value == null || value.isEmpty() || value.equalsIgnoreCase("NG")) continue;

				totalForCourse += Double.parseDouble(value);
				countValidGrades++;
			}

			if (countValidGrades > 0) averages[col-1] = totalForCourse / countValidGrades;
 			else averages[col - 1] = Double.NaN; // No grades for this course
		}
		return averages;
	}

	//below we have 2 methods for question 1 in step 1:  finding most easy and most difficult subject
	public static String mostEasy(String[][] array, double[] avg){ 
        double max = 0;
        int index = 0;
        for (int i = 0; i < avg.length; i++) {
            if (avg[i] > max && avg[i] != Double.NaN) {
                max = avg[i];
                index = i;
            }
        }
		return array[0][index+1];
	}

	public static String mostDifficult(String[][] array, double[] avg){
        double min = 11;
        int index = 0;
        for (int i = 0; i < avg.length; i++) {
            if (avg[i] < min && avg[i] != Double.NaN) {
                min = avg[i];
                index = i;
            }
        }
		return array[0][index+1];
	}
/**
 * @Deprecated
*avoid using this, feedback said we shouldnt go for std..
@author matvei
*/	
 public static String[] similarCourses(String[][] array){
		int rows = array.length;
		int cols = array[0].length;
		String[] Similar = new String[cols];
		double[] stdDevs = new double[cols - 1]; // ignore StudentID column
		
		// Compute averages first
		double[] averages = new double[cols - 1];
		for (int indexc = 1; indexc < cols; indexc++) {
			double sum = 0;
			for (int r = 1; r < rows; r++) {
				sum += Double.parseDouble(array[r][indexc]);
			}
			averages[indexc - 1] = sum / (rows - 1);
		}
		
		// Compute standard deviation for each course
		for (int c = 1; c < cols; c++) {
			double sumSq = 0;
			for (int r = 1; r < rows; r++) {
				double value = Double.parseDouble(array[r][c]);
				sumSq += Math.pow(value - averages[c - 1], 2);
			}
			stdDevs[c - 1] = Math.sqrt(sumSq / (rows - 1));
		}

		//Compare stds
		for (int i=1; i<stdDevs.length; i++){
			int similarityScore = 0;
			for ( int j=1; j<stdDevs.length; j++){
				if (stdDevs[i] - stdDevs[j]<0.2) similarityScore +=1;

			if (similarityScore> stdDevs.length*0.15) Similar[i] = array[0][i];
			}
		}
		return Similar;
	}

	/**
	 * for this method i decided we will check difference in means for grades according to a threshold and fill a list with the ones similar enough. 
	 * @param array with data, a threshold for just HOW similar we need the methods to be
	 * @return List with similar pairs of courses according to our threshold. 
	 * @see averages method.
	 * @author Matvei
	 * 
	 */
	public static List<List<String>> similarCoursesByMean(String[][] array, double threshold) {
		List<List<String>> similar = new ArrayList<>();
		 

		String[] CourseNameStrings = array[0];
		double[] avg =averages(array);
		int courseCount = avg.length;
		for ( int i =1; i< courseCount; i++){
			for (int j=1; j<courseCount; j++){
				double meanDiff = Math.abs(avg[i]-avg[j]);
				if( meanDiff<threshold){
					List<String> pair = new ArrayList<>();
					pair.add(CourseNameStrings[i+1]);
					pair.add(CourseNameStrings[j+1]);
					similar.add(pair);
				}
			}
		}
		return similar;
	}

	public static boolean cumLaude(String[][] array, int studentId) {
		int rows = array.length;
		int columns = array[0].length;
		double[] grades = new double[rows];

		for(int i=1; i<rows; i++){
			double grade = 0.0;
			for(int j=1; j<columns; j++){
				String value = array[i][j];
				if(value == null || value.isEmpty()){
					continue;
				}else {
				grade += Double.parseDouble(array[i][j]);
				}

			}
			grades[i]=grade;
		
		}

		double target = grades[studentId+1];
		Arrays.sort(grades);
		int fiftiethPercentile = (int) Math.ceil(0.85*(rows - 1));
		double cutoff = grades[fiftiethPercentile];

		return target >= cutoff;
	}

	// Percentage of graded students in each course
	public static double[] completionRates(String[][] array) {
		int cols = array[0].length;
		int rows = array.length;
		double[] rates = new double[cols - 1];

		for (int c = 1; c < cols; c++) {
			int count = 0;
			for (int r = 1; r < rows; r++) {
				String value = array[r][c];
				if (value == null || value.equalsIgnoreCase("NG") || value.isEmpty()) continue;
				count++;
			}
			rates[c - 1] = (double) count / (rows - 1) * 100.0;
		}
		return rates;
	}
	
	public static int[] ngPerStudent(String[][] array) {
		int cols = array[0].length;
		int rows = array.length;
		int[] missingCounts = new int[rows - 1]; // exclude header

		for (int r = 1; r < rows; r++) {
			int count = 0;
			for (int c = 1; c < cols; c++) {
				String value = array[r][c];
				if (value == null || value.equalsIgnoreCase("NG") || value.isEmpty()) {
					count++;
				}
			}
			missingCounts[r - 1] = count;
		}
		return missingCounts;
	}

	public static double bestGpa(String[][] array){
		int rows = array.length;
		int columns = array[0].length;
		double bestGrade = 0;

		for(int i = 1;i < rows;i++){
			double sumOfGrade = 0;
			int count = 0;
			
			for(int j = 1; j < columns; j++){
				String value = array[i][j];
				if(value == null || value.equalsIgnoreCase("NG")|| value.trim().isEmpty()){
					continue;
				}
				try{
					sumOfGrade += Double.parseDouble(value.trim());
					count++;
				}
				catch(NumberFormatException error){

				}
			}
			double averageGrade = sumOfGrade/count;
			if(averageGrade>bestGrade){
				bestGrade = averageGrade;
			}
		}
		return bestGrade;
	}

	public static double worstGpa(String[][] array){
		int rows = array.length;
		int columns = array[0].length;
		double worstGrade = 10;

		for(int i = 1;i < rows; i++){
			double sumOfGrades = 0;
			double count = 0;
			for(int j = 1; j< columns; j++){
				String value = array[i][j];
				if(value == null || value.equalsIgnoreCase("NG") || value.trim().isEmpty()){
					continue;
				}
				try{
					sumOfGrades += Double.parseDouble(value.trim());
					count++;
				}
				catch(NumberFormatException error){

				}
			}
			double averageGrade = sumOfGrades/count;
			if(averageGrade < worstGrade){
				worstGrade = averageGrade;
			}
		}
		return worstGrade;
	}
	// This function predicts whether a student will be eligible to graduate
	// by the end of the program based on their current grades.
	public static String[] eligibleForGrad(String[][] array){
		String[] eligible = new String[array.length - 1];
		//eligible for grad
		for (int rows=1; rows< array.length; rows++){
			int nonPass=0;
			for (int cols=1; cols < array[0].length; cols++){
				String value = array[rows][cols];
				if (value.equals("NG") || value == (null)) continue;
				if (Double.parseDouble(array[rows][cols]) < 6.0) nonPass++;
			} 
			if (nonPass<1) eligible[rows]=array[rows][0];
			else eligible[rows]="NE";
		}

		//System.out.println("done");
		//System.out.println(Arrays.deepToString(eligible));
		return eligible;
	}

	// Do students who perform well in early courses also perform well in later courses.
	public static double performanceCorrelation(String[][] array) {
		double[] rates = completionRates(array);
		List<Integer> earlyCourses = new ArrayList<>();
		List<Integer> lateCourses  = new ArrayList<>();

		for (int i = 0; i < rates.length; i++) {
			double rate = rates[i];
			if (rate >= 90) {
				earlyCourses.add(i + 1);
			} else if (rate < 50 && rate > 0) {
				lateCourses.add(i + 1);
			}
		}

		int[] earlyCols = earlyCourses.stream().mapToInt(Integer::intValue).toArray();
        int[] lateCols  = lateCourses.stream().mapToInt(Integer::intValue).toArray();

		double[] earlyAvg = Utility.studentAverage(array, earlyCols);
        double[] lateAvg  = Utility.studentAverage(array, lateCols);

		double r = Utility.correlation(earlyAvg, lateAvg);

		return r;
	}

	//predicting passing grades
	public static double prediction(String[][] array){
		int rows = array.length;
		int columns = array[0].length;
		double[] grades = new double[array.length];
		for(int i = 1;i < rows;i++){
			double gradeForCourse = 0;
			int gradeCounter=0;
			for(int j = 1; j<columns;j++){
				if (array[i][j]==null || array[i][j].equals("NG")){
					gradeCounter++;
					continue;
				}
				else{
					gradeForCourse += Double.parseDouble(array[i][j]);
					gradeCounter++;
				}
			}
			grades[i] = gradeForCourse/gradeCounter;  //for each course;
		}
		int pass=0;
		for (int x=0; x<grades.length;x++){
			if (grades[x] >5.5){
				pass++;
			}
		}
		double rate = (double) pass / (grades.length - 1) * 100;
		return rate;
	}
















	/**
	 * the overalll prediction method takes the students current GPA, and uses is as a default value. Then, we adjust our prediction based on all of those stumps below.
	 * @param grades     2D array of student grades.
	 * @param StudentRow   row of the student we need the prediction for
	 * @return predictedGPA.
	 */
	public static double OverallPrediction(String[][] grades, int StudentRow, String[][] data){
		double currentGPA=StudentGPA(grades, StudentRow);

		double variance= variance(grades, StudentRow);
		double consistency = consistencyStump(grades, StudentRow);
		double ngRIsk= ngRISKstump(grades, StudentRow);
		double improvement=improvementStump(grades, StudentRow);
		// double passFailMargin = passFailMarginStump(grades,StudentRow);
		//by testing in main ive proven that Harmonized property has a positive impact on grades, therefore
		if( HarmonizedCheck(StudentRow, data)){
			currentGPA*=1.1;
		}
		currentGPA-=ngRIsk *0.3;
		currentGPA-=variance*0.1;
		currentGPA+=consistency*0.3;
		currentGPA+= improvement*0.15;
		
		return currentGPA;
	}


	 /**
		* COmputes the variance of all available non-NG grades 	for a given student
		* i suggest running this in a for loop with all needed students
		* @param 2D ARRAY grades, row of student ( >=1)
		* @return the variance of student's grades or 0 if there arents enough grades for computation.
		*@author Matvei
	  */
	 public static double variance (String[][] grades, int StudentRow){
		int collums = grades[0].length;
		double sum=0;
		int n=0;
		for (int c = 1; c < collums; c++) {
			String v = grades[StudentRow][c];
			if (v == null || v.equals("NG")) continue;
			sum += Double.parseDouble(v);
			n++;
    	}
		if (n<=1){ return 0;}

		double mean = sum /n;
		double sumSquares =0;
		for (int c =1; c< collums; c++) {
			String v = grades[StudentRow][c];
			if (v == null || v.equals("NG")) continue;
      double x = Double.parseDouble(v);
		  sumSquares += (x - mean) * (x - mean);
		}
		return sumSquares/n;
	 }
	 /**
     * this method computes the grade range (max minus min) for a given student,skipping invalid values
     * @param 2D ARRAY grades
     * @return the difference between highest and lowest valid grades
     * @author Matvei
     */
	 public static double consistencyStump(String[][] grades, int studentRow){
		int cols = grades[0].length;
		double min=10;
		double max=0;
		for (int i=1;i<cols;i++){
			String v = grades[studentRow][i];
			if (v == null || v.equals("NG")) continue;
			double g = Double.parseDouble(v);
			if (g<min){min=g;}
			if (g>max){max=g;}
		}
		return max-min;
		}
	
		/**
		 * this method counts how many ng or missing grades a student has, finding the ratio.
		 * @return ratio count/total
		 * @param 2D ARRAY of grades, studentRow
		 * @author Matvei		
		*/
		public static double ngRISKstump(String[][] grades, int StudentRow){
			int cols = grades[0].length;
			int total = cols-1;
			int count=0;
			for (int i=1;i<cols;i++){
				String v = grades[StudentRow][i];
				if (v == null || v.equals("NG")){count++;}
			}
			double a= (double)count/total;
			return a;
		}
	 
	 /**
		* computes the average improvement between consecutive grades for a given student, skipping null and NG values
		@param 2D ARRAY grades, row of student ( >=1)
		@return the average improvement accross all valid consecutive grade pairs.
		@author Matvei
	  */
	public static double improvementStump(String[][]grades, int StudentRow){
		int collums = grades[0].length;
		double sum=0;
		int count=0;
		for (int c = 2; c < collums; c++) { // 2 cuz we check value -1
			String currentGrade = grades[StudentRow][c];
			String previousGrade = grades[StudentRow][c-1];
			if(previousGrade==null || currentGrade==null){ continue;}

			if(previousGrade.equals("NG") || currentGrade.equals("NG")){ continue;}
			double diff = Double.parseDouble(currentGrade) - Double.parseDouble(previousGrade);
			sum+=diff;
			count++;
		}
		if (count==0){return 0;}
		return count/sum;
	}

	
	/**
	 * this method checks if the student has overall been succesfful been passing their courses so far
	 * @param grades 2D ARRAY OF GRRADES
	 * @param StudentRow row of the student we are checking
	 * @return  ratio.
	 * @author Matvei
	 */

	//  public double passFailMarginStump(String[][] grades, int StudentRow){
	// 	int collums =grades[0].length;
	// 	double[] margins = new double[collums-1];
		
	// 	for (int c=1; c< collums; c++){
	// 		String v= grades[StudentRow][c];
	// 		if (v==null || v.equals("NG")){ margins[c-1]=-1;}
	// 		else{ margins[c-1] =Math.abs(Double.parseDouble(v)-<PASSING GRADE>;} //FIX
	// 	}
	// 	return variance(margins, StudentRow);  //FIX
	// }




	/**
	 * this method checks if students with non-null good grades have the harmonized property.
	 * @param grades 2D array of studentgrades
	 * @param data 2D array of studentData
	 * @return ratio of Harmonized good students compared to all good students
	 * @author matvei
	 */
	public static double correlationCheckerStump(String[][] grades,String[][] data){
		int collums = grades[0].length;
		int rows=grades.length;
		int harmTotal=collums-1;
		int harmGood=0;
		int nonTotal=0;
		int nonHarmGood=0;

		for (int r=0; r<rows;r++){
			boolean IsHarmonized = HarmonizedCheck(r, data);

			for (int c = 1; c < collums; c++) { 
				String v=grades[r][c];
				if (v==null || v.equals("NG")){continue;}
				double g= Double.parseDouble(v);
				if (IsHarmonized){ harmTotal++; if(g>5){harmGood++;}}
				else{nonTotal++; if (g>5){nonHarmGood++;}}
			}
		}
		double dH = (double) harmGood/harmTotal;
		double dN = (double)nonHarmGood/nonTotal;
		return dN/dH;
	}
	
}