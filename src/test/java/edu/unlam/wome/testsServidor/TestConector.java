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

	
	private String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
//	@Test
//	public void testConexionConLaDB() {
//		new Servidor();
//		Servidor.main(null);
//
//		Conector conector = new Conector();
//		conector.connect();
//
//		// Pasado este punto la conexi�n con la base de datos result� exitosa
//
//		Assert.assertEquals(1, 1);
//
//		conector.close();
//	}
//
//	@Test
//	public void testRegistrarUsuario() {
//		new Servidor();
//		Servidor.main(null);
//
//		Conector conector = new Conector();
//		conector.connect();
//
//		PaqueteUsuario pu = new PaqueteUsuario();
//		pu.setUsername("UserTest");
//		pu.setPassword("test");
//
//		conector.registrarUsuario(pu);
//
//		pu = conector.getUsuario("UserTest");
//
//		Assert.assertEquals("UserTest", pu.getUsername());
//
//		conector.close();
//	}
//
//	@Test
//	public void testRegistrarPersonaje() throws IOException {
//		new Servidor();
//		Servidor.main(null);
//
//		Conector conector = new Conector();
//		conector.connect();
//		String randomero = getRandomString();
//		PaquetePersonaje pp = new PaquetePersonaje();
//		pp.setCasta("Humano");
//		pp.setDestreza(1);
//		pp.setEnergiaTope(1);
//		pp.setExperiencia(1);
//		pp.setFuerza(1);
//		pp.setInteligencia(1);
//		pp.setNivel(1);
//		pp.setNombre(randomero);
//		pp.setRaza("Asesino");
//		pp.setSaludTope(1);
//
//		PaqueteUsuario pu = new PaqueteUsuario();
//		pu.setUsername(getRandomString());
//		pu.setPassword("test");
//
//		conector.registrarUsuario(pu);
//		conector.registrarPersonaje(pp, pu);
//		
//		pp = conector.getPersonaje(pu);
//
//		Assert.assertEquals(randomero, pp.getNombre());
//		conector.close();
//	}
//
//	@Test
//	public void testLoginUsuario() {
//		new Servidor();
//		Servidor.main(null);
//
//		Conector conector = new Conector();
//		conector.connect();
//
//		PaqueteUsuario pu = new PaqueteUsuario();
//		pu.setUsername("UserTest");
//		pu.setPassword("test");
//
//		conector.registrarUsuario(pu);
//
//		boolean resultadoLogin = conector.loguearUsuario(pu);
//
//		Assert.assertEquals(true, resultadoLogin);
//
//		conector.close();
//	}
//
//	@Test
//	public void testLoginUsuarioFallido() {
//		new Servidor();
//		Servidor.main(null);
//
//		Conector conector = new Conector();
//		conector.connect();
//
//		PaqueteUsuario pu = new PaqueteUsuario();
//		pu.setUsername("userInventado");
//		pu.setPassword("test");
//
//		boolean resultadoLogin = conector.loguearUsuario(pu);
//
//		Assert.assertEquals(false, resultadoLogin);
//
//		conector.close();
//	}
	
	@Test
	public void probarEntidadRegistro() {
		assertEquals(false, EntRegistro.registrarUsuario(acceso, new PaqueteUsuario(155,"FedeTest", "232323")));
	}
	
//	@Test
//	public void registrarPersonaje() {
//		try {
//			PaquetePersonaje pa = new PaquetePersonaje();
//			PaqueteUsuario pu = new PaqueteUsuario();
//			pu.setUsername("adssdasda");
//			pu.setPassword("sasasa");
//			assertEquals(true, EntPersonaje.registrarPersonaje(new Acceso("hibernate.cfg.xml"), pa, pu));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
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
}
