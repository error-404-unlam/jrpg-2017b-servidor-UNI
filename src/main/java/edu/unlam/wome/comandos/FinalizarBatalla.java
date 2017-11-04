package edu.unlam.wome.comandos;

import java.io.IOException;

import edu.unlam.wome.servidor.EscuchaCliente;
import edu.unlam.wome.servidor.Servidor;
import edu.unlam.wome.estados.Estado;
import edu.unlam.wome.mensajeria.PaqueteFinalizarBatalla;

/**
 * Clase FinalizarBatalla.
 * Extiende de la clase ComnadosServer
 */
public class FinalizarBatalla extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override // Recibe paquetes de un cliente, y los envía a otro cliente o al
                // NPC. El NPC no envía paquetes con este método porque no tiene
                // cliente.
    public void ejecutar() {
        PaqueteFinalizarBatalla paqueteFinalizarBatalla = (PaqueteFinalizarBatalla) getGson().fromJson(getCadenaLeida(),
                PaqueteFinalizarBatalla.class);
        escuchaCliente.setPaqueteFinalizarBatalla(paqueteFinalizarBatalla);
        Servidor.getConector().actualizarInventario(paqueteFinalizarBatalla.getGanadorBatalla());
        Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteFinalizarBatalla().getId())
                .setEstado(Estado.getEstadoJuego());
        if (escuchaCliente.getPaqueteFinalizarBatalla().getIdEnemigo() > 0) {
            Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteFinalizarBatalla().getIdEnemigo())
                    .setEstado(Estado.getEstadoJuego());
            for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
                if (conectado.getIdPersonaje() == escuchaCliente.getPaqueteFinalizarBatalla().getIdEnemigo()) {
                    try {
                        conectado.getSalida().writeObject(getGson().
                        		toJson(escuchaCliente.getPaqueteFinalizarBatalla()));
                    } catch (IOException e) {
                        Servidor.getLog().append("Falló al intentar enviar finalizarBatalla a:"
                                + conectado.getPaquetePersonaje().getId() + "\n");
                    }
                }
            }
        } else { // El enemigo es un NPC.
            // Si se llega a ejecutar esto, quiere decir que el NPC perdió la
            // batalla.
            Servidor.getNPCsCargados().get(escuchaCliente.getPaqueteFinalizarBatalla().getIdEnemigo())
                    .setPfb(escuchaCliente.getPaqueteFinalizarBatalla());
        }

        synchronized (Servidor.getAtencionConexiones()) {
            Servidor.getAtencionConexiones().notify();
        }

    }

    /**
     * Ejecutar desde NPC.
     *
     * @param pfb Objeto de la clase PaqueteFinalizarBatalla
     */
    // Recibe paquetes de un NPC y los envía a un cliente.
    public void ejecutarDesdeNPC(final PaqueteFinalizarBatalla pfb) {
        // Si se llega a ejecutar esto, quiere decir que el NPC ganó la batalla.
        try {
            Servidor.getPersonajesConectados().get(pfb.getId()).setEstado(Estado.getEstadoJuego());
            Servidor.getPersonajesConectados().get(pfb.getIdEnemigo()).setEstado(Estado.getEstadoJuego());
            for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
                if (conectado.getIdPersonaje() == pfb.getIdEnemigo()) {
                    conectado.getSalida().writeObject(getGson().toJson(pfb));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (Servidor.getAtencionConexiones()) {
            Servidor.getAtencionConexiones().notify();
        }

    }

}
