import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Task1 {


    /*
      main() takes file name pointing to csv input file as a command-line argument (String)
      This is parsed to an

     */
    public static void main(String[] args) {
        // Taking csv filename from command line arguments
        String filename = args[0];

        String line;
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] employee = line.split(",");    // use comma as separator
                System.out.println("Employee [First Name=" + employee[0] + ", Last Name=" + employee[1] + ", Designation=" + employee[2] + ", Contact=" + employee[3] + ", Salary= " + employee[4] + ", City= " + employee[5] +"]");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float calcMAE() {

        return 0;
    }

    public float calcMSE() {

        return 0;
    }

    public float calcRMSE() {
        return 0;
    }
}
