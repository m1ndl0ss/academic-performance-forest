package com.ken25.Modules;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Utility {

//------------------------------------------------------------------------------------------------------//
//                                           Printing Functions                                         //
//------------------------------------------------------------------------------------------------------//

    // Print the completion rate of each course
	public static void printCourseProgress(String[] courses, double[] completionRates) {
		System.out.printf("%-35s %-12s %-20s%n", "Course", "Completion %", "Category");
		System.out.println("--------------------------------------------------------------------------");

		for (int i = 0; i < courses.length; i++) {
			double rate = completionRates[i];
			String category;

			if (rate >= 90) {
				category = "Early / Core";
			} else if (rate >= 50) {
				category = "Mid-program";
			} else if (rate > 0) {
				category = "Late / Elective";
			} else {
				category = "Next Semester";
			}

			System.out.printf("%-35s %-12.2f %-20s%n", courses[i], rate, category);
		}
	}

    // Print the completed courses per student (disregarding grades)
    public static void printSoonToGraduate(String[][] array, int maxMissingAllowed, int[] missingCounts) {
        System.out.println("=== Students Close to Graduation ===");
        System.out.printf("%-15s %-10s %-10s%n", "StudentID", "Completed", "Remaining");

        for (int i = 0; i < missingCounts.length; i++) {
            if (missingCounts[i] <= maxMissingAllowed) {
                String studentID = array[i + 1][0]; // +1 because missingCounts skips header
                System.out.printf("%-15s %-10d %-10d%n", studentID, array[0].length - missingCounts[i] - 1, missingCounts[i]);
            }
        }
    }


//------------------------------------------------------------------------------------------------------//
//                                           Array Utils                                                //
//------------------------------------------------------------------------------------------------------//

    // Converts csv files into String arrays
    public static String[][] fillArray(String filepath) {   
		try {
			File file=new File(filepath);
			Scanner counter = new Scanner(file);
			int rows = 0;
			int collums = 0;
			while(counter.hasNextLine()){
				String line = counter.nextLine();
				rows++;
				String[] comma = line.split(",");
				collums = comma.length;
			}
			counter.close();

			String[][] array = new String[rows][collums];

			 // This code uses two Scanners, one which scans the file line per line
            Scanner fileScanner = new Scanner(file);

            int row = 0;
            
            while (fileScanner.hasNextLine()) {
            	String line = fileScanner.nextLine();
				String[] comma = line.split(",");
				for(int i = 0; i<collums; i++){
					String value = comma[i].trim();
                    if (value.equals("NG")) array[row][i] = null;
					else array[row][i] = comma[i].trim();
				}
            	row++;
			}
			fileScanner.close();
			return array;
			
		} catch (Exception e) {
			e.printStackTrace();
        	return null;
		}
	}

    // Pearson correlation between two arrays
    public static double correlation(double[] x, double[] y) {
        double meanX = 0, meanY = 0;
        int n = 0;

        for (int i = 0; i < x.length; i++) {
            if (!Double.isNaN(x[i]) && !Double.isNaN(y[i])) {
                meanX += x[i];
                meanY += y[i];
                n++;
            }
        }
        meanX /= n;
        meanY /= n;

        double numerator = 0;
        double denomX = 0;
        double denomY = 0;

        for (int i = 0; i < x.length; i++) {
            if (!Double.isNaN(x[i]) && !Double.isNaN(y[i])) {
                double dx = x[i] - meanX;
                double dy = y[i] - meanY;
                numerator += dx * dy;
                denomX += dx * dx;
                denomY += dy * dy;
            }
        }

        return numerator / Math.sqrt(denomX * denomY);
    }

	// Computes per-student average for given columns, skipping NG
    public static double[] studentAverage(String[][] data, int[] columns) {
        int numStudents = data.length - 1;
        double[] averages = new double[numStudents];

        for (int r = 1; r < data.length; r++) {
            double total = 0;
            int count = 0;

            for (int c : columns) {
                String value = data[r][c];
                if (value != null && !value.equalsIgnoreCase("NG") && !value.isEmpty()) {
                    try {
                        total += Double.parseDouble(value);
                        count++;
                    } catch (NumberFormatException e) {
                        // skip malformed entries
                    }
                }
            }

            averages[r - 1] = (count > 0) ? total / count : Double.NaN;
        }
        return averages;
    }

    public static double[][] getGrades(String[][] data) {
        int studentCount = data.length - 1;   // skip header row
        int courseCount  = data[0].length - 1; // skip student column
        double[][] grades = new double[studentCount][courseCount];
        
        for (int r = 1; r < data.length; r++) {
            for (int c = 1; c < data[0].length; c++) {
                String value = data[r][c];
                if (value == null || value.isEmpty() || value.equalsIgnoreCase("NG")) {
                    grades[r - 1][c - 1] = Double.NaN;
                } else {
                    try {
                        grades[r - 1][c - 1] = Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        grades[r - 1][c - 1] = Double.NaN;
                    }
                }
            }
        }
        return grades;
    }

    /**
     * Calculates linear regression coefficients using least squares.
     * @param x independent variable values
     * @param y dependent variable values
     * @return double[] where [0] = slope, [1] = intercept
     */
    public static double[] linearRegression(double[] x, double[] y) {
        int n = x.length;
        
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        
        return new double[] { slope, intercept };
    }

    /**
     * Loads StudentInfo.csv and returns a map of StudentID -> attribute value
     * @param studentInfoData the parsed StudentInfo.csv as String[][]
     * @param attributeColumn the column index of the attribute (1-5)
     */
    public static Map<String, String> getStudentAttribute(String[][] studentInfoData, int attributeColumn) {
        Map<String, String> attributeMap = new HashMap<>();
        for (int r = 1; r < studentInfoData.length; r++) {
            String studentId = studentInfoData[r][0];
            String attribute = studentInfoData[r][attributeColumn];
            if (attribute != null) {
                attributeMap.put(studentId, attribute.trim());
            }
        }
        return attributeMap;
    }

}
