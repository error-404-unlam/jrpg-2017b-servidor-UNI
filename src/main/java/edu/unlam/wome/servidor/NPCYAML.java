package edu.unlam.wome.servidor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import edu.unlam.wome.dominio.main.Asesino;
import edu.unlam.wome.dominio.main.Casta;
import edu.unlam.wome.dominio.main.Elfo;
import edu.unlam.wome.dominio.main.Guerrero;
import edu.unlam.wome.dominio.main.Hechicero;
import edu.unlam.wome.dominio.main.Humano;
import edu.unlam.wome.dominio.main.Orco;
import edu.unlam.wome.dominio.main.Personaje;
import edu.unlam.wome.comandos.Atacar;
import edu.unlam.wome.comandos.FinalizarBatalla;
import edu.unlam.wome.estados.Estado;
import edu.unlam.wome.mensajeria.PaqueteAtacar;
import edu.unlam.wome.mensajeria.PaqueteBatalla;
import edu.unlam.wome.mensajeria.PaqueteFinalizarBatalla;
import edu.unlam.wome.mensajeria.PaqueteMovimiento;
import edu.unlam.wome.mensajeria.PaquetePersonaje;
import edu.unlam.wome.mundo.Tile;

/**
 * Clase NPC.
 */
public class NPCYAML {
    private static final long PERIODONPC = 500;
	private static final int REVIVE_EN_OTRA_MITAD_DEL_MAPA = 3;
	private static final int REVIVE_EN_OTRO_RECTANGULO = 2;
	private static final int DESAPARECE = 1;
	private static final long ESPERA = 500;
	private static final int ENERGIZADO = 10;
	private static final int CENTRADOX = 0;
	private static final int CENTRADOY = 0;
	private static final int DIRECCION1 = 1;
	private static final int DIRECCION5 = 5;
	private int id;
    private int dificultad; // Define qué metodo utiliza para pelear.
    private int movimiento; // Define qué metodo utiliza para moverse.
    private int persistencia; // Define qué hace luego de morir.
    private PaquetePersonaje pp;
    private PaqueteMovimiento pm;
    private PaqueteBatalla pb;
    private PaqueteAtacar pa;
    private PaqueteFinalizarBatalla pfb;
    private Map<String, String> map;

    /**
     * Constructor
     *
     * @param map ruta del archivo
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public NPCYAML(final Map<String, String> m) throws IOException { // Crea un NPC a partir
                                                        // de la dirección de un
                                                        // archivo.
        final NPCYAML npc = this;

        // Asigna el mapa.
        npc.map = m;

        // Crea el PaqueteMovimiento y el PaquetePersonaje.
        PaqueteMovimiento pmov = new PaqueteMovimiento();
        PaquetePersonaje ppaq = new PaquetePersonaje();
        pmov.setIp("localhost");
        ppaq.setIp("localhost");

        // Carga la dificultad, el movimiento y la persistencia.
        npc.setId(Integer.parseInt(map.get("id")));
        npc.setDificultad(Integer.parseInt(map.get("dificultad")));
        npc.setMovimiento(Integer.parseInt(map.get("movimiento")));
        npc.setPersistencia(Integer.parseInt(map.get("persistencia")));

        // Carga el PaqueteMovimiento.
        pmov.setIdPersonaje(Integer.parseInt(map.get("id")));
        int jMin = Integer.parseInt(map.get("jmin"));
        int jMax = Integer.parseInt(map.get("jmax"));
        int iMin = Integer.parseInt(map.get("imin"));
        int iMax = Integer.parseInt(map.get("imax"));
        int j = ThreadLocalRandom.current().nextInt(jMin, jMax + 1);
        int i = ThreadLocalRandom.current().nextInt(iMin, iMax + 1);
        pmov.setPosX(baldosasACoordenadas(j, i)[0]);
        pmov.setPosY(baldosasACoordenadas(j, i)[1]);
        pmov.setDireccion(Integer.parseInt(map.get("direccion")));
        pmov.setFrame(Integer.parseInt(map.get("frame")));

        // Carga el PaquetePersonaje.
        ppaq.setId(Integer.parseInt(map.get("id")));
        ppaq.setMapa(Integer.parseInt(map.get("mapa")));
        ppaq.setEstado(Integer.parseInt(map.get("estado")));
        ppaq.setCasta((String) map.get("casta"));
        ppaq.setRaza((String) map.get("raza"));
        ppaq.setNombre((String) map.get("nombre"));
        ppaq.setSaludTope(Integer.parseInt(map.get("salud")));
        ppaq.setEnergiaTope(Integer.parseInt(map.get("energia")));
        ppaq.setFuerza(Integer.parseInt(map.get("fuerza")));
        ppaq.setDestreza(Integer.parseInt(map.get("destreza")));
        ppaq.setInteligencia(Integer.parseInt(map.get("inteligencia")));
        ppaq.setNivel(Integer.parseInt(map.get("nivel")));
        ppaq.setExperiencia(Integer.parseInt(map.get("experiencia")));
        ppaq.eliminarItems();

        // Carga el PaqueteMovimiento y el PaquetePersonaje al NPC.
        npc.setPm(pmov);
        npc.setPp(ppaq);

        // Carga el NPC a las listas del servidor.
        Servidor.getUbicacionPersonajes().put(npc.getId(), npc.getPm());
        Servidor.getPersonajesConectados().put(npc.getId(), npc.getPp());
        Servidor.getNPCsCargadosYAML().put(npc.getId(), npc);
        Servidor.getLog().append("NPC " + npc.getId() + " creado en coordenadas (" + j + ", " + i + ") del mapa "
                + npc.getPp().getMapa() + "." + System.lineSeparator());

        // Crea el thread para controlar al NPC.
        Runnable mNPC = new Runnable() {
            public void run() {
                if (Servidor.getPersonajesConectados().get(npc.getId()).getEstado() == Estado.getEstadoJuego()) {
                    npc.mover();
                } else if (Servidor.getPersonajesConectados().get(npc.getId()).
                		getEstado() == Estado.getEstadoBatalla()) {
                    npc.batallar();
                }
            }
        };
        ScheduledExecutorService executor2 = Executors.newScheduledThreadPool(1);
        executor2.scheduleAtFixedRate(mNPC, 0, PERIODONPC, TimeUnit.MILLISECONDS);
       //El codigo del NPC se ejecuta cada 0.5 segundos
    }

    /**
     * Baldosas A coordenadas.
     *
     * @param j posicion j
     * @param i posicion i
     * @return vector de float
     */
    public static float[] baldosasACoordenadas(final int j, final int i) {
        float[] vec = new float[2];

        vec[0] = (j - i) * (Tile.ANCHO / 2) + CENTRADOX; // El +2 es un parche para que
                                                    // quede centrado en la
                                                    // baldosa.
        vec[1] = (j + i) * (Tile.ALTO / 2) + CENTRADOY; // El +4 es un parche para que
                                                // quede centrado en la baldosa.

        return vec;
    }

