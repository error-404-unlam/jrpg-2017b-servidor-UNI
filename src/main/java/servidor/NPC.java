package servidor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import comandos.Atacar;
import comandos.FinalizarBatalla;
import dominio.Asesino;
import dominio.Casta;
import dominio.Elfo;
import dominio.Guerrero;
import dominio.Hechicero;
import dominio.Humano;
import dominio.Orco;
import dominio.Personaje;
import estados.Estado;
import mensajeria.PaqueteAtacar;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteFinalizarBatalla;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import mundo.Tile;

public class NPC {
	private int id;
	private int dificultad; // Define qué metodo utiliza para pelear.
	private int movimiento; // Define qué metodo utiliza para moverse.
	private int persistencia; // Define qué hace luego de morir.
	private PaquetePersonaje pp;
	private PaqueteMovimiento pm;
	private PaqueteBatalla pb;
	private PaqueteAtacar pa;
	private PaqueteFinalizarBatalla pfb;
	String path;

	public NPC(String path) throws IOException { // Crea un NPC a partir de la dirección de un archivo. 
		final NPC npc = this;
		
		// Abre el archivo.
		Scanner npcFile = new Scanner(new File(path));
		npc.path = path;
		
		// Crea el PaqueteMovimiento y el PaquetePersonaje.
		PaqueteMovimiento pm = new PaqueteMovimiento();
		PaquetePersonaje pp = new PaquetePersonaje();
		pm.setIp("localhost");
		pp.setIp("localhost");

		// Carga la dificultad, el movimiento y la persistencia.
		npc.setId(npcFile.nextInt());
		npcFile.nextLine();
		npc.setDificultad(npcFile.nextInt());
		npcFile.nextLine();
		npc.setMovimiento(npcFile.nextInt());
		npcFile.nextLine();
		npc.setPersistencia(npcFile.nextInt());
		npcFile.nextLine();
		npcFile.nextLine();

		// Carga el PaqueteMovimiento.
		pm.setIdPersonaje(npc.getId());
		int jMin = npcFile.nextInt();
		npcFile.nextLine();
		int jMax = npcFile.nextInt();
		npcFile.nextLine();
		int iMin = npcFile.nextInt();
		npcFile.nextLine();
		int iMax = npcFile.nextInt();
		npcFile.nextLine();
		int j = ThreadLocalRandom.current().nextInt(jMin, jMax + 1);
		int i = ThreadLocalRandom.current().nextInt(iMin, iMax + 1);
		pm.setPosX(baldosasACoordenadas(j, i)[0]);
		pm.setPosY(baldosasACoordenadas(j, i)[1]);
		pm.setDireccion(npcFile.nextInt());
		npcFile.nextLine();
		pm.setFrame(npcFile.nextInt());
		npcFile.nextLine();
		npcFile.nextLine();

		// Carga el PaquetePersonaje.
		pp.setId(npc.getId());
		pp.setMapa(npcFile.nextInt());
		npcFile.nextLine();
		pp.setEstado(npcFile.nextInt());
		npcFile.nextLine();
		pp.setCasta(npcFile.nextLine());
		pp.setRaza(npcFile.nextLine());
		pp.setNombre(npcFile.nextLine());
		pp.setSaludTope(npcFile.nextInt());
		npcFile.nextLine();
		pp.setEnergiaTope(npcFile.nextInt());
		npcFile.nextLine();
		pp.setFuerza(npcFile.nextInt());
		npcFile.nextLine();
		pp.setDestreza(npcFile.nextInt());
		npcFile.nextLine();
		pp.setInteligencia(npcFile.nextInt());
		npcFile.nextLine();
		pp.setNivel(npcFile.nextInt());
		npcFile.nextLine();
		pp.setExperiencia(npcFile.nextInt());
		pp.eliminarItems();
		
		// Carga el PaqueteMovimiento y el PaquetePersonaje al NPC.
		npc.setPm(pm);
		npc.setPp(pp);
		
		// Carga el NPC a las listas del servidor.
		Servidor.getUbicacionPersonajes().put(npc.getId(), npc.getPm());
		Servidor.getPersonajesConectados().put(npc.getId(), npc.getPp());
		Servidor.getNPCsCargados().put(npc.getId(), npc);
		Servidor.log.append("NPC " + npc.getId() + " creado en coordenadas (" + j + ", " + i + ") del mapa " + npc.getPp().getMapa() + "." + System.lineSeparator());
		npcFile.close();
		
		// Crea el thread para controlar al NPC.
		Runnable mNPC = new Runnable() {
			public void run() {
				if (Servidor.getPersonajesConectados().get(npc.getId()).getEstado() == Estado.estadoJuego) {
					npc.mover();
				} else if (Servidor.getPersonajesConectados().get(npc.getId()).getEstado() == Estado.estadoBatalla) {
					npc.batallar();
				}
			}
		};
		ScheduledExecutorService executor2 = Executors.newScheduledThreadPool(1);
		executor2.scheduleAtFixedRate(mNPC, 0, 500, TimeUnit.MILLISECONDS); // El código de mNPC se ejecuta cada 0.5 segundos.
	}
	
