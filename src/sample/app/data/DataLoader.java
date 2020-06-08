package sample.app.data;

import sample.app.LatLonPair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataLoader {
    private ArrayList<String[]> csv; // Contains the whole CSV : csv.get(i)[j] gives the i-th line and j-th column
    private ArrayList<Integer> availableYears;
    private ArrayList<ArrayList<Float>> allData;
    private ArrayList<LatLonPair> knownLocations;

    public DataLoader(String CSVName) {
        loadCSV(CSVName);
    }

    private void loadCSV(String CSVName) {
        csv = new ArrayList<>();
        try {
            FileReader file = new FileReader(CSVName);
            BufferedReader bufRead = new BufferedReader(file);

            String line = bufRead.readLine();
            while (line != null) {
                String[] array = line.split(",");

                csv.add(array);

                line = bufRead.readLine();
            }
            bufRead.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(csv.get(0)[2]);
    }
}