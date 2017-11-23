package edu.unlam.wome.entidades;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.unlam.wome.mensajeria.PaquetePersonaje;

/**
 * <h2>Mapa de la tabla personaje de la base de datos</h2>
 * <h3>Atributos</h3>
 * <ul>
 * 		<li>idPersonaje : int</li>
 * 		<li>idInventario : int</li>
 * 		<li>idMochila : int</li>
 * 		<li>casta : String</li>
 * 		<li>raza : String</li>
 * 		<li>fuerza : int</li>
 * 		<li>destreza : int</li>
 * 		<li>inteligencia : int</li>
 * 		<li>saludTope : int</li>
 * 		<li>energiaTope : int</li>
 * 		<li>nombre : String</li>
 * 		<li>experiencia : int</li>
 * 		<li>nivel : int</li>
 * 		<li>idAlianza : int</li>
 * </ul>
 * <h3>Metodos Estàticos</h3>
 * <ul>
 * 		<li>private static int registrar(Acceso conexion, EntPersonaje ent)  : true / false</li>
 * 		<li>private static void actualizar(Acceso conexion, EntPersonaje ent) : void</li>
 * 		<li>public static int actualizarPersonaje(Acceso acceso, PaquetePersonaje paquetePersonaje, int idInventarioMochila):</li>
 * 		<li>public static EntPersonaje damePersonaje(Acceso acceso, int idPersonaje)  : </li>
 * </ul>
 * <h3>Metodos</h3>
 * <ul>
 * 		<li>Accesos getter and setter</li>
 * </ul>
 * @see hibernate.cfg.xml
 * @see personaje.hbm.xml
 */
