package zopencv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


public class pasarimageneslistaacarpeta {
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
				
				File nuevoDirec = new File(base+nombre_imagen_sin_formato.toUpperCase());
				nuevoDirec.mkdir();
				try {
					Files.move(Paths.get(base+cadena), Paths.get(base+nombre_imagen_sin_formato.toUpperCase()+"\\"+nombre_imagen_sin_formato.toUpperCase()+"."+formato));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				
				
			}
		}
	}
}
