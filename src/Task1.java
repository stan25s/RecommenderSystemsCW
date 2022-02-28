import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Task1 {


    /*
      main() takes file name pointing to each csv input file as a command-line argument (String)
      This is parsed to an ArrayList<String[]> before parsing the String values to floats
      The resulting 2d Arrays are passed to functions which separately calculate MAE, MSE, RMSE values.
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
            e.printStackTrace();
        }

        //Temporary ArrayList to handle parsing the Gold csv file.
        ArrayList<String[]> tempALGold = new ArrayList<>();
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(fileNameGold));
            while ((line = br.readLine()) != null) {
                String[] singleRecord = line.split(",");    // use comma as separator
                tempALPred.add(singleRecord);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //Converting my arraylist of string arrays into a 2d string array for processing
        predArray = get2DArrayFromAL(tempALPred);
        goldArray = get2DArrayFromAL(tempALGold);

        //Test printing float lists
        for(float[] f : predArray) {
            System.out.println(Arrays.toString(f));
        }
        for(float[] f : goldArray) {
            System.out.println(Arrays.toString(f));
        }
    }

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

    private float calcMAE() {

        return 0;
    }

    private float calcMSE() {

        return 0;
    }

    private float calcRMSE() {
        return 0;
    }
}
