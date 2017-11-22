package edu.unlam.wome.entidades;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class EntInventario implements Serializable{

	private static final long serialVersionUID = 1L;
	private int idInventario;
	private int manos1;
	private int manos2;
	private int pie;
	private int cabeza;
	private int pecho;
	private int accesorio;
	
	public EntInventario() {
		
	}
	
	
	public EntInventario(int idInventario) {
		this.idInventario = idInventario;
		this.manos1 = -1;
		this.manos2 = -1;
		this.pie = -1;
		this.cabeza = -1;
		this.pecho = -1;
		this.accesorio = -1;
	}

	public static boolean asignarInventario(Acceso conexion, int idInventario) {
		EntInventario inv = new EntInventario(idInventario);
		Session session = conexion.getFabrica().openSession();
		session.beginTransaction();
		try {
			session.saveOrUpdate(inv);
			session.getTransaction().commit();
		} catch (HibernateException | SecurityException e) {
			session.getTransaction().getRollbackOnly();
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
		return true;
	}
	

	public int getIdInventario() {
		return idInventario;
	}


	public void setIdInventario(int idInventario) {
		this.idInventario = idInventario;
	}


	public int getManos1() {
		return manos1;
	}


	public void setManos1(int manos1) {
		this.manos1 = manos1;
	}


	public int getManos2() {
		return manos2;
	}


	public void setManos2(int manos2) {
		this.manos2 = manos2;
	}


	public int getPie() {
		return pie;
	}


	public void setPie(int pie) {
		this.pie = pie;
	}


	public int getCabeza() {
		return cabeza;
	}


	public void setCabeza(int cabeza) {
		this.cabeza = cabeza;
	}


	public int getPecho() {
		return pecho;
	}


	public void setPecho(int pecho) {
		this.pecho = pecho;
	}


	public int getAccesorio() {
		return accesorio;
	}


	public void setAccesorio(int accesorio) {
		this.accesorio = accesorio;
	}
}