public class EntPersonaje implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int idPersonaje;
	private int idInventario;
	private int idMochila;
	private String casta;
	private String raza;
	private int fuerza;
	private int destreza;
	private int inteligencia;
	private int saludTope;
	private int energiaTope;
	private String nombre;
	private int experiencia;
	private int nivel;
	private int idAlianza;
	
	
	/**Contructor por defecto*/
	public EntPersonaje(){}
	
	
	public EntPersonaje(PaquetePersonaje paquetePersonaje) {
		this.casta = paquetePersonaje.getCasta();
		this.raza = paquetePersonaje.getRaza();
		this.fuerza = paquetePersonaje.getFuerza();
		this.destreza = paquetePersonaje.getDestreza();
		this.inteligencia = paquetePersonaje.getInteligencia();
		this.saludTope = paquetePersonaje.getSaludTope();
		this.energiaTope = paquetePersonaje.getEnergiaTope();
		this.nombre = paquetePersonaje.getNombre();
		this.experiencia = 0;
		this.nivel = 1;
		this.idAlianza = -1;
	}
	
	
	private static EntPersonaje cargarPaquete(PaquetePersonaje paquetePersonaje) {
		return new EntPersonaje(paquetePersonaje);
	}
	
	
	/**
	 * <h3>Metodo para guardar un personaje nuevo en la base de datos</h3>
	 * @param conexion : Acceso 
	 * @param ent : EntPersonaje
	 * @return idPersonajeNuevo
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	private static int registrar(Acceso conexion, EntPersonaje ent) {
		Session session = conexion.getFabrica().openSession();
		session.beginTransaction();
		try {
			session.save(ent);
			session.getTransaction().commit();
		} catch (HibernateException | SecurityException e) {
			session.getTransaction().getRollbackOnly();
			e.printStackTrace();
			return -1;
		} finally {
			session.close();
		}
		return ent.getIdPersonaje();
	}
	
	/**
	 * <h3>Metodo para actualizar un personaje en la base de datos</h3>
	 * @param conexion : Acceso 
	 * @param ent : EntPersonaje
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	private static void actualizar(Acceso conexion, EntPersonaje ent){
		Session session = conexion.getFabrica().openSession();
		session.beginTransaction();
		try {
			session.clear();
			session.update(ent);
			session.flush();
			session.getTransaction().commit();
		} catch (HibernateException | SecurityException e) {
			try {
				session.getTransaction().rollback();
				session.close();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	
	/**
	 * <h3>Metodo para intermedio para registrar personaje un personaje en la base de datos</h3>
	 * @param conexion : Acceso 
	 * @param ent : PaquetePersonaje
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	public static int registrarPersonaje(Acceso conexion, PaquetePersonaje paquetePersonaje) {
		EntPersonaje personaje = cargarPaquete(paquetePersonaje);
		personaje.setIdMochila(-1);
		personaje.setIdInventario(-1);
		return registrar(conexion, personaje);
	}
	
	/**
	 * <h3>Metodo para intermedio para actualizar personaje un personaje en la base de datos</h3>
	 * @param conexion : Acceso 
	 * @param ent : PaquetePersonaje
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	public static int actualizarPersonaje(Acceso acceso, PaquetePersonaje paquetePersonaje) {
		EntPersonaje ent = cargarPaquete(paquetePersonaje);
		ent.setIdPersonaje(paquetePersonaje.getId());
		actualizar(acceso, ent);
		return paquetePersonaje.getId();
	}
	
	/**
	 * <h3>Metodo para intermedio para actualizar personaje con el idPersonajeNuevo un personaje en la base de datos</h3>
	 * @param conexion : Acceso 
	 * @param ent : PaquetePersonaje
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	public static int actualizarPersonaje(Acceso acceso, PaquetePersonaje paquetePersonaje, int idInventarioMochila) {
		EntPersonaje ent = cargarPaquete(paquetePersonaje);
		ent.setIdInventario(idInventarioMochila);
		ent.setIdMochila(idInventarioMochila);
		ent.setIdPersonaje(idInventarioMochila);
		actualizar(acceso, ent);
		return paquetePersonaje.getId();
	}

	/**
	 * <h3>Devuelve un personaje de la bd</h3>
	 * @param conexion : Acceso 
	 * @param ent : PaquetePersonaje id a buscar
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	public static EntPersonaje damePersonaje(Acceso acceso, int idPersonaje) {
		Session session = acceso.getFabrica().openSession();
		EntPersonaje e = (EntPersonaje) session.createQuery(
				"SELECT r FROM EntPersonaje r WHERE idPersonaje=" + idPersonaje).uniqueResult();
		session.close();
		return e;
	}

	public int getIdPersonaje() {
		return idPersonaje;
	}


	public int getIdInventario() {
		return idInventario;
	}


	public int getIdMochila() {
		return idMochila;
	}


	public String getCasta() {
		return casta;
	}


	public String getRaza() {
		return raza;
	}


	public int getFuerza() {
		return fuerza;
	}


	public int getDestreza() {
		return destreza;
	}


	public int getInteligencia() {
		return inteligencia;
	}


	public int getSaludTope() {
		return saludTope;
	}


	public int getEnergiaTope() {
		return energiaTope;
	}


	public String getNombre() {
		return nombre;
	}


	public int getExperiencia() {
		return experiencia;
	}


	public int getNivel() {
		return nivel;
	}


	public int getIdAlianza() {
		return idAlianza;
	}


	public void setIdPersonaje(int idPersonaje) {
		this.idPersonaje = idPersonaje;
	}


	public void setIdInventario(int idInventario) {
		this.idInventario = idInventario;
	}


	public void setIdMochila(int idMochila) {
		this.idMochila = idMochila;
	}


	public void setCasta(String casta) {
		this.casta = casta;
	}


	public void setRaza(String raza) {
		this.raza = raza;
	}


	public void setFuerza(int fuerza) {
		this.fuerza = fuerza;
	}


	public void setDestreza(int destreza) {
		this.destreza = destreza;
	}


	public void setInteligencia(int inteligencia) {
		this.inteligencia = inteligencia;
	}


	public void setSaludTope(int saludTope) {
		this.saludTope = saludTope;
	}


	public void setEnergiaTope(int energiaTope) {
		this.energiaTope = energiaTope;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public void setExperiencia(int experiencia) {
		this.experiencia = experiencia;
	}


	public void setNivel(int nivel) {
		this.nivel = nivel;
	}


	public void setIdAlianza(int idAlianza) {
		this.idAlianza = idAlianza;
	}
}
