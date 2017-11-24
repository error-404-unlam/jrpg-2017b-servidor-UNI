package edu.unlam.wome.modos;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;

import edu.unlam.wome.mensajeria.Comando;
import edu.unlam.wome.mensajeria.PaqueteMensaje;
import edu.unlam.wome.mensajeria.PaqueteModoJuego;
import edu.unlam.wome.mensajeria.PaquetePersonaje;
import edu.unlam.wome.potenciados.PersonajesPotenciados;
import edu.unlam.wome.servidor.EscuchaCliente;
import edu.unlam.wome.servidor.Servidor;


public class ModoDios extends ModoJuego{
	private PaqueteMensaje paqueteMensaje;
	private Gson gson = new Gson();
	
	
	public ModoDios(PaqueteMensaje paqueteMensaje) {
		this.paqueteMensaje = paqueteMensaje;
	}
	
	
	@Override
	public boolean actualizar() {
		int idPersonaje = buscarIdPersobaje(paqueteMensaje, paqueteMensaje.getUserEmisor());
		PaqueteModoJuego paqueteModoJuego = configurarPaquete(idPersonaje);
		actualizarModoJuegoAlJugador(idPersonaje, paqueteModoJuego);
		Servidor.potenciados.add(new PersonajesPotenciados(idPersonaje, PaqueteModoJuego.MODO_DIOS));
		enviarMensaje(paqueteMensaje);
		return actualizarPotenciasdosATodos(paqueteMensaje, paqueteModoJuego);	
		
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
	public void enviarMensaje(PaqueteMensaje paqueteMensaje) {
    	int idUser= 0;
    	paqueteMensaje.setComando(Comando.TALK);
    	paqueteMensaje.setMensaje("El Truco fue aprobado");
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
	
	private PaqueteModoJuego configurarPaquete(int idPersonaje) {
		PaqueteModoJuego paqueteModoJuego = new PaqueteModoJuego();
		paqueteModoJuego.setIdPersonaje(idPersonaje);
		paqueteModoJuego.setModo(PaqueteModoJuego.MODO_DIOS);
		return paqueteModoJuego;
	}
	
	private void actualizarModoJuegoAlJugador(int idPersonaje, PaqueteModoJuego paqueteModoJuego) {
		Servidor.getPersonajesConectados().get(idPersonaje).setModoJuego(paqueteModoJuego.getModo());
	}

}
