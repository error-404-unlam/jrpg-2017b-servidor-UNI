package edu.unlam.wome.modos;

import edu.unlam.wome.mensajeria.PaqueteMensaje;
import edu.unlam.wome.mensajeria.PaqueteModoJuego;
import edu.unlam.wome.potenciados.PersonajesPotenciados;
import edu.unlam.wome.servidor.Servidor;

public class ModoNormal extends ModoJuego{

	
	private PaqueteMensaje paqueteMensaje;
	
	public ModoNormal(PaqueteMensaje paqueteMensaje) {
		this.paqueteMensaje = paqueteMensaje;
	}
	
	
	@Override
	public boolean actualizar() {
		int idPersonaje = buscarIdPersobaje(paqueteMensaje, paqueteMensaje.getUserEmisor());
		PaqueteModoJuego paqueteModoJuego = configurarPaquete(idPersonaje, PaqueteModoJuego.NORMAL);
		actualizarModoJuegoAlJugador(idPersonaje, paqueteModoJuego);
		Servidor.potenciados.add(new PersonajesPotenciados(idPersonaje, PaqueteModoJuego.NORMAL));
		enviarMensaje(paqueteMensaje, dameMensaje());
		return actualizarPotenciasdosATodos(paqueteMensaje, paqueteModoJuego);	
	}

	@Override
	public String dameMensaje() {
		return "Los trucos fueron removidos";
	}

}
