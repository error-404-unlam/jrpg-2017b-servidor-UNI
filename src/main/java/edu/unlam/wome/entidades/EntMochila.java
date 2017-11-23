package edu.unlam.wome.entidades;

import java.io.Serializable;
import java.util.LinkedList;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.unlam.wome.mensajeria.PaquetePersonaje;

/**
 * <h2>Mapa de la tabla mochila de la base de datos</h2>
 * <h3>Atributos</h3>
 * <ul>
 * 		<li>idMochila : int</li>
 * 		<li>item1 : int</li>
 * 		<li>item1 : int</li>
 * <li>item1 : int</li>
 * <li>item2 : int</li>
 * <li>item3 : int</li>
 * <li>item4 : int</li>
 * <li>item5 : int</li>
 * <li>item6 : int</li>
 * <li>item7 : int</li>
 * <li>item8 : int</li>
 * <li>item9 : int</li>
 * <li>item10 : int</li>
 * <li>item11 : int</li>
 * <li>item12 : int</li>
 * <li>item13 : int</li>
 * <li>item14 : int</li>
 * <li>item15 : int</li>
 * <li>item16 : int</li>
 * <li>item17 : int</li>
 * <li>item18 : int</li>
 * <li>item19: int</li>
 * <li>item20 : int</li>
 * </ul>
 * <h3>Metodos Estàticos</h3>
 * <ul>
 * 		<li>public static void actualizarMochila(Acceso acceso, EntMochila mochila)</li>
 * 		<li>public static EntMochila cargarMochila(PaquetePersonaje personaje)</li>
 * 		<li>public static EntMochila dameMochila(Acceso acceso, int idMochila)</li>
 * 		<li>public static boolean asignarMochila(Acceso conexion, int idMochila) </li>
 * </ul>
 * <h3>Metodos</h3>
 * <ul>
 * 		<li>Accesos getter and setter</li>
 * </ul>
 * @see hibernate.cfg.xml
 * @see mochila.hbm.xml
 */

public class EntMochila implements Serializable{

	private static final long serialVersionUID = 1L;
	private int idMochila;
	private int item1;
	private int item2;
	private int item3;
	private int item4;
	private int item5;
	private int item6;
	private int item7;
	private int item8;
	private int item9;
	private int item10;
	private int item11;
	private int item12;
	private int item13;
	private int item14;
	private int item15;
	private int item16;
	private int item17;
	private int item18;
	private int item19;
	private int item20;
	
	
	public EntMochila(){
		
	}
	
	public EntMochila(int idMochila){
		this.idMochila = idMochila;
		this.item1 = 	-1;
		this.item2 = 	-1;
		this.item3= 	-1;
		this.item4= 	-1;
		this.item5= 	-1;
		this.item6= 	-1;
		this.item7= 	-1;
		this.item8= 	-1;
		this.item9= 	-1;
		this.item10= 	-1;
		this.item11= 	-1;
		this.item12= 	-1;
		this.item13= 	-1;
		this.item14= 	-1;
		this.item15= 	-1;
		this.item16= 	-1;
		this.item17= 	-1;
		this.item18= 	-1;
		this.item19= 	-1;
		this.item20= 	-1;
	}
	
	public EntMochila(PaquetePersonaje personaje) {
		
		this(personaje.getId());
		
		this.item1 = personaje.getItemID(0);
		this.item2 = personaje.getItemID(1);
		this.item3 = personaje.getItemID(2);
		this.item4 = personaje.getItemID(3);
		this.item5 = personaje.getItemID(4);
		this.item6 = personaje.getItemID(5);
		this.item7 = personaje.getItemID(6);
		this.item8 = personaje.getItemID(7);
		this.item9 = personaje.getItemID(8);
		this.item10 = personaje.getItemID(9);
		this.item11 = personaje.getItemID(10);
		this.item12 = personaje.getItemID(11);
		this.item13 = personaje.getItemID(12);
		this.item14 = personaje.getItemID(13);
		this.item15 = personaje.getItemID(14);
		this.item16 = personaje.getItemID(15);
		this.item17 = personaje.getItemID(16);
		this.item18 = personaje.getItemID(17);
		this.item19 = personaje.getItemID(18);
		this.item20 = personaje.getItemID(19);
	}

	
	/**
	 * <h3>Metodo que devuelve un listado de items</h3>
	 * @param conexion : Acceso 
	 * @param ent : EntMochila
	 */
	public static LinkedList<Integer> dameListadoItems(EntMochila mochila){
		LinkedList<Integer> listadoItems = new LinkedList<>();
		listadoItems.add(mochila.getItem1());
		listadoItems.add(mochila.getItem2());
		listadoItems.add(mochila.getItem3());
		listadoItems.add(mochila.getItem4());
		listadoItems.add(mochila.getItem5());
		listadoItems.add(mochila.getItem6());
		listadoItems.add(mochila.getItem7());
		listadoItems.add(mochila.getItem8());
		listadoItems.add(mochila.getItem9());
		return new LinkedList<Integer>(listadoItems);
	}
	
