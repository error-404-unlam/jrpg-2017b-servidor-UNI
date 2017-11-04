package edu.unlam.wome.servidor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import net.sourceforge.yamlbeans.YamlReader;
import edu.unlam.wome.estados.Estado;
import edu.unlam.wome.mensajeria.Comando;
import edu.unlam.wome.mensajeria.PaqueteDeMovimientos;
import edu.unlam.wome.mensajeria.PaqueteDePersonajes;

/**
 * Clase ModuloNPC.
 */
public class ModuloNPCYAML {

	private static final long PERIODO = 500;

	/**
	 * Constructor.
	 */
	public ModuloNPCYAML() {

	}

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

		YamlReader reader;
		try {
			reader = new YamlReader(new FileReader("npcs//iniciales.yml"));

			Object object;
			Map<String, String> map;

			object = reader.read();
			while (object != null) {
				map = (Map<String, String>) object;
				new NPCYAML(map);
				object = reader.read();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enviar paquetes NPcs.
	 */
	private static void enviarPaquetesNPCs() {
		Gson gson = new Gson();
		for (EscuchaCliente conectado : Servidor.getClientesConectados()) {

			if (conectado.getPaquetePersonaje().getEstado() != Estado.getEstadoOffLine()) {

				PaqueteDePersonajes pdp = (PaqueteDePersonajes) new PaqueteDePersonajes(Servidor.getPersonajesConectados()).clone();
				pdp.setComando(Comando.CONEXION);
				synchronized (conectado) {
					try {
						conectado.getSalida().writeObject(gson.toJson(pdp));
					} catch (IOException e) {
						Servidor.getLog().append("No se pueden actualizar los PaquetePersonaje de los NPCs " + "en este momento." + System.lineSeparator());
					}
				}
			}

			if (conectado.getPaquetePersonaje().getEstado() == Estado.getEstadoJuego()) {

				PaqueteDeMovimientos pdp = (PaqueteDeMovimientos) new PaqueteDeMovimientos(Servidor.getUbicacionPersonajes()).clone();
				pdp.setComando(Comando.MOVIMIENTO);
				synchronized (conectado) {
					try {
						conectado.getSalida().writeObject(gson.toJson(pdp));
					} catch (IOException e) {
						Servidor.getLog().append("No se pueden actualizar los PaqueteMovimiento de los NPCs " + "en este momento." + System.lineSeparator());
					}
				}
			}

		}
	}
}
