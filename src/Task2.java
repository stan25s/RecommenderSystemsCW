import java.io.*;
import java.util.*;

public class Task2 {

    public static void main(String[] args) {
        String trainingFile = "comp3208_example_package/comp3208_100k_train_withratings.csv";
        String testFile = "comp3208_example_package/comp3208_100k_test_withoutratings.csv";

        float[][] trainingData;

        float[][] testData;

        int neighborhoodSize = 10; //Change this variable to experiment with accuracy over different neighborhood sizes.

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

        float[][] similarityMatrix = getSimilarityMatrix(trainingData);

        //System.out.println(Arrays.deepToString(trainingData));
        //System.out.println(Arrays.deepToString(similarityMatrix));

        Map<Float, List<Float>> neighbors = getNeighboursPerUser(trainingData, similarityMatrix, neighborhoodSize);

        float[][] predictions = fillPredictedRatings(similarityMatrix, neighbors, trainingData, testData);

//        for (float[] i : predictions) {
//            System.out.println(Arrays.toString(i));
//        }

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

    private static void writeToFile(float[][] predictions) {
        File results = new File("resultsTask2.csv");
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(results);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

//        MAE = calcMAE(predArray, goldArray);
//        MSE = calcMSE(predArray, goldArray);
//        RMSE = calcRMSE(MSE);
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

    private static Map<Float, List<Float>> getNeighboursPerUser(float[][] trainingData,
                                                              float[][] similarityMatrix, int neighborhoodSize) {
        int N = neighborhoodSize;
        Map<Float, List<Float>> neighbors = new HashMap<>();

        List<Float> listOfUsers = new ArrayList<>();
        for (float[] i : trainingData) {
            //first value of subarray within trainingData is userID
            if (!listOfUsers.contains(i[0])) {
                listOfUsers.add(i[0]);
            }
        }

        //iterate through users list in order to create a list of neighbours for each user
        for (float i : listOfUsers) {
            float[] usersToCompareWithI = similarityMatrix[(int)i-1];
            //find top N neighbours based on similarity rating

            //add all users to a map of users to similarity rating
            Map<Float, Float> usersToSimilarity = new HashMap<>();
            for (int count = 0; count < usersToCompareWithI.length; count++) {
                int userID = count + 1;
                usersToSimilarity.put((float) userID, usersToCompareWithI[count]);
            }
            //Sort the map to create an ordered map, so I can select most similar users
            Map<Float, Float> sortedMap = sortMap(usersToSimilarity);
            List<Float> l = new ArrayList<>(sortedMap.keySet());

            //Add top N to neighour set
            List<Float> neighborsToAdd = new ArrayList<>();
            for(int k = l.size() - 1; k >= l.size() - N; k--) {
                neighborsToAdd.add(l.get(k));
            }
//            System.out.println(i);
//            for(float n : neighborsToAdd) {
//                System.out.print(n + ":" + similarityMatrix[(int)i-1][(int)n-1]);
//            }
//            System.out.println();
            neighbors.put(i, neighborsToAdd);
        }
        return neighbors;
    }

    /*
      sortMap() is a helper function, used to sort the order of a map of user to similarity
      This is used when finding the most similar users
     */
    private static Map<Float, Float> sortMap(Map<Float, Float> userToSim) {
        List<Map.Entry<Float, Float>> list = new ArrayList<>(userToSim.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<Float, Float> result = new LinkedHashMap<>();
        for (Map.Entry<Float, Float> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private static float[][] fillPredictedRatings(float[][] similarityMatrix, Map<Float, List<Float>> neighbourMap,
                                                  float[][] trainingData, float[][] testData) {
        float[][] outputArray = new float[testData.length][4];
        int indexForOutputArray = 0;

        for (float[] row : testData) {
            float userID = row[0];
            float itemID = row[1];
            float userAverageRating;
            Map<Float, Float> userAItems = getUserRatedItems(trainingData, userID);

            //get average ratings
            float ratingsSum = 0;
            for (float rating : userAItems.values()) {
                ratingsSum += rating;
            }
            userAverageRating = ratingsSum / userAItems.size();

            float sum1 = 0;
            float sum2 = 0;
            for (float neighbor : neighbourMap.get(userID)) {
                Map<Float, Float> neighborRatedItems = getUserRatedItems(trainingData, neighbor);
                float adjustedRating;
                if (neighborRatedItems.containsKey(itemID)) {
                    float meanRating = 0;
                    for (float item : neighborRatedItems.values()) {
                        meanRating += item;
                    }
                    meanRating = meanRating / neighborRatedItems.size();
                    adjustedRating = neighborRatedItems.get(itemID) - meanRating;
                } else {
                    adjustedRating = 0;
                }
                sum1 += getSimilarityBetweenUsers(similarityMatrix, userID, neighbor) * adjustedRating;

                //Calculating sum of similarities between users
                sum2 += getSimilarityBetweenUsers(similarityMatrix, userID, neighbor);
            }

            float prediction = userAverageRating + (sum1 / sum2);

            //add new row with prediction to output array
            outputArray[indexForOutputArray] = new float[]{row[0], row[1], prediction, (int) row[2]};
            indexForOutputArray += 1;
        }
        return outputArray;
    }

    /*
      getSimilarityBetweenUsers() - takes 2 userIDs as inputs, and returns the
        corresponding value from the similarity matrix.
     */
    private static float getSimilarityBetweenUsers(float[][] similarityMatrix, float user1, float user2) {
        return similarityMatrix[(int)user1-1][(int)user2-1];
    }

    private static float[][] getSimilarityMatrix(float[][] trainingData) {
        float[][] resultMatrix;

        List<Float> listOfUsers = new ArrayList<>();
        for (float[] i : trainingData) {
            //first value of subarray within trainingData is userID
            if (!listOfUsers.contains(i[0])) {
                listOfUsers.add(i[0]);
            }
        }
        //initialize result matrix with amount of users found
        resultMatrix = new float[listOfUsers.size()][listOfUsers.size()];

        for(int i = 0; i < listOfUsers.size(); i++) {
            for(int j = 0; j < listOfUsers.size(); j++) {
                Map<Float, Float> userAMap = getUserRatedItems(trainingData, i + 1);
                Map<Float, Float> userBMap = getUserRatedItems(trainingData, j + 2);

                resultMatrix[i][j] = cosineSimilarity(userAMap, userBMap);
            }
        }

        return resultMatrix;
    }

    private static Map<Float, Float> getUserRatedItems(float[][] allTrainingData, float userID) {
        //This function needs to iterate over the trainingData 2d array and find all items rated by userID
        //Then add these as key-value pairs to the map to output
            //Map should contain itemID, ItemRating as key-value respectively.
        Map<Float, Float> resultMap = new HashMap<>();

        float currentUser = 0;
        int currentIndex = 0;
        try {
            //System.out.println(allTrainingData.length);
            while (currentUser <= userID && currentIndex < allTrainingData.length) {
                currentUser = allTrainingData[currentIndex][0];
                if(currentUser == userID) {
                    //if the current line from the training array is for the correct user, add item and rating to map.
                    resultMap.put(allTrainingData[currentIndex][1], allTrainingData[currentIndex][2]);
                }
                currentIndex += 1;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println("Error Getting Item Ratings Map For User: " + userID);
        }
        //System.out.println(resultMap);
        return resultMap;
    }

    /*
      get2DArrayFromAL() - converts ArrayList<String[]> to float[][]
      Input: ArrayList<String[]> arrayListStr
      Output: float[][] containing parsed values from csv input
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


    /*
      cosineSimilarity() - calculates the similarity rating between 2 users
      Input: float[][] user1Ratings, float[][] user2Ratings
        These user-rating 2d arrays contain the item and rating for all items that the user has rated.
      Output: float similarity.
        Similarity is calculated using cosine similarity algorithm
     */
    private static float cosineSimilarity(Map<Float, Float> user1Map, Map<Float, Float> user2Map) {
        float similarity = 0;

        //Create a list of the items that both users have rated:
        float[][] ratings;
        //List<Float> user1Items = new ArrayList<>();
        List<Float> itemsInCommon = new ArrayList<>();

//        for (float[] i : user1Ratings) {
//            //Take the item id number from the user1ratings and add this to the user1items list.
//            user1Map.put(i[0], i[1]);
//        }

//        for (float[] i : user2Ratings) {
//            //Take the item id number from the user1ratings and add this to the user1items list.
//            user2Map.put(i[0], i[1]);
//        }

        for (float i : user2Map.keySet()) {
            if (user1Map.containsKey(i)){
                //If user 1 and 2 have both rated item, add to itemsInCommon ArrayList
                itemsInCommon.add(i);
            }
        }

        //Here's the actual cosine similarity algorithm implementation
        if(itemsInCommon.size() != 0) {
            float sumOfRatingProducts = 0;
            for (float i : itemsInCommon) {
                sumOfRatingProducts += user1Map.get(i) * user2Map.get(i);
            }

            double sqrtOfUser1RatingsSquared = 0, sqrtOfUser2RatingsSquared = 0;
            for (float i : itemsInCommon) {
                sqrtOfUser1RatingsSquared += user1Map.get(i) * user1Map.get(i);
                sqrtOfUser2RatingsSquared += user2Map.get(i) * user2Map.get(i);
            }
            sqrtOfUser1RatingsSquared = Math.sqrt(sqrtOfUser1RatingsSquared);
            sqrtOfUser2RatingsSquared = Math.sqrt(sqrtOfUser2RatingsSquared);

            similarity = (float) (sumOfRatingProducts / (sqrtOfUser1RatingsSquared * sqrtOfUser2RatingsSquared));
        } else {
            similarity = 0;
        }


        return similarity;
    }

}