    /**
     * Coordenadas A baldosas.
     *
     * @param x the x
     * @param y the y
     * @return vector de enteros
     */
    public static int[] coordenadasABaldosas(final float x, final float y) {
        int[] vec = new int[2];

        vec[0] = (int) x / (Tile.ANCHO / 2); // Esto da como resultado j - i
        vec[1] = (int) y / (Tile.ALTO / 2); // Esto da como resultado j + i

        vec[0] = (vec[0] + vec[1]) / 2;
        vec[1] = vec[1] - vec[0];

        // Funciona sin -2 y -4 porque existe un rango de valores de válidos
        // para cada baldosa.

        return vec;
    }

    /**
     * Obtiene el Paquete personaje.
     *
     * @return pp Objeto de la clase PaquetePersonaje
     */
    public PaquetePersonaje getPp() {
        return pp;
    }

    /**
     * Setea el paquete personaje.
     *
     * @param ppaq Objeto de la clase PaquetePersonaje
     */
    public void setPp(final PaquetePersonaje ppaq) {
        this.pp = ppaq;
    }

    /**
     * Obtiene el paquete movimiento.
     *
     * @return pm Objeto de la clase PaqueteMovimientos
     */
    public PaqueteMovimiento getPm() {
        return pm;
    }

    /**
     * Setea el paquete movimiento.
     *
     * @param pmov Objeto de la clase PaqueteMovimiento
     */
    public void setPm(final PaqueteMovimiento pmov) {
        this.pm = pmov;
    }

    /**
     * Obtiene el paquete batalla.
     *
     * @return pb Objeto de la clase PaqueteBatalla
     */
    public PaqueteBatalla getPb() {
        return pb;
    }

    /**
     * Setea el paquete batalla.
     *
     * @param pbat Objeto de la clase PaqueteBatalla
     */
    public void setPb(final PaqueteBatalla pbat) {
        this.pb = pbat;
    }

    /**
     * Obtiene el paquete atacar.
     *
     * @return pa Objeto de la clase PaqueteAtacar
     */
    public PaqueteAtacar getPa() {
        return pa;
    }

    /**
     * Setea el paquete atacar.
     *
     * @param pat Objeto de la clase PaqueteAtacar
     */
    public void setPa(final PaqueteAtacar pat) {
        this.pa = pat;

        if (pat == null) {
            return;
        }

        if (pat.getId() == this.getPp().getId()) {
            this.enviarAtaque();
            this.getPb().setMiTurno(false);
            return;
        }

        this.getPb().setMiTurno(true);
    }