	public static float[] baldosasACoordenadas(int j, int i) {
		float[] vec = new float[2];

		vec[0] = (j - i) * (Tile.ANCHO / 2) + 2; // El +2 es un parche para que quede centrado en la baldosa.
		vec[1] = (j + i) * (Tile.ALTO / 2) + 4; // El +4 es un parche para que quede centrado en la baldosa.

		return vec;
	}

	public static int[] coordenadasABaldosas(float x, float y) {
		int[] vec = new int[2];

		vec[0] = (int) x / (Tile.ANCHO / 2); // Esto da como resultado j - i
		vec[1] = (int) y / (Tile.ALTO / 2); // Esto da como resultado j + i

		vec[0] = (vec[0] + vec[1]) / 2;
		vec[1] = vec[1] - vec[0];

		// Funciona sin -2 y -4 porque existe un rango de valores de válidos para cada baldosa.

		return vec;
	}
	
	public PaquetePersonaje getPp() {
		return pp;
	}

	public void setPp(PaquetePersonaje pp) {
		this.pp = pp;
	}

	public PaqueteMovimiento getPm() {
		return pm;
	}

	public void setPm(PaqueteMovimiento pm) {
		this.pm = pm;
	}

	public PaqueteBatalla getPb() {
		return pb;
	}

	public void setPb(PaqueteBatalla pb) {
		this.pb = pb;
	}

	public PaqueteAtacar getPa() {
		return pa;
	}

	public void setPa(PaqueteAtacar pa) {
		this.pa = pa;

		if (pa == null) {
			return;
		}

		if (pa.getId() == this.getPp().getId()) {
			this.enviarAtaque();
			this.getPb().setMiTurno(false);
			return;
		}

		this.getPb().setMiTurno(true);
	}

	public PaqueteFinalizarBatalla getPfb() {
		return pfb;
	}

	public void setPfb(PaqueteFinalizarBatalla pfb) {
		this.pfb = pfb;

		if (pfb == null) {
			return;
		}

		if (pfb.getGanadorBatalla() == this.getPp().getId()) {
			this.ganarBatalla();
			return;
		}

		this.morir();
	}

	public void enviarAtaque() {
		Atacar at = new Atacar();
		at.ejecutarDesdeNPC(this.pa);
		this.setPa(null);
	}

	public void ganarBatalla() {
		FinalizarBatalla fb = new FinalizarBatalla();
		fb.ejecutarDesdeNPC(this.pfb);

		this.setPa(null);
		this.setPb(null);
		this.setPfb(null);
	}

	public void morir() {
		if (this.persistencia == 1) {
			morirTipo1(); // Desaparece.
		}
		if (this.persistencia == 2) {
			morirTipo2(); // "Revive" en otro rectángulo específicado en el archivo.
		}
		if (this.persistencia == 3) {
			morirTipo3(); // "Revive" en la otra mitad del mapa.
		}
	}

	private void morirTipo1() {
		Servidor.getPersonajesConectados().remove(this.getId());
		Servidor.getUbicacionPersonajes().remove(this.getId());
		Servidor.getNPCsCargados().remove(this.getId());
	}
	
