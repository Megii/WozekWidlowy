package data;

import neuron.TrainingSet;

import java.io.*;
import java.nio.Buffer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadWriteFile {

    public static ArrayList<TrainingSet> readTrainingSets() {
        ArrayList<TrainingSet> trainingSets = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            char letterValue = (char) (i + 65);
            String letter = String.valueOf(letterValue);
            System.out.println(letter);
            for (ArrayList<Integer> list : readFromFile("/" + letter + ".txt")) {
                trainingSets.add(new TrainingSet(list, GoodOutputs.getInstance().getGoodOutput(letter)));
            }
        }


        return trainingSets;
    }

    private static ArrayList<ArrayList<Integer>> readFromFile(String filename) {
        ArrayList<ArrayList<Integer>> inputs = new ArrayList<>();

        try {
            InputStream in = ReadWriteFile.class.getResourceAsStream(filename);
            System.out.println(in==null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                ArrayList<Integer> input = new ArrayList<>();
                for (int i = 0; i < line.length(); i++) {
                    int value = 0;
                    try {
                        value = Integer.parseInt(String.valueOf(line.charAt(i)));
                    } catch (Exception e) {
                    }
                    input.add(value);
                }
                inputs.add(input);
            }
            reader.close();
        } catch (IOException e) {	
            e.printStackTrace();
        }

        return inputs;
    }

    public static void saveToFile(ArrayList<Integer> input, String filename) {
        try {
            char charLetter = filename.charAt(0);
            File file = new File("input/" + charLetter + ".txt");
            PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
            for (Integer i : input) {
                pw.write(Integer.toString(i));
            }
            pw.write("\n");
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
