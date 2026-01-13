package com.ken25.Modules;
public class PropertyReduction {

    public static String bestProperty(
        double[][] quantumResult,
        double[][] astroResult,
        double[][] bioResult,
        double[][] psionicResult,
        double[][] symbioticResult,
        double[] currentGradesScores){

            int size = 0;
            double average = 0;
            for(int i=0; i<currentGradesScores.length; i++){
                if(currentGradesScores[i]==-1)continue;
                average += currentGradesScores[i]; 
                size++;
            }
            System.out.println(size);
            average = average/size;
          
            double initialVariance = 0;

            for(int i = 0; i<currentGradesScores.length; i++){
                if(currentGradesScores[i]==-1)continue;
                initialVariance += Math.pow(currentGradesScores[i]-average, 2);
            }
            initialVariance = initialVariance/size;
            System.out.println(initialVariance);

        
        
        // Calculate Reductions
        double astro = varianceReduction(astroResult, initialVariance);
        double quantum = varianceReduction(quantumResult, initialVariance);
        double psionic = varianceReduction(psionicResult, initialVariance);
        double symbiotic = varianceReduction(symbioticResult, initialVariance);
        double bio = varianceReduction(bioResult, initialVariance);

        System.out.println("Quantum Red: " + quantum);
        System.out.println("Psionic Red: " + psionic);
        System.out.println("Bio Red:     " + bio);
        System.out.println("Astro Red:   " + astro);
        System.out.println("Symb Red:    " + symbiotic);

        double max = -Double.MAX_VALUE; 
        String best = "Insufficient Data"; 

        // Only consider a property if it is NOT NaN (valid calculation)
        if(!Double.isNaN(bio) && bio > max){ max = bio; best = "bio"; }
        if(!Double.isNaN(quantum) && quantum > max){ max = quantum; best = "quantum"; }
        if(!Double.isNaN(astro) && astro > max){ max = astro; best = "astro"; }
        if(!Double.isNaN(symbiotic) && symbiotic > max){ max = symbiotic; best = "symbiotic"; }
        if(!Double.isNaN(psionic) && psionic > max){ max = psionic; best = "psionic"; }

        return best;
}

        public static double varianceReduction(double[][] result, double initialVariance){

            double weightedVariance = 0;
            double totalCount =0;

            for(int i =0;i<result.length;i++){

                double sum = 0;
                int count = 0;
            
                for(int j =0 ;j<result[0].length;j++){
                    if(result[i][j]==-1.0)continue;
                    sum += result[i][j]; 
                    count++;
                }
                    if(count==0)continue;
                double average = sum/count;

                double variance = 0;
                for (int j = 0; j < result[0].length; j++) {
                if (result[i][j] == -1.0) continue;
                variance += Math.pow(result[i][j] - average, 2);
                }
                variance = variance/count;
                

                weightedVariance += variance;
                totalCount += count;
            }

            double totalWeightedVariance = weightedVariance/totalCount;
            double varianceReduction = initialVariance - totalWeightedVariance;

            return varianceReduction;
        }
        public static double PredictGradeStudent(
            String bestProperty,
            double[] currentGradesScores,
            int targetStudentId,
            String[][] studentInfo
) {

    int propertyCol = findPropertyColumn(studentInfo, bestProperty);
    if (propertyCol == -1) {
        System.out.println("Property " + bestProperty + " not found in studentInfo.");
        return -1;
    }

    int studentRow = findStudentIndex(studentInfo, targetStudentId);
    if (studentRow == -1) {
        System.out.println("Student ID " + targetStudentId + " not found.");
        return -1;
    }

    String propertyValue = studentInfo[studentRow][propertyCol];
    if (propertyValue == null || propertyValue.equals("NG") || propertyValue.isEmpty()) {
        System.out.println("No property value found for student " + targetStudentId);
        return -1;
    }

    double sum = 0;
    int count = 0;
    for (int i = 1; i < studentInfo.length; i++) {
        if (!studentInfo[i][propertyCol].equals(propertyValue)) continue;
        if (studentInfo[i][propertyCol].equals(propertyValue)) {
            if (currentGradesScores[i] == -1) continue;
            sum += currentGradesScores[i];
            count++;
        }
    }

        double average;
        if (count == 0) {
            average = -1;
        } else {
            average = sum / count;
        }
    System.out.println("Best property: " + bestProperty);
    System.out.println("Student " + targetStudentId + " has property: " + propertyValue);
    System.out.println("Average grade for all students with property '" + propertyValue + "' = " + average);

    return average;
    }

	public static int findStudentIndex(String[][] studentInfo, int studentId) {    
      
       for (int i = 1; i < studentInfo.length; i++) {
                if (Integer.parseInt(studentInfo[i][0]) == studentId) {
                    return i;
                }
            }
        return -1;
    }		

    public static int findPropertyColumn(String[][] studentInfo, String bestProperty) {
    String[] properties = studentInfo[0]; 
    for (int i = 0; i < properties.length; i++) {
        String header = properties[i].toLowerCase();
        if (header.contains(bestProperty.toLowerCase())) {
            return i;
        }
    }
    return -1;
}

}
