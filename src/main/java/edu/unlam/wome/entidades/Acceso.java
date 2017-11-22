package edu.unlam.wome.entidades;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Acceso {
	private SessionFactory fabrica;
	
	public Acceso(String configuracion) {
		this.fabrica = new Configuration().configure(configuracion).buildSessionFactory();
	}
	
	public SessionFactory getFabrica() {
		return fabrica;
	}
}
