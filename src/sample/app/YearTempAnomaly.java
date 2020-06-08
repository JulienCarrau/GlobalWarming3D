package sample.app;

import java.util.ArrayList;
import java.util.HashMap;

public class YearTempAnomaly {
    private int year;
    private HashMap<LatLonPair, Float> tempAnomaly;

    public YearTempAnomaly(int year, ArrayList<Float> tempAnomaly, ArrayList<LatLonPair> knownLocations) {
        this.year = year;
        this.tempAnomaly = new HashMap<>();
        for (int i = 0; i < tempAnomaly.size(); i++)
            this.tempAnomaly.put(knownLocations.get(i), tempAnomaly.get(i));
    }

    public int getYear() {
        return year;
    }

    public float getLocalTempAnomaly(int lat, int lon) {
        return tempAnomaly.get(new LatLonPair(lat, lon));
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Year " + getYear() + " :\n");
        for (LatLonPair llp : tempAnomaly.keySet())
            out.append(llp.toString() + " : " + tempAnomaly.get(llp).toString()).append("\n");
        return out.toString();
    }
}