	/**
	 * <h3>Metodo que carga la mochila desde un listado de items</h3>
	 * @param conexion : Acceso 
	 * @param ent : EntMochila
	 */
	public static EntMochila cargarListadoEnMochila(PaquetePersonaje personaje, LinkedList<Integer> listadoItems) {
		EntMochila mochila = new EntMochila();
		mochila.setIdMochila(personaje.getId());
		mochila.setItem1(listadoItems.get(0));
		mochila.setItem2(listadoItems.get(1));
		mochila.setItem3(listadoItems.get(2));
		mochila.setItem4(listadoItems.get(3));
		mochila.setItem5(listadoItems.get(4));
		mochila.setItem6(listadoItems.get(5));
		mochila.setItem7(listadoItems.get(6));
		mochila.setItem8(listadoItems.get(7));
		mochila.setItem9(listadoItems.get(8));
		mochila.setItem10(listadoItems.get(9));
		mochila.setItem11(listadoItems.get(10));
		mochila.setItem12(listadoItems.get(11));
		mochila.setItem13(listadoItems.get(12));
		mochila.setItem14(listadoItems.get(13));
		mochila.setItem15(listadoItems.get(14));
		mochila.setItem16(listadoItems.get(15));
		mochila.setItem17(listadoItems.get(16));
		mochila.setItem18(listadoItems.get(17));
		mochila.setItem19(listadoItems.get(18));
		mochila.setItem20(listadoItems.get(19));
		
		return mochila;
	}
	
	
	/**
	 * <h3>Metodo para actualizar la mochila</h3>
	 * @param conexion : Acceso 
	 * @param ent : EntMochila
	 * @return idPersonajeNuevo
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	public static void actualizarMochila(Acceso acceso, EntMochila mochila) {
		Session session = acceso.getFabrica().openSession();
		session.beginTransaction();
		try {
			session.saveOrUpdate(mochila);
			session.getTransaction().commit();
		} catch (HibernateException | SecurityException e) {
			session.getTransaction().getRollbackOnly();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	/**
	 * <h3>Metodo para cargar la mochila desde un personaje</h3>
	 * @param conexion : Acceso 
	 * @return EntMochila
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	public static EntMochila cargarMochila(PaquetePersonaje personaje) {
		EntMochila mochila = new EntMochila(personaje);
		return mochila;
	}
	
	
	/**
	 * <h3>Metodo para obtener una mochila de la BD</h3>
	 * @param conexion : Acceso 
	 * @return EntMochila
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	public static EntMochila dameMochila(Acceso acceso, int idMochila) {
		Session session = acceso.getFabrica().openSession();
		EntMochila moc = (EntMochila) session.createQuery( 
				"SELECT m FROM EntMochila m WHERE idMochila="  + 
						idMochila ).uniqueResult();
		session.close();
		return moc;
	}
	
	/**
	 * <h3>Metodo para asignar una mochila a un personaje</h3>
	 * @param conexion : Acceso 
	 * @return true / false
	 * @see edu.unlam.wome.entidades.Acceso
	 */
	public static boolean asignarMochila(Acceso conexion, int idMochila) {
		EntMochila moc = new EntMochila(idMochila);
		Session session = conexion.getFabrica().openSession();
		session.beginTransaction();
		try {
			session.saveOrUpdate(moc);
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
	
	public int getIdMochila() {
		return idMochila;
	}

	public void setIdMochila(int idMochila) {
		this.idMochila = idMochila;
	}

	public int getItem1() {
		return item1;
	}

	public void setItem1(int item1) {
		this.item1 = item1;
	}

	public int getItem2() {
		return item2;
	}

	public void setItem2(int item2) {
		this.item2 = item2;
	}

	public int getItem3() {
		return item3;
	}

	public void setItem3(int item3) {
		this.item3 = item3;
	}

	public int getItem4() {
		return item4;
	}

	public void setItem4(int item4) {
		this.item4 = item4;
	}

	public int getItem5() {
		return item5;
	}

	public void setItem5(int item5) {
		this.item5 = item5;
	}

	public int getItem6() {
		return item6;
	}

	public void setItem6(int item6) {
		this.item6 = item6;
	}

	public int getItem7() {
		return item7;
	}

	public void setItem7(int item7) {
		this.item7 = item7;
	}

	public int getItem8() {
		return item8;
	}

	public void setItem8(int item8) {
		this.item8 = item8;
	}

	public int getItem9() {
		return item9;
	}

	public void setItem9(int item9) {
		this.item9 = item9;
	}

	public int getItem10() {
		return item10;
	}

	public void setItem10(int item10) {
		this.item10 = item10;
	}

	public int getItem11() {
		return item11;
	}

	public void setItem11(int item11) {
		this.item11 = item11;
	}

	public int getItem12() {
		return item12;
	}

	public void setItem12(int item12) {
		this.item12 = item12;
	}

	public int getItem13() {
		return item13;
	}

	public void setItem13(int item13) {
		this.item13 = item13;
	}

	public int getItem14() {
		return item14;
	}

	public void setItem14(int item14) {
		this.item14 = item14;
	}

	public int getItem15() {
		return item15;
	}

	public void setItem15(int item15) {
		this.item15 = item15;
	}

	public int getItem16() {
		return item16;
	}

	public void setItem16(int item16) {
		this.item16 = item16;
	}

	public int getItem17() {
		return item17;
	}

	public void setItem17(int item17) {
		this.item17 = item17;
	}

	public int getItem18() {
		return item18;
	}

	public void setItem18(int item18) {
		this.item18 = item18;
	}

	public int getItem19() {
		return item19;
	}

	public void setItem19(int item19) {
		this.item19 = item19;
	}

	public int getItem20() {
		return item20;
	}

	public void setItem20(int item20) {
		this.item20 = item20;
	}

	@Override
	public String toString() {
		return "EntMochila [idMochila=" + idMochila + ", item1=" + item1 + ", item2=" + item2 + ", item3=" + item3
				+ ", item4=" + item4 + ", item5=" + item5 + ", item6=" + item6 + ", item7=" + item7 + ", item8=" + item8
				+ ", item9=" + item9 + ", item10=" + item10 + ", item11=" + item11 + ", item12=" + item12 + ", item13="
				+ item13 + ", item14=" + item14 + ", item15=" + item15 + ", item16=" + item16 + ", item17=" + item17
				+ ", item18=" + item18 + ", item19=" + item19 + ", item20=" + item20 + "]";
	}
	
	
}
