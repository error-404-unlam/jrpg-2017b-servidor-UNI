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

/**
 * Clase NPC.
 */
public class NPC {
    private static final long PERIDO500 = 500;
	private static final int REVIVE_EN_OTRA_MITAD_DEL_MAPA = 3;
	private static final int REVIVE_EN_OTRO_RECTANGULO = 2;
	private static final int DESAPARECE = 1;
	private static final int INDICE5 = 05;
	private static final int INDICE17 = 17;
	private static final long ESPERA = 500;
	private static final int INDICE1 = 1;
	private static final int ENERGIZADO = 10;
	private static final int CENTRADO2 = 0;
	private static final int CENTRADO4 = 0;
	private int id;
    private int dificultad; // Define qué metodo utiliza para pelear.
    private int movimiento; // Define qué metodo utiliza para moverse.
    private int persistencia; // Define qué hace luego de morir.
    private PaquetePersonaje pp;
    private PaqueteMovimiento pm;
    private PaqueteBatalla pb;
    private PaqueteAtacar pa;
    private PaqueteFinalizarBatalla pfb;
    private String path;

    /**
     * Constructor
     *
     * @param path ruta del archivo
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public NPC(final String path) throws IOException { // Crea un NPC a partir
                                                        // de la dirección de un
                                                        // archivo.
        final NPC npc = this;

        // Abre el archivo.
        Scanner npcFile = new Scanner(new File(path));
        npc.path = path;

        // Crea el PaqueteMovimiento y el PaquetePersonaje.
        PaqueteMovimiento pmov = new PaqueteMovimiento();
        PaquetePersonaje ppaq = new PaquetePersonaje();
        pmov.setIp("localhost");
        ppaq.setIp("localhost");

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
        pmov.setIdPersonaje(npc.getId());
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
        pmov.setPosX(baldosasACoordenadas(j, i)[0]);
        pmov.setPosY(baldosasACoordenadas(j, i)[1]);
        pmov.setDireccion(npcFile.nextInt());
        npcFile.nextLine();
        pmov.setFrame(npcFile.nextInt());
        npcFile.nextLine();
        npcFile.nextLine();

        // Carga el PaquetePersonaje.
        ppaq.setId(npc.getId());
        ppaq.setMapa(npcFile.nextInt());
        npcFile.nextLine();
        ppaq.setEstado(npcFile.nextInt());
        npcFile.nextLine();
        ppaq.setCasta(npcFile.nextLine());
        ppaq.setRaza(npcFile.nextLine());
        ppaq.setNombre(npcFile.nextLine());
        ppaq.setSaludTope(npcFile.nextInt());
        npcFile.nextLine();
        ppaq.setEnergiaTope(npcFile.nextInt());
        npcFile.nextLine();
        ppaq.setFuerza(npcFile.nextInt());
        npcFile.nextLine();
        ppaq.setDestreza(npcFile.nextInt());
        npcFile.nextLine();
        ppaq.setInteligencia(npcFile.nextInt());
        npcFile.nextLine();
        ppaq.setNivel(npcFile.nextInt());
        npcFile.nextLine();
        ppaq.setExperiencia(npcFile.nextInt());
        ppaq.eliminarItems();

        // Carga el PaqueteMovimiento y el PaquetePersonaje al NPC.
        npc.setPm(pmov);
        npc.setPp(ppaq);

        // Carga el NPC a las listas del servidor.
        Servidor.getUbicacionPersonajes().put(npc.getId(), npc.getPm());
        Servidor.getPersonajesConectados().put(npc.getId(), npc.getPp());
        Servidor.getNPCsCargados().put(npc.getId(), npc);
        Servidor.getLog().append("NPC " + npc.getId() + " creado en coordenadas (" + j + ", " + i + ") del mapa "
                + npc.getPp().getMapa() + "." + System.lineSeparator());
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
        executor2.scheduleAtFixedRate(mNPC, 0, PERIDO500, TimeUnit.MILLISECONDS);
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

        vec[0] = (j - i) * (Tile.ANCHO / 2) + CENTRADO2; // El +2 es un parche para que
                                                    // quede centrado en la
                                                    // baldosa.
        vec[1] = (j + i) * (Tile.ALTO / 2) + CENTRADO4; // El +4 es un parche para que
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

        // Se mueve según especificado en archivo de texto.
        Scanner npcFile = null;
        try {
            npcFile = new Scanner(new File(this.path));
        } catch (FileNotFoundException e) { // Si falla al abrir al archivo, se
                                            // desconecta el NPC.
            Servidor.getPersonajesConectados().remove(this.getId());
            Servidor.getUbicacionPersonajes().remove(this.getId());
            Servidor.getNPCsCargados().remove(this.getId());
            return;
        }
        for (int k = 0; k < INDICE5; k++) {
            npcFile.nextLine();
        }
        int jMin = npcFile.nextInt();
        npcFile.nextLine();
        int jMax = npcFile.nextInt();
        npcFile.nextLine();
        int iMin = npcFile.nextInt();
        npcFile.nextLine();
        int iMax = npcFile.nextInt();
        for (int k = 0; k < INDICE17; k++) {
            npcFile.nextLine();
        }
        int jMinB = npcFile.nextInt();
        npcFile.nextLine();
        int jMaxB = npcFile.nextInt();
        npcFile.nextLine();
        int iMinB = npcFile.nextInt();
        npcFile.nextLine();
        int iMaxB = npcFile.nextInt();
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
        npcFile.close();
        Servidor.getNPCsCargados().get(this.id).getPp().setEstado(Estado.estadoJuego);
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
        Servidor.getNPCsCargados().get(this.id).getPp().setEstado(Estado.estadoJuego);
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
        if (Servidor.getUbicacionPersonajes().get(this.getId()).getDireccion() == INDICE1) {
            Servidor.getUbicacionPersonajes().get(this.getId()).setDireccion(INDICE5);
        } else {
            Servidor.getUbicacionPersonajes().get(this.getId()).setDireccion(INDICE1);
        }
    }

    /**
     * Batallar tipo 1.
     */
    private void batallarTipo1() {
        NPC npc = this;
        PaquetePersonaje paqueteNPC = (PaquetePersonaje) npc.getPp().clone();
        PaquetePersonaje paqueteEnemigo = (PaquetePersonaje) Servidor.getPersonajesConectados()
                .get(npc.getPb().getIdEnemigo()).clone();
        Personaje personaje = crearPersonajes(paqueteNPC, paqueteEnemigo)[0];
        Personaje enemigo = crearPersonajes(paqueteNPC, paqueteEnemigo)[1];

        while (npc.getPp().getEstado() == Estado.estadoBatalla && npc.getPb() != null) { // Mientras
                                                                                            // dure
                                                                                            // la
                                                                                            // batalla

            try {
                Thread.sleep(ESPERA); // Espera por 0.5 segundos mientras el
                                    // enemigo elige el ataque
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (npc.getPp().getEstado() == Estado.estadoBatalla && npc.getPb() != null && npc.getPb().isMiTurno()) {
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
            casta = (Casta) Class.forName("dominio" + "." + paquetePersonaje.getCasta()).newInstance();
            personaje = (Personaje) Class.forName("dominio" + "." + paquetePersonaje.getRaza())
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
