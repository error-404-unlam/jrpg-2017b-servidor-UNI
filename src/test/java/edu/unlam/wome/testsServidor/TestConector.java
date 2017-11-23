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
	
	@Test
	public void asignarInventario() {
		assertEquals(true, EntInventario.asignarInventario(acceso, 140));
	}
	
	@Test
	public void asignarMochila(){
		assertEquals(true, EntMochila.asignarMochila(acceso, 140));
	}
	
	@Test
	public void levantarItems() {
		List<EntItem> lista = EntItem.levantarItems(acceso);
		for(EntItem e : lista) 
			System.out.println(e);
	}
	
	@Test
	public void probarLogin() {
		assertEquals(true, EntRegistro.login(acceso, new PaqueteUsuario(0,"PedroTest","Test")));
	}
	
	@Test
	public void probarDameMochila() {
		EntMochila moc = EntMochila.dameMochila(acceso, 2);
		System.out.println(moc);
	}
	
	@Test
	public void probarDameItem(){
		EntItem it = EntItem.dameItem(acceso, 1);
		System.out.println(it);
		System.out.println(EntItem.dameListadoAtributos(it));
	}
	
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
	
	@Test
	public void probarDameUsuario() {
		EntRegistro ent = EntRegistro.dameUsuario(acceso, new PaqueteUsuario(0, "Nico2", "hhhh"));
		System.out.println(ent);
	}
	
	@Test
	public void probarDamePersonaje() {
		EntPersonaje ent = EntPersonaje.damePersonaje(acceso,2);
		System.out.println(ent);
	}
	
	@Test
	public void probarObtenerIdPersonaje() {
		int idPersonaje = EntRegistro.obtenerIdPersonaje(acceso);
		System.out.println(idPersonaje);
	}
	
	
	@Test
	public void probarGuardarRegistro() {
		assertEquals(true, EntRegistro.guardar(acceso, new PaqueteUsuario(0,"FedeTest", "232323")));
	}
}
