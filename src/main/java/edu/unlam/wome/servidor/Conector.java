package edu.unlam.wome.servidor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import edu.unlam.wome.dominio.main.Personaje;
import edu.unlam.wome.entidades.Acceso;
import edu.unlam.wome.entidades.EntInventario;
import edu.unlam.wome.entidades.EntItem;
import edu.unlam.wome.entidades.EntMochila;
import edu.unlam.wome.entidades.EntPersonaje;
import edu.unlam.wome.entidades.EntRegistro;
import edu.unlam.wome.mensajeria.PaquetePersonaje;
import edu.unlam.wome.mensajeria.PaqueteUsuario;

public class Conector {

	private Acceso acceso; 

	public void connect() {
		acceso = new Acceso("hibernate.cfg.xml");
		Servidor.log.append("Estableciendo conexión con la base de datos..." + System.lineSeparator());
	}

	public void close() {
		acceso.getFabrica().close();
		Servidor.log.append("Error al intentar cerrar la conexión con la base de datos." + System.lineSeparator());
	}

	public boolean registrarUsuario(PaqueteUsuario user) {
		if(EntRegistro.guardar(acceso, user)) {
			Servidor.log.append("El usuario " + user.getUsername() + " se ha registrado." + System.lineSeparator());
			return true;
		}
		Servidor.log.append("El usuario " + user.getUsername() + " ya se encuentra en uso." + System.lineSeparator());
		return false;
	}

	public boolean registrarPersonaje(PaquetePersonaje paquetePersonaje, PaqueteUsuario paqueteUsuario) {
		int idPersonajeNuevo = EntPersonaje.registrarPersonaje(acceso, paquetePersonaje);
		if(idPersonajeNuevo != -1) {
			EntRegistro.actualizar(acceso, paqueteUsuario, idPersonajeNuevo);
			EntInventario.asignarInventario(acceso, idPersonajeNuevo);
			EntMochila.asignarMochila(acceso, idPersonajeNuevo);
			EntPersonaje.actualizarPersonaje(acceso, paquetePersonaje, idPersonajeNuevo);
			Servidor.log.append("El usuario " + paqueteUsuario.getUsername() + " ha creado el personaje "
					+ paquetePersonaje.getId() + System.lineSeparator());
			return true;
		}
		Servidor.log.append("Error al registrar la mochila y el inventario del usuario " + 
				paqueteUsuario.getUsername() + " con el personaje" + paquetePersonaje.getId() + System.lineSeparator());
		return false;
	}

	public boolean registrarInventarioMochila(int idInventarioMochila) {
		if( EntInventario.asignarInventario(acceso, idInventarioMochila) && 
				EntMochila.asignarMochila(acceso, idInventarioMochila)){
			Servidor.log.append("Se ha registrado el inventario de " + idInventarioMochila + System.lineSeparator());
			return true;
		}
		Servidor.log.append("Error al registrar el inventario de " + idInventarioMochila + System.lineSeparator());
		return false;
	}

	public boolean loguearUsuario(PaqueteUsuario user) {
		if(EntRegistro.login(acceso, user)) {
			Servidor.log.append("El usuario " + user.getUsername() + 
					" ha iniciado sesión." + System.lineSeparator());
			return true;
		}
		Servidor.log.append("El usuario " + 
		user.getUsername() + " ha realizado un intento fallido de inicio de sesión." + System.lineSeparator());
		return false;
	}

	public void actualizarPersonaje(PaquetePersonaje paquetePersonaje)  {
	
		int i = 0;
		int j = 0;
		
		EntPersonaje.actualizarPersonaje(acceso, paquetePersonaje, paquetePersonaje.getId());
			EntMochila mochila = EntMochila.dameMochila(acceso, paquetePersonaje.getId());
			paquetePersonaje.eliminarItems();
			LinkedList<Integer> listadoItems = EntMochila.dameListadoItems(mochila);
			while(j < 9) {
				if(listadoItems.get(i) != -1) {
					EntItem item = EntItem.dameItem(acceso, listadoItems.get(i));
					paquetePersonaje.anadirItem(
							item.getIdItem(), item.getNombre(), item.getWereable(),item.getBonusSalud(), 
							item.getBonusEnergia(), item.getBonusFuerza(),item.getBonusDestreza(),
							item.getBonusInteligencia(), item.getFoto(), item.getFotoEquipado());
				}
				i ++;
				j ++;
			}
	}

