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

    public EscuchaCliente(final String ip, final Socket socket, final ObjectInputStream entrada,
    		final ObjectOutputStream salida)
            throws IOException {
        this.socket = socket;
        this.entrada = entrada;
        this.salida = salida;
        paquetePersonaje = new PaquetePersonaje();
    }

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

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getEntrada() {
        return entrada;
    }

    public ObjectOutputStream getSalida() {
        return salida;
    }

    public PaquetePersonaje getPaquetePersonaje() {
        return paquetePersonaje;
    }

    public int getIdPersonaje() {
        return idPersonaje;
    }

    public PaqueteMovimiento getPaqueteMovimiento() {
        return paqueteMovimiento;
    }

    public void setPaqueteMovimiento(final PaqueteMovimiento paqueteMovimiento) {
        this.paqueteMovimiento = paqueteMovimiento;
    }

    public PaqueteBatalla getPaqueteBatalla() {
        return paqueteBatalla;
    }

    public void setPaqueteBatalla(final PaqueteBatalla paqueteBatalla) {
        this.paqueteBatalla = paqueteBatalla;
    }

    public PaqueteAtacar getPaqueteAtacar() {
        return paqueteAtacar;
    }

    public void setPaqueteAtacar(final PaqueteAtacar paqueteAtacar) {
        this.paqueteAtacar = paqueteAtacar;
    }

    public PaqueteFinalizarBatalla getPaqueteFinalizarBatalla() {
        return paqueteFinalizarBatalla;
    }

    public void setPaqueteFinalizarBatalla(final PaqueteFinalizarBatalla paqueteFinalizarBatalla) {
        this.paqueteFinalizarBatalla = paqueteFinalizarBatalla;
    }

    public PaqueteDeMovimientos getPaqueteDeMovimiento() {
        return paqueteDeMovimiento;
    }

    public void setPaqueteDeMovimiento(final PaqueteDeMovimientos paqueteDeMovimiento) {
        this.paqueteDeMovimiento = paqueteDeMovimiento;
    }

    public PaqueteDePersonajes getPaqueteDePersonajes() {
        return paqueteDePersonajes;
    }

    public void setPaqueteDePersonajes(final PaqueteDePersonajes paqueteDePersonajes) {
        this.paqueteDePersonajes = paqueteDePersonajes;
    }

    public void setIdPersonaje(final int idPersonaje) {
        this.idPersonaje = idPersonaje;
    }

    public void setPaquetePersonaje(final PaquetePersonaje paquetePersonaje) {
        this.paquetePersonaje = paquetePersonaje;
    }

    public PaqueteUsuario getPaqueteUsuario() {
        return paqueteUsuario;
    }

    public void setPaqueteUsuario(final PaqueteUsuario paqueteUsuario) {
        this.paqueteUsuario = paqueteUsuario;
    }
}
