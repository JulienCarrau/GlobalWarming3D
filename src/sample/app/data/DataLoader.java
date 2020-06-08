package sample.app.data;

import sample.app.LatLonPair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataLoader {
    private ArrayList<String[]> csv; // Contains the whole CSV : csv.get(i)[j] gives the i-th line and j-th column
    private ArrayList<Integer> availableYears;
    private ArrayList<LatLonPair> knownLocations;

    public DataLoader(String CSVName) {
        loadCSV(CSVName);
        setAvailableYears();
        setKnownLocations();
    }

    /**
     * Load a CSV according to its name and copy it into a variable.
     * @param CSVName CSV filename.
     */
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
    }

    /**
     * Initialize the available years array. Basically it takes the first csv's line from index 2 to the end.
     * Because years are between ", we substring from second to before last indexes.
     */
    private void setAvailableYears() {
        availableYears = new ArrayList<>();
        for (int j = 2; j < csv.get(0).length; j++)
            availableYears.add(Integer.parseInt(csv.get(0)[j].substring(1, csv.get(0)[j].length() - 1)));
    }

    /**
     *  Initialize known locations array. Basically it takes the 2 firsts columns from first to last indexes and create LatLonPair instances.
     */
    private void setKnownLocations() {
        knownLocations = new ArrayList<>();
        for (int i = 1; i < csv.size(); i++)
            knownLocations.add(new LatLonPair(Integer.parseInt(csv.get(i)[0]), Integer.parseInt(csv.get(i)[1])));
    }

    /**
     * Available years getter.
     * @return ArrayList<Integer>
     */
    public ArrayList<Integer> getAvailableYears() {
        return availableYears;
    }

    /**
     * Known locations getter.
     * @return ArrayList<LatLonPair>
     */
    public ArrayList<LatLonPair> getKnownLocations() {
        return knownLocations;
    }

    /**
     * Get all temperatures anomaly in the world according to a specific year.
     * @param year Specific wanted year.
     * @return ArrayList<Float> containing all values.
     */
    public ArrayList<Float> getAllTempAnomalyForYear(int year) {
        int yearIndex = 2; // By default indexYear is 2 (corresponding to year 1880)
        for (int j = 2; j < csv.get(0).length; j++)
            if (csv.get(0)[j].substring(1, csv.get(0)[j].length() - 1).equals(year)) {
                yearIndex = j;
                break;
            }

        ArrayList<Float> out = new ArrayList<>();
        for (int i = 1; i < csv.size(); i++) {
            if (csv.get(i)[yearIndex].equals("NA")) out.add(Float.parseFloat("NaN")); // If val is not present, we put a NaN
            else out.add(Float.parseFloat(csv.get(i)[yearIndex]));
        }

        return out;
    }
}