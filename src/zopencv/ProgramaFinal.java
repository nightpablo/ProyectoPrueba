package zopencv;

import zopencv.Utils.Load;

public class ProgramaFinal {
	
	public static void main(String[] args) {
		Load load = new Load("cascade.xml");
		load.cargarImagen("C:\\Users\\NightCrawler-NBOOK\\Desktop\\Salida-resultado\\456748.jpg");
		load.encuadrarImagen();
		load.procesoExtraer();
	}
}