	private void morirTipo2() {
		this.setPa(null);
		this.setPb(null);
		this.setPfb(null);

		float x = this.pm.getPosX();
		float y = this.pm.getPosY();
		int j = coordenadasABaldosas(x, y)[0];
		int i = coordenadasABaldosas(x, y)[1];

		// Se mueve según especificado en archivo de texto.
		Scanner npcFile = null;
		try {
			npcFile = new Scanner(new File(this.path));
		} catch (FileNotFoundException e) { // Si falla al abrir al archivo, se desconecta el NPC.
			Servidor.getPersonajesConectados().remove(this.getId());
			Servidor.getUbicacionPersonajes().remove(this.getId());
			Servidor.getNPCsCargados().remove(this.getId());
			return;
		}		
		for (int k = 0; k < 5; k++) {
			npcFile.nextLine();
		}
		int jMin = npcFile.nextInt();
		npcFile.nextLine();
		int jMax = npcFile.nextInt();
		npcFile.nextLine();
		int iMin = npcFile.nextInt();
		npcFile.nextLine();
		int iMax = npcFile.nextInt();
		for (int k = 0; k < 17; k++) {
			npcFile.nextLine();
		}
		int jMinB = npcFile.nextInt();
		npcFile.nextLine();
		int jMaxB = npcFile.nextInt();
		npcFile.nextLine();
		int iMinB = npcFile.nextInt();
		npcFile.nextLine();
		int iMaxB = npcFile.nextInt();
		if (j >= jMin && j <= jMax && i >= iMin && i <= iMax) { // Si está en el primer rectángulo, se mueve al segundo
			int jNuevo = ThreadLocalRandom.current().nextInt(jMinB, jMaxB + 1);
			int iNuevo = ThreadLocalRandom.current().nextInt(iMinB, iMaxB + 1);
			j = jNuevo;
			i = iNuevo;
		} else { // Si está en el segundo rectángulo, se mueve al primero
			int jNuevo = ThreadLocalRandom.current().nextInt(jMin, jMax + 1);
			int iNuevo = ThreadLocalRandom.current().nextInt(iMin, iMax + 1);
			j = jNuevo;
			i = iNuevo;
		}
		
		Servidor.log.append("NPC " + this.id + " ha revivido en las coordenadas (" + j + ", " + i + ") del mapa " + this.pp.getMapa() + "." + System.lineSeparator());
		pm.setPosX(baldosasACoordenadas(j, i)[0]);
		pm.setPosY(baldosasACoordenadas(j, i)[1]);
		npcFile.close();
		Servidor.getNPCsCargados().get(this.id).getPp().setEstado(Estado.estadoJuego);
	}

	private void morirTipo3() {
		this.setPa(null);
		this.setPb(null);
		this.setPfb(null);

		float x = this.pm.getPosX();
		float y = this.pm.getPosY();
		int j = coordenadasABaldosas(x, y)[0];
		int i = coordenadasABaldosas(x, y)[1];

		// Se mueve a la otra mitad del mapa.
		if (j < Tile.ALTO / 2) {
			j += Tile.ALTO / 2;
		} else {
			j -= Tile.ALTO / 2;
		}

		Servidor.log.append("NPC " + this.id + " ha revivido en las coordenadas (" + j + ", " + i + ") del mapa " + this.pp.getMapa() + "." + System.lineSeparator());
		pm.setPosX(baldosasACoordenadas(j, i)[0]);
		pm.setPosY(baldosasACoordenadas(j, i)[1]);
		Servidor.getNPCsCargados().get(this.id).getPp().setEstado(Estado.estadoJuego);
	}
	
	public void mover() {
		if (this.movimiento == 1) {
			this.moverTipo1();
		}
	}
	
	public void batallar() {
		if (this.dificultad == 1) {
			this.batallarTipo1();
		}
	}
	
	private void moverTipo1() {
		if (Servidor.getUbicacionPersonajes().get(this.getId()).getDireccion() == 1) {
			Servidor.getUbicacionPersonajes().get(this.getId()).setDireccion(5);
		} else {
			Servidor.getUbicacionPersonajes().get(this.getId()).setDireccion(1);
		}
	}

