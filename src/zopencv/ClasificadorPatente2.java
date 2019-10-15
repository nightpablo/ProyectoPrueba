package zopencv;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ClasificadorPatente2 {
	public static void main(String[] arg) {
		String base = System.getProperty("user.dir") + "\\recursos\\";
		System.load(base + Core.NATIVE_LIBRARY_NAME + ".dll");
		
		String nombre_imagen = "456748.jpg";

		  
		CascadeClassifier detectorPatente = new CascadeClassifier(base + "cascade.xml");
		Mat image_gray = Imgcodecs.imread(base + nombre_imagen);
		MatOfRect vectoresPatente = new MatOfRect();
		//image.shape[0] es filas  -  image.shape[1] es columna
		
		
		
		
		double en_scale = 1.08;
		double top_bottom_padding_rate = 0.005;
		int resize_h = 720; //image
		int height = image_gray.rows(); //height = image_gray.shape[0]
		int width = image_gray.cols();
		
		int padding = (int) (height*top_bottom_padding_rate); //padding = int(height*top_bottom_padding_rate)
		float scale = ((float) image_gray.cols()/(float) image_gray.rows()); // scale = image_gray.shape[1]/float(image_gray.shape[0])
		
		
		Mat image = new Mat();
		Size size = new Size((int) (scale*resize_h), resize_h);
		Imgproc.resize(image_gray, image_gray, size); //image = cv2.resize(image_gray, (int(scale*resize_h), resize_h))
		
		
		Mat image_color_cropped = null;
		Rect roi = new Rect(0,padding,image_gray.cols(),resize_h-padding);
//		Rect roi = new Rect(0,padding,image_gray.cols(),resize_h-padding);
//		System.out.println(String.format("[%s:%s,%s:%s]", 0,image_gray.rows(),0,image_gray.cols()));
//		System.out.println(String.format("[%s:%s,%s:%s]", padding,resize_h-padding,0,image_gray.cols()));
		System.out.println("\n"+new Rect(0,0,width,height));
		System.out.println(roi);
		System.out.println("scale: "+scale+". size: "+size);
		
		image = image_gray.clone();
		//0 <= roi.x && 0 <= roi.width && roi.x + roi.width <= m.cols && 0 <= roi.y && 0 <= roi.height && roi.y + roi.height <= m.rows
		if(0<= roi.x && 0 <= roi.width && roi.x + roi.width <= image.cols() && 0 <= roi.y && 0 <= roi.height && roi.y + roi.height <= image.rows()) {
			//image_color_cropped = image.submat(roi);
			image_color_cropped = new Mat(image,roi);
			
		}
		else {
			System.out.println(roi+"\n"+(roi.x + roi.width)+"<="+image.cols()+"    -     "+(roi.y + roi.height)+"<="+image.rows());
			return;
		}
		//image_color_cropped = new Mat(image1,rect1);
		Imgproc.cvtColor(image_color_cropped, image_gray, Imgproc.COLOR_RGB2GRAY);
		
		detectorPatente.detectMultiScale(image_gray, vectoresPatente, en_scale, 2,0, new Size(36,9), new Size(36*40,9*40)); //watches
		
		Imgcodecs.imwrite("image_color_cropped.jpg", image_color_cropped);
		Imgcodecs.imwrite("image_gray.jpg", image_gray);
		
		
		System.out.println(vectoresPatente.toArray().length+" patentes encontrados");
		System.out.println(String.format("en_scale = %s, top_bottom_padding_rate = %s, resize_h = %s\n"+
				"height = %s, padding = %s, scale = %s\n"+
				"size = %s, roi = %s", en_scale,top_bottom_padding_rate,resize_h,height,padding,scale,size,roi));
		if(vectoresPatente.toArray().length==0) return;
		nombre_imagen = nombre_imagen.substring(0, nombre_imagen.length()-4)+"-";
		
		int i = 0;
		for (Rect rect : vectoresPatente.toArray()) {
			
			int x = (int) (rect.x - rect.width*0.14);
			int w = (int) (rect.width + rect.width*0.28);
			int y = (int) (rect.y - rect.height * 0.15);
			int h = (int) (rect.height + rect.height*0.3);
			
			// Recortamos todos los patentes encontrados en la imagen
			Imgcodecs.imwrite(nombre_imagen+"patenteRecortada"+i+".png", //Crea una nueva imagen recortada por cada patente encontrada 
					new Mat(image, // imagen original
							new Rect(x,y,w,h) // recuadro de la patente
							)
					);
//			Imgcodecs.imwrite(nombre_imagen+"patenteRecortada"+i+".png", //Crea una nueva imagen recortada por cada patente encontrada 
//					new Mat(image, // imagen original
//							new Rect(rect.x,rect.y,rect.width,rect.height) // recuadro de la patente
//							)
//					);		
			i++;
			
			// Modifica a la variable image por una imagen con cuadros para todos los patentes encontrados
			Imgproc.rectangle(image,
					new Point(x,y), 
					new Point(x+w,y+h), 
					new Scalar(0,255,0)); 
		}
		
		
		
		Imgcodecs.imwrite(nombre_imagen+"patenteDetectado.png", image);
		
		
		
		


		
		
	}
	
	
	
}
