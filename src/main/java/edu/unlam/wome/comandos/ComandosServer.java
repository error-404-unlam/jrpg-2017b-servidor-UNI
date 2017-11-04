package edu.unlam.wome.comandos;

import edu.unlam.wome.servidor.EscuchaCliente;
import edu.unlam.wome.mensajeria.Comando;

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
