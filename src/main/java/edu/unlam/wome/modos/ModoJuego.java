package edu.unlam.wome.modos;

import edu.unlam.wome.mensajeria.PaqueteMensaje;

public abstract class ModoJuego {
	
	public ModoJuego seleccion(PaqueteMensaje paqueteMensaje) {
		switch(paqueteMensaje.getMensaje()) {
			case "iddqd": return new ModoDios(paqueteMensaje);
		}
		return new ModoDefecto();
	}
	
	public abstract boolean actualizar();
	
}
