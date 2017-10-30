package comandos;

import mensajeria.Comando;
import servidor.EscuchaCliente;

/**
 * The Class ComandosServer.
 */
public abstract class ComandosServer extends Comando {
    protected EscuchaCliente escuchaCliente;

    /**
     * Sets the escucha cliente.
     *
     * @param escuchaCliente the new escucha cliente
     */
    public void setEscuchaCliente(final EscuchaCliente escuchaCliente) {
        this.escuchaCliente = escuchaCliente;
    }

}
