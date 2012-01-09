package classifiers;

import java.io.File;
import java.io.IOException;

import weka.classifiers.functions.LibSVM;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ArffLoader;

public class SVMClassifier extends BaseClassifier{

	public static double calcVariance(Instances data) {
		int n = data.numInstances();
		double[] mean = new double[data.numAttributes()];
		double var  = 0.0;
		for (int i = 0; i<data.numInstances(); i++) {
			Instance inst = data.instance(i);
			double[] x = inst.toDoubleArray();
			for (int j = 0 ; j < data.numAttributes(); j++) {
				mean[j] += x[j]/n;
			}
		}
		for (int i = 0; i<data.numInstances(); i++) {
			Instance inst = data.instance(i);
			double[] x = inst.toDoubleArray();
			for (int j = 0 ; j < data.numAttributes(); j++) {
				var += (x[j]-mean[j])*(x[j]-mean[j])/n;
			}
		}
		
		return var;
	}
	
	public SVMClassifier() {
		this.boosted = new LibSVM();
		SelectedTag kernel = new SelectedTag(LibSVM.KERNELTYPE_RBF,LibSVM.TAGS_KERNELTYPE);
		((LibSVM) this.boosted).setKernelType(kernel);
		this.used = this.boosted;
	}
	
	
	@Override
	public void setTrainingData(String trainingDataPath) throws IOException {
		File trainFile = new File(trainingDataPath);
		ArffLoader arf = new ArffLoader();
		arf.setFile(trainFile);
		trainData = arf.getDataSet();
		trainData.setClassIndex(trainData.numAttributes()-1);
		((LibSVM) this.boosted).setGamma(1.0/(2*calcVariance(trainData)));
	}
	
}
