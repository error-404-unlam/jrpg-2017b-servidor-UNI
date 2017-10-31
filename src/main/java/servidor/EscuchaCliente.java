package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.google.gson.Gson;

import comandos.ComandosServer;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteAtacar;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeMovimientos;
import mensajeria.PaqueteDePersonajes;
import mensajeria.PaqueteFinalizarBatalla;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

/**
 * Clase EscuchaCliente.
 * Extiende de la clase Thread
 */
public class EscuchaCliente extends Thread {

    private final Socket socket;
    private final ObjectInputStream entrada;
    private final ObjectOutputStream salida;
    private int idPersonaje;
    private final Gson gson = new Gson();

    private PaquetePersonaje paquetePersonaje;
    private PaqueteMovimiento paqueteMovimiento;
    private PaqueteBatalla paqueteBatalla;
    private PaqueteAtacar paqueteAtacar;
    private PaqueteFinalizarBatalla paqueteFinalizarBatalla;
    private PaqueteUsuario paqueteUsuario;
    private PaqueteDeMovimientos paqueteDeMovimiento;
    private PaqueteDePersonajes paqueteDePersonajes;

    /**
     * Constructor de EscuchaCliente
     *
     * @param ip IP
     * @param socket Socket
     * @param entrada Objeto de la clase ObjectInputStream
     * @param salida Objeto de la clase  ObjectOutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public EscuchaCliente(final String ip, final Socket socket, final ObjectInputStream entrada,
    		final ObjectOutputStream salida)
            throws IOException {
        this.socket = socket;
        this.entrada = entrada;
        this.salida = salida;
        paquetePersonaje = new PaquetePersonaje();
    }

    /**
     * Metodo de la clase Thread
     */
    public void run() {
        try {
            ComandosServer comand;
            Paquete paquete;
            Paquete paqueteSv = new Paquete(null, 0);
            paqueteUsuario = new PaqueteUsuario();

            String cadenaLeida = (String) entrada.readObject();
            while (!((paquete = gson.fromJson(cadenaLeida, Paquete.class)).getComando() == Comando.DESCONECTAR)) {

                comand = (ComandosServer) paquete.getObjeto(Comando.NOMBREPAQUETE);
                comand.setCadena(cadenaLeida);
                comand.setEscuchaCliente(this);
                comand.ejecutar();
                cadenaLeida = (String) entrada.readObject();
            }

            entrada.close();
            salida.close();
            socket.close();

            Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
            Servidor.getUbicacionPersonajes().remove(paquetePersonaje.getId());
            Servidor.getClientesConectados().remove(this);

            for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
                paqueteDePersonajes = new PaqueteDePersonajes(Servidor.getPersonajesConectados());
                paqueteDePersonajes.setComando(Comando.CONEXION);
                conectado.salida.writeObject(gson.toJson(paqueteDePersonajes, PaqueteDePersonajes.class));
            }

            Servidor.getLog().append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());

        } catch (IOException | ClassNotFoundException e) {
            Servidor.getLog().append(
                    "Error de conexión al escuchar clientes: ''" + e.getMessage() + "''." + System.lineSeparator());
        }
    }

    /**
     * Obtiene socket.
     *
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Obtiene entrada.
     *
     * @return entrada Objeto de la clase ObjectInputStream
     */
    public ObjectInputStream getEntrada() {
        return entrada;
    }

    /**
     * Obtiene the salida.
     *
     * @return salida Objeto de la clase ObjectOutputStream
     */
    public ObjectOutputStream getSalida() {
        return salida;
    }

    /**
     * Obtiene paquete personaje.
     *
     * @return paquetepersonaje Objeto de la clase PaquetePersonaje
     */
    public PaquetePersonaje getPaquetePersonaje() {
        return paquetePersonaje;
    }

    /**
     * Obtiene id personaje.
     *
     * @return id personaje
     */
    public int getIdPersonaje() {
        return idPersonaje;
    }

    /**
     * Obtiene paquete movimiento.
     *
     * @return paquete movimiento Objeto de la clase PaqueteMovimiento
     */
    public PaqueteMovimiento getPaqueteMovimiento() {
        return paqueteMovimiento;
    }

    /**
     * Setea paquete movimiento.
     *
     * @param paqueteMovimiento Objeto de la clase PaqueteMovimiento
     */
    public void setPaqueteMovimiento(final PaqueteMovimiento paqueteMovimiento) {
        this.paqueteMovimiento = paqueteMovimiento;
    }

    /**
     * Obtiene paquete batalla.
     *
     * @return paquete batalla Objeto de la clase PaqueteBatalla
     */
    public PaqueteBatalla getPaqueteBatalla() {
        return paqueteBatalla;
    }

    /**
     * Setea paquete batalla.
     *
     * @param paqueteBatalla Objeto de la clase PaqueteBatalla
     */
    public void setPaqueteBatalla(final PaqueteBatalla paqueteBatalla) {
        this.paqueteBatalla = paqueteBatalla;
    }

    /**
     * Obtiene paquete atacar.
     *
     * @return paquete atacar Objeto de la clase Paqueteatacar
     */
    public PaqueteAtacar getPaqueteAtacar() {
        return paqueteAtacar;
    }

    /**
     * Setea paquete atacar.
     *
     * @param paqueteAtacar Objeto de la clase  PaqueteAtacar
     */
    public void setPaqueteAtacar(final PaqueteAtacar paqueteAtacar) {
        this.paqueteAtacar = paqueteAtacar;
    }

    /**
     * Obtiene paquete finalizar batalla.
     *
     * @return paquete finalizar batalla Objeto de la clase PaqueteFinalizarBatalla
     */
    public PaqueteFinalizarBatalla getPaqueteFinalizarBatalla() {
        return paqueteFinalizarBatalla;
    }

    /**
     * Setea paquete finalizar batalla.
     *
     * @param paqueteFinalizarBatalla Objeto de la clase PaqueteFinalizarBatalla
     */
    public void setPaqueteFinalizarBatalla(final PaqueteFinalizarBatalla paqueteFinalizarBatalla) {
        this.paqueteFinalizarBatalla = paqueteFinalizarBatalla;
    }

    /**
     * Obtiene paquete de movimiento.
     *
     * @return paquete de movimiento Objeto de la clase PaqueteDeMovimientos
     */
    public PaqueteDeMovimientos getPaqueteDeMovimiento() {
        return paqueteDeMovimiento;
    }

    /**
     * Setea paquete de movimiento.
     *
     * @param paqueteDeMovimiento Objeto de la clase PaqueteMovimientos
     */
    public void setPaqueteDeMovimiento(final PaqueteDeMovimientos paqueteDeMovimiento) {
        this.paqueteDeMovimiento = paqueteDeMovimiento;
    }

    /**
     * Obtiene paquete de personajes.
     *
     * @return paquete de personajes Objeto de la clase PaqueteDePersonajes
     */
    public PaqueteDePersonajes getPaqueteDePersonajes() {
        return paqueteDePersonajes;
    }

    /**
     * Setea paquete de personajes.
     *
     * @param paqueteDePersonajes Objeto de la clase PaqueteDePersonajes
     */
    public void setPaqueteDePersonajes(final PaqueteDePersonajes paqueteDePersonajes) {
        this.paqueteDePersonajes = paqueteDePersonajes;
    }

    /**
     * Setea id personaje.
     *
     * @param idPersonaje id del personaje
     */
    public void setIdPersonaje(final int idPersonaje) {
        this.idPersonaje = idPersonaje;
    }

    /**
     * Setea paquete personaje.
     *
     * @param paquetePersonaje Objeto de la clase PaquetePersonaje
     */
    public void setPaquetePersonaje(final PaquetePersonaje paquetePersonaje) {
        this.paquetePersonaje = paquetePersonaje;
    }

    /**
     * Obtiene paquete usuario.
     *
     * @return paquete usuario Objeto de la clase PaqueteUsuario
     */
    public PaqueteUsuario getPaqueteUsuario() {
        return paqueteUsuario;
    }

    /**
     * Setea paquete usuario.
     *
     * @param paqueteUsuario Objeto de la clase PaqueteUsuario
     */
    public void setPaqueteUsuario(final PaqueteUsuario paqueteUsuario) {
        this.paqueteUsuario = paqueteUsuario;
    }
}
