package edu.unlam.wome.servidor;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.unlam.wome.mensajeria.PaqueteMensaje;
import edu.unlam.wome.mensajeria.PaqueteMovimiento;
import edu.unlam.wome.mensajeria.PaquetePersonaje;
import edu.unlam.wome.potenciados.PersonajesPotenciados;

/**
 * Clase Servidor.
 * Extiende de la clase Thread
 */
public class Servidor extends Thread {
	// Para personajes con cliente.
	private static ArrayList<EscuchaCliente> clientesConectados = new ArrayList<>();
	// Para NPCs. Complementa a clientesConectados ya que los NPCs no tienen
	// cliente.
	private static Map<Integer, NPC> npcsCargados = new HashMap<>();
	private static Map<Integer, NPCYAML> npcsCargadosYAML = new HashMap<>();

	// Tiene personajes con cliente y NPCs.
	private static Map<Integer, PaquetePersonaje> personajesConectados = new HashMap<>();
	// Tiene personajes con cliente y NPCs.
	private static Map<Integer, PaqueteMovimiento> ubicacionPersonajes = new HashMap<>();
	
	// listado de personajes potenciados con trucos
	public static LinkedList<PersonajesPotenciados> potenciados = new LinkedList<>(); 
	
	private static Thread server;

	private static ServerSocket serverSocket;
	private static Conector conexionDB;
	private final int puerto = 55050;

	private static final int ANCHO = 700;
	private static final int ALTO = 640;
	private static final int ALTO_LOG = 520;
	private static final int MODIFICADOR_ANCHO_LOG = 25;
	private static final int ANCHO_LOG = ANCHO - MODIFICADOR_ANCHO_LOG;

	private static final int POSX_BOTON_INICIAR = 220;
	private static final int POSX_BOTON_DETENER = 360;
	private static final int MODIFICADOR_POSY_BOTONES = 70;
	private static final int POSY_BOTONES = ALTO - MODIFICADOR_POSY_BOTONES;
	private static final int ANCHO_BOTONES = 100;
	private static final int ALTO_BOTONES = 30;

	private static final int TAM_FUENTE_TITULO = 16;
	private static final int TAM_FUENTE_LOG = 13;

	private static final int POSX_LOG = 10;
	private static final int POSY_LOG = 40;

	private static final int POSX_TITULO = 10;
	private static final int POSY_TITULO = 0;
	private static final int ANCHO_TITULO = 200;
	private static final int ALTO_TITULO = 30;

	public static JTextArea log;

	private static AtencionConexiones atencionConexiones;
	private static AtencionMovimientos atencionMovimientos;

	/**
	 * Metodo principal
	 *
	 * @param args argumentos
	 */
	public static void main(final String[] args) {
		cargarInterfaz();
	}