    /**
     * Obtiene el paquete finalizar batalla.
     *
     * @return pfb Objeto de la clase PaqueteFinalizarBatalla
     */
    public PaqueteFinalizarBatalla getPfb() {
        return pfb;
    }

    /**
     * Setea el paquete finalizar batalla.
     *
     * @param pfbat Obejto de la clase PaqueteFinalizarBatalla.
     */
    public void setPfb(final PaqueteFinalizarBatalla pfbat) {
        this.pfb = pfbat;

        if (pfbat == null) {
            return;
        }

        if (pfbat.getGanadorBatalla() == this.getPp().getId()) {
            this.ganarBatalla();
            return;
        }

        this.morir();
    }

    /**
     * Enviar ataque.
     */
    public void enviarAtaque() {
        Atacar at = new Atacar();
        at.ejecutarDesdeNPC(this.pa);
        this.setPa(null);
    }

    /**
     * Ganar batalla.
     */
    public void ganarBatalla() {
        FinalizarBatalla fb = new FinalizarBatalla();
        fb.ejecutarDesdeNPC(this.pfb);

        this.setPa(null);
        this.setPb(null);
        this.setPfb(null);
    }

    /**
     * Metodo Morir.
     */
    public void morir() {
        if (this.persistencia == DESAPARECE) {
            morirTipo1(); // Desaparece.
        }
        if (this.persistencia == REVIVE_EN_OTRO_RECTANGULO) {
            morirTipo2(); // "Revive" en otro rectángulo específicado en el
                            // archivo.
        }
        if (this.persistencia == REVIVE_EN_OTRA_MITAD_DEL_MAPA) {
            morirTipo3(); // "Revive" en la otra mitad del mapa.
        }
    }

    /**
     * Morir tipo 1.
     * Desaparece el NPC
     */
    private void morirTipo1() {
        Servidor.getPersonajesConectados().remove(this.getId());
        Servidor.getUbicacionPersonajes().remove(this.getId());
        Servidor.getNPCsCargados().remove(this.getId());
    }

    /**
     * Morir tipo 2.
     * NPC revive en otro rectangulo del mapa
     * especificado en el archivo
     */
    private void morirTipo2() {
        this.setPa(null);
        this.setPb(null);
        this.setPfb(null);

        float x = this.pm.getPosX();
        float y = this.pm.getPosY();
        int j = coordenadasABaldosas(x, y)[0];
        int i = coordenadasABaldosas(x, y)[1];

        // Se mueve según especificado en el mapa.
        int jMin = Integer.parseInt(map.get("jmin"));
        int jMax = Integer.parseInt(map.get("jmax"));
        int iMin = Integer.parseInt(map.get("imin"));
        int iMax = Integer.parseInt(map.get("imax"));
        int jMinB = Integer.parseInt(map.get("jminb"));
        int jMaxB = Integer.parseInt(map.get("jmaxb"));
        int iMinB = Integer.parseInt(map.get("iminb"));
        int iMaxB = Integer.parseInt(map.get("imaxb"));
        if (j >= jMin && j <= jMax && i >= iMin && i <= iMax) {
        //Si esta en el primer rectangulo, se mueve al segundo
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

        Servidor.getLog().append("NPC " + this.id + " ha revivido en las coordenadas (" + j + ", " + i + ") del mapa "
                + this.pp.getMapa() + "." + System.lineSeparator());
        pm.setPosX(baldosasACoordenadas(j, i)[0]);
        pm.setPosY(baldosasACoordenadas(j, i)[1]);
        Servidor.getNPCsCargados().get(this.id).getPp().setEstado(Estado.getEstadoJuego());
    }

    /**
     * Morir tipo 3.
     * NPC revive en la otra mitad del mapa
     */
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

        Servidor.getLog().append("NPC " + this.id + " ha revivido en las coordenadas (" + j + ", " + i + ") del mapa "
                + this.pp.getMapa() + "." + System.lineSeparator());
        pm.setPosX(baldosasACoordenadas(j, i)[0]);
        pm.setPosY(baldosasACoordenadas(j, i)[1]);
        Servidor.getNPCsCargados().get(this.id).getPp().setEstado(Estado.getEstadoJuego());
    }

    /**
     * Mover.
     */
    public void mover() {
        if (this.movimiento == 1) {
            this.moverTipo1();
        }
    }

    /**
     * Batallar.
     */
    public void batallar() {
        if (this.dificultad == 1) {
            this.batallarTipo1();
        }
    }

    /**
     * Mover tipo 1.
     */
    private void moverTipo1() {
        if (Servidor.getUbicacionPersonajes().get(this.getId()).getDireccion() == DIRECCION1) {
            Servidor.getUbicacionPersonajes().get(this.getId()).setDireccion(DIRECCION5);
        } else {
            Servidor.getUbicacionPersonajes().get(this.getId()).setDireccion(DIRECCION1);
        }
    }

