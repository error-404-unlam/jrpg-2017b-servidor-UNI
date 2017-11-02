package comandos;

import java.io.IOException;

import mensajeria.PaqueteAtacar;
import servidor.EscuchaCliente;
import servidor.Servidor;

/**
 * Clase Atacar.
 * Extiende de la clase ComandosServer
 */
public class Atacar extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override // Recibe paquetes de un cliente, y los envía a otro cliente o al
                // NPC. Un NPC no envía paquetes con este método porque no tiene
                // cliente.
    public void ejecutar() {
        escuchaCliente.setPaqueteAtacar((PaqueteAtacar) getGson().fromJson(getCadenaLeida(), PaqueteAtacar.class));
        if (escuchaCliente.getPaqueteAtacar().getIdEnemigo() > 0) {
            for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
                if (conectado.getIdPersonaje() == escuchaCliente.getPaqueteAtacar().getIdEnemigo()) {
                    try {
                        conectado.getSalida().writeObject(getGson().toJson(escuchaCliente.getPaqueteAtacar()));
                    } catch (IOException e) {
                        Servidor.getLog().append(
                              "Falló al intentar enviar ataque a:" + conectado.getPaquetePersonaje().getId() + "\n");
                    }
                }
            }
        } else { // El enemigo es un NPC.
            Servidor.getNPCsCargados().get(escuchaCliente.getPaqueteAtacar().getIdEnemigo())
                    .setPa(escuchaCliente.getPaqueteAtacar());
        }

    }

    /**
     * Ejecutar desde NPC.
     *
     * @param pa objeto de la clase ParqueteAtacar
     */
    // Recibe paquetes de un NPC y los envía a un cliente.
    public void ejecutarDesdeNPC(final PaqueteAtacar pa) {
        for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
            if (conectado.getIdPersonaje() == pa.getIdEnemigo()) {
                try {
                    conectado.getSalida().writeObject(getGson().toJson(pa));
                } catch (IOException e) {
                    Servidor.getLog().append(
                            "Falló al intentar enviar ataque a:" + conectado.getPaquetePersonaje().getId() + "\n");
                }
            }
        }
    }
}
