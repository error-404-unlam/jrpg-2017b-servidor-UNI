package edu.unlam.wome.entidades;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * <h2>Objetos que permiten la conexion con la base de datos y los accesos a la misma</h2>
 * <h3>Atributos</h3>
 * <ul>
 * 		<li>fablica : SessionFactory</li>
 * </ul>
 *
 */
public class Acceso {
	private SessionFactory fabrica;
	
	public Acceso(String configuracion) {
		this.fabrica = new Configuration().configure(configuracion).buildSessionFactory();
	}
	
	public SessionFactory getFabrica() {
		return fabrica;
	}
}
