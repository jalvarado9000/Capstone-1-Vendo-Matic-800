package com.techelevator;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuditLog {
    public static void log(String message) {

        //establish path where audit log file is stored
        String dataFile = "C:\\Users\\johne\\OneDrive\\Desktop\\Capstone\\module-1-capstone\\capstone\\src\\main\\java\\com\\techelevator\\Log.txt";


        //build output for writing to the audit log file
        try (PrintWriter dataOutput = new PrintWriter(new FileOutputStream(dataFile, true))) {

            //set up a format for the timestamp
            SimpleDateFormat formatter= new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
            Date timeStamp = new Date(System.currentTimeMillis());

            //write the log entry to the file including timestamp prefix
            dataOutput.println(formatter.format(timeStamp) + " " + message);

        } catch (Exception e) {
            System.out.println("An error has occurred.");
        }
    }

}