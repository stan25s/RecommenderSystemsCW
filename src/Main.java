import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // write your code here
        // Taking csv file from command line args
        String filename = args[0];
        try {
            Scanner sc = new Scanner(new File(filename));
        } catch (Exception e) {
            System.out.println("File Exception");
        }
    }
}