	private void batallarTipo1() {
		NPC npc = this;
		PaquetePersonaje paqueteNPC = (PaquetePersonaje) npc.getPp().clone();
		PaquetePersonaje paqueteEnemigo = (PaquetePersonaje) Servidor.getPersonajesConectados().get(npc.getPb().getIdEnemigo()).clone();
		Personaje personaje = crearPersonajes(paqueteNPC, paqueteEnemigo)[0];
		Personaje enemigo = crearPersonajes(paqueteNPC, paqueteEnemigo)[1];

		while (npc.getPp().getEstado() == Estado.estadoBatalla && npc.getPb() != null) { // Mientras dure la batalla

			try {
				Thread.sleep(500); // Espera por 0.5 segundos mientras el enemigo elige el ataque
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (npc.getPp().getEstado() == Estado.estadoBatalla && npc.getPb() != null && npc.getPb().isMiTurno()) { // Si es mi turno
				// Calcular daño recibido
				int daño = personaje.getSalud() - npc.getPa().getNuevaSaludEnemigo();
				personaje.reducirSalud(daño); // Actualiza salud del NPC.
				// Calcular agotamiento del enemigo
				int agotamiento = enemigo.getEnergia() - npc.getPa().getNuevaEnergiaPersonaje();
				enemigo.reducirEnergia(agotamiento); // Actualiza energía del enemigo.
				// Calcular ataque
				if (!personaje.habilidadCasta1(enemigo)) { // Actualiza salud del enemigo y energía del NPC.
					personaje.serEnergizado(10); // Si se queda sin energía, pide ser energizado
				}

				// Intentar atacar
				if (enemigo.getSalud() > 0) {
					PaqueteAtacar pa = new PaqueteAtacar(personaje.getIdPersonaje(), enemigo.getIdPersonaje(), personaje.getSalud(), personaje.getEnergia(), enemigo.getSalud(), enemigo.getEnergia(), personaje.getDefensa(), enemigo.getDefensa(), personaje.getCasta().getProbabilidadEvitarDanio(), enemigo.getCasta().getProbabilidadEvitarDanio());
					npc.setPa(pa);
				} else {
					PaqueteFinalizarBatalla pfb = new PaqueteFinalizarBatalla();
					pfb.setId(npc.getId());
					pfb.setIdEnemigo(npc.getPb().getIdEnemigo());
					pfb.setGanadorBatalla(npc.getId());
					npc.setPfb(pfb);
				}
			}
		}
	}

	private static Personaje[] crearPersonajes(PaquetePersonaje paquetePersonaje, PaquetePersonaje paqueteEnemigo) {
		Personaje personaje = null;
		Personaje enemigo = null;

		String nombre = paquetePersonaje.getNombre();
		int salud = paquetePersonaje.getSaludTope();
		int energia = paquetePersonaje.getEnergiaTope();
		int fuerza = paquetePersonaje.getFuerza();
		int destreza = paquetePersonaje.getDestreza();
		int inteligencia = paquetePersonaje.getInteligencia();
		int experiencia = paquetePersonaje.getExperiencia();
		int nivel = paquetePersonaje.getNivel();
		int id = paquetePersonaje.getId();

		Casta casta = null;
		try {
			casta = (Casta) Class.forName("dominio" + "." + paquetePersonaje.getCasta()).newInstance();
			personaje = (Personaje) Class.forName("dominio" + "." + paquetePersonaje.getRaza()).getConstructor(String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Casta.class, Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(nombre, salud, energia, fuerza, destreza, inteligencia, casta, experiencia, nivel, id);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			JOptionPane.showMessageDialog(null, "Error al crear la batalla");
		}

		nombre = paqueteEnemigo.getNombre();
		salud = paqueteEnemigo.getSaludTope();
		energia = paqueteEnemigo.getEnergiaTope();
		fuerza = paqueteEnemigo.getFuerza();
		destreza = paqueteEnemigo.getDestreza();
		inteligencia = paqueteEnemigo.getInteligencia();
		experiencia = paqueteEnemigo.getExperiencia();
		nivel = paqueteEnemigo.getNivel();
		id = paqueteEnemigo.getId();

		casta = null;
		if (paqueteEnemigo.getCasta().equals("Guerrero")) {
			casta = new Guerrero();
		} else if (paqueteEnemigo.getCasta().equals("Hechicero")) {
			casta = new Hechicero();
		} else if (paqueteEnemigo.getCasta().equals("Asesino")) {
			casta = new Asesino();
		}

		if (paqueteEnemigo.getRaza().equals("Humano")) {
			enemigo = new Humano(nombre, salud, energia, fuerza, destreza, inteligencia, casta, experiencia, nivel, id);
		} else if (paqueteEnemigo.getRaza().equals("Orco")) {
			enemigo = new Orco(nombre, salud, energia, fuerza, destreza, inteligencia, casta, experiencia, nivel, id);
		} else if (paqueteEnemigo.getRaza().equals("Elfo")) {
			enemigo = new Elfo(nombre, salud, energia, fuerza, destreza, inteligencia, casta, experiencia, nivel, id);
		}

		Personaje[] devolver = new Personaje[2];
		devolver[0] = personaje;
		devolver[1] = enemigo;
		return devolver;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDificultad() {
		return dificultad;
	}

	public void setDificultad(int dificultad) {
		this.dificultad = dificultad;
	}

	public int getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(int movimiento) {
		this.movimiento = movimiento;
	}

	public int getPersistencia() {
		return persistencia;
	}

	public void setPersistencia(int persistencia) {
		this.persistencia = persistencia;
	}
}
