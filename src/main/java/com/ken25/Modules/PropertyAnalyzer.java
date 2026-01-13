package com.ken25.Modules;

import java.util.Arrays;

public abstract class PropertyAnalyzer {
    private String[] properties;
    private int propertyColumn;

    public PropertyAnalyzer(String[] properties, int propertyColumn){
        this.properties = properties;
        this.propertyColumn = propertyColumn;
    }

    public double[][] analyze(String[][] studentInfo, String[][] currentGrades, int courseNumber, int[] students){

		double[][] result = new double[properties.length][students.length];
        for (double[] row : result) Arrays.fill(row, -1.0);
        
        for(int i = 0; i < students.length; i++){
            int studentID = students[i];

            int infoRow = findRowById(studentInfo, studentID);
            if (infoRow == -1) continue;

            int gradeRow = findRowById(currentGrades, studentID);
            if (gradeRow == -1) continue;

            if (currentGrades[gradeRow][courseNumber] == null) continue;

            try {
                double grade = Double.parseDouble(currentGrades[gradeRow][courseNumber]);
                
                if(grade > 0){
                    String rawProperty = studentInfo[infoRow][propertyColumn];
                    if(rawProperty == null) continue;
                    
                    String studentProperty = rawProperty.trim(); 

                    for(int j = 0; j < properties.length; j++){
                        if(studentProperty.equalsIgnoreCase(properties[j].trim())){ 
                            result[j][i] = grade;
                            break; 
                        }
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }   

    public static int findRowById(String[][] data, int studentId) {    
       for (int i = 1; i < data.length; i++) {
            if (data[i][0] != null && !data[i][0].isEmpty()) {
                try {
                    if (Integer.parseInt(data[i][0].trim()) == studentId) {
                        return i;
                    }
                } catch (NumberFormatException e) { continue; }
            }
        }
        return -1;
    }
}