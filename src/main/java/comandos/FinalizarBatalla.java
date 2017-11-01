package comandos;

import java.io.IOException;

import estados.Estado;
import mensajeria.PaqueteFinalizarBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

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
        PaqueteFinalizarBatalla paqueteFinalizarBatalla = (PaqueteFinalizarBatalla) gson.fromJson(cadenaLeida,
                PaqueteFinalizarBatalla.class);
        escuchaCliente.setPaqueteFinalizarBatalla(paqueteFinalizarBatalla);
        Servidor.getConector().actualizarInventario(paqueteFinalizarBatalla.getGanadorBatalla());
        Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteFinalizarBatalla().getId())
                .setEstado(Estado.estadoJuego);
        if (escuchaCliente.getPaqueteFinalizarBatalla().getIdEnemigo() > 0) {
            Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteFinalizarBatalla().getIdEnemigo())
                    .setEstado(Estado.estadoJuego);
            for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
                if (conectado.getIdPersonaje() == escuchaCliente.getPaqueteFinalizarBatalla().getIdEnemigo()) {
                    try {
                        conectado.getSalida().writeObject(gson.toJson(escuchaCliente.getPaqueteFinalizarBatalla()));
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
            Servidor.getPersonajesConectados().get(pfb.getId()).setEstado(Estado.estadoJuego);
            Servidor.getPersonajesConectados().get(pfb.getIdEnemigo()).setEstado(Estado.estadoJuego);
            for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
                if (conectado.getIdPersonaje() == pfb.getIdEnemigo()) {
                    conectado.getSalida().writeObject(gson.toJson(pfb));
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
