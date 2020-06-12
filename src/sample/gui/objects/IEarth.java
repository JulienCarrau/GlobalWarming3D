package sample.gui.objects;

import sample.app.LatLonPair;
import sample.app.YearTempAnomaly;

import java.util.ArrayList;

public interface IEarth {
    void addQuadrilateralFilterOverWorld(ArrayList<LatLonPair> locations, YearTempAnomaly anomaly);
}
