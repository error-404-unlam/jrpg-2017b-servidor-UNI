package servidor;

import com.google.gson.Gson;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.PaqueteDeMovimientos;

/**
 * The Class AtencionMovimientos.
 */
public class AtencionMovimientos extends Thread {

    private final Gson gson = new Gson();

    /**
     * Instantiates a new atencion movimientos.
     */
    public AtencionMovimientos() {

    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {

        synchronized (this) {

            try {

                while (true) {

                    // Espero a que se mueva alguien
                    wait();

                    // Le reenvio el movimiento a todos
                    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {

                        if (conectado.getPaquetePersonaje().getEstado() == Estado.estadoJuego) {

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
