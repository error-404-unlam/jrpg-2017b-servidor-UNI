package comandos;

import mensajeria.PaquetePersonaje;
import servidor.Servidor;

/**
 * Clase MostrarMapas.
 * Extiende de la clase ComandosServer
 */
public class MostrarMapas extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override
    public void ejecutar() {
        escuchaCliente.setPaquetePersonaje((PaquetePersonaje) getGson().
        		fromJson(getCadenaLeida(), PaquetePersonaje.class));
        Servidor.getLog().append(escuchaCliente.getSocket().getInetAddress().getHostAddress() + " ha elegido el mapa "
                + escuchaCliente.getPaquetePersonaje().getMapa() + System.lineSeparator());

    }

}
