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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LatLonPair)) return false;
        LatLonPair key = (LatLonPair) o;
        return lat == key.lat && lon == key.lon;
    }

    @Override
    public int hashCode() {
        int result = lat;
        result = 31 * result + lon;
        return result;
    }

    @Override
    public String toString() {
        return "Latitude : " + getLat() + ", longitude : " + getLon();
    }
}
