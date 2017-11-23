package edu.unlam.wome.testsServidor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Random;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.unlam.wome.entidades.Acceso;
import edu.unlam.wome.entidades.EntInventario;
import edu.unlam.wome.entidades.EntItem;
import edu.unlam.wome.entidades.EntMochila;
import edu.unlam.wome.entidades.EntPersonaje;
import edu.unlam.wome.entidades.EntRegistro;
import edu.unlam.wome.mensajeria.PaquetePersonaje;
import edu.unlam.wome.mensajeria.PaqueteUsuario;

public class TestConector {
	private Acceso acceso = new Acceso("hibernate.cfg.xml");
	
	/**
	 * Prueba si actualiza correctamente la tabla inventario 
	 * segun los datos del personaje recien ingresado
	 */
	@Test
	public void asignarInventario() {
		assertEquals(true, EntInventario.asignarInventario(acceso, 140));
	}
	
	/**
	 * Prueba si actualiza correctamente la tabla mochila
	 * segun los datos del personaje recien ingresado
	 */
	@Test
	public void asignarMochila(){
		assertEquals(true, EntMochila.asignarMochila(acceso, 140));
	}
	
	/**
	 * Probar si realiza la consulta correctamente en la tabla de items
	 */
	@Test
	public void levantarItems() {
		List<EntItem> lista = EntItem.levantarItems(acceso);
		for(EntItem e : lista) 
			System.out.println(e);
	}
	
	
	/**
	 * Prueba si se realizacorrectamente el ingreso con un usuario que ya esta en la BD
	 */
	@Test
	public void probarLogin() {
		assertEquals(true, EntRegistro.login(acceso, new PaqueteUsuario(0,"PedroTest","Test")));
	}
	
	
	/**
	 * Prueba si con el idPersonaje se obtiene la mochila correcta
	 */
	@Test
	public void probarDameMochila() {
		EntMochila moc = EntMochila.dameMochila(acceso, 2);
		System.out.println(moc);
	}
	
	/**
	 * Prueba si se actualiza el idInventario e idMochila correctamente en la tabla personaje 
	 * @throws IOException
	 */
	@Test
	public void probarActualizarPersonaje() throws IOException {
		PaquetePersonaje pa = new PaquetePersonaje();
		pa.setId(3);
		pa.setCasta("Guerrero");
		pa.setRaza("Humano");
		pa.setEnergiaTope(55);
		pa.setInteligencia(10);
		pa.setFuerza(15);
		EntPersonaje.actualizarPersonaje(acceso, pa, pa.getId());
	}
	
	
	/**
	 * Prueba si los registros se ingresan correctamente
	 */
	@Test
	public void probarDameUsuario() {
		EntRegistro ent = EntRegistro.dameUsuario(acceso, new PaqueteUsuario(0, "Nico2", "hhhh"));
		System.out.println(ent);
	}
	
	/**
	 * Prueba si se levanta correctamente un personaje de la BD
	 */
	@Test
	public void probarDamePersonaje() {
		EntPersonaje ent = EntPersonaje.damePersonaje(acceso,2);
		System.out.println(ent);
	}
	
	
	/**
	 * Prueba si los registros de usuarios se ingresan correctamente
	 */
	@Test
	public void probarGuardarRegistro() {
		assertEquals(true, EntRegistro.guardar(acceso, new PaqueteUsuario(0,"FedeTest", "232323")));
	}
}
