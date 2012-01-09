package learners;

import classifiers.BaseClassifier;
import classifiers.NeuralNetClassifier;
import classifiers.SVMClassifier;

public class DialogueActLearner {
	
	public static void main(String args[]) throws Exception {
//		BaseClassifier svm = new SVMClassifier();
		BaseClassifier svm = new NeuralNetClassifier(120);
		svm.setTrainingData(args[0]);
		svm.train();
		svm.serialize("ANNmodel");
		svm.evaluate(2);
	}

}
