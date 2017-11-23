package edu.unlam.wome.entidades;

import java.io.Serializable;


import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.unlam.wome.mensajeria.PaqueteUsuario;


/**
 * <h2>Mapa de la tabla <b>registro</b> de la base de datos</h2>
 * <h3>Atributos</h3>
 * <ul>
 * 		<li>usuario : String</li>
 * 		<li>password : String</li>
 * 		<li>idPersonaje : int</li>
 * </ul>
 * <h3>Metodos Estàticos</h3>
 * <ul>
 * 		<li>public static boolean guardar(Acceso acceso, PaqueteUsuario user)  : true / false</li>
 * 		<li>public static void actualizar(Acceso acceso, PaqueteUsuario user, int idPersonaje) : void</li>
 * 		<li>public static EntRegistro dameUsuario(Acceso acceso, PaqueteUsuario paqueteUsuario)  : EntRegistro</li>
 * 		<li>public static boolean login(Acceso acceso, PaqueteUsuario paqueteUsuario) : true / false</li>
 * </ul>
 * <h3>Metodos</h3>
 * <ul>
 * 		<li>Accesos getter and setter</li>
 * </ul>
 * @see hibernate.cfg.xml
 * @see registro.hbm.xml
 */
public class EntRegistro implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/** Atributo ID usuario*/
	private String usuario;
	
	/** Atributo password*/
	private String password;
	
	/** Atributo idPersonaje*/
	private int idPersonaje;
	
	
	/** Constructor por defecto necesario para el correcto mapeo de las tablas de la BD*/
	public EntRegistro(){
		
	}
	
	public EntRegistro(String usuario, String password, int idPersonaje) {
		this.usuario = usuario;
		this.password = password;
		this.idPersonaje = idPersonaje;
	}
	
	/**
	 * <h3>Metodo para guardar un registro nuevo en la base de datos</h3>
	 * @param acceso : Acceso 
	 * @param user : PaqueteUsuario
	 * @return <ul><li>true</li><li>false</li></ul>
	 * @see edu.unlam.wome.entidades.Acceso
	 * @see edu.unlam.wome.entidades.PaqueteUsuario
	 */
	public static boolean guardar(Acceso acceso, PaqueteUsuario user) {
		EntRegistro reg = new EntRegistro(user.getUsername(), user.getPassword(), 0);
		Session session = acceso.getFabrica().openSession();
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
	
	/**
	 * <h3>Metodo para actualizar el idPersonaje la base de datos</h3>
	 * @param acceso : Acceso 
	 * @param user : PaqueteUsuario
	 * @param idPersonaje : int
	 * @see edu.unlam.wome.entidades.Acceso
	 * @see edu.unlam.wome.entidades.PaqueteUsuario
	 */
	public static void actualizar(Acceso acceso, PaqueteUsuario user, int idPersonaje) {
		EntRegistro reg = new EntRegistro(user.getUsername(), user.getPassword(), idPersonaje);
		Session session = acceso.getFabrica().openSession();
		session.beginTransaction();
		try {
			session.saveOrUpdate(reg);
			session.flush();
			session.getTransaction().commit();
		} catch (HibernateException | SecurityException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	
	/**
	 * <h3>Metodo para obtener un usuario de la tabla registro</h3>
	 * @param acceso : Acceso 
	 * @param user : PaqueteUsuario
	 * @see edu.unlam.wome.entidades.Acceso
	 * @see edu.unlam.wome.entidades.PaqueteUsuario
	 */
	public static EntRegistro dameUsuario(Acceso acceso, PaqueteUsuario paqueteUsuario) {
		Session session = acceso.getFabrica().openSession();
		EntRegistro e = (EntRegistro) session.createQuery(
				"SELECT r FROM EntRegistro r WHERE usuario=" + "'" + paqueteUsuario.getUsername() + "'").uniqueResult();
		session.close();
		return e;
	}
	
	/**
	 * <h3>Metodo para validar el acceso de los usuarios al juego</h3>
	 * @param acceso : Acceso 
	 * @param user : PaqueteUsuario
	 * @see edu.unlam.wome.entidades.Acceso
	 * @see edu.unlam.wome.entidades.PaqueteUsuario
	 */
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
