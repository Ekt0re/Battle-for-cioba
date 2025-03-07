package battle_cioba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe astratta che rappresenta un Territorio.
 */
public abstract class Territorio {

    /**
     * Contatore statico per assegnare ID univoci a ogni Territorio.
     * In questo modo evitiamo di usare una stringa statica fissa e
     * garantiamo un identificativo diverso per ciascun oggetto.
     */
    private static long globalIdCounter = 0;

    /**
     * ID univoco del territorio.
     */
    private final long idUnivoco;

    /**
     * Nome del territorio.
     */
    protected String name;

    /**
     * Nome dello Stato che possiede questo territorio.
     */
    private String statoPadrone;

    /**
     * Indica se il territorio è prevalentemente acquatico.
     */
    protected boolean acqua = true;

    /**
     * Elenco delle risorse presenti nel territorio.
     */
    protected final List<String> risorse;

    /**
     * Numero di civili che abitano il territorio.
     */
    protected long numCivili;

    /**
     * Nome della regione di appartenenza.
     */
    protected String regione;

    /**
     * Numero di basi militari presenti nel territorio.
     */
    protected int countBasiMilitari = 0;

    /**
     * Indica se questo territorio è una capitale.
     */
    protected boolean capitale = false;

    /**
     * Centro principale nel territorio (Capitale, Capoluogo, ecc.)
     */
    protected Centro centro;

    /**
     * Costruttore astratto. Le sottoclassi dovranno chiamare super(...) per istanziare correttamente il Territorio.
     *
     * @param name         nome del territorio
     * @param statoPadrone stato proprietario del territorio
     * @param acqua        true se è acquatico, false se è terrestre
     * @param risorse      lista di risorse presenti (può essere null)
     * @param numCivili    numero di civili
     * @param regione      nome della regione di appartenenza
     */
    protected Territorio(String name,
                         String statoPadrone,
                         boolean acqua,
                         List<String> risorse,
                         long numCivili,
                         String regione) {
        this.idUnivoco = ++globalIdCounter;
        this.name = name;
        this.statoPadrone = statoPadrone;
        this.acqua = acqua;
        // Copia difensiva per evitare modifiche esterne
        this.risorse = (risorse == null) ? new ArrayList<>() : new ArrayList<>(risorse);
        this.numCivili = numCivili;
        this.regione = regione;
    }

    /* =========================== GETTER / SETTER =========================== */

    public long getIdUnivoco() {
        return idUnivoco;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatoPadrone() {
        return statoPadrone;
    }

    public void setStatoPadrone(String statoPadrone) {
        this.statoPadrone = statoPadrone;
    }

    public boolean isAcqua() {
        return acqua;
    }

    public void setAcqua(boolean acqua) {
        this.acqua = acqua;
    }

    /**
     * Restituisce la lista di risorse come una lista non modificabile.
     * Se necessario aggiungere o rimuovere risorse, si possono creare metodi ad hoc.
     */
    public List<String> getRisorse() {
        return Collections.unmodifiableList(risorse);
    }

    /**
     * Aggiunge una risorsa alla lista, se non presente.
     *
     * @param risorsa risorsa da aggiungere
     */
    public void addRisorsa(String risorsa) {
        if (risorsa != null && !risorse.contains(risorsa)) {
            risorse.add(risorsa);
        }
    }

    /**
     * Rimuove una risorsa, se presente.
     *
     * @param risorsa risorsa da rimuovere
     */
    public void removeRisorsa(String risorsa) {
        risorse.remove(risorsa);
    }

    public long getNumCivili() {
        return numCivili;
    }

    public void setNumCivili(long numCivili) {
        this.numCivili = numCivili;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public int getCountBasiMilitari() {
        return countBasiMilitari;
    }

    public void setCountBasiMilitari(int countBasiMilitari) {
        this.countBasiMilitari = countBasiMilitari;
    }

    public boolean isCapitale() {
        return capitale;
    }

    public void setCapitale(boolean capitale) {
        this.capitale = capitale;
    }

    /**
     * Ottiene il centro principale del territorio.
     * 
     * @return oggetto Centro
     */
    public Centro getCentro() {
        return centro;
    }
    
    /**
     * Imposta il centro principale del territorio.
     * 
     * @param centro oggetto Centro da impostare
     */
    public void setCentro(Centro centro) {
        this.centro = centro;
    }

    /* =========================== METODI UTILI =========================== */

    /**
     * Incrementa il numero di basi militari di una certa quantità.
     *
     * @param incremento quantità da aggiungere (può essere negativa per ridurre).
     */
    public void incrementaBasiMilitari(int incremento) {
        this.countBasiMilitari += incremento;
        if (this.countBasiMilitari < 0) {
            this.countBasiMilitari = 0; // Evita valori negativi
        }
    }

    /**
     * Stampa informazioni di base sul territorio.
     */
    public void stampaInfo() {
        System.out.println("Territorio ID: " + idUnivoco
                + " | Nome: " + name
                + " | Stato Padrone: " + statoPadrone
                + " | Acqua: " + acqua
                + " | Regione: " + regione
                + " | Civili: " + numCivili
                + " | Basi Militari: " + countBasiMilitari
                + " | Risorse: " + risorse);
    }
}
