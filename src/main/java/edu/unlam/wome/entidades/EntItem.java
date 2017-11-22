package edu.unlam.wome.entidades;

import java.io.Serializable;


import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;

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
	
	public static HashMap<String, Object> dameListadoAtributos(EntItem item){
		HashMap<String, Object> listadoAtributos = new HashMap<>();
		listadoAtributos.put("idItem", item.getIdItem());
		listadoAtributos.put("nombre", item.getNombre());
		listadoAtributos.put("wereable", item.getWereable());
		listadoAtributos.put("bonusSalud", item.getBonusSalud());
		listadoAtributos.put("bonusEnergia", item.getBonusEnergia());
		listadoAtributos.put("bonusFuerza", item.getBonusFuerza());
		listadoAtributos.put("bonusDestreza", item.getBonusDestreza());
		listadoAtributos.put("bonusInteligencia", item.getBonusInteligencia());
		listadoAtributos.put("foto", item.getFoto());
		listadoAtributos.put("fotoEquipado", item.getFotoEquipado());
		return new HashMap<String, Object>(listadoAtributos);
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
