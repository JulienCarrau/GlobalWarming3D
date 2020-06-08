package sample.app;

import sample.app.data.DataLoader;

public class Application {
    private DataLoader dataLoader;

    public Application(String CSVName) {
        dataLoader = new DataLoader(CSVName);
    }
}
