package comandos;

import mensajeria.Comando;
import servidor.EscuchaCliente;

/**
 * Clase ComandosServer.
 * Extiende de la clase Comando
 */
public abstract class ComandosServer extends Comando {
    protected EscuchaCliente escuchaCliente;

    /**
     * Setea escucha cliente.
     *
     * @param escuchaCliente Objeto de la clase EscuchaCliente
     */
    public void setEscuchaCliente(final EscuchaCliente escuchaCliente) {
        this.escuchaCliente = escuchaCliente;
    }

}
