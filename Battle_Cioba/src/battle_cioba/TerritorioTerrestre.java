package battle_cioba;

import java.util.List;

/**
 * Implementazione concreta di un territorio terrestre.
 */
public class TerritorioTerrestre extends Territorio {
    
    /**
     * Livello di fertilità del terreno.
     */
    private int fertilitaTerreno;
    
    /**
     * Costruttore base per territorio terrestre.
     * 
     * @param nome Nome del territorio
     * @param statoPadrone Nome dello stato a cui appartiene
     * @param risorse Lista delle risorse disponibili
     * @param numCivili Numero di civili presenti
     * @param regione Nome della regione a cui appartiene
     */
    public TerritorioTerrestre(String nome, String statoPadrone, List<String> risorse, long numCivili, String regione) {
        super(nome, statoPadrone, false, risorse, numCivili, regione);
        this.fertilitaTerreno = 50; // Valore di default
    }
    
    /**
     * Costruttore semplificato.
     * 
     * @param nome Nome del territorio
     */
    public TerritorioTerrestre(String nome) {
        super(nome, null, false, null, 1000, null);
        this.fertilitaTerreno = 50; // Valore di default
    }

    /**
     * Ottiene il livello di fertilità del terreno.
     * 
     * @return livello di fertilità
     */
    public int getFertilitaTerreno() {
        return fertilitaTerreno;
    }

    /**
     * Imposta il livello di fertilità del terreno.
     * 
     * @param fertilitaTerreno nuovo livello di fertilità
     */
    public void setFertilitaTerreno(int fertilitaTerreno) {
        this.fertilitaTerreno = Math.max(0, Math.min(100, fertilitaTerreno));
    }
    
    @Override
    public void stampaInfo() {
        super.stampaInfo();
        System.out.println(" | Fertilità: " + fertilitaTerreno);
    }
} 