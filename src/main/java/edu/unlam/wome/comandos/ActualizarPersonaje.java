package edu.unlam.wome.comandos;

import java.io.IOException;

import edu.unlam.wome.servidor.EscuchaCliente;
import edu.unlam.wome.servidor.Servidor;
import edu.unlam.wome.mensajeria.PaquetePersonaje;

/**
 * Clase ActualizarPersonaje.
 * Extiende de la clase ComandosServer
 */
public class ActualizarPersonaje extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override
    public void ejecutar() {
        escuchaCliente.setPaquetePersonaje((PaquetePersonaje) getGson().
        		fromJson(getCadenaLeida(), PaquetePersonaje.class));

        if (escuchaCliente.getPaquetePersonaje().getId() < 0) {
            Servidor.getLog().append("El NPC " + escuchaCliente.getPaquetePersonaje().getId()
                    + " ha evitado la actualización con éxito." + System.lineSeparator());
            return;
        }

        Servidor.getConector().actualizarPersonaje(escuchaCliente.getPaquetePersonaje());
       
        
        Servidor.getPersonajesConectados().remove(escuchaCliente.getPaquetePersonaje().getId());
        Servidor.getPersonajesConectados().put(escuchaCliente.getPaquetePersonaje().getId(),
                escuchaCliente.getPaquetePersonaje());

        for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
            try {
                conectado.getSalida().writeObject(getGson().toJson(escuchaCliente.getPaquetePersonaje()));
            } catch (IOException e) {
                Servidor.getLog().append("Falló al intentar enviar paquetePersonaje a:"
                        + conectado.getPaquetePersonaje().getId() + "\n");
            }
        }

    }

}
