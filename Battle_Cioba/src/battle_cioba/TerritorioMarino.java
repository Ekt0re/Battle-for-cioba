package battle_cioba;

import java.util.List;

/**
 * Implementazione concreta di un territorio marino.
 */
public class TerritorioMarino extends Territorio {
    
    /**
     * Profondità media in metri.
     */
    private int profonditaMedia;
    
    /**
     * Costruttore base per territorio marino.
     * 
     * @param nome Nome del territorio
     * @param statoPadrone Nome dello stato a cui appartiene
     * @param risorse Lista delle risorse disponibili
     * @param numCivili Numero di civili presenti
     * @param regione Nome della regione a cui appartiene
     */
    public TerritorioMarino(String nome, String statoPadrone, List<String> risorse, long numCivili, String regione) {
        super(nome, statoPadrone, true, risorse, numCivili, regione);
        this.profonditaMedia = 100; // Valore di default
    }
    
    /**
     * Costruttore semplificato.
     * 
     * @param nome Nome del territorio
     */
    public TerritorioMarino(String nome) {
        super(nome, null, true, null, 0, null);
        this.profonditaMedia = 100; // Valore di default
    }

    /**
     * Ottiene la profondità media del territorio marino.
     * 
     * @return profondità media in metri
     */
    public int getProfonditaMedia() {
        return profonditaMedia;
    }

    /**
     * Imposta la profondità media del territorio marino.
     * 
     * @param profonditaMedia nuova profondità media in metri
     */
    public void setProfonditaMedia(int profonditaMedia) {
        this.profonditaMedia = Math.max(1, profonditaMedia);
    }
    
    @Override
    public void stampaInfo() {
        super.stampaInfo();
        System.out.println(" | Profondità media: " + profonditaMedia + " metri");
    }
} 