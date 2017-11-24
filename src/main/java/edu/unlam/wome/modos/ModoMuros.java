package edu.unlam.wome.modos;
import edu.unlam.wome.mensajeria.PaqueteMensaje;
import edu.unlam.wome.mensajeria.PaqueteModoJuego;
import edu.unlam.wome.potenciados.PersonajesPotenciados;
import edu.unlam.wome.servidor.Servidor;

/**
 * Clase que extiende de ModoJuego que permite cargar la funcionalidad
 * modo muros a los personajes
 * @see edu.unlam.wome.modos.ModoJuego
 *
 */
public class ModoMuros extends ModoJuego{
	private PaqueteMensaje paqueteMensaje;
	
	public ModoMuros(PaqueteMensaje paqueteMensaje) {
		this.paqueteMensaje = paqueteMensaje;
	}
	
	/**
	 * Metodo sobrescrito que possen todas las clases derivadas de ModoJuego
	 */
	@Override
	public boolean actualizar() {
		int idPersonaje = buscarIdPersobaje(paqueteMensaje, paqueteMensaje.getUserEmisor());
		PaqueteModoJuego paqueteModoJuego = configurarPaquete(idPersonaje, PaqueteModoJuego.MODO_MUROS);
		actualizarModoJuegoAlJugador(idPersonaje, paqueteModoJuego);
		int indice = Servidor.potenciados.indexOf(new PersonajesPotenciados(idPersonaje, PaqueteModoJuego.MODO_MUROS));
		if(indice == -1)
			Servidor.potenciados.add(new PersonajesPotenciados(idPersonaje, PaqueteModoJuego.MODO_MUROS));
		else {
			Servidor.potenciados.remove(indice);
			Servidor.potenciados.add(new PersonajesPotenciados(idPersonaje, PaqueteModoJuego.MODO_MUROS));
		}
		enviarMensaje(paqueteMensaje, dameMensaje());
		return actualizarPotenciasdosATodos(paqueteMensaje, paqueteModoJuego);	
		
	}
	
	public String dameMensaje() {
		return "Modo Muros Activado";
	}
}