import java.io.*;
import java.util.*;

public class Task3 {

    public static void main(String[] args) {
        String trainingFile = "comp3208_example_package/comp3208_100k_train_withratings.csv";
        String testFile = "comp3208_example_package/comp3208_100k_test_withoutratings.csv";

        float[][] trainingData;

        float[][] testData;


        //Temporary ArrayList to handle parsing the Training csv file.
        ArrayList<String[]> tempALTraining = new ArrayList<>();
        String line;
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(trainingFile));
            while ((line = br.readLine()) != null) {
                String[] singleRecord = line.split(",");    // use comma as separator
                tempALTraining.add(singleRecord);
            }
        }
        catch (IOException e) {
            System.out.println();
        }

        //Temporary ArrayList to handle parsing the Test csv file.
        ArrayList<String[]> tempALTest = new ArrayList<>();
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(testFile));
            while ((line = br.readLine()) != null) {
                String[] singleRecord = line.split(",");    // use comma as separator
                tempALTest.add(singleRecord);
            }
        }
        catch (IOException e) {
            System.out.println();
        }

        trainingData = get2DArrayFromAL(tempALTraining, 4);
        testData = get2DArrayFromAL(tempALTest, 3);

        float[][] ratingsMatrix = new float[943][1682];
        //ArrayList<ArrayList<Float>> ratingsMatrix = new ArrayList<>();

        for (float[] row : trainingData) {
            try {
                //Use the userID and itemID from initial training matrix to assign the indexes of the rating value
                ratingsMatrix[(int) row[0]-1][(int) row[1]-1] = row[2];
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        //generate completed ratings matrix with predictions here, using matrix factorisation
        float[][] fullRatingsMatrix = matrixFactorisation(ratingsMatrix, 3, 0.0002f, 0.02f);

        float[][] predictions = new float[testData.length][4];

        //use ratings matrix to generate the array needed for outputting data
        for(int i = 0; i < testData.length; i++) {
            predictions[i][0] = testData[i][0];
            predictions[i][1] = testData[i][1];
            predictions[i][2] = fullRatingsMatrix[(int) testData[i][0] - 1][(int) testData[i][1]];
            predictions[i][3] = testData[i][2];
        }

        writeToFile(predictions);
    }

    /*
      writeToFile() - this is a helper function that takes the result (output for this task) as a 2d array and writes
        this to a .csv file.
      Input -  float[][] predictions (This is a copy of the 100k test set with predictions added)
      Output - void
     */
    private static void writeToFile(float[][] predictions) {
        File results = new File("resultsTask3.csv");
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(results);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        for (float[] i : predictions) {
            sb = new StringBuilder();
            sb.append((int)i[0]);
            sb.append(",");
            sb.append((int)i[1]);
            sb.append(",");
            sb.append(i[2]);
            sb.append(",");
            sb.append((int)Math.floor(i[3]));
            sb.append("\n");
            writer.write(sb.toString());
        }

        writer.close();
    }

    /*
      matrixFactorisation function uses a basic Gradient Descent algorithm to minimise the RMSE of the predicted values
      Input -  float[][] RM (this is the input ratings matrix, with 0 values for user/items that have no rating.)
               int numberOfFeatures (The number of latent features to use in the matrix factors (the user and item matrix))
               float alpha (The learning rate for the gradient descent algorithm)
               float beta (The regularization parameter for gradient descent algorithm)
      Output - float[][] RM (New version of Ratings Matrix, featuring all predicted values, calculated as the product
                    of the 2 factor matrices)
     */
    private static float[][] matrixFactorisation(float[][] RM,
                                                 int numberOfFeatures, float alpha, float beta) {

        int numOfUsers = RM.length;
        int numOfItems = RM[0].length;

        //Create UM and IM as 2d arrays with random values
        //UM and IM are User Matrix and Item Matrix respectively
        float[][] UM = new float[numOfUsers][numberOfFeatures];
        float[][] IM = new float[numOfItems][numberOfFeatures];

        Random random = new Random();
        for (int i = 0; i < UM.length; i++) {
            for (int j = 0; j < UM[0].length; j++) {
                UM[i][j] = random.nextFloat();
            }
        }
        for (int i = 0; i < IM.length; i++) {
            for (int j = 0; j < IM[0].length; j++) {
                IM[i][j] = random.nextFloat();
            }
        }


        int steps = 5000;
        /*
        Change 'steps' value to experiment with the accuracy of results, this is the number of times the algorithm iterates
         */

        for (int steps2 = 0; steps2 < steps; steps2++) {
            //loops for the specified number of iterations

            for (int i = 0; i < RM.length; i++) {
                for (int j = 0; j < RM[0].length; j++) {
                    //calculate error for gradient
                    if (RM[i][j] > 0) {
                        float error = RM[i][j] - vectorDotProduct(UM[i], IM[j]);

                        //calculate gradient using learning rate and regularisation param (alpha and beta)
                        for (int k = 0; k < numberOfFeatures; k++) {
                            UM[i][k] = UM[i][k] + alpha * (2 * error * IM[j][k] - beta * UM[i][k]);
                            IM[j][k] = IM[j][k] + alpha * (2 * error * UM[i][k] - beta * IM[j][k]);
                        }
                    }
                }
            }

            float[][] dotOfFactors = matrixDotProduct(UM, IM);

            //calculate error
            float MSE = 0;
            for (int j = 0; j < RM.length; j++) {
                for (int k = 0; k < RM[0].length; k++) {
                    //only calculate error for values that aren't 0
                    if (RM[j][k] > 0) {
                        MSE += Math.pow(RM[j][k] - dotOfFactors[j][k], 2);

                        for (int l = 0; l < numberOfFeatures; l++) {
                            MSE += (beta/2) * (Math.pow(UM[j][l], 2) + Math.pow(IM[k][l], 2));
                        }
                    }
                }
            }

            //If RMSE less than 0.001 this is a local minimum, so we can stop iterating.
            if (MSE < 0.001) {
                break;
            }
        }
        return matrixDotProduct(UM, IM);
    }

    //Helper function which returns the dot product of two input vectors represented as float[].
    private static float vectorDotProduct(float[] vector1, float[] vector2) {
        float sum = 0;

        if (vector1.length == vector2.length) {
            for(int i = 0; i < vector1.length; i++) {
                sum += vector1[i] * vector2[i];
            }
        } else {
            System.out.println("Error calculating dot product: vectors not same size");
        }

        return sum;
    }

    //Returns the product of two matrices, using the vectorDotProduct() function on each vector within the matrices.
    private static float[][] matrixDotProduct(float[][] matrix1, float[][] matrix2) {
        //assuming matrix2 transposed, so first dimension is used for both dimensions of result
        float[][] resultMatrix = new float[matrix1.length][matrix2.length];

        for(int i = 0; i < matrix1.length; i++) {
            for(int j = 0; j < matrix2.length; j++) {
                resultMatrix[i][j] = vectorDotProduct(matrix1[i], matrix2[j]);
            }
        }

        return resultMatrix;
    }

    /*
      get2DArrayFromAL() - helper function which converts ArrayList<String[]> to float[][]
      Input: ArrayList<String[]> arrayListStr
      Output: float[][] 2DArray (containing parsed values from csv input)
     */
    private static float[][] get2DArrayFromAL(ArrayList<String[]> arrayListStr, int rowSize) {
        int size = arrayListStr.size();
        float[][] output = new float[size][rowSize];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < rowSize; j++) {
                output[i][j] = Float.parseFloat(arrayListStr.get(i)[j]);
            }
        }

        return output;
    }
}
