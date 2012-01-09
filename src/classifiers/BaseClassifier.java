package classifiers;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.IteratedSingleClassifierEnhancer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;

public class BaseClassifier {

	protected Classifier boosted;
	protected IteratedSingleClassifierEnhancer booster;
	protected Classifier used;
	protected Instances trainData;
	
	protected BaseClassifier() {}
	
	public void useBoosting(int numIterations) {
		this.booster.setNumIterations(numIterations);
		this.used = booster;
	}
	
	public void removeBoosting() {
		this.used = boosted;
	}
	
	public void useBoosting(IteratedSingleClassifierEnhancer booster, int numIterations) {
		this.booster = booster;
		this.booster.setClassifier(boosted);
		useBoosting(numIterations);
	}
	
	public void setTrainingData(String trainingDataPath) throws IOException {
		File trainFile = new File(trainingDataPath);
		ArffLoader arf = new ArffLoader();
		arf.setFile(trainFile);
		trainData = arf.getDataSet();
		trainData.setClassIndex(trainData.numAttributes()-1);
	}
	
	public void train() throws Exception {
		if(trainData == null) { 
			throw new Exception("Training Data has not been set, please use <instance>.setTrainingData(path)"); 
		}
		used.buildClassifier(trainData);
	}
	
	public void evaluate(int numFolds) throws Exception {
		if(trainData == null) { 
			throw new Exception("Training Data has not been set, please use <instance>.setTrainingData(path)"); 
		}
		Evaluation eval = new Evaluation(trainData);
		eval.crossValidateModel(used, trainData, numFolds, new Random(1));
		System.out.println(eval.toSummaryString("\nResults\n======\n", true));
		System.out.println(eval.toClassDetailsString());
	}
	
	public String classify(Instance i) throws Exception {
		if(trainData == null) { 
			throw new Exception("Training Data has not been set, please use <instance>.setTrainingData(path)"); 
		}
		return trainData.classAttribute().value((int)used.classifyInstance(i));
	}
	
	public void serialize(String serialFilePath) throws Exception {
		SerializationHelper.write(serialFilePath+".boosted", boosted);
		SerializationHelper.write(serialFilePath+".booster", booster);
	}
	
	public void deserialize(String serialFilePath) throws Exception {
		boosted = (Classifier) weka.core.SerializationHelper.read(serialFilePath+".boosted");
		booster = (IteratedSingleClassifierEnhancer) weka.core.SerializationHelper.read(serialFilePath+".booster");
		
	}
	
}
