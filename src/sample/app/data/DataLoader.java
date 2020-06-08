package sample.app.data;

import sample.app.LatLonPair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataLoader {
    private ArrayList<Integer> availableYears;
    private ArrayList<ArrayList<Float>> allData;
    private ArrayList<LatLonPair> knownLocations;

    public DataLoader(String CSVName) {
        loadCSV(CSVName);
    }

    private void loadCSV(String CSVName) {
        try {
            FileReader file = new FileReader(CSVName);
            BufferedReader bufRead = new BufferedReader(file);

            String line = bufRead.readLine();
            while (line != null) {
                String[] array = line.split(",");

                int id = Integer.parseInt(array[0]);
                float val = Float.parseFloat(array[6]);

                System.out.println(id + " " + val);

                line = bufRead.readLine();
            }

            bufRead.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}