    /**
     * Batallar tipo 1.
     */
    private void batallarTipo1() {
        NPCYAML npc = this;
        PaquetePersonaje paqueteNPC = (PaquetePersonaje) npc.getPp().clone();
        PaquetePersonaje paqueteEnemigo = (PaquetePersonaje) Servidor.getPersonajesConectados()
                .get(npc.getPb().getIdEnemigo()).clone();
        Personaje personaje = crearPersonajes(paqueteNPC, paqueteEnemigo)[0];
        Personaje enemigo = crearPersonajes(paqueteNPC, paqueteEnemigo)[1];

        while (npc.getPp().getEstado() == Estado.getEstadoBatalla() && npc.getPb() != null) { // Mientras
                                                                                            // dure
                                                                                            // la
                                                                                            // batalla

            try {
                Thread.sleep(ESPERA); // Espera por 0.5 segundos mientras el
                                    // enemigo elige el ataque
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (npc.getPp().getEstado() == Estado.getEstadoBatalla()
            		&& npc.getPb() != null && npc.getPb().isMiTurno()) {
                // Si es mi turno, calcular daño recibido
                int danio = personaje.getSalud() - npc.getPa().getNuevaSaludEnemigo();
                personaje.reducirSalud(danio); // Actualiza salud del NPC.
                // Calcular agotamiento del enemigo
                int agotamiento = enemigo.getEnergia() - npc.getPa().getNuevaEnergiaPersonaje();
                enemigo.reducirEnergia(agotamiento); // Actualiza energía del
                                                        // enemigo.
                // Calcular ataque
                if (!personaje.habilidadCasta1(enemigo)) { // Actualiza salud
                                                            // del enemigo y
                                                            // energía del NPC.
                    personaje.serEnergizado(ENERGIZADO); // Si se queda sin energía,
                                                    // pide ser energizado
                }

                // Intentar atacar
                if (enemigo.getSalud() > 0) {
                    PaqueteAtacar pat = new PaqueteAtacar(personaje.getIdPersonaje(), enemigo.getIdPersonaje(),
                            personaje.getSalud(), personaje.getEnergia(), enemigo.getSalud(), enemigo.getEnergia(),
                            personaje.getDefensa(), enemigo.getDefensa(),
                            personaje.getCasta().getProbabilidadEvitarDanio(),
                            enemigo.getCasta().getProbabilidadEvitarDanio());
                    npc.setPa(pat);
                } else {
                    PaqueteFinalizarBatalla pfbat = new PaqueteFinalizarBatalla();
                    pfbat.setId(npc.getId());
                    pfbat.setIdEnemigo(npc.getPb().getIdEnemigo());
                    pfbat.setGanadorBatalla(npc.getId());
                    npc.setPfb(pfbat);
                }
            }
        }
    }

    /**
     * Crear personajes.
     *
     * @param paquetePersonaje Objeto de la clase PaquetePersonaje
     * @param paqueteEnemigo Objeto de la clase PaquetePersonaje
     * @return vector de personajes
     */
    private static Personaje[] crearPersonajes(final PaquetePersonaje paquetePersonaje,
    		final PaquetePersonaje paqueteEnemigo) {
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
            casta = (Casta) Class.forName("edu.unlam.wome.dominio.main" + "." + paquetePersonaje.getCasta()).newInstance();
            personaje = (Personaje) Class.forName("edu.unlam.wome.dominio.main" + "." + paquetePersonaje.getRaza())
                    .getConstructor(String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE,
                            Casta.class, Integer.TYPE, Integer.TYPE, Integer.TYPE)
                    .newInstance(nombre, salud, energia, fuerza, destreza, inteligencia, casta, experiencia, nivel, id);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
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

    /**
     * Obtine id.
     *
     * @return id del npc
     */
    public int getId() {
        return id;
    }

    /**
     * Setea id.
     *
     * @param id del Npc
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Obtiene dificultad.
     *
     * @return dificultad del npc
     */
    public int getDificultad() {
        return dificultad;
    }

    /**
     * Setea dificultad.
     *
     * @param dificultad del npc
     */
    public void setDificultad(final int dificultad) {
        this.dificultad = dificultad;
    }

    /**
     * Obtiene movimiento.
     *
     * @return movimiento del npc
     */
    public int getMovimiento() {
        return movimiento;
    }

    /**
     * Setea the movimiento.
     *
     * @param movimiento del npc
     */
    public void setMovimiento(final int movimiento) {
        this.movimiento = movimiento;
    }

    /**
     * Obtiene persistencia.
     *
     * @return persistencia del npc
     */
    public int getPersistencia() {
        return persistencia;
    }

    /**
     * Setea persistencia.
     *
     * @param persistencia del npc
     */
    public void setPersistencia(final int persistencia) {
        this.persistencia = persistencia;
    }
}
