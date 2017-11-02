package comandos;

import java.io.IOException;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

/**
 * Clase InicioSesion.
 * Extiende de la clase ComandosServer
 */
public class InicioSesion extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override
    public void ejecutar() {
        Paquete paqueteSv = new Paquete(null, 0);
        paqueteSv.setComando(Comando.INICIOSESION);

        // Recibo el paquete usuario
        escuchaCliente.setPaqueteUsuario((PaqueteUsuario) (getGson().fromJson(getCadenaLeida(), PaqueteUsuario.class)));

        // Si se puede loguear el usuario le envio un mensaje de exito y el
        // paquete personaje con los datos
        try {
            if (Servidor.getConector().loguearUsuario(escuchaCliente.getPaqueteUsuario())) {

                PaquetePersonaje paquetePersonaje = new PaquetePersonaje();
                paquetePersonaje = Servidor.getConector().getPersonaje(escuchaCliente.getPaqueteUsuario());
                paquetePersonaje.setComando(Comando.INICIOSESION);
                paquetePersonaje.setMensaje(Paquete.getMsjExito());
                escuchaCliente.setIdPersonaje(paquetePersonaje.getId());

                escuchaCliente.getSalida().writeObject(getGson().toJson(paquetePersonaje));

            } else {
                paqueteSv.setMensaje(Paquete.getMsjFracaso());
                escuchaCliente.getSalida().writeObject(getGson().toJson(paqueteSv));
            }
        } catch (IOException e) {
            Servidor.getLog().append("Falló al intentar iniciar sesión \n");
        }

    }

}
