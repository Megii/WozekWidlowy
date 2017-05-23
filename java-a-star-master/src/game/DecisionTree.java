package game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import weka.classifiers.trees.Id3;
import weka.core.Instance;
import weka.core.Instances;

public class DecisionTree {
	
	private Instances trainingData;

	public DecisionTree(String fileName) {
		BufferedReader reader = null;
		try {
			// Read the training data
			reader = new BufferedReader(new FileReader(fileName));
			trainingData = new Instances(reader);

			// Setting class attribute
			trainingData.setClassIndex(trainingData.numAttributes() - 1);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Id3 trainTheTree() {
		Id3 id3tree = new Id3();

		String[] options = new String[1];
		// Use unpruned tree.
		options[0] = "-U";

		try {
			id3tree.setOptions(options);
			id3tree.buildClassifier(trainingData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id3tree;
	}

	public Instance prepareTestInstance(String type, String size, String time) {
		Instance instance = new Instance(3);
		instance.setDataset(trainingData);

		instance.setValue(trainingData.attribute(0), type);
		instance.setValue(trainingData.attribute(1), size);
		instance.setValue(trainingData.attribute(2), time);

		return instance;
	}
	
	public Instances getTrainingData(){
		return this.trainingData;
	}
	


}
