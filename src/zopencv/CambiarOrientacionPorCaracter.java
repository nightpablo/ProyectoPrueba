package zopencv;

import java.io.File;

import org.datavec.image.loader.NativeImageLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class CambiarOrientacionPorCaracter {
public static void main(String[] args) {
		
		String base = System.getProperty("user.dir") + "\\recursos\\Matriculas-test\\train\\";
		System.load(System.getProperty("user.dir") + "\\recursos\\"+ Core.NATIVE_LIBRARY_NAME + ".dll");
		
		
		File train = new File(base);
		String[] ficheros = train.list();
		for (String cadena : ficheros) {
			cadena = cadena.toLowerCase();
			String[] component_name = cadena.split("[.]+",0);
			
			if(component_name[component_name.length-1].contains("jpg")
					|| component_name[component_name.length-1].contains("png")
					|| component_name[component_name.length-1].contains("gif")
					|| component_name[component_name.length-1].contains("jpeg")
					|| component_name[component_name.length-1].contains("bmp")) {
				String formato = component_name[component_name.length-1];
				String nombre_imagen_sin_formato = cadena.replaceFirst("."+formato, "");
				
				File nuevoDirec = new File(base+nombre_imagen_sin_formato);
				nuevoDirec.mkdir();
				Mat imagen_original = Imgcodecs.imread(base + cadena);
				
				cambiarorientacion(base+nombre_imagen_sin_formato+"\\",cadena,imagen_original);
				
			}
		}
	}
	
	private static void cambiarorientacion(String base, String nombre_imagen, Mat imagen_original) {
		
		for(int i=0; i<360;i++) {
			if(i==29) i = 336;
			Mat orientado = imagen_original.clone();
		    Point center = new Point(imagen_original.cols() / 2, imagen_original.rows() / 2);
		    Mat M = Imgproc.getRotationMatrix2D(center, 360-i, 1.0);
			Imgproc.warpAffine(orientado, orientado, M, imagen_original.size(),
					Imgproc.INTER_LINEAR,
					Core.BORDER_CONSTANT,
					new Scalar(255,255,255));
			
//			System.out.println(base);
			Mat invertido = new Mat();
			Core.bitwise_not(orientado, invertido);
//			orientado.convertTo(invertido, CvType.CV_8U, 1/384.0);
			
//			Imgcodecs.imwrite(base+i+nombre_imagen, orientado.clone());
			Imgcodecs.imwrite(base+"zzzz"+i+nombre_imagen, invertido);
			
		}
	}
}
