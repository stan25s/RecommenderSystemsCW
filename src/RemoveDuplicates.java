import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RemoveDuplicates {


        public static void main(String[] args) {
            ArrayList<String> list = new ArrayList<>();
            String line;
            try {
                //parsing a CSV file into BufferedReader class constructor
                BufferedReader br = new BufferedReader(new FileReader("resultsTask2.csv"));
                while ((line = br.readLine()) != null) {   // use comma as separator
                    list.add(line);
                }
            }
            catch (IOException e) {
                System.out.println();
            }

            ArrayList<String> newList = removeDupes(list);

            File results = new File("resultsTask2.csv");
            PrintWriter writer = null;

            try {
                writer = new PrintWriter(results);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            for (String s : newList) {
                writer.write(s);
                writer.write("\n");
            }
            writer.close();
        }

        private static ArrayList<String> removeDupes(ArrayList<String> originalList) {
            ArrayList<String> newList = new ArrayList<>();
            for (String s : originalList) {
                if (!newList.contains(s)) {
                    newList.add(s);
                }
            }

            return newList;
        }

        private static ArrayList<String> splitStrings(ArrayList<String> originalList) {
            ArrayList<String> newList = new ArrayList<>();
            String sb = "";
            for (int i = 0; i < originalList.size(); i++) {
                if(i % 5 == 0 && i != 0) {
                    newList.add(sb);
                    sb = "";
                } else {
                    if (sb != "") {
                        sb = sb + "," + originalList.get(i);
                    } else {
                        sb = originalList.get(i);
                    }
                }
            }
            return newList;
        }
}
