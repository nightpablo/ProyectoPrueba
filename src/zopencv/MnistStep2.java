package zopencv;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MnistStep2 {
	//The absolute path of the folder containing MNIST training and testing subfolders
	private static final String MNIST_DATASET_ROOT_FOLDER = "C:\\Users\\NightCrawler-NBOOK\\eclipse-workspace\\ProyectoPrueba\\recursos\\imagenesaprocesarcopia\\";
//	private static final String MNIST_DATASET_ROOT_FOLDER = "C:\\Users\\NightCrawler-NBOOK\\Desktop\\mnist_png\\";
	//Height and widht in pixel of each image
//	private static final int HEIGHT = 28;
//	private static final int WIDTH = 28;
	private static final int HEIGHT = 100;
	private static final int WIDTH = 50;
	//The total number of images into the training and testing set
//	private static final int N_SAMPLES_TRAINING = 60000;
//	private static final int N_SAMPLES_TESTING = 10000;
	private static final int N_SAMPLES_TRAINING = 450;
	private static final int N_SAMPLES_TESTING = 18;
	//The number of possible outcomes of the network for each input, 
	//correspondent to the 0..9 digit classification
	private static final int N_OUTCOMES = 10;
	
	public static void main(String[] args) throws IOException {
		String base = System.getProperty("user.dir") + "\\recursos\\";
		System.load(base + Core.NATIVE_LIBRARY_NAME + ".dll");
		
		
//		long t0 = System.currentTimeMillis();
//		DataSetIterator dsi;
//		try {
//			dsi = getDataSetIterator(MNIST_DATASET_ROOT_FOLDER + "training", N_SAMPLES_TRAINING, 3);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//		int rngSeed = 123;
//		int nEpochs = 2; // Number of training epochs
//
//		//log.info("Build model....");
//		System.out.println("Build model....");
//		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
//		  .seed(rngSeed) //include a random seed for reproducibility
//		  // use stochastic gradient descent as an optimization algorithm
//		  .updater(new Nesterovs(0.006, 0.9))
//		  .l2(1e-4)
//		  .list()
//		  .layer(new DenseLayer.Builder() //create the first, input layer with xavier initialization
//		    .nIn(HEIGHT*WIDTH*3)
//		    .nOut(1000)
//		    .activation(Activation.RELU)
//		    .weightInit(WeightInit.XAVIER)
//		    .build())
//		  .layer(new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
//		    .nIn(1000)
//		    .nOut(N_OUTCOMES)
//		    .activation(Activation.SOFTMAX)
//		    .weightInit(WeightInit.XAVIER)
//		    .build())
//		  .build();
//		MultiLayerNetwork model = new MultiLayerNetwork(conf);
//		model.init();
//		//print the score with every 500 iteration
//		model.setListeners(new ScoreIterationListener(500));
//		//log.info("Train model....");
//		System.out.println("Train model....");
//		model.fit(dsi, nEpochs);
		
		boolean saveUpdater= true;
		MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork("C:\\Users\\NightCrawler-NBOOK\\Desktop\\Salida-resultado\\MyMultiLayerNetwork.zip",saveUpdater);
		
		
		DataSetIterator testDsi = getDataSetIterator2( MNIST_DATASET_ROOT_FOLDER + "testing2", N_SAMPLES_TESTING, 3);
		//log.info("Evaluate model....");
		System.out.println("Evaluate model....");
		Evaluation eval = model.evaluate(testDsi);
//		log.info(eval.stats());
		System.out.println(eval.stats());

		long t1 = System.currentTimeMillis();
//		double t = (double)(t1 - t0) / 1000.0;
		//log.info("\n\nTotal time: "+t+" seconds");
//		System.out.println("\n\nTotal time: "+t+" seconds");
		File locationToSave = new File("C:\\Users\\NightCrawler-NBOOK\\Desktop\\Salida-resultado\\MyMultiLayerNetwork.zip");      //Where to save the network. Note: the file is in .zip format - can be opened externally
//        boolean saveUpdater = true;                                     //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);
	}
	
	private static DataSetIterator getDataSetIterator(String folderPath, int nSamples, int multiplo) throws IOException{
		File folder = new File(folderPath);
		File[] digitFolders = folder.listFiles();
		NativeImageLoader nil = new NativeImageLoader(HEIGHT, WIDTH);
		ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0,1);
		INDArray input = Nd4j.create(new int[]{ nSamples, HEIGHT*WIDTH*multiplo });
		INDArray output = Nd4j.create(new int[]{ nSamples, N_OUTCOMES });
		System.out.println("Filas: "+input.rows()+"Columnas: "+input.columns());
		System.out.println("Filas: "+output.rows()+"Columnas: "+output.columns());
		int n = 0;
		//scan all 0..9 digit subfolders
		for (File digitFolder : digitFolders) {
		  //take note of the digit in processing, since it will be used as a label
		  int labelDigit = Integer.parseInt(digitFolder.getName());
		  //scan all the images of the digit in processing
		  File[] imageFiles = digitFolder.listFiles();
		  for (File imageFile : imageFiles) {
			  Mat a = Imgcodecs.imread(imageFile.getAbsolutePath());
			  a.convertTo(a, CvType.CV_8U);
			  Imgcodecs.imwrite(imageFile.getAbsolutePath(),a);
			  imageFile = new File(imageFile.getAbsolutePath());
		    //read the image as a one dimensional array of 0..255 values
		    INDArray img = nil.asRowVector(imageFile);
		    //scale the 0..255 integer values into a 0..1 floating range
		    //Note that the transform() method returns void, since it updates its input array
		    scaler.transform(img);
		    //copy the img array into the input matrix, in the next row
		    
		    System.out.println("Filas: "+img.rows()+". Columnas: "+img.columns()+"\n"+img);
		    System.out.println(imageFile.getAbsolutePath());
		    input.putRow( n, img );
		    //in the same row of the output matrix, fire (set to 1 value) the column correspondent to the label
		    output.put( n, labelDigit, 1.0 );
		    //row counter increment
		    n++;
		  }
		}
		//Join input and output matrixes into a dataset
		DataSet dataSet = new DataSet( input, output );
		//Convert the dataset into a list
		List<DataSet> listDataSet = dataSet.asList();
		//Shuffle its content randomly
		Collections.shuffle( listDataSet, new Random(System.currentTimeMillis()) );
		//Set a batch size
		int batchSize = 10;
		//Build and return a dataset iterator that the network can use
		DataSetIterator dsi = new ListDataSetIterator<DataSet>( listDataSet, batchSize );
		return dsi;
		
	}
	
	private static DataSetIterator getDataSetIterator2(String folderPath, int nSamples, int multiplo) throws IOException{
		File folder = new File(folderPath);
		File[] digitFolders = folder.listFiles();
		NativeImageLoader nil = new NativeImageLoader(HEIGHT, WIDTH);
		ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0,1);
		INDArray input = Nd4j.create(new int[]{ nSamples, HEIGHT*WIDTH*multiplo });
		INDArray output = Nd4j.create(new int[]{ nSamples, N_OUTCOMES });
		System.out.println("Filas: "+input.rows()+"Columnas: "+input.columns());
		System.out.println("Filas: "+output.rows()+"Columnas: "+output.columns());
		int n = 0;
		//scan all 0..9 digit subfolders
		for (File digitFolder : digitFolders) {
		  //take note of the digit in processing, since it will be used as a label
		  int labelDigit = Integer.parseInt(digitFolder.getName());
		  //scan all the images of the digit in processing
		  File[] imageFiles = digitFolder.listFiles();
		  for (File imageFile : imageFiles) {
			  Mat a = Imgcodecs.imread(imageFile.getAbsolutePath());
			  a.convertTo(a, CvType.CV_8U);
			  Imgcodecs.imwrite(imageFile.getAbsolutePath(),a);
			  imageFile = new File(imageFile.getAbsolutePath());
		    //read the image as a one dimensional array of 0..255 values
		    INDArray img = nil.asRowVector(imageFile);
		    //scale the 0..255 integer values into a 0..1 floating range
		    //Note that the transform() method returns void, since it updates its input array
		    scaler.transform(img);
		    //copy the img array into the input matrix, in the next row
		    
		    System.out.println("Filas: "+img.rows()+". Columnas: "+img.columns()+"\n"+img);
		    System.out.println(imageFile.getAbsolutePath());
		    input.putRow( n, img );
		    //in the same row of the output matrix, fire (set to 1 value) the column correspondent to the label
		    output.put( n, labelDigit, 1.0 );
		    //row counter increment
		    n++;
		  }
		}
		//Join input and output matrixes into a dataset
		DataSet dataSet = new DataSet( input, output );
		//Convert the dataset into a list
		List<DataSet> listDataSet = dataSet.asList();
		//Shuffle its content randomly
		Collections.shuffle( listDataSet, new Random(System.currentTimeMillis()) );
		//Set a batch size
		int batchSize = 10;
		//Build and return a dataset iterator that the network can use
		DataSetIterator dsi = new ListDataSetIterator<DataSet>( listDataSet, batchSize );
		return dsi;
		
	}
	
}
