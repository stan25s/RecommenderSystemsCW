import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Task2 {

    public static void main(String[] args) {
        String trainingFile = "comp3208_example_package/comp3208_100k_train_withratings.csv";
        String testFile = "comp3208_example_package/comp3208_100k_test_withoutratings.csv";

        float[][] trainingData;

        float[][] testData;

        int neighborhoodSize; //Change this variable to experiment with accuracy over different neighborhood sizes.

        //Temporary ArrayList to handle parsing the Prediction csv file.
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

        trainingData = get2DArrayFromAL(tempALTraining);

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
      cosineSimilarity() - calculates the similarity rating between 2 users
      Input: float[][] user1Ratings, float[][] user2Ratings
        These user-rating 2d arrays contain the item and rating for all items that the user has rated.
      Output: float similarity.
        Similarity is calculated using cosine similarity algorithm
     */
    private static float cosineSimilarity(float[][] user1Ratings, float[][] user2Ratings) {
        float similarity = 0;

        //Create a list of the items that both users have rated:
        float[][] ratings;
        //List<Float> user1Items = new ArrayList<>();
        List<Float> itemsInCommon = new ArrayList<>();
        Map<Float, Float> user1Map = new HashMap<>();
        Map<Float, Float> user2Map = new HashMap<>();

        for (float[] i : user1Ratings) {
            //Take the item id number from the user1ratings and add this to the user1items list.
            user1Map.put(i[0], i[1]);
        }

        for (float[] i : user2Ratings) {
            //Take the item id number from the user1ratings and add this to the user1items list.
            user2Map.put(i[0], i[1]);
        }

        for (float[] i : user2Ratings) {
            if (user1Map.containsKey(i[0])){
                //If user 1 and 2 have both rated item, add to items in common ArrayList
                itemsInCommon.add(i[0]);
            }
        }

        float sumOfRatingProducts = 0;
        for (float i : itemsInCommon) {
            sumOfRatingProducts += user1Map.get(i) * user2Map.get(i);
        }

        float sqrtOfUser1RatingsSquared = 0, sqrtOfUser2RatingsSquared = 0;
        for (float i : itemsInCommon) {
            sqrtOfUser1RatingsSquared += user1Map.get(i) * user1Map.get(i);
            sqrtOfUser2RatingsSquared += user2Map.get(i) * user2Map.get(i);
        }
        sqrtOfUser1RatingsSquared = (float) Math.sqrt(sqrtOfUser1RatingsSquared);
        sqrtOfUser2RatingsSquared = (float) Math.sqrt(sqrtOfUser2RatingsSquared);

        similarity = sumOfRatingProducts / (sqrtOfUser1RatingsSquared * sqrtOfUser2RatingsSquared);

        return similarity;
    }

    /*
      contains() is a helper function which decides whether an array of floats contains a given float
      Input: float[] array, float item
      Output: boolean contains.
        If array contains float item, returns true.
     */
    private static boolean contains(float[] array, float item) {
        boolean result = false;

        for (float i : array){
            if (i == item) {
                result = true;
                break;
            }
        }

        return result;
    }

}
