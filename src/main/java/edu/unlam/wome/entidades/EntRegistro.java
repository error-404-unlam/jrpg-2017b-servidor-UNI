package edu.unlam.wome.entidades;

import java.io.Serializable;


import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.unlam.wome.mensajeria.PaqueteUsuario;



public class EntRegistro implements Serializable{

	private static final long serialVersionUID = 1L;
	private String usuario;
	private String password;
	private int idPersonaje;
	
	public EntRegistro(){
		
	}

	public EntRegistro(String usuario, String password, int idPersonaje) {
		this.usuario = usuario;
		this.password = password;
		this.idPersonaje = idPersonaje;
	}
	
	private static boolean existe(Acceso acceso, EntRegistro r) {
		Session session = acceso.getFabrica().openSession();
		EntRegistro e = (EntRegistro) session.createQuery("SELECT r FROM EntRegistro r WHERE usuario=" + "'" + r.getUsuario() + "'").uniqueResult();
		session.close();
		return e == null;
	}
	
	public static EntRegistro dameUsuario(Acceso acceso, PaqueteUsuario paqueteUsuario) {
		Session session = acceso.getFabrica().openSession();
		EntRegistro e = (EntRegistro) session.createQuery(
				"SELECT r FROM EntRegistro r WHERE usuario=" + "'" + paqueteUsuario.getUsername() + "'").uniqueResult();
		session.close();
		return e;
	}
	
	public static int obtenerIdPersonaje(Acceso acceso) {
		Session session = acceso.getFabrica().openSession();
		int idPersonaje = (int) session.createQuery("SELECT MAX(idPersonaje) AS id FROM EntPersonaje").uniqueResult();
		session.close();
		return idPersonaje + 1;
	}
	
	public static boolean registrarUsuario(Acceso conexion, PaqueteUsuario user)  {
		int idPersonaje = obtenerIdPersonaje(conexion);
		EntRegistro reg = new EntRegistro(user.getUsername(), user.getPassword(), idPersonaje);
		
		Session session = conexion.getFabrica().openSession();
		session.beginTransaction();
		
		try {
			session.flush();
			session.saveOrUpdate(reg);
			session.flush();
			session.getTransaction().commit();
		} catch (HibernateException | SecurityException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	
		return reg != null;
	}
	
	public static boolean login(Acceso acceso, PaqueteUsuario paqueteUsuario) {
		Session session = acceso.getFabrica().openSession();
		EntRegistro ent = (EntRegistro) session.createQuery(
				"SELECT r FROM EntRegistro r WHERE usuario=" + 
				"'" + paqueteUsuario.getUsername() + "'" +  "AND password=" + "'" 
					+ paqueteUsuario.getPassword() + "'").uniqueResult();
		session.close();
		return ent != null;
	}
	
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getIdPersonaje() {
		return idPersonaje;
	}

	public void setIdPersonaje(int idPersonaje) {
		this.idPersonaje = idPersonaje;
	}

	@Override
	public String toString() {
		return "Registro [usuario=" + usuario + ", password=" + password + ", idPersonaje=" + idPersonaje + "]";
	}
}
