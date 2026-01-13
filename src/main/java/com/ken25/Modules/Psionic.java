package com.ken25.Modules;

import java.util.Arrays;

public class Psionic extends PropertyAnalyzer {

    public Psionic(){
        super(new String[]{"Low (-1 to -0.5)", "Mid-Low (-0.5 to 0)", "Mid-High (0 to 0.5)", "High (0.5 to 1)"}, 4);
    }

    @Override
    public double[][] analyze(String[][] studentInfo, String[][] currentGrades, int courseNumber, int[] students){

        double[][] result = new double[4][students.length];
        for (double[] row : result) Arrays.fill(row, -1.0);
        
        for(int i = 0; i < students.length; i++){
            int studentID = students[i];

         
            int infoRow = findStudentIndex(studentInfo, studentID);
            
            if (infoRow == -1) continue; 

            int gradeRow = findStudentIndex(currentGrades, studentID);
            if(gradeRow == -1) continue;

            if(currentGrades[gradeRow][courseNumber] == null) continue;
            
            try {
                double grade = Double.parseDouble(currentGrades[gradeRow][courseNumber]);
                
                double psionicValue = Double.parseDouble(studentInfo[infoRow][4]); 
                
                int rangeIndex = getRangeIndex(psionicValue);

             
                if(rangeIndex != -1 && grade > 0){
                    result[rangeIndex][i] = grade;
                }

            } catch (NumberFormatException e) {
                continue;
            }
        }
        return result;
    }

    private int getRangeIndex(double value){
        if(value > -1.0 && value <= -0.5) return 0;
        if(value > -0.5 && value <= 0.0) return 1;
        if(value > 0.0 && value <= 0.5) return 2;
        if(value > 0.5 && value <= 1.0) return 3;
        return -1;
    }
    
    public static int findStudentIndex(String[][] data, int studentId) {    
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