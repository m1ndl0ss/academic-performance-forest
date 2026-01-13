package com.ken25.App;

import java.util.ArrayList;
import java.util.List;

import com.ken25.Modules.Utility;

public class Analyzer {

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

	/**

 * Students eligible vs not eligible for graduation
    @param 2D ARRAY grades
    @return students eligible for graduation
    @author Andre

 */
    public static String[] EligibleForGraduation(String[][] array){
        String[] eligible = new String[array.length];

        for(int r = 1; r < array.length; r++){
            int noPass = 0;
            for(int c = 1; c < array[0].length; c++){
                String grade = array [r][c];
                if(grade == null || grade.equalsIgnoreCase("NG")|| grade.trim().isEmpty())
                    continue;
                if(Double.parseDouble(grade) < 6)
                    noPass++;
            }
            if(noPass < 1)
                eligible[r] = array [r][0];
            else eligible[r] = "NE";
        }
        return eligible;
    }
}
