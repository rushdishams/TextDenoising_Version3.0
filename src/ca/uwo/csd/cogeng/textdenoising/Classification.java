package ca.uwo.csd.cogeng.textdenoising;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.supervised.attribute.AddClassification;
/**
 * A class to build cascaded classifiers KNN-NB on a data file provided in the constructor.
 * The class has methods to read the data file, set up necessary parameters for classifier, and return results
 * in String format
 * @author Rushdi Shams
 * @version 1.0 February 03 2015
 *
 */

public class Classification {
	//---------------------------------------------------------------------------------------------
	//Instance Variables
	//---------------------------------------------------------------------------------------------
	private DataSource source = null;
	private Instances data = null;
	private AddClassification distributionFilter;
	private IBk knn;
	private NaiveBayes nb;
	private FilteredClassifier fc;
	private final int k = 11;
	private Evaluation evaluation = null;
	
	/**
	 * Constructor
	 * @param trainingFile is the path+name of the ARFF data file
	 */
	public Classification(String trainingFile){
		try {
			source = new DataSource(trainingFile);
		} catch (Exception e) {
			System.out.println("Training file not found");
			e.printStackTrace();
		}
		try {
			data = source.getDataSet();
		} catch (Exception e) {
			System.out.println("Error reading training file");
			e.printStackTrace();
		}
		if (data.classIndex() == -1)
			data.setClassIndex(data.numAttributes() - 1);
	}
	//---------------------------------------------------------------------------------------------
	//Methods
	//---------------------------------------------------------------------------------------------
	/**
	 * Method to set classifier parameters
	 * Sets KNN with 11 (squared root of number of features), Naive Bayes (default),
	 * adds filter to generate distribution of + and - instances using KNN,
	 * sets the Filtered Classifier. NB as the classifier, and the filter mentioned before as filter
	 */
	public void setParameters(){
		knn = new IBk(k);
		nb = new NaiveBayes();
		distributionFilter = new AddClassification();
		distributionFilter.setClassifier(knn); 
		distributionFilter.setOutputDistribution(true);
		fc = new FilteredClassifier();
		fc.setFilter(distributionFilter);
		fc.setClassifier(nb);
	}
	/**
	 * Method to classify instances. Generates 10-fold CV results for the dataset
	 */
	public void classify(){	
		try {
			 fc.buildClassifier(data);
		} catch (Exception e1) {
			System.out.println("Error in building classifier from data");
			e1.printStackTrace();
		}
		
		try {
			evaluation = new Evaluation (data);
		} catch (Exception e) {
			System.out.println("Error in evaluation");
			e.printStackTrace();
		}
		try {
			evaluation.crossValidateModel(fc, data, 10, new Random(1));
		} catch (Exception e) {
			System.out.println("Error in Cross-validation");
			e.printStackTrace();
		}
	}
	/**
	 * Returns results 
	 */
	public String toString(){
		String output = evaluation.toSummaryString() + "\n";
		try {
			output += evaluation.toClassDetailsString();
			output += evaluation.toMatrixString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;		
	}
	
	//------------------------------------------------------
	//Driver method
	//------------------------------------------------------
	public static void main(String[] args){
		Classification c = new Classification("out/" + "aimed-arff-normalized.arff"); 
		c.setParameters();
		c.classify();
		System.out.println(c.toString());
	}
}
