package edu.unlam.wome.comandos;

import java.io.IOException;
import java.util.Map;

import edu.unlam.wome.servidor.EscuchaCliente;
import edu.unlam.wome.servidor.Servidor;
import edu.unlam.wome.mensajeria.Comando;
import edu.unlam.wome.mensajeria.PaqueteMensaje;
import edu.unlam.wome.mensajeria.PaquetePersonaje;
import edu.unlam.wome.modos.ModoDefecto;
import edu.unlam.wome.modos.ModoJuego;


/**
 * Clase Talk.
 * Extiende de la clase ComandosServer
 */
public class Talk extends ComandosServer {

	
    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
	
	private ModoJuego modoJuego = new ModoDefecto();
	
	
    @Override
    public void ejecutar() {
        int idUser = 0;
        PaqueteMensaje paqueteMensaje = (PaqueteMensaje) (getGson().fromJson(getCadenaLeida(), PaqueteMensaje.class));
       
        if(ingresoTruco(paqueteMensaje))
        	return;
        if (!(paqueteMensaje.getUserReceptor() == null)) {
        	if (Servidor.mensajeAUsuario(paqueteMensaje)) {
                paqueteMensaje.setComando(Comando.TALK);
                for (Map.Entry<Integer, PaquetePersonaje> personaje : Servidor.getPersonajesConectados().entrySet()) {
                    if (personaje.getValue().getNombre().equals(paqueteMensaje.getUserReceptor())) {
                        idUser = personaje.getValue().getId();
                    }
                }
                for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
                    if (conectado.getIdPersonaje() == idUser) {
                        try {
                            conectado.getSalida().writeObject(getGson().toJson(paqueteMensaje));
                        } catch (IOException e) {
                            Servidor.getLog().append("Falló al intentar enviar mensaje a:"
                                    + conectado.getPaquetePersonaje().getId() + "\n");
                        }
                    }
                }
            } else {
                Servidor.getLog().append("No se envió el mensaje \n");
            }
        } else {
            for (Map.Entry<Integer, PaquetePersonaje> personaje : Servidor.getPersonajesConectados().entrySet()) {
                if (personaje.getValue().getNombre().equals(paqueteMensaje.getUserEmisor())) {
                    idUser = personaje.getValue().getId();
                }
            }
            for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
                if (conectado.getIdPersonaje() != idUser) {
                    try {
                        conectado.getSalida().writeObject(getGson().toJson(paqueteMensaje));
                    } catch (IOException e) {
                        Servidor.getLog().append(
                                "Falló al intentar enviar mensaje a:" + conectado.getPaquetePersonaje().getId() + "\n");
                    }
                }
            }
            
            Servidor.mensajeAAll();
        }
    }

    /**
     * Configura y actualiza el listado de personajes truqueados
     * @param paquete
     * @return true / false
     */
    public boolean ingresoTruco(PaqueteMensaje paqueteMensaje) {
    	modoJuego = modoJuego.seleccion(paqueteMensaje);
    	return modoJuego.actualizar();
    }
}
