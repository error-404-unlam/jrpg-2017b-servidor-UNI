package edu.unlam.wome.comandos;

import java.io.IOException;
import java.util.Map;

import edu.unlam.wome.servidor.EscuchaCliente;
import edu.unlam.wome.servidor.Servidor;
import edu.unlam.wome.mensajeria.Comando;
import edu.unlam.wome.mensajeria.Paquete;
import edu.unlam.wome.mensajeria.PaqueteMensaje;
import edu.unlam.wome.mensajeria.PaqueteModoJuego;
import edu.unlam.wome.mensajeria.PaquetePersonaje;
import edu.unlam.wome.potenciados.PersonajesPotenciados;

/**
 * Clase Talk.
 * Extiende de la clase ComandosServer
 */
public class Talk extends ComandosServer {

    /* (non-Javadoc)
     * @see mensajeria.Comando#ejecutar()
     */
    @Override
    public void ejecutar() {
        int idUser = 0;
        PaqueteMensaje paqueteMensaje = (PaqueteMensaje) (getGson().fromJson(getCadenaLeida(), PaqueteMensaje.class));
       
        if(ingresoTruco(paqueteMensaje))
        	return;
        if (!(paqueteMensaje.getUserReceptor() == null)) {
        	if (Servidor.mensajeAUsuario(paqueteMensaje)) {
                paqueteMensaje.setComando(Comando.TALK);
                idUser = buscarIdPersobaje(paqueteMensaje, paqueteMensaje.getUserReceptor());
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
     * Envia el mensaje al 
     * @param paqueteMensaje
     */
    public void enviarMensaje(PaqueteMensaje paqueteMensaje) {
    	int idUser= 0;
    	paqueteMensaje.setComando(Comando.TALK);
    	paqueteMensaje.setMensaje("El Truco fue aprobado");
        idUser = buscarIdPersobaje(paqueteMensaje, paqueteMensaje.getUserEmisor());
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
    }
    
    /**
     * Envia a todos los conectados el paquete que contiene al jugador que realizo el truco
     * @param paqueteMensaje
     * @param paqueteModoJuego
     * @return true /  false
     */
    public boolean actualizarPotenciasdosATodos(PaqueteMensaje paqueteMensaje, PaqueteModoJuego paqueteModoJuego) {
        for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
              try {
                   conectado.getSalida().writeObject(getGson().toJson(paqueteModoJuego));
               } catch (IOException e) {
                    Servidor.getLog().append(
                            "Falló al intentar enviar mensaje a:" + conectado.getPaquetePersonaje().getId() + "\n");
               }
        }
        return true;
    }
    
    /**
     * Devuelve el id del personaje que realizo el truco
     * @param paqueteMensaje
     * @param modoJuego
     * @return idUsuario que realizo el truco
     */
    public int buscarIdPersobaje(PaqueteMensaje paqueteMensaje, String usuarioBuscar) {
    	int idUser = 0;
    	for (Map.Entry<Integer, PaquetePersonaje> personaje : Servidor.getPersonajesConectados().entrySet()) {
            if (personaje.getValue().getNombre().equals(usuarioBuscar)) {
                idUser = personaje.getValue().getId();
                return idUser;
            }
        }
    	return idUser;
    }
    
    /**
     * Configura y actualiza el listado de personajes truqueados
     * @param paquete
     * @return true / false
     */
    public boolean ingresoTruco(PaqueteMensaje paquete) {
    	switch(paquete.getMensaje()) {
    	case "Dios": 
    		PaqueteModoJuego modoJuego = new PaqueteModoJuego(PaqueteModoJuego.MODO_DIOS);
    		int idPersonaje = buscarIdPersobaje(paquete, paquete.getUserEmisor());
    		modoJuego.setIdPersonaje(idPersonaje);
    		Servidor.getPersonajesConectados().get(idPersonaje).setModoJuego(modoJuego.getModo());
    		modoJuego.setComando(Comando.ACTUALIZAR_MODO_JUEGO);
    		Servidor.potenciados.add(new PersonajesPotenciados(idPersonaje, PaqueteModoJuego.MODO_DIOS));
    		enviarMensaje(paquete);
    		return actualizarPotenciasdosATodos(paquete, modoJuego);	
    	}
    	return false;
    }
}