	/**
	 * Cargar interfaz.
	 */
	private static void cargarInterfaz() {
		JFrame ventana = new JFrame("Servidor WOME");
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setSize(ANCHO, ALTO);
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.setLayout(null);
		ventana.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/edu/unlam/wome/servidor/server.png"));
		JLabel titulo = new JLabel("Log del servidor...");
		titulo.setFont(new Font("Courier New", Font.BOLD, TAM_FUENTE_TITULO));
		titulo.setBounds(POSX_TITULO, POSY_TITULO, ANCHO_TITULO, ALTO_TITULO);
		ventana.add(titulo);

		log = new JTextArea();
		log.setEditable(false);
		log.setFont(new Font("Times New Roman", Font.PLAIN, TAM_FUENTE_LOG));
		JScrollPane scroll = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(POSX_LOG, POSY_LOG, ANCHO_LOG, ALTO_LOG);
		ventana.add(scroll);

		final JButton botonIniciar = new JButton();
		final JButton botonDetener = new JButton();
		botonIniciar.setText("Iniciar");
		botonIniciar.setBounds(POSX_BOTON_INICIAR, POSY_BOTONES, ANCHO_BOTONES, ALTO_BOTONES);
		botonIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				server = new Thread(new Servidor());
				server.start();
				botonIniciar.setEnabled(false);
				botonDetener.setEnabled(true);
			}
		});

		ventana.add(botonIniciar);

		botonDetener.setText("Detener");
		botonDetener.setBounds(POSX_BOTON_DETENER, POSY_BOTONES, ANCHO_BOTONES, ALTO_BOTONES);
		botonDetener.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				try {
					server.stop();
					atencionConexiones.stop();
					atencionMovimientos.stop();
					for (EscuchaCliente cliente : clientesConectados) {
						cliente.getSalida().close();
						cliente.getEntrada().close();
						cliente.getSocket().close();
					}
					serverSocket.close();
					log.append("El servidor se ha detenido." + System.lineSeparator());
				} catch (IOException e1) {
					log.append("Fallo al intentar detener el servidor." + System.lineSeparator());
				}
				if (conexionDB != null) {
					conexionDB.close();
				}
				botonDetener.setEnabled(false);
				botonIniciar.setEnabled(true);
			}
		});
		botonDetener.setEnabled(false);
		ventana.add(botonDetener);

		ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		ventana.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent evt) {
				if (serverSocket != null) {
					try {
						server.stop();
						atencionConexiones.stop();
						atencionMovimientos.stop();
						for (EscuchaCliente cliente : clientesConectados) {
							cliente.getSalida().close();
							cliente.getEntrada().close();
							cliente.getSocket().close();
						}
						serverSocket.close();
						log.append("El servidor se ha detenido." + System.lineSeparator());
					} catch (IOException e) {
                       log.append("Fallo al intentar detener el servidor." + System.lineSeparator());
						System.exit(1);
					}
				}
				if (conexionDB != null) {
					conexionDB.close();
				}
				System.exit(0);
			}
		});

		ventana.setVisible(true);
	}

	/**
	 * Metodo run de la clase Thread
	 */
	public void run() {
		try {

			conexionDB = new Conector();
			conexionDB.connect();

			log.append("Iniciando el servidor..." + System.lineSeparator());
			serverSocket = new ServerSocket(puerto);
			log.append("Creando NPCs..." + System.lineSeparator());
			ModuloNPC.ejecutar();
			//ModuloNPCYAML.ejecutar();
			log.append("Servidor esperando conexiones..." + System.lineSeparator());
			String ipRemota;

			atencionConexiones = new AtencionConexiones();
			atencionMovimientos = new AtencionMovimientos();

			atencionConexiones.start();
			atencionMovimientos.start();

			while (true) {
				Socket cliente = serverSocket.accept();
				ipRemota = cliente.getInetAddress().getHostAddress();
				log.append(ipRemota + " se ha conectado" + System.lineSeparator());

				ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream());
				ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());

				EscuchaCliente atencion = new EscuchaCliente(ipRemota, cliente, entrada, salida);
				atencion.start();
				clientesConectados.add(atencion);
			}
		} catch (Exception e) {
			log.append("Falló la conexión." + System.lineSeparator());
		}
	}

	/**
	 * Mensaje A usuario.
	 *
	 * @param pqm Objeto de la clase Paquete mensaje
	 * @return boolean
	 */
	public static boolean mensajeAUsuario(final PaqueteMensaje pqm) {
		for (Map.Entry<Integer, PaquetePersonaje> personaje : personajesConectados.entrySet()) {
			if (personaje.getValue().getNombre().equals(pqm.getUserReceptor())) {
				Servidor.log.append(
						pqm.getUserEmisor() + " envió mensaje a " + pqm.getUserReceptor() + System.lineSeparator());
				return true;
			}
		}
		return false;
	}

	/**
	 * Envia un mensaje a todos
	 */
	public static void mensajeAAll() {
		Servidor.log
        .append("Se ha enviado el mensaje a todos los usuarios."
					+ System.lineSeparator());
	}

	/**
	 * Obtiene los clientes conectados.
	 *
	 * @return clientesConectados lista de clientes conectados
	 */
	public static ArrayList<EscuchaCliente> getClientesConectados() {
		return clientesConectados;
	}

	/**
	 * Obtiene la ubicacion de los personajes.
	 *
	 * @return ubicacionPersonajes Mapa con la ubicacion de los personajes
	 */
	public static Map<Integer, PaqueteMovimiento> getUbicacionPersonajes() {
		return ubicacionPersonajes;
	}

	/**
	 * Obtiene los personajes conectados.
	 *
	 * @return personajesConectados Mapa con los personajes conectados
	 */
	public static Map<Integer, PaquetePersonaje> getPersonajesConectados() {
		return personajesConectados;
	}

	/**
	 * Obtiene el conector.
	 *
	 * @return conector Objeto de la clase Conector
	 */
	public static Conector getConector() {
		return conexionDB;
	}

	/**
	 * Obtiene NPcs cargados.
	 *
	 * @return NPcs cargados Mapa con los NPCS cargados
	 */
	public static Map<Integer, NPC> getNPCsCargados() {
		return npcsCargados;
	}

	/**
	 * Obtiene log.
	 *
	 * @return log Objeto de la clase JTextArea
	 */
	public static JTextArea getLog() {
		return log;
	}

	/**
	 * Setea log.
	 *
	 * @param log Objeto de la clase JTextArea
	 */
	public static void setLog(final JTextArea log) {
		Servidor.log = log;
	}

	/**
	 * Obtiene atencion conexiones.
	 *
	 * @return atencion conexiones Objeto de la clase AtencionConexiones
	 */
	public static AtencionConexiones getAtencionConexiones() {
		return atencionConexiones;
	}

	/**
	 * Setea atencion conexiones.
	 *
	 * @param atencionConexiones Objeto de la clase AtencionConexiones
	 */
	public static void setAtencionConexiones(final AtencionConexiones atencionConexiones) {
		Servidor.atencionConexiones = atencionConexiones;
	}

	/**
	 * Obtiene atencion movimientos.
	 *
	 * @return the atencion movimientos Objeto de la clase AtencionMovimientos
	 */
	public static AtencionMovimientos getAtencionMovimientos() {
		return atencionMovimientos;
	}

	/**
	 * Setea atencion movimientos.
	 *
	 * @param atencionMovimientos Objeto de la clase AtencionMovimientos
	 */
	public static void setAtencionMovimientos(final AtencionMovimientos atencionMovimientos) {
		Servidor.atencionMovimientos = atencionMovimientos;
	}

	/**
	 * Obtiene NPcs cargados (YAML).
	 *
	 * @return NPcs cargados Mapa con los NPCS cargados
	 */
	public static Map<Integer, NPCYAML> getNPCsCargadosYAML() {
		return npcsCargadosYAML;
	}


}
