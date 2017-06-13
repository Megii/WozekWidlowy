package data;

import java.util.ArrayList;
import java.util.Arrays;

public class GoodOutputs {

    private static GoodOutputs instance;

    private ArrayList<ArrayList<Double>> goodValues;

    public static GoodOutputs getInstance() {
        if (instance == null)
            instance = new GoodOutputs();

        return instance;
    }

    public GoodOutputs() {
        this.goodValues = new ArrayList<>();
        addGoodOutputs();
    }

    private void addGoodOutputs() {
        // Góra
        goodValues.add(new ArrayList<>(Arrays.asList(1.0, 0.0, 0.0)));

        // Chroniæ przed wilgoci¹
        goodValues.add(new ArrayList<>(Arrays.asList(0.0, 1.0, 0.0)));

        // Uwaga szk³o
        goodValues.add(new ArrayList<>(Arrays.asList(0.0, 0.0, 1.0)));

        }

    public ArrayList<Double> getGoodOutput(String letter) {
        char charLetter = letter.charAt(0);
        int index = ((int) charLetter) - 65;

        return goodValues.get(index);
    }

}
