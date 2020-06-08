package sample.app;

import java.util.ArrayList;
import java.util.HashMap;

public class YearTempAnomaly {
    private int year;
    private float minTempAnomaly, maxTempAnomaly;
    private HashMap<LatLonPair, Float> tempAnomaly;

    public YearTempAnomaly(int year, ArrayList<Float> tempAnomaly, ArrayList<LatLonPair> knownLocations) {
        this.year = year;
        this.tempAnomaly = new HashMap<>();
        for (int i = 0; i < tempAnomaly.size(); i++)
            this.tempAnomaly.put(knownLocations.get(i), tempAnomaly.get(i));
        setMinAndMaxTempAnomaly();
    }

    /**
     * Initialize minimal and maximal values of temperatures anomaly for this year.
     */
    private void setMinAndMaxTempAnomaly() {
        boolean firstValueSet = false; // To initialize max and min temperatures anomaly with first hashmap's value (because there's no first index)
        for (LatLonPair llp : tempAnomaly.keySet()) {
            if (!firstValueSet) {
                minTempAnomaly = tempAnomaly.get(llp);
                maxTempAnomaly = tempAnomaly.get(llp);
                firstValueSet = true;
            }
            if (tempAnomaly.get(llp) < minTempAnomaly) minTempAnomaly = tempAnomaly.get(llp);
            if (tempAnomaly.get(llp) > maxTempAnomaly) maxTempAnomaly = tempAnomaly.get(llp);
        }
    }

    /**
     * Year getter.
     * @return Int year.
     */
    public int getYear() {
        return year;
    }

    /**
     * Minimal temperature anomaly getter.
     * @return Float temperature anomaly.
     */
    public float getMinTempAnomaly() {
        return minTempAnomaly;
    }

    /**
     * Maximal temperature anomaly getter.
     * @return Float temperature anomaly.
     */
    public float getMaxTempAnomaly() {
        return maxTempAnomaly;
    }

    /**
     * Get temperature anomaly for a specific location.
     * @param lat Latitude value.
     * @param lon Longitude value.
     * @return Float corresponding to asked location's temperature anomaly.
     */
    public float getLocalTempAnomaly(int lat, int lon) {
        return tempAnomaly.get(new LatLonPair(lat, lon));
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Year ").append(getYear()).append(" :\n");
        for (LatLonPair llp : tempAnomaly.keySet())
            out.append(llp.toString()).append(" : ").append(tempAnomaly.get(llp).toString()).append("\n");
        return out.toString();
    }
}
