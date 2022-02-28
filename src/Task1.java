import java.io.*;
import java.util.ArrayList;

public class Task1 {


    /*
      main() takes file name pointing to each csv input file as a command-line argument (String)
      This is parsed to an ArrayList<String[]> before parsing the String values to floats
      The resulting 2d Arrays are passed to functions which separately calculate MAE, MSE, RMSE values.
        Following these calculations, the results are written to file (results.csv) using a PrintWriter.
     */
    public static void main(String[] args) {
        //Taking csv filename from command line arguments
        String fileNamePred = args[0];
        String fileNameGold = args[1];

        float[][] predArray;
        float[][] goldArray;

        //Temporary ArrayList to handle parsing the Prediction csv file.
        ArrayList<String[]> tempALPred = new ArrayList<>();
        String line;
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(fileNamePred));
            while ((line = br.readLine()) != null) {
                String[] singleRecord = line.split(",");    // use comma as separator
                tempALPred.add(singleRecord);
            }
        }
        catch (IOException e) {
            System.out.println();
        }

        //Temporary ArrayList to handle parsing the Gold csv file.
        ArrayList<String[]> tempALGold = new ArrayList<>();
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(fileNameGold));
            while ((line = br.readLine()) != null) {
                String[] singleRecord = line.split(",");    // use comma as separator
                tempALGold.add(singleRecord);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //Converting my arraylist of string arrays into a 2d string array for processing
        predArray = get2DArrayFromAL(tempALPred);
        goldArray = get2DArrayFromAL(tempALGold);

        File results = new File("results.csv");
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(results);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        float MAE, MSE, RMSE;

        MAE = calcMAE(predArray, goldArray);
        MSE = calcMSE(predArray, goldArray);
        RMSE = calcRMSE(MSE);

        StringBuilder sb = new StringBuilder();
        sb.append(MSE);
        sb.append(",");
        sb.append(RMSE);
        sb.append(",");
        sb.append(MAE);
        sb.append("\n");
        writer.write(sb.toString());
        writer.close();
    }

    /*
      get2DArrayFromAL() - converts ArrayList<String[]> to float[][]
      Input: ArrayList<String[]> arrayListStr
      Output: float[][] containing parsed values from csv input
     */
    private static float[][] get2DArrayFromAL(ArrayList<String[]> arrayListStr) {
        int size = arrayListStr.size();
        float[][] output = new float[size][4];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 4; j++) {
                output[i][j] = Float.parseFloat(arrayListStr.get(i)[j]);
            }
        }

        return output;
    }

    /*
      In order to calculate Mean Average Error (MAE), using this formula:
        MAE = (1/n) * Σ|yi – xi|
      Where n is number of ratings, yi is gold rating for item i, and xi is predicted rating for item i.

      Input: float[][] predArray, float[][] goldArray - these are the micro test sets for gold and predicted ratings
      Output: float - calculated MAE as a float
     */
    private static float calcMAE(float[][] predArray, float[][] goldArray) {
        float MAE;
        float n = predArray.length;
        float sum = 0;

        for(int i = 0; i < predArray.length; i++) {
            float diff = (goldArray[i][2] - predArray[i][2]);
            diff = Math.abs(diff);
            sum += diff;
        }

        MAE = sum / n;
        return MAE;
    }

    /*
      In order to calculate Mean Squared Error (MSE), using this formula:
        MSE = (1/n) * Σ|(yi – xi)^2|
      Where n is number of ratings, yi is gold rating for item i, and xi is predicted rating for item i.

      Input: float[][] predArray, float[][] goldArray - these are the micro test sets for gold and predicted ratings
      Output: float - calculated MAE as a float
     */
    private static float calcMSE(float[][] predArray, float[][] goldArray) {
        float MSE;
        float n = predArray.length;
        float sum = 0;

        for(int i = 0; i < predArray.length; i++) {
            float diff = (goldArray[i][2] - predArray[i][2]);
            sum += diff * diff;
        }

        MSE = sum / n;
        return MSE;
    }

    /*
      In order to calculate Root Mean Squared Error (RMSE), using this formula:
        RMSE = sqrt((1/n) * Σ|(yi – xi)^2|)
            (This is the same as the square root of the MSE, hence my calculation using the RMSE)
      Where n is number of ratings, yi is gold rating for item i, and xi is predicted rating for item i.

      Input: float[][] predArray, float[][] goldArray - these are the micro test sets for gold and predicted ratings
      Output: float - calculated MAE as a float
     */
    private static float calcRMSE(float MSE) {
        return (float)Math.sqrt(MSE);
    }
}
