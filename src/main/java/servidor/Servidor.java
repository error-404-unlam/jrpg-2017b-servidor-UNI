package servidor;

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
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mensajeria.PaqueteMensaje;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;

/**
 * The Class Servidor.
 */
public class Servidor extends Thread {
	// Para personajes con cliente.
	private static ArrayList<EscuchaCliente> clientesConectados = new ArrayList<>();
	// Para NPCs. Complementa a clientesConectados ya que los NPCs no tienen
	// cliente.
	private static Map<Integer, NPC> npcsCargados = new HashMap<>();

	// Tiene personajes con cliente y NPCs.
	private static Map<Integer, PaquetePersonaje> personajesConectados = new HashMap<>();
	// Tiene personajes con cliente y NPCs.
	private static Map<Integer, PaqueteMovimiento> ubicacionPersonajes = new HashMap<>();

	private static Thread server;

	private static ServerSocket serverSocket;
	private static Conector conexionDB;
	private final int puerto = 55050;

	private static final int ANCHO = 700;
	private static final int ALTO = 640;
	private static final int ALTO_LOG = 520;
	private static final int ANCHO_LOG = ANCHO - 25;

	private static JTextArea log;

	private static AtencionConexiones atencionConexiones;
	private static AtencionMovimientos atencionMovimientos;

	/**
	 * The main method.
	 *
	 * @param args the arguments
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
		ventana.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/servidor/server.png"));
		JLabel titulo = new JLabel("Log del servidor...");
		titulo.setFont(new Font("Courier New", Font.BOLD, 16));
		titulo.setBounds(10, 0, 200, 30);
		ventana.add(titulo);

		log = new JTextArea();
		log.setEditable(false);
		log.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		JScrollPane scroll = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(10, 40, ANCHO_LOG, ALTO_LOG);
		ventana.add(scroll);

		final JButton botonIniciar = new JButton();
		final JButton botonDetener = new JButton();
		botonIniciar.setText("Iniciar");
		botonIniciar.setBounds(220, ALTO - 70, 100, 30);
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
		botonDetener.setBounds(360, ALTO - 70, 100, 30);
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
				if (conexionDB != null){
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
				if (conexionDB != null){
					conexionDB.close();
				}
				System.exit(0);
			}
		});

		ventana.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {

			conexionDB = new Conector();
			conexionDB.connect();

			log.append("Iniciando el servidor..." + System.lineSeparator());
			serverSocket = new ServerSocket(puerto);
			log.append("Creando NPCs..." + System.lineSeparator());
			ModuloNPC.ejecutar();
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
	 * @param pqm the pqm
	 * @return true, if successful
	 */
	public static boolean mensajeAUsuario(final PaqueteMensaje pqm) {
		boolean result = false;
		for (Map.Entry<Integer, PaquetePersonaje> personaje : personajesConectados.entrySet()) {
			String nom = personaje.getValue().getNombre();
			if (personaje.getValue().getNombre().equals(pqm.getUserReceptor())) {
				Servidor.log.append(
                       pqm.getUserEmisor() + " envió mensaje a " + pqm.getUserReceptor() + System.lineSeparator());
				result = true;
			}
		}
		return result;
	}

	/**
	 * Mensaje A all.
	 *
	 * @param contador the contador
	 * @return true, if successful
	 */
	public static boolean mensajeAAll(final int contador) {
		boolean result = true;
		if (personajesConectados.size() != contador + 1) {
			Servidor.log
                 .append("Uno o más de todos los usuarios se ha desconectado, se ha mandado el mensaje a los demas."
							+ System.lineSeparator());
			result = false;
		} else {
           Servidor.log.append("Se ha enviado un mensaje a todos los usuarios" + System.lineSeparator());
			result = true;
		}
		return result;
	}

	/**
	 * Gets the clientes conectados.
	 *
	 * @return the clientes conectados
	 */
	public static ArrayList<EscuchaCliente> getClientesConectados() {
		return clientesConectados;
	}

	/**
	 * Gets the ubicacion personajes.
	 *
	 * @return the ubicacion personajes
	 */
	public static Map<Integer, PaqueteMovimiento> getUbicacionPersonajes() {
		return ubicacionPersonajes;
	}

	/**
	 * Gets the personajes conectados.
	 *
	 * @return the personajes conectados
	 */
	public static Map<Integer, PaquetePersonaje> getPersonajesConectados() {
		return personajesConectados;
	}

	/**
	 * Gets the conector.
	 *
	 * @return the conector
	 */
	public static Conector getConector() {
		return conexionDB;
	}

	/**
	 * Gets the NP cs cargados.
	 *
	 * @return the NP cs cargados
	 */
	public static Map<Integer, NPC> getNPCsCargados() {
		return npcsCargados;
	}

	/**
	 * Gets the log.
	 *
	 * @return the log
	 */
	public static JTextArea getLog() {
		return log;
	}

	/**
	 * Sets the log.
	 *
	 * @param log the new log
	 */
	public static void setLog(final JTextArea log) {
		Servidor.log = log;
	}

	/**
	 * Gets the atencion conexiones.
	 *
	 * @return the atencion conexiones
	 */
	public static AtencionConexiones getAtencionConexiones() {
		return atencionConexiones;
	}

	/**
	 * Sets the atencion conexiones.
	 *
	 * @param atencionConexiones the new atencion conexiones
	 */
	public static void setAtencionConexiones(final AtencionConexiones atencionConexiones) {
		Servidor.atencionConexiones = atencionConexiones;
	}

	/**
	 * Gets the atencion movimientos.
	 *
	 * @return the atencion movimientos
	 */
	public static AtencionMovimientos getAtencionMovimientos() {
		return atencionMovimientos;
	}

	/**
	 * Sets the atencion movimientos.
	 *
	 * @param atencionMovimientos the new atencion movimientos
	 */
	public static void setAtencionMovimientos(final AtencionMovimientos atencionMovimientos) {
		Servidor.atencionMovimientos = atencionMovimientos;
	}


}
