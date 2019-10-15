package zopencv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class PasarA01 {
//	private static final String MNIST_DATASET_ROOT_FOLDER = "C:\\Users\\NightCrawler-NBOOK\\eclipse-workspace\\ProyectoPrueba\\recursos\\imagenesaprocesar\\";//	private static final String MNIST_DATASET_ROOT_FOLDER = "C:\\Users\\NightCrawler-NBOOK\\eclipse-workspace\\ProyectoPrueba\\recursos\\imagenesaprocesar\\";
	private static final String MNIST_DATASET_ROOT_FOLDER = "C:\\Users\\NightCrawler-NBOOK\\Desktop\\asd\\";

	private static final int N_SAMPLES_TRAINING = 450;
	private static final int HEIGHT = 28;
	private static final int WIDTH = 28;
	private static final int N_OUTCOMES = 10;
	
	
	public static void main(String[] args) {
		String base = System.getProperty("user.dir") + "\\recursos\\";
		System.load(base + Core.NATIVE_LIBRARY_NAME + ".dll");
		
		DataSetIterator dsi;
		try {
			dsi = getDataSetIterator(MNIST_DATASET_ROOT_FOLDER + "train8bits", N_SAMPLES_TRAINING, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	private static DataSetIterator getDataSetIterator(String folderPath, int nSamples, int multiplo) throws IOException{
		File folder = new File(folderPath);
		File[] digitFolders = folder.listFiles();
		NativeImageLoader nil = new NativeImageLoader(HEIGHT, WIDTH);
		ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0,1);
//		INDArray input = Nd4j.create(new int[]{ nSamples, HEIGHT*WIDTH*multiplo });
//		INDArray output = Nd4j.create(new int[]{ nSamples, N_OUTCOMES });
//		System.out.println("Filas: "+input.rows()+"Columnas: "+input.columns());
//		System.out.println("Filas: "+output.rows()+"Columnas: "+output.columns());
		PrintWriter out = new PrintWriter("character_dataset_10x20_8bits.dat");
		String str = "";
		for (File digitFolder : digitFolders) {
		  String labelDigit = digitFolder.getName();
		  File[] imageFiles = digitFolder.listFiles();
		  for (File imageFile : imageFiles) {
			  
		    INDArray img = nil.asRowVector(imageFile);
		    scaler.transform(img);
//		    System.out.println(img);
		    
//		    System.out.println(transformLine(labelDigit, img));
//		    System.out.println("----------------------------------------------------------------------");
		    str = transformLine(labelDigit, img);
//		    System.out.println("HEIGHTxWIDTH="+HEIGHT+"x"+WIDTH);
//		    for (int i = 0; i < str.substring(2).length(); i+=WIDTH) {
//				System.out.println(str.substring(2).substring(i,i+WIDTH));
//			}
		    out.println(str);
		   // System.out.println(str);
		  }
		}
		out.close();
		System.out.println("Guardado");
		return null;
		
	}
	
	private static String transformLine(String Character, INDArray img){
		float i[] = img.toFloatVector();
		int[] a = new int[i.length];
		int counter = 0;
		for (float j : i) {
			a[counter] =(int) (j>0.3? 1 : 0);
			counter++;
		}
		String str = Arrays.toString(a)
					.replaceAll("\\s+", "");
		str = str.substring(1,str.length()-1);
		return str+","+Character.toUpperCase();
		
	}
}
