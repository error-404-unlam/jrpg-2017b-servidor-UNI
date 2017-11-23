package edu.unlam.wome.entidades;

import java.io.Serializable;


import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;


/**
 * <h2>Mapa de la tabla item de la base de datos</h2>
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
public class EntItem implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int idItem;
	private String nombre;
	private int wereable;
	private int bonusSalud;
	private int bonusEnergia;
	private int bonusFuerza;
	private int bonusDestreza;
	private int bonusInteligencia;
	private String foto;
	private String fotoEquipado;
	private int fuerzaRequerida;
	private int destrezaRequerida;
	private int inteligenciarequerida;

	@SuppressWarnings("unchecked")
	public static List<EntItem> levantarItems(Acceso conexion){
		Session session = conexion.getFabrica().openSession();
		session.beginTransaction();
		List<EntItem> listado = (List<EntItem>)session.createQuery("FROM  EntItem").list(); 
		session.close();
		return listado;
	}
	
	public static EntItem dameItem(Acceso acceso, int idItem) {
		Session session = acceso.getFabrica().openSession();
		EntItem ent = (EntItem) session.createQuery("SELECT i FROM EntItem i WHERE idItem=" + idItem).uniqueResult();
		session.close();
		return ent;
	}
	
	public int getIdItem() {
		return idItem;
	}

	public void setIdItem(int idItem) {
		this.idItem = idItem;
	}



	public String getNombre() {
		return nombre;
	}



	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	public int getWereable() {
		return wereable;
	}



	public void setWereable(int wereable) {
		this.wereable = wereable;
	}



	public int getBonusSalud() {
		return bonusSalud;
	}



	public void setBonusSalud(int bonusSalud) {
		this.bonusSalud = bonusSalud;
	}



	public int getBonusEnergia() {
		return bonusEnergia;
	}



	public void setBonusEnergia(int bonusEnergia) {
		this.bonusEnergia = bonusEnergia;
	}



	public int getBonusFuerza() {
		return bonusFuerza;
	}



	public void setBonusFuerza(int bonusFuerza) {
		this.bonusFuerza = bonusFuerza;
	}



	public int getBonusDestreza() {
		return bonusDestreza;
	}



	public void setBonusDestreza(int bonusDestreza) {
		this.bonusDestreza = bonusDestreza;
	}



	public int getBonusInteligencia() {
		return bonusInteligencia;
	}



	public void setBonusInteligencia(int bonusInteligencia) {
		this.bonusInteligencia = bonusInteligencia;
	}



	public String getFoto() {
		return foto;
	}



	public void setFoto(String foto) {
		this.foto = foto;
	}



	public String getFotoEquipado() {
		return fotoEquipado;
	}



	public void setFotoEquipado(String fotoEquipado) {
		this.fotoEquipado = fotoEquipado;
	}



	public int getFuerzaRequerida() {
		return fuerzaRequerida;
	}



	public void setFuerzaRequerida(int fuerzaRequerida) {
		this.fuerzaRequerida = fuerzaRequerida;
	}



	public int getDestrezaRequerida() {
		return destrezaRequerida;
	}



	public void setDestrezaRequerida(int destrezaRequerida) {
		this.destrezaRequerida = destrezaRequerida;
	}



	public int getInteligenciarequerida() {
		return inteligenciarequerida;
	}



	public void setInteligenciarequerida(int inteligenciarequerida) {
		this.inteligenciarequerida = inteligenciarequerida;
	}

	@Override
	public String toString() {
		return "EntItem [idItem=" + idItem + ", nombre=" + nombre + ", wareable=" + wereable + ", bonisSalud="
				+ bonusSalud + ", bonusEnergia=" + bonusEnergia + ", bonosFuerza=" + bonusFuerza + ", bonusDestreza="
				+ bonusDestreza + ", bonusInteligencia=" + bonusInteligencia + ", foto=" + foto + ", fotoEquipado="
				+ fotoEquipado + ", fuerzaRequerida=" + fuerzaRequerida + ", destrezaRequerida=" + destrezaRequerida
				+ ", inteligenciarequerida=" + inteligenciarequerida + "]";
	}
	
	
}
