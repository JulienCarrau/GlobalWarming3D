package sample.app;

import sample.app.data.DataLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class Application {
    private DataLoader dataLoader;
    private HashMap<Integer, YearTempAnomaly> allTempAnomaly;
    private ArrayList<LatLonPair> knownLocations;
    private float globalMin, globalMax;

    public Application(String CSVName) {
        dataLoader = new DataLoader(CSVName);
        knownLocations = dataLoader.getKnownLocations();
        setAllTempAnomaly();
        System.out.println(allTempAnomaly.get(1880).getLocalTempAnomaly(-64,-102));
    }

    private void setAllTempAnomaly() {
        allTempAnomaly = new HashMap<>();
        for (Integer year : dataLoader.getAvailableYears())
        allTempAnomaly.put(year, new YearTempAnomaly(year, dataLoader.getAllTempAnomalyForYear(year), knownLocations));
    }
}
