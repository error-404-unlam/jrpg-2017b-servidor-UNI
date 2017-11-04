package edu.unlam.wome.servidor;

import com.google.gson.Gson;

import edu.unlam.wome.estados.Estado;
import edu.unlam.wome.mensajeria.Comando;
import edu.unlam.wome.mensajeria.PaqueteDeMovimientos;

/**
 * Clase AtencionMovimientos.
 * Extiende de la clase Thread
 */
public class AtencionMovimientos extends Thread {

    private final Gson gson = new Gson();

    /**
     * Constructor.
     */
    public AtencionMovimientos() {

    }

    /**
     * Metodo de la clase Thread
     */
    public void run() {

        synchronized (this) {

            try {

                while (true) {

                    // Espero a que se mueva alguien
                    wait();

                    // Le reenvio el movimiento a todos
                    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {

                        if (conectado.getPaquetePersonaje().getEstado() == Estado.getEstadoJuego()) {

                            PaqueteDeMovimientos pdp = (PaqueteDeMovimientos) new PaqueteDeMovimientos(
                                    Servidor.getUbicacionPersonajes()).clone();
                            pdp.setComando(Comando.MOVIMIENTO);
                            synchronized (conectado) {
                                conectado.getSalida().writeObject(gson.toJson(pdp));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Servidor.getLog().append("Falló al intentar enviar paqueteDeMovimientos \n");
            }
        }
    }
}
