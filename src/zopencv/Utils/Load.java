package zopencv.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Load {
	private String base;
	private CascadeClassifier classifier;
	private Mat imagenOriginal;
	private TreeMap<String, Mat> imagenesRecortadas;
	
	private String ubicacionCarpetaDeLaImagen;
	private String ubicacionCarpetaDeLaImagenProcesada;
	private String nombreImagen;
	private String nombreImagenSinFormato;
	private boolean problemas,problemas2;
	
	public Load(String classifier) {
		this.base = System.getProperty("user.dir") + "\\recursos\\";
		System.load(base + Core.NATIVE_LIBRARY_NAME + ".dll");
		this.classifier = new CascadeClassifier(base+classifier);
		this.imagenesRecortadas = new TreeMap<String, Mat>();
		this.problemas = false;
		this.problemas2 = false;
	}
	
	public void cargarImagen(String ubicacionImagen) {
		this.imagenOriginal = Imgcodecs.imread(ubicacionImagen);
		this.ubicacionCarpetaDeLaImagen = new File(ubicacionImagen).getParentFile().getAbsolutePath() + File.separator;
		System.out.println(this.ubicacionCarpetaDeLaImagen+"\n"+ubicacionImagen);
		this.nombreImagen = ubicacionImagen.substring(this.ubicacionCarpetaDeLaImagen.length());
		this.nombreImagenSinFormato = nombreImagen.replaceFirst(
				"."+nombreImagen.split("[.]+",0)[nombreImagen.split("[.]+",0).length-1],
				"");
	}
	
	public void encuadrarImagen() {
		System.out.println("Proceso 1: Iniciado");
		MatOfRect vectores = new MatOfRect();
		Mat image_gray = imagenOriginal.clone(), imageCloned, image_color_cropped;
		double en_scale = 1.08, top_bottom_padding_rate = 0.005;
		int resize_h = 720, height = image_gray.rows(), padding = (int) (height*top_bottom_padding_rate);
		float scale = ((float) image_gray.cols()/(float) image_gray.rows());		
		Size size = new Size((int) (scale*resize_h), resize_h);
		
//		Mat source = image_gray.clone();
//		Mat destination = new Mat(source.rows(),source.cols(),source.type());
//        Imgproc.GaussianBlur(source, destination, new Size(0,0), 10);
//        Core.addWeighted(source, 1.5, destination, -0.5, 0, image_gray);
//        Imgcodecs.imwrite(this.ubicacionCarpetaDeLaImagen+"ja.jpg", image_gray);

        
		Imgproc.resize(image_gray, image_gray, size,0,0,Imgproc.INTER_LINEAR);
		
		Rect roi = new Rect(0,padding,image_gray.cols(),resize_h-padding);
		
		
		
		if(!(0<= roi.x && 0 <= roi.width && roi.x + roi.width <= image_gray.cols() && 0 <= roi.y && 0 <= roi.height && roi.y + roi.height <= image_gray.rows())) {
			System.out.println(String.format("0 <= %s && 0 <= %s && %s <= %s && 0 <= %s && 0 <= %s && %s <= %s",
					roi.x,roi.width,roi.x+roi.width,image_gray.cols(),roi.y,roi.height,roi.y+roi.height,image_gray.rows()
					));
			System.out.println("Proceso 1: Error. Imagen no válido.");
			return;
		}
		
		image_color_cropped = new Mat(image_gray.clone(),roi);
		imageCloned = image_color_cropped.clone();
		
		System.out.println("Proceso 1: Completo");
		System.out.println("Proceso 2: Iniciado");
		
		Imgproc.cvtColor(image_color_cropped, image_gray, Imgproc.COLOR_RGB2GRAY);
		classifier.detectMultiScale(image_gray, vectores, en_scale, 2,0, new Size(36,9), new Size(36*40,9*40));
		
		System.out.println(vectores.toArray().length+" patentes encontrados");
		
		if(vectores.toArray().length==0) {
			System.out.println(String.format("en_scale = %s, top_bottom_padding_rate = %s, resize_h = %s\n"+
					"height = %s, padding = %s, scale = %s\n"+
					"size = %s, roi = %s", en_scale,top_bottom_padding_rate,resize_h,height,padding,scale,size,roi));
			System.out.println("Proceso 2: Error. Ninguna patente encontrado.");
			return;
		}
		
		System.out.println("Proceso 2: Completo");
		System.out.println("Proceso 3: Iniciado");
		
		String nombre_imagen = this.nombreImagenSinFormato;
		
		this.ubicacionCarpetaDeLaImagenProcesada = this.ubicacionCarpetaDeLaImagen+"\\"+nombre_imagen+"-procesada";
		File nuevoDirec = new File(this.ubicacionCarpetaDeLaImagenProcesada);
		nuevoDirec.mkdir();
		this.ubicacionCarpetaDeLaImagenProcesada += "\\";
		if(vectores.toArray().length>1) {
			imagenesRecortadas.put("patenteRecortada"+0+".jpg", imageCloned);
			Imgcodecs.imwrite(this.ubicacionCarpetaDeLaImagenProcesada+"patenteRecortada0"+".jpg",imageCloned);
			return;
		}
		int i = 0;
		for (Rect rect : vectores.toArray()) {
			
			int x = (int) (rect.x - rect.width*0.14);
			int w = (int) (rect.width + rect.width*0.28);
			int y = (int) (rect.y - rect.height * 0.15);
			int h = (int) (rect.height + rect.height*0.3);
			
			Mat aux = new Mat(imageCloned,new Rect(x,y,w,h));
			imagenesRecortadas.put("patenteRecortada"+i+".jpg", aux);
			
			Imgcodecs.imwrite(this.ubicacionCarpetaDeLaImagenProcesada+"patenteRecortada"+i+".jpg",aux);
			
			Imgproc.rectangle(imageCloned,new Point(x,y),new Point(x+w,y+h),new Scalar(0,255,0));
			
			i++;
		}
		
		Imgcodecs.imwrite(this.ubicacionCarpetaDeLaImagenProcesada+"patenteDetectado.jpg", imageCloned);
		System.out.println("Proceso 3: Completo");
		
	}
	
	public void procesoExtraer() {
		System.out.println("Proceso 4: Iniciado");
		if(imagenesRecortadas.size()==0) {
			System.out.println("Proceso 4: Error. No hay imagenes recortadas a procesar");
		}
		System.out.println("Proceso 4: Completo");
		int k = 0;
		
		for (Map.Entry<String, Mat> element : this.imagenesRecortadas.entrySet()) {
			System.out.println(String.format("Proceso 5.[%d]: Iniciado",k)); k++;
			String nombre_imagen = element.getKey();
			Mat imagen_recortada = element.getValue();
			String nombre_imagen_sin_formato = nombre_imagen.replaceFirst(
					"."+nombre_imagen.split("[.]+",0)[nombre_imagen.split("[.]+",0).length-1],
					"");
			String formato = nombre_imagen.substring(nombre_imagen_sin_formato.length());
			
			//Imgproc.resize(imagen_recortada, imagen_recortada, new Size(500,250));
			
			String base = this.ubicacionCarpetaDeLaImagenProcesada+"imagenes_recortadas";
			File nuevoDirec = new File(base);
			nuevoDirec.mkdir();
			base += "\\";
			Size size = new Size(400,100);
			System.out.println(String.format("Proceso 5.[%d].1: Iniciado",k));
			Mat img = segmentar(imagen_recortada,base,nombre_imagen,size);
			System.out.println(String.format("Proceso 5.[%d].1: Terminado",k));
			System.out.println(String.format("Proceso 5.[%d].2: Iniciado",k));
			extraer_caracteres(imagen_recortada,base,img,nombre_imagen,formato,nombre_imagen_sin_formato,size);
			if(problemas2) {
				size = new Size(500,250);
				img = segmentar(imagen_recortada,base,nombre_imagen,size);
				Imgcodecs.imwrite(this.ubicacionCarpetaDeLaImagenProcesada+"imagenresizeadoporproblema.jpg", img);
				extraer_caracteres(imagen_recortada,base,img,nombre_imagen,formato,nombre_imagen_sin_formato,size);
			}
			System.out.println(String.format("Proceso 5.[%d].2: Terminado",k));
			System.out.println(String.format("Proceso 5.[%d]: Terminado",k)); k++;
		}
		
		
	}


	private Mat segmentar(Mat imagen_recortada, String base, String nombre_imagen, Size size) {
//		Mat img = imagen_recortada.clone();
//		Imgproc.cvtColor(img , img , Imgproc.COLOR_RGB2GRAY);
//		Imgproc.resize(img, img, img.size(),5.0,5.0,Imgproc.INTER_CUBIC);
//		Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2BGR);
//		Imgproc.threshold(img, img, 144, 255, Imgproc.THRESH_BINARY);		
//		
//		Mat dst = img.clone();
//		
//		int dilatacion_size = 2;
//		Size s = new Size(2*dilatacion_size + 1, 2*dilatacion_size+1);
//		Point p = new Point(dilatacion_size, dilatacion_size);
//		Mat element = Imgproc.getStructuringElement( Imgproc.MORPH_ELLIPSE,s,p);
//		Imgproc.dilate(img, dst, element);
		
		
		Mat abc = new Mat();
		
		Imgproc.threshold(imagen_recortada, abc, 55, 255, Imgproc.THRESH_BINARY);
		
		
//		Imgcodecs.imwrite(base+"abc0-threshold.png", abc);
//		Imgcodecs.imwrite(base+"patenteMask4.png", dst);
//		Imgcodecs.imwrite(base+"abc0-resized.png", abc);
		
		
		Imgproc.resize(abc, abc, size);
		Imgproc.resize(imagen_recortada, imagen_recortada, size);
		
		return abc;
	}
	
	private void extraer_caracteres(Mat imagen_recortada, String base, Mat img, String nombre_imagen, String formato,
			String nombre_imagen_sin_formato, Size size) {
		
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
		
		List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
		double[] scalar = {1,1.1,1.2,1.3,0.9,0.8,0.7};
		double[] escalado = {1,1};
		
		if(problemas2) { escalado[0] = 1.25;escalado[1] =2.5;}
		
		int k = 0;
		do {
			contours2 = new ArrayList<MatOfPoint>();
			for (MatOfPoint matOfPoint : contours) {
				int width = (int) (Imgproc.boundingRect(matOfPoint).width);
				int height = (int) (Imgproc.boundingRect(matOfPoint).height );
				if(width * height * scalar[k]>700 && width * height * scalar[k] <10000)
					System.out.println("width: "+width +" - height: "+height +" -- width * height * scalar" +(width * height * scalar[k]));
				if(width * height * scalar[k]>1000 //700
							&& width * height * scalar[k] <14000
							&& width<= 80 * escalado[0]
							&& height<=140 * escalado[1]
							&& width>20) {
					contours2.add(matOfPoint);
					
				}
				
			}
			//System.out.println(contours2.size()>5 && contours.size()<8);
			if(contours2.size()>5 && contours2.size()<8) k=9;
//			k=9;
			System.out.println("size: " +contours2.size()+ " - " + k + " - problemas: "+ problemas2 + " - escalado0: "+ escalado[0]+".escalado1: "+escalado[1]);
			k++;
		} while(k<=6);
//			contours2 = contours.stream()
//					.filter(matOfPoint -> (int) 
//							(Imgproc.boundingRect(matOfPoint).width * Imgproc.boundingRect(matOfPoint).height)>1500
//							&& (int) 
//							Imgproc.boundingRect(matOfPoint).width * Imgproc.boundingRect(matOfPoint).height<10000
//							&& Imgproc.boundingRect(matOfPoint).width<80 
//							&& Imgproc.boundingRect(matOfPoint).height<140 
//							&& Imgproc.boundingRect(matOfPoint).width>20)
//					.collect(Collectors.toList());
		
		if(contours2.size()==0) {
			if(problemas) {
				problemas2 = true;
				problemas = false;
//				contours2 = contours.stream()
//						.filter(matOfPoint -> (int) 
//								Imgproc.boundingRect(matOfPoint).width * Imgproc.boundingRect(matOfPoint).height>1800
//								&& (int) 
//								Imgproc.boundingRect(matOfPoint).width * Imgproc.boundingRect(matOfPoint).height<20000)
//						.collect(Collectors.toList());
				System.out.println("error");
				return;
			}
			else {
				problemas = true;
				Core.bitwise_not(imagen_recortada, imagen_recortada);
				extraer_caracteres(imagen_recortada,base,segmentar(imagen_recortada,base,nombre_imagen,size),nombre_imagen,formato,nombre_imagen_sin_formato,size);
				return;
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
			Imgproc.rectangle(imagen_recortada,
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
		Imgcodecs.imwrite(base+nombre_imagen_sin_formato+"\\resultado-patentes-encuadradas-original."+formato, imagen_recortada);
		Mat clean = new Mat();
		Core.bitwise_and(char_mask, char_mask, img);
		Core.bitwise_not(img, clean);
		//Imgcodecs.imwrite("patenteMask4.png", clean);
		
		
		i = 0;
		for (Map.Entry<Integer, Mat> element : mapa.entrySet()) {
			
			Mat rr = new Mat();
			Imgproc.resize((Mat) element.getValue(), rr, new Size(50,100));
//			if(mapa.size()==6) Core.bitwise_not(rr, rr); 
			Imgcodecs.imwrite(base+nombre_imagen_sin_formato+"\\"+i+"-"+nombre_imagen_sin_formato+"."+formato, rr);
			i++;
		}
		
	}
}
