package servidor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.PaqueteDeMovimientos;
import mensajeria.PaqueteDePersonajes;

/**
 * The Class ModuloNPC.
 */
public class ModuloNPC {

	private static final long PERIODO = 500;

	/**
	 * Ejecutar.
	 */
	public static void ejecutar() {
		cargarNPCsIniciales(); // Carga los NPCs iniciales.

		Runnable epNPC = new Runnable() {
			public void run() {
				enviarPaquetesNPCs(); // Envía los movimientos de los NPCs a los clientes.
			}
		};
		ScheduledExecutorService executor1 = Executors.newScheduledThreadPool(1);
		// El código de epNPC se ejecuta cada 0.5 segundos.
        executor1.scheduleAtFixedRate(epNPC, 0, PERIODO, TimeUnit.MILLISECONDS);
	}

	/**
	 * Cargar Npcs iniciales.
	 */
	private static void cargarNPCsIniciales() {
		int cant;
		int nroArchivo = 1;
		String path = null;

		Scanner cantFile = null; // Contiene la cantidad de archivos a leer.
		try {
			cantFile = new Scanner(new File("npcs//start//cant.txt"));
		} catch (FileNotFoundException e1) {
        Servidor.getLog().append("No se pudo abrir el archivo cant.txt de "
        		+ "la carpeta de NPCs iniciales." + System.lineSeparator());
			return;
		}
		cant = cantFile.nextInt();
		cantFile.close();

		while (cant != 0) {
			path = "npcs//start//" + nroArchivo + ".txt";
			try {
               // El constructor carga el nuevo NPC a las listas del servidor por lo que el objeto no se pierde.
				new NPC(path);
			} catch (IOException e) {
            Servidor.getLog().append("No se pudo cargar el archivo "
			   + nroArchivo + ".txt de la carpeta de NPCs iniciales." + System.lineSeparator());
			}
			nroArchivo++;
			cant--;
		}
	}

	/**
	 * Enviar paquetes NPcs.
	 */
	private static void enviarPaquetesNPCs() {
		Gson gson = new Gson();
		for (EscuchaCliente conectado : Servidor.getClientesConectados()) {

			if (conectado.getPaquetePersonaje().getEstado() != Estado.estadoOffline) {

                PaqueteDePersonajes pdp = (PaqueteDePersonajes) new PaqueteDePersonajes(
                		Servidor.getPersonajesConectados()).clone();
				pdp.setComando(Comando.CONEXION);
				synchronized (conectado) {
					try {
						conectado.getSalida().writeObject(gson.toJson(pdp));
					} catch (IOException e) {
                    Servidor.getLog().append("No se pueden actualizar los PaquetePersonaje de los NPCs "
                    		+ "en este momento." + System.lineSeparator());
					}
				}
			}

			if (conectado.getPaquetePersonaje().getEstado() == Estado.estadoJuego) {

                PaqueteDeMovimientos pdp = (PaqueteDeMovimientos) new PaqueteDeMovimientos(
                				Servidor.getUbicacionPersonajes()).clone();
				pdp.setComando(Comando.MOVIMIENTO);
				synchronized (conectado) {
					try {
						conectado.getSalida().writeObject(gson.toJson(pdp));
					} catch (IOException e) {
                     Servidor.getLog().append("No se pueden actualizar los PaqueteMovimiento de los NPCs "
                     		+ "en este momento." + System.lineSeparator());
					}
				}
			}

		}
	}
}
