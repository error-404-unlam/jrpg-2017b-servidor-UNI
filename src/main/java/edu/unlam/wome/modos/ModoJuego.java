package edu.unlam.wome.modos;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;

import edu.unlam.wome.mensajeria.Comando;
import edu.unlam.wome.mensajeria.PaqueteMensaje;
import edu.unlam.wome.mensajeria.PaqueteModoJuego;
import edu.unlam.wome.mensajeria.PaquetePersonaje;
import edu.unlam.wome.servidor.EscuchaCliente;
import edu.unlam.wome.servidor.Servidor;


/**
 * Clase abstrata que provee las funcionalidades necesarias para cargar trucos
 * @see edu.unlam.wome.modos.ModoDios
 *
 */
public abstract class ModoJuego {
	
	private static final String MODO_DIOS = "iddqd";
	private static final String REMOVER = "sacar";
	private Gson gson = new Gson();
	
	
	public ModoJuego seleccion(PaqueteMensaje paqueteMensaje) {
		switch(paqueteMensaje.getMensaje()) {
			case MODO_DIOS: return new ModoDios(paqueteMensaje);
			case REMOVER: return new ModoNormal(paqueteMensaje);
		}
		return new ModoDefecto();
	}
	
	public abstract boolean actualizar();
	public abstract String dameMensaje();
	
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
     * Envia a todos los conectados el paquete que contiene al jugador que realizo el truco
     * @param paqueteMensaje
     * @param paqueteModoJuego
     * @return true /  false
     */
	public boolean actualizarPotenciasdosATodos(PaqueteMensaje paqueteMensaje, PaqueteModoJuego paqueteModoJuego) {
		paqueteModoJuego.setComando(Comando.ACTUALIZAR_MODO_JUEGO);
		for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
              try {
                   conectado.getSalida().writeObject(gson.toJson(paqueteModoJuego));
               } catch (IOException e) {
                    Servidor.getLog().append(
                            "Falló al intentar enviar mensaje a:" + conectado.getPaquetePersonaje().getId() + "\n");
                    return false;
               }
        }
        return true;
    }
	
	
	/**
     * Envia el mensaje al 
     * @param paqueteMensaje
     */
	public void enviarMensaje(PaqueteMensaje paqueteMensaje, String mje) {
    	int idUser= 0;
    	paqueteMensaje.setComando(Comando.TALK);
    	paqueteMensaje.setMensaje(mje);
        idUser = buscarIdPersobaje(paqueteMensaje, paqueteMensaje.getUserEmisor());
        for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
            if (conectado.getIdPersonaje() == idUser) {
                try {
                    conectado.getSalida().writeObject(gson.toJson(paqueteMensaje));
                } catch (IOException e) {
                    Servidor.getLog().append("Falló al intentar enviar mensaje a:"
                            + conectado.getPaquetePersonaje().getId() + "\n");
                }
            }
        }
    }
	
	/**
	 * Carga el paquete con los datos solicitados por el personaje
	 * @param idPersonaje
	 * @param modo
	 * @return PaqueteModoJuego
	 */
	public PaqueteModoJuego configurarPaquete(int idPersonaje, int modo) {
		PaqueteModoJuego paqueteModoJuego = new PaqueteModoJuego();
		paqueteModoJuego.setIdPersonaje(idPersonaje);
		paqueteModoJuego.setModo(modo);
		return paqueteModoJuego;
	}
	
	public void actualizarModoJuegoAlJugador(int idPersonaje, PaqueteModoJuego paqueteModoJuego) {
		Servidor.getPersonajesConectados().get(idPersonaje).setModoJuego(paqueteModoJuego.getModo());
	}
}
