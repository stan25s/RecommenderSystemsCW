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

        //generate completed ratings matrix with predictions here, using ratingsMatrix
        float[][] fullRatingsMatrix = matrixFactorisation(ratingsMatrix, 10, 0.0002f, 0.1f);

        //use ratings matrix to generate the array needed for outputting data
        float[][] predictions = new float[0][0];

        writeToFile(predictions);

        /*
        Training Function:
        Takes in trainingData 2d array

        Similarity:
        - Create a matrix with the list of user on both sides.
        - Calculate similarity using Cosine Similarity between each pair of users.
        - - Ignore pairs of same user, and reverse pairs of users already calculated for to save processing time and memory.

        Neighborhood Selection:
        - Decide whether to select all users or a subset
        - Using similarity ratings, find top M most similar users for Neighborhood
            Create a list of user numbers to include for prediction algorithm

        Prediction:
        - Take one user and one item as input, along with list of neighbors, and similarity matrix, and training set of ratings.
        - - Calculate prediction using algorithm from lecture notes (Recommender Systems 2)
            Iterate through entire array from the test set. Calculate each predicted rating and append to 2d array.
         */

    }

    /*
      writeToFile() - this is a helper function that takes the result (output for this task) as a 2d array and writes
        this to a .csv file.
      Input -  float[][] predictions (This is a copy of the 100k test set with predictions added)
      Output - void
     */
    private static void writeToFile(float[][] predictions) {
        File results = new File("resultsTask2.csv");
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(results);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        for (float[] i : predictions) {
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

    private static float[][] matrixFactorisation(float[][] RM,
                                                 int numberOfFeatures, float alpha, float beta) {
        /*
            Params:
            ArrayList<Map<Integer, Float>> RM - This represents the Ratings Matrix, with
         */

        int numOfUsers = RM.length;
        int numOfItems = RM[0].length;

        //Create UM and IM as 2d arrays with random values
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


        int steps = 1000; //Change this value to experiment with the accuracy of results

        for (int i = 0; i < steps; i++) {
            //loops for the specified number of iterations

            for (int j = 0; j < RM.length; j++) {
                for (int k = 0; k < RM[0].length; k++) {
                    //calculate error for gradient
                    if (RM[j][k] > 0) {
                        float error = RM[j][k] - vectorDotProduct(UM[j], IM[k]);

                        //calculate gradient using learning rate and regularisation param (alpha and beta)
                        for (int features = 0; features < numberOfFeatures; features++) {
                            UM[j][features] = UM[j][features] + alpha * (2 * error * IM[features][k] - beta * UM[j][features]);
                            IM[j][features] = IM[j][features] + alpha * (2 * error * UM[features][k] - beta * IM[j][features]);
                        }
                    }
                }
            }

            float[][] dotOfFactors = matrixDotProduct(UM, IM);

            //calculate root mean squared error
            double RMSE = 0.0f;
            float MSE = 0;
            for (int j = 0; j < RM.length; j++) {
                for (int k = 0; k < RM[0].length; k++) {
                    //only calculate error for values that aren't 0
                    if (RM[j][k] > 0) {
                        MSE += Math.pow(RM[j][k] - dotOfFactors[j][k], 2);
                    }
                }
            }
            RMSE = Math.sqrt(MSE);

            //If RMSE less than 0.001 this is a local minimum, so we can stop iterating.
            if (RMSE < 0.001) {
                break;
            }
        }
        return matrixDotProduct(UM, IM);
    }

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

//    private static float[][] transposeMatrix(float[][] matrix) {
//        int m = matrix.length;
//        int n = matrix[0].length;
//
//        float[][] transposedMatrix = new float[n][m];
//
//        for (int i = 0; i < n; i++) {
//            for(int j = 0; j < m; j++) {
//                transposedMatrix[i][j] = matrix[j][i];
//            }
//        }
//
//        return transposedMatrix;
//    }


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
