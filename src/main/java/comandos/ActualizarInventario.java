package comandos;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

/**
 * The Class ActualizarInventario.
 */
public class ActualizarInventario extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override
    public void ejecutar() {
        escuchaCliente.setPaquetePersonaje((PaquetePersonaje) gson.fromJson(cadenaLeida, PaquetePersonaje.class));

        Servidor.getConector().actualizarInventario(escuchaCliente.getPaquetePersonaje());
        Servidor.getPersonajesConectados().remove(escuchaCliente.getPaquetePersonaje().getId());
        Servidor.getPersonajesConectados().put(escuchaCliente.getPaquetePersonaje().getId(),
                escuchaCliente.getPaquetePersonaje());

        for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
            try {
                conectado.getSalida().writeObject(gson.toJson(escuchaCliente.getPaquetePersonaje()));
            } catch (IOException e) {
                Servidor.getLog().append("Falló al intentar enviar paquetePersonaje a:"
                        + conectado.getPaquetePersonaje().getId() + "\n");
            }
        }

    }

}
