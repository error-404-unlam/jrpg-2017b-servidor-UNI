package edu.unlam.wome.modos;
import edu.unlam.wome.mensajeria.PaqueteMensaje;
import edu.unlam.wome.mensajeria.PaqueteModoJuego;
import edu.unlam.wome.potenciados.PersonajesPotenciados;
import edu.unlam.wome.servidor.Servidor;

/**
 * Clase que extiende de ModoJuego que permite cargar la funcionalidad
 * modo dios a los personajes
 * @see edu.unlam.wome.modos.ModoJuego
 *
 */
public class ModoDios extends ModoJuego{
	private PaqueteMensaje paqueteMensaje;
	
	public ModoDios(PaqueteMensaje paqueteMensaje) {
		this.paqueteMensaje = paqueteMensaje;
	}
	
	/**
	 * Metodo sobrescrito que possen todas las clases derivadas de ModoJuego
	 */
	@Override
	public boolean actualizar() {
		int idPersonaje = buscarIdPersobaje(paqueteMensaje, paqueteMensaje.getUserEmisor());
		PaqueteModoJuego paqueteModoJuego = configurarPaquete(idPersonaje, PaqueteModoJuego.MODO_DIOS);
		actualizarModoJuegoAlJugador(idPersonaje, paqueteModoJuego);
		Servidor.potenciados.add(new PersonajesPotenciados(idPersonaje, PaqueteModoJuego.MODO_DIOS));
		enviarMensaje(paqueteMensaje);
		return actualizarPotenciasdosATodos(paqueteMensaje, paqueteModoJuego);	
		
	}
}
