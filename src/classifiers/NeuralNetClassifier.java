package classifiers;

import weka.classifiers.functions.MultilayerPerceptron;

public class NeuralNetClassifier extends BaseClassifier {

	public NeuralNetClassifier(int seconds) {
		this.boosted = new MultilayerPerceptron();
		((MultilayerPerceptron) this.boosted).setDebug(true);
		((MultilayerPerceptron) this.boosted).setMomentum(0.1);
		((MultilayerPerceptron) this.boosted).setLearningRate(0.3);
		((MultilayerPerceptron) this.boosted).setTrainingTime(seconds);
		
		this.used = this.boosted;
	}
	
}
