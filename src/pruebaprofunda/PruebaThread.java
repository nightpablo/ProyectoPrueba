package pruebaprofunda;

public class PruebaThread {
	public static int total = 0;
	public static void main(String[] arg) {
		Thread b = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 100000000; i++) {
					total+=i;
				}
				for (int i = 0; i < 100000000; i++) {
					total+=i;
				}
				for (int i = 0; i < 100000000; i++) {
					total+=i;
				}
				System.out.println("Termine el Thread y empiezo a notificar");
				
//				this.notify();
			}
		});
		System.out.println("Comienzo a correr el Thread");
		b.start();
		synchronized (b) {
			try {
				System.out.println("Bloqueo acá para esperar a que complete el Thread");
				b.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Se liberó el bloqueo y el total es "+total);
		
	}
}
