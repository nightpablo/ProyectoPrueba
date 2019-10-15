package zopencv;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class AdelgazarEntrenamiento {

	public static void main(String[] args) {
		
		String base = System.getProperty("user.dir") + "\\recursos\\Matriculas-test\\train\\";
		System.load(System.getProperty("user.dir") + "\\recursos\\"+ Core.NATIVE_LIBRARY_NAME + ".dll");
		
		
		final String base2 = "C:\\Users\\NightCrawler-NBOOK\\Desktop\\asd\\train\\";
		File train2 = new File(base2);
		String[] ficheros2 = train2.list();
		for (String string : ficheros2) {
			base = "";
			base += base2+string+"\\";
			File nuevoDirec = new File(base+"invertido");
			nuevoDirec.mkdir();
			File train = new File(base);
			String[] ficheros = train.list();
			System.out.println(base+"  -  "+string);
			for (String cadena : ficheros) {
				cadena = cadena.toLowerCase();
				String[] component_name = cadena.split("[.]+",0);
				
				if(component_name[component_name.length-1].contains("jpg")
						|| component_name[component_name.length-1].contains("png")
						|| component_name[component_name.length-1].contains("gif")
						|| component_name[component_name.length-1].contains("jpeg")
						|| component_name[component_name.length-1].contains("bmp")) {
//					Mat imagen_original = Imgcodecs.imread(base + cadena);
//					Mat adelgazado = Adelgazar(imagen_original);
					try {
						FileUtils.forceDelete(new File(base+"invertido"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(base+"invertido\\"+cadena);
//					Imgcodecs.imwrite(base/*+"invertido\\"*/+cadena, adelgazado);
				}
			}
		}
		
		
		
		
		
		
		
	}
	
	private static Mat Adelgazar(Mat img) {
		Mat dst = new Mat();
//		System.out.println(dst.dims()+"."+dst.step1());
		
//		if(dst.dims()>2 || dst.step1()<=0) return dst;
		Core.bitwise_not(img, img);
		double dilatacion_size = 0.5;
		Size s = new Size(2*dilatacion_size + 1, 2*dilatacion_size+1);
		Point p = new Point(dilatacion_size, dilatacion_size);
		Mat element = Imgproc.getStructuringElement( Imgproc.MORPH_ELLIPSE,s,p);
		Imgproc.dilate(img, dst, element);
		Core.bitwise_not(dst, dst);
//		/Imgcodecs.imwrite(contador+"patenteMask1.png", imagen_original);
//		Imgcodecs.imwrite(contador+"patenteMask2.png", dst);
		Core.bitwise_not(img, img);
		
		return dst;
	}

}
