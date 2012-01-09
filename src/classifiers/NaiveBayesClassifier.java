package classifiers;

import weka.classifiers.bayes.NaiveBayesMultinomial;

public class NaiveBayesClassifier extends BaseClassifier{

	public NaiveBayesClassifier() {
		this.boosted = new NaiveBayesMultinomial();
		this.used = this.boosted;
	}
	
}
