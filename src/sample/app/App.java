package sample.app;

import sample.app.data.DataLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class App {
    private DataLoader dataLoader;
    private HashMap<Integer, YearTempAnomaly> allTempAnomaly;
    private ArrayList<LatLonPair> knownLocations;
    private float globalMin, globalMax;

    /**
     * Functionality: Parser le fichier .csv et stocker les données dans une structure de données appropriée.
     * @param CSVName CSV filename.
     */
    public App(String CSVName) {
        dataLoader = new DataLoader(CSVName);
        knownLocations = dataLoader.getKnownLocations();
        setAllTempAnomaly();
        setMinAndMaxTempAnomaly();
    }

    /**
     * Initialize all temperatures anomaly hasmap. Basically it itterates through all available years and generate a YearTempAnomaly with all temperatures anomaly in this year.
     */
    private void setAllTempAnomaly() {
        allTempAnomaly = new HashMap<>();
        for (Integer year : dataLoader.getAvailableYears())
            allTempAnomaly.put(year, new YearTempAnomaly(year, dataLoader.getAllTempAnomalyForYear(year), knownLocations));
    }

    /**
     * Initialize global minimal and maximal values of temperatures anomaly.
     */
    private void setMinAndMaxTempAnomaly() {
        boolean firstValueSet = false; // To initialize max and min temperatures anomaly with first hashmap's value (because there's no first index)
        for (int year : allTempAnomaly.keySet()) {
            if (!firstValueSet) {
                globalMin = allTempAnomaly.get(year).getMinTempAnomaly();
                globalMax = allTempAnomaly.get(year).getMaxTempAnomaly();
                firstValueSet = true;
            }
            if (allTempAnomaly.get(year).getMinTempAnomaly() < globalMin) globalMin = allTempAnomaly.get(year).getMinTempAnomaly();
            if (allTempAnomaly.get(year).getMaxTempAnomaly() > globalMax) globalMax = allTempAnomaly.get(year).getMaxTempAnomaly();
        }
    }

    /**
     * Get temperatures anomalies for a specific year.
     * @param year Wanted year.
     * @return YearTempAnomly corresponding to this year.
     */
    public YearTempAnomaly getYearTempAnomaly(int year) {
        return allTempAnomaly.get(year);
    }

    /**
     * Functionality: Récupérer la valeur minimum et la valeur maximum des anomalies de temperature parmi toutes les valeurs présentes dans le fichier.
     * Get global maximal and minimal temperature anomaly through all available data.
     * @return ArrayList<Float> which size is 2 and where index 0 is min and index 1 is max.
     */
    public ArrayList<Float> getGlobalMinAndMax() {
        ArrayList<Float> out = new ArrayList<>();
        out.add(globalMin);
        out.add(globalMax);
        return out;
    }

    /**
     * Functionality: Récupérer dans le fichier .csv la liste des années pour lesquelles des données sont disponibles (sur la première ligne du fichier).
     * Available years getter.
     * @return ArrayList<Integer>
     */
    public ArrayList<Integer> getAvailableYears() {
        return dataLoader.getAvailableYears();
    }

    /**
     * Functionality: Récupérer la liste de toutes zones connues avec leur latitude et leur longitude.
     * Known locations getter.
     * @return ArrayList<LatLonPair>
     */
    public ArrayList<LatLonPair> getKnownLocations() {
        return knownLocations;
    }

    /**
     * Functionality: Récupérer la valeur de l’anomalie de temperature pour une zone donnée à une année donnée.
     * Get a temperature anomaly for a specific location in the world and at a certain given year.
     * @param year Wanted year.
     * @param lat Latitude value.
     * @param lon Longitude value.
     * @return A float value corresponding to the temperature anomaly for the specific given period of time and location.
     */
    public float getYearTempAnomalyAtLatLon(int year, int lat, int lon) {
        return allTempAnomaly.get(year).getLocalTempAnomaly(lat, lon);
    }

    /**
     * Functionality: Récupérer les valeurs des anomalies de température pour toutes les années pour une zone donnée. (dans l’ordre croissant des années)
     * Get all temperatures anomaly for a specific location.
     * @param lat Latitude value.
     * @param lon Longitude value.
     * @return ArrayList<Float> of all returned values by getYearTempAnomalyAtLatLon.
     */
    public ArrayList<Float> getAllTempAnomalyAtLatLon(int lat, int lon) {
        ArrayList<Float> out = new ArrayList<>();
        for (int year : allTempAnomaly.keySet())
            out.add(getYearTempAnomalyAtLatLon(year, lat, lon));
        return out;
    }

    /**
     * Functionality: Récupérer de façon optimisé les valeurs des anomalies de température de toutes les zones pour une année donnée (les zones doivent être dans l’ordre de lecture du fichier).
     * Get all temperatures anomaly in the world according to a specific year.
     * @param year Specific wanted year.
     * @return ArrayList<Float> containing all values.
     */
    public ArrayList<Float> getAllTempAnomalyForYear(int year) {
        return dataLoader.getAllTempAnomalyForYear(year);
    }
}
