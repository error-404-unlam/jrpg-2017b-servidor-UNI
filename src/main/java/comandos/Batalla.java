package comandos;

import java.io.IOException;

import estados.Estado;
import mensajeria.PaqueteBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

/**
 * Clase Batalla.
 * Extiende de la Clase ComandosServer
 */
public class Batalla extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override // Recibe paquetes de un cliente y los envía a otro cliente o a un
                // NPC. Un NPC no inicia batallas con este método porque no
                // tiene cliente.
    public void ejecutar() {
        // Se crea el PaqueteBatalla a partir del EscuchaCliente del retador.
        escuchaCliente.setPaqueteBatalla((PaqueteBatalla) getGson().fromJson(getCadenaLeida(), PaqueteBatalla.class));
        Servidor.getLog().append(escuchaCliente.getPaqueteBatalla().getId() + " quiere batallar con "
                + escuchaCliente.getPaqueteBatalla().getIdEnemigo() + System.lineSeparator());

        try {
            // Se le asigna estadoBatalla en la lista de personajes del servidor
            // a ambos.
            Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteBatalla().getId())
                    .setEstado(Estado.getEstadoBatalla());
            Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteBatalla().getIdEnemigo())
                    .setEstado(Estado.getEstadoBatalla());
            escuchaCliente.getPaqueteBatalla().setMiTurno(true);
            // Se le envía el PaqueteBatalla al cliente del retador.
            escuchaCliente.getSalida().writeObject(getGson().toJson(escuchaCliente.getPaqueteBatalla()));

            // Si tiene ID positivo, el enemigo es un usuario.
            if (escuchaCliente.getPaqueteBatalla().getIdEnemigo() > 0) {
                // Busca el cliente del que fue retado en la lista de clientes.
                for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
                    if (conectado.getIdPersonaje() == escuchaCliente.getPaqueteBatalla().getIdEnemigo()) {
                        int aux = escuchaCliente.getPaqueteBatalla().getId();
                        escuchaCliente.getPaqueteBatalla().setId(escuchaCliente.getPaqueteBatalla().getIdEnemigo());
                        escuchaCliente.getPaqueteBatalla().setIdEnemigo(aux);
                        escuchaCliente.getPaqueteBatalla().setMiTurno(false);
                        // Se le envía el PaqueteBatalla al cliente del que fue
                        // retado.
                        conectado.getSalida().writeObject(getGson().toJson(escuchaCliente.getPaqueteBatalla()));
                        break; // Sale del ciclo for each
                    }
                }
            } else { // Si tiene ID negativo, el enemigo es un NPC.
                int aux = escuchaCliente.getPaqueteBatalla().getId();
                escuchaCliente.getPaqueteBatalla().setId(escuchaCliente.getPaqueteBatalla().getIdEnemigo());
                escuchaCliente.getPaqueteBatalla().setIdEnemigo(aux);
                escuchaCliente.getPaqueteBatalla().setMiTurno(false);
                // Se le envía el PaqueteBatalla al NPC.
                Servidor.getNPCsCargados().get(escuchaCliente.getPaqueteBatalla().getId())
                        .setPb(escuchaCliente.getPaqueteBatalla());
            }
        } catch (IOException e) {
            Servidor.getLog().append("Falló al intentar enviar Batalla \n");
        }

        synchronized (Servidor.getAtencionConexiones()) {
            Servidor.getAtencionConexiones().notify();
        }

    }

}