	public PaquetePersonaje getPersonaje(PaqueteUsuario user) throws IOException {
		int i = 0;
		int j = 0;

		EntPersonaje personaje = EntPersonaje.damePersonaje(
				acceso, EntRegistro.dameUsuario(acceso, user).getIdPersonaje());
		EntMochila mochila = EntMochila.dameMochila(acceso, personaje.getIdPersonaje());
		LinkedList<Integer> listadoItems = EntMochila.dameListadoItems(mochila);
		
		PaquetePersonaje pa = new PaquetePersonaje();
		pa.setId(personaje.getIdPersonaje());
		pa.setRaza(personaje.getRaza());
		pa.setCasta(personaje.getCasta());
		pa.setFuerza(personaje.getFuerza());
		pa.setInteligencia(personaje.getInteligencia());
		pa.setDestreza(personaje.getDestreza());
		pa.setEnergiaTope(personaje.getEnergiaTope());
		pa.setSaludTope(personaje.getSaludTope());
		pa.setNombre(personaje.getNombre());
		pa.setExperiencia(personaje.getExperiencia());
		pa.setNivel(personaje.getNivel());
		
		while(j < 9) {
			if(listadoItems.get(i) != -1) {
				EntItem item = EntItem.dameItem(acceso,listadoItems.get(i) );
				pa.anadirItem(
					item.getIdItem(), item.getNombre(), item.getWereable(), 
					item.getBonusSalud(), item.getBonusEnergia(), 
					item.getBonusFuerza(), item.getBonusDestreza(), 
					item.getBonusInteligencia(), item.getFoto(), 
					item.getFotoEquipado());
			}
			
			i ++;
			j ++;
		}
		
		return pa;
	}
	
	public PaqueteUsuario getUsuario(String usuario) {
		EntRegistro ent = EntRegistro.dameUsuario(acceso, new PaqueteUsuario(0,usuario,""));
		if(ent == null) {
			Servidor.log.append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			return new PaqueteUsuario();
		}
		PaqueteUsuario paqueteRetorno = new PaqueteUsuario(ent.getIdPersonaje(), ent.getUsuario(), ent.getPassword());
		return paqueteRetorno;
	}

	public void actualizarInventario(PaquetePersonaje paquetePersonaje) {
		EntMochila mochila = EntMochila.cargarMochila(paquetePersonaje);
		EntMochila.actualizarMochila(acceso, mochila);
	}		
		
	public void actualizarInventario(int idPersonaje) {
		PaquetePersonaje paquetePersonaje = Servidor.getPersonajesConectados().get(idPersonaje);
		LinkedList<Integer> listadoItems = new LinkedList<>();
		
		for(int i = 0; i < paquetePersonaje.getCantItems();  i++) 
			listadoItems.addLast(paquetePersonaje.getItemID(i));
		if(paquetePersonaje.getCantItems() < 9) {
			int itemGanado = new Random().nextInt(29);
			itemGanado += 1;
			listadoItems.add(paquetePersonaje.getCantItems() + 1, itemGanado);
			for(int  i = paquetePersonaje.getCantItems() + 2; i < 21; i++) 
				listadoItems.addLast(-1);
		}else 
			for(int  i = paquetePersonaje.getCantItems() + 1; i < 21; i++) 
				listadoItems.addLast(-1);
		
			
		EntMochila mochila = EntMochila.cargarListadoEnMochila(paquetePersonaje, listadoItems);
		EntMochila.actualizarMochila(acceso, mochila);
	}

	public void actualizarPersonajeSubioNivel(PaquetePersonaje paquetePersonaje) {
		EntPersonaje.actualizarPersonaje(acceso, paquetePersonaje, paquetePersonaje.getId());
			Servidor.log.append("El personaje " + paquetePersonaje.getNombre() + 
					" se ha actualizado con éxito."  + System.lineSeparator());
	}
}
