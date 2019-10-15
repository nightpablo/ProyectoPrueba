package zopencv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ClasificadorPatente {
	private static int contador = 0;
	private static boolean problemas = false;
	
	public static void main(String[] arg) {
		
		String base = System.getProperty("user.dir") + "\\recursos\\";
		System.load(base + Core.NATIVE_LIBRARY_NAME + ".dll");
		File dir = new File(base+"Matriculas-test\\");
		String[] ficheros = dir.list();
		int k = 0;
		
		Mat imagen_original = Imgcodecs.imread("C:\\Users\\NightCrawler-NBOOK\\Desktop\\java\\Python\\1200px-FE-Schrift.svg.png");
//		Core.bitwise_not(imagen_original, imagen_original);
		Imgproc.resize(imagen_original, imagen_original, new Size(500,250));
//		System.out.println(nombre_imagen);
//		Imgproc.resize(imagen_original, imagen_original, new Size(500,250));
//		String formato = component_name[component_name.length-1];
//		String nombre_imagen_sin_formato = nombre_imagen.replaceFirst("."+formato, "");
		base = "C:\\Users\\NightCrawler-NBOOK\\Desktop\\java\\Python\\";
		Mat img = segmentar(imagen_original,base,"1200px-FE-Schrift.svg.png");
		Object[] img_caracteres = extraer_caracteres(imagen_original,base,img,"1200px-FE-Schrift.svg.png","png","1200px-FE-Schrift.svg");
		
		
		/*
		for (String cadena : ficheros) {

			cadena = cadena.toLowerCase();
			String[] component_name = cadena.split("[.]+",0);
			
			if(component_name[component_name.length-1].contains("jpg")
					|| component_name[component_name.length-1].contains("png")
					|| component_name[component_name.length-1].contains("gif")
					|| component_name[component_name.length-1].contains("jpeg")
					|| component_name[component_name.length-1].contains("bmp")) {
				String nombre_imagen = cadena;
	
				Mat imagen_original = Imgcodecs.imread(base+"Matriculas-test\\" + nombre_imagen);
				System.out.println(nombre_imagen);
				Imgproc.resize(imagen_original, imagen_original, new Size(500,250));
				String formato = component_name[component_name.length-1];
				String nombre_imagen_sin_formato = nombre_imagen.replaceFirst("."+formato, "");
				
				Mat img = segmentar(imagen_original,base+"Matriculas-test\\",nombre_imagen);
				Object[] img_caracteres = extraer_caracteres(imagen_original,base+"Matriculas-test\\",img,nombre_imagen,formato,nombre_imagen_sin_formato);
				k++;
			}
		}
		System.out.println(k+" patentes encontrados");*/
	
	}
	
	private static Object[] extraer_caracteres(Mat imagen_original, String base, Mat img,String nombre_imagen, String formato, String nombre_imagen_sin_formato) {
		Mat a = new Mat();
		
		
		Core.bitwise_not(img, a);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.cvtColor(a, a, Imgproc.COLOR_BGR2GRAY);
		Imgproc.findContours(a, contours,new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
		
		Mat char_mask = a;

		int i = 0;
		
		TreeMap<Integer, Mat> mapa = new TreeMap<Integer, Mat>();
		ArrayList<ArrayList<Object>> lista = new ArrayList<ArrayList<Object>>();
		Mat lol = a.clone();
		
		List<MatOfPoint> contours2 = contours.stream()
				.filter(matOfPoint -> (int) 
						Imgproc.boundingRect(matOfPoint).width * Imgproc.boundingRect(matOfPoint).height>500
						&& (int) 
						Imgproc.boundingRect(matOfPoint).width * Imgproc.boundingRect(matOfPoint).height<40000)
				.collect(Collectors.toList());
		
		if(contours2.size()==0) {
			if(problemas) {
				contours2 = contours.stream()
						.filter(matOfPoint -> (int) 
								Imgproc.boundingRect(matOfPoint).width * Imgproc.boundingRect(matOfPoint).height>200
								&& (int) 
								Imgproc.boundingRect(matOfPoint).width * Imgproc.boundingRect(matOfPoint).height<40000)
						.collect(Collectors.toList());
				
			}
			else {
				problemas = true;
				Core.bitwise_not(imagen_original, imagen_original);
				return extraer_caracteres(imagen_original,base,segmentar(imagen_original,base,nombre_imagen),nombre_imagen,formato,nombre_imagen_sin_formato);
			}
		}
		problemas = false;
		
		for (MatOfPoint matOfPoint : contours2) {
			Rect rct = Imgproc.boundingRect(matOfPoint), rct2 = null;

//			System.out.println(rct.width+"x"+rct.height);
					
			rct2 = new Rect(rct.x, rct.y-4, rct.width-2, rct.height+8);
			Mat l = a.clone();
			
			Mat ot = new Mat(l,new Rect(new Point(rct2.x,rct2.y),new Point(rct2.x+rct2.width,rct2.y+rct2.height)));
			//System.out.println(new Rect(new Point(rct2.x,rct2.y),new Point(rct2.x+rct2.width,rct2.y+rct2.height)));
			mapa.put(rct2.x, ot);
			
			ArrayList<Object> ll = new ArrayList<Object>();
			ll.add(ot);
			ll.add(new Rect(new Point(rct2.x,rct2.y),new Point(rct2.x+rct2.width,rct2.y+rct2.height)));
			lista.add(ll);
			Imgproc.rectangle(lol,
					new Point(rct2.x,rct2.y), 
					new Point(rct2.x+rct2.width,rct2.y+rct2.height), 
					new Scalar(0, 0, 255),2);
			Imgproc.rectangle(imagen_original,
					new Point(rct2.x,rct2.y), 
					new Point(rct2.x+rct2.width,rct2.y+rct2.height), 
					new Scalar(0, 0, 255),2);
			
			
			i++;
			
			
			Imgproc.rectangle(char_mask,
					new Point(rct2.x,rct2.y), 
					new Point(rct2.x+rct2.width,rct2.y+rct2.height), 
					new Scalar(0,255,0),-1); // modifique el vacio por -1, sino borrarlo
			
		}
	
		
		Core.bitwise_not(lol, lol);
		File nuevoDirec = new File(base+nombre_imagen_sin_formato);
		nuevoDirec.mkdir();
		//System.out.println(base+nombre_imagen_sin_formato);
		//System.out.println();
		Imgcodecs.imwrite(base+nombre_imagen_sin_formato+"\\resultado-patentes-encuadradas."+formato, lol);
		Imgcodecs.imwrite(base+nombre_imagen_sin_formato+"\\resultado-patentes-encuadradas-original."+formato, imagen_original);
		Mat clean = new Mat();
		Core.bitwise_and(char_mask, char_mask, img);
		Core.bitwise_not(img, clean);
		//Imgcodecs.imwrite("patenteMask4.png", clean);
		
		
		i = 0;
		for (Map.Entry<Integer, Mat> element : mapa.entrySet()) {
			
			Mat rr = new Mat();
			Imgproc.resize((Mat) element.getValue(), rr, new Size(50,100));
//			if(mapa.size()==6) Core.bitwise_not(rr, rr); 
			Imgcodecs.imwrite(base+nombre_imagen_sin_formato+"\\"+i+"-"+nombre_imagen_sin_formato+"."+formato, (Mat) element.getValue());
			i++;
		}
		
		
		return new Object[]{clean,lista};
	}
	
	
	private static Mat segmentar(Mat imagen_original, String base, String nombre_imagen) {
		Mat img = imagen_original.clone();
		Imgproc.cvtColor(img , img , Imgproc.COLOR_RGB2GRAY);
		Imgproc.resize(img, img, img.size(),5.0,5.0,Imgproc.INTER_CUBIC);
		Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2BGR);
		Imgproc.threshold(img, img, 144, 255, Imgproc.THRESH_BINARY);		
		
		Mat dst = img.clone();
		
		int dilatacion_size = 1;
		Size s = new Size(2*dilatacion_size + 1, 2*dilatacion_size+1);
		Point p = new Point(dilatacion_size, dilatacion_size);
		Mat element = Imgproc.getStructuringElement( Imgproc.MORPH_ELLIPSE,s,p);
		Imgproc.dilate(img, dst, element);
//		Imgcodecs.imwrite(contador+"patenteMask1.png", imagen_original);
//		Imgcodecs.imwrite(contador+"patenteMask2.png", dst);
		contador++;
		return dst;
		
		
	}
}
