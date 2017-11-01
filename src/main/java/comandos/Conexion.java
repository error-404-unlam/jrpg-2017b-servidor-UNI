package comandos;

import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import servidor.Servidor;

/**
 * Clase Conexion.
 * Extiende de la clase ComandosServer
 */
public class Conexion extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override
    public void ejecutar() {
        escuchaCliente
                .setPaquetePersonaje((PaquetePersonaje) (gson.fromJson(cadenaLeida, PaquetePersonaje.class)).clone());

        Servidor.getPersonajesConectados().put(escuchaCliente.getPaquetePersonaje().getId(),
                (PaquetePersonaje) escuchaCliente.getPaquetePersonaje().clone());
        Servidor.getUbicacionPersonajes().put(escuchaCliente.getPaquetePersonaje().getId(),
                (PaqueteMovimiento) new PaqueteMovimiento(escuchaCliente.getPaquetePersonaje().getId()).clone());

        synchronized (Servidor.getAtencionConexiones()) {
            Servidor.getAtencionConexiones().notify();
        }

    }

}
