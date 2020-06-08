package sample.app;

import java.util.HashMap;

public class YearTempAnomaly {
    private int year;
    private HashMap<LatLonPair, Float> tempAnomaly;

    public YearTempAnomaly(int year, HashMap<LatLonPair, Float> tempAnomaly) {
        this.year = year;
        this.tempAnomaly = tempAnomaly;
    }

    public int getYear() {
        return year;
    }

    public HashMap<LatLonPair, Float> getTempAnomaly() {
        return tempAnomaly;
    }
}
