package comandos;

import mensajeria.PaquetePersonaje;
import servidor.Servidor;

/**
 * The Class MostrarMapas.
 */
public class MostrarMapas extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override
    public void ejecutar() {
        escuchaCliente.setPaquetePersonaje((PaquetePersonaje) gson.fromJson(cadenaLeida, PaquetePersonaje.class));
        Servidor.getLog().append(escuchaCliente.getSocket().getInetAddress().getHostAddress() + " ha elegido el mapa "
                + escuchaCliente.getPaquetePersonaje().getMapa() + System.lineSeparator());

    }

}
