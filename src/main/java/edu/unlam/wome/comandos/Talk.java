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
        ingresoTruco(paqueteMensaje);
    }
    
    public boolean actualizarPotenciasdosATodos(PaqueteMensaje paqueteMensaje, PaqueteModoJuego paqueteModoJuego) {
        for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
              try {
                   conectado.getSalida().writeObject(getGson().toJson(paqueteModoJuego));
               } catch (IOException e) {
                    Servidor.getLog().append(
                            "Falló al intentar enviar mensaje a:" + conectado.getPaquetePersonaje().getId() + "\n");
               }
        }
        return false;
    }
    
    public int buscarIdPersobaje(PaqueteMensaje paqueteMensaje, int modoJuego) {
    	int idUser = 0;
    	for (Map.Entry<Integer, PaquetePersonaje> personaje : Servidor.getPersonajesConectados().entrySet()) {
            if (personaje.getValue().getNombre().equals(paqueteMensaje.getUserEmisor())) {
                idUser = personaje.getValue().getId();
                Servidor.getPersonajesConectados().get(idUser).setModoJuego(modoJuego);
                return idUser;
            }
        }
    	return idUser;
    }
    
    public boolean ingresoTruco(PaqueteMensaje paquete) {
    	
    	switch(paquete.getMensaje()) {
    	case "Dios": 
    		PaqueteModoJuego modoJuego = new PaqueteModoJuego(PaqueteModoJuego.MODO_DIOS);
    		int idPersonaje = buscarIdPersobaje(paquete, PaqueteModoJuego.MODO_DIOS);
    		modoJuego.setIdPersonaje(idPersonaje);
    		modoJuego.setComando(Comando.ACTUALIZAR_MODO_JUEGO);
    		Servidor.potenciados.add(new PersonajesPotenciados(idPersonaje, PaqueteModoJuego.MODO_DIOS));
    		return actualizarPotenciasdosATodos(paquete, modoJuego);	
    	}
    	return false;
    }
}
