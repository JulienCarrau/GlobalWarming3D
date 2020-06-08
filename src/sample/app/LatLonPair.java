package sample.app;

public class LatLonPair {
    private int lat, lon;

    public LatLonPair(int lat, int lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public int getLat() {
        return lat;
    }

    public int getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "Latitude : " + getLat() + ", longitude : " + getLon() + ".\n";
    }
}
