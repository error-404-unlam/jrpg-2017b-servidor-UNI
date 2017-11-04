package edu.unlam.wome.comandos;

import edu.unlam.wome.servidor.Servidor;
import edu.unlam.wome.mensajeria.PaqueteMovimiento;

/**
 * Clase Movimiento.
 * Extiende de la clase ComandosServer
 */
public class Movimiento extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override
    public void ejecutar() {
        escuchaCliente.setPaqueteMovimiento(
                (PaqueteMovimiento) (getGson().fromJson((String) getCadenaLeida(), PaqueteMovimiento.class)));

        Servidor.getUbicacionPersonajes().get(escuchaCliente.getPaqueteMovimiento().getIdPersonaje())
                .setPosX(escuchaCliente.getPaqueteMovimiento().getPosX());
        Servidor.getUbicacionPersonajes().get(escuchaCliente.getPaqueteMovimiento().getIdPersonaje())
                .setPosY(escuchaCliente.getPaqueteMovimiento().getPosY());
        Servidor.getUbicacionPersonajes().get(escuchaCliente.getPaqueteMovimiento().getIdPersonaje())
                .setDireccion(escuchaCliente.getPaqueteMovimiento().getDireccion());
        Servidor.getUbicacionPersonajes().get(escuchaCliente.getPaqueteMovimiento().getIdPersonaje())
                .setFrame(escuchaCliente.getPaqueteMovimiento().getFrame());

        synchronized (Servidor.getAtencionMovimientos()) {
            Servidor.getAtencionMovimientos().notify();
        }

    }

}
