package battle_cioba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe astratta che rappresenta un Centro (Base militare, Capoluogo, Capitale, ecc).
 * Serve come base per tutti i punti di interesse sulla mappa.
 */
public abstract class Centro {

    /**
     * Contatore statico per assegnare ID univoci a ogni Centro.
     */
    private static long globalIdCounter = 0;

    /**
     * ID univoco del Centro.
     */
    private final long idUnivoco;

    /**
     * Nome del Centro.
     */
    protected String nome;

    /**
     * Tipo di centro (CAPITALE, CAPOLUOGO, BASE_MILITARE, ecc.)
     */
    private TipoCentro tipo;

    /**
     * Importanza strategica del centro (da 1 a 10)
     */
    private int importanzaStrategica;

    /**
     * Livello di difesa del centro (da 1 a 10)
     */
    private int livelloDifesa;

    /**
     * Popolazione presente nel centro
     */
    private long popolazione;

    /**
     * Coordinate X e Y del centro sulla mappa
     */
    private int coordinataX;
    private int coordinataY;

    /**
     * Territorio su cui si trova il centro
     */
    private Territorio territorio;

    /**
     * Nome dello Stato che possiede questo Centro.
     */
    private String statoPadrone;

    /**
     * Nome della regione di appartenenza.
     */
    protected String regione;
    
    /**
     * Lista di risorse prodotte nel centro
     */
    private List<String> risorseProdotte;
    
    /**
     * Costruttore base per tutti i centri.
     * 
     * @param nome Nome del centro
     * @param tipo Tipo di centro
     * @param importanzaStrategica Importanza strategica (1-10)
     * @param livelloDifesa Livello di difesa (1-10)
     * @param popolazione Popolazione del centro
     * @param territorio Territorio su cui si trova
     * @param statoPadrone Stato a cui appartiene
     * @param regione Regione a cui appartiene
     */
    protected Centro(String nome, TipoCentro tipo, int importanzaStrategica, int livelloDifesa, 
                    long popolazione, Territorio territorio, String statoPadrone, String regione) {
        this.idUnivoco = ++globalIdCounter;
        this.nome = nome;
        this.tipo = tipo;
        this.importanzaStrategica = Math.max(1, Math.min(10, importanzaStrategica)); // Limita tra 1 e 10
        this.livelloDifesa = Math.max(1, Math.min(10, livelloDifesa)); // Limita tra 1 e 10
        this.popolazione = popolazione;
        this.territorio = territorio;
        this.statoPadrone = statoPadrone;
        this.regione = regione;
        this.risorseProdotte = new ArrayList<>();
        
        // I centri devono essere posizionati manualmente
        this.coordinataX = 0;
        this.coordinataY = 0;
    }
    
    /**
     * Enum che rappresenta i vari tipi di centri.
     */
    public enum TipoCentro {
        CAPITALE("Capitale", 10),          // Capitale di uno stato
        CAPOLUOGO("Capoluogo", 7),         // Capoluogo di una regione
        BASE_MILITARE("Base Militare", 5), // Base militare
        PORTO("Porto", 4),                 // Porto
        CITTA("Città", 3),                 // Città generica
        VILLAGGIO("Villaggio", 2),         // Villaggio o piccolo insediamento
        AVAMPOSTO("Avamposto", 1);         // Avamposto o insediamento minore
        
        private final String nome;
        private final int importanzaBase;
        
        TipoCentro(String nome, int importanzaBase) {
            this.nome = nome;
            this.importanzaBase = importanzaBase;
        }
        
        public String getNome() {
            return nome;
        }
        
        public int getImportanzaBase() {
            return importanzaBase;
        }
    }

    // Getters e setters
    
    public long getIdUnivoco() {
        return idUnivoco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoCentro getTipo() {
        return tipo;
    }

    public void setTipo(TipoCentro tipo) {
        this.tipo = tipo;
    }

    public int getImportanzaStrategica() {
        return importanzaStrategica;
    }

    public void setImportanzaStrategica(int importanzaStrategica) {
        this.importanzaStrategica = Math.max(1, Math.min(10, importanzaStrategica));
    }

    public int getLivelloDifesa() {
        return livelloDifesa;
    }

    public void setLivelloDifesa(int livelloDifesa) {
        this.livelloDifesa = Math.max(1, Math.min(10, livelloDifesa));
    }

    public long getPopolazione() {
        return popolazione;
    }

    public void setPopolazione(long popolazione) {
        this.popolazione = popolazione;
    }

    public int getCoordinataX() {
        return coordinataX;
    }

    public void setCoordinataX(int coordinataX) {
        this.coordinataX = coordinataX;
    }

    public int getCoordinataY() {
        return coordinataY;
    }

    public void setCoordinataY(int coordinataY) {
        this.coordinataY = coordinataY;
    }

    public Territorio getTerritorio() {
        return territorio;
    }

    public void setTerritorio(Territorio territorio) {
        this.territorio = territorio;
        // Non aggiorniamo più le coordinate automaticamente
    }

    public String getStatoPadrone() {
        return statoPadrone;
    }

    public void setStatoPadrone(String statoPadrone) {
        this.statoPadrone = statoPadrone;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }
    
    public List<String> getRisorseProdotte() {
        return Collections.unmodifiableList(risorseProdotte);
    }
    
    public void aggiungiRisorsa(String risorsa) {
        if (risorsa != null && !risorseProdotte.contains(risorsa)) {
            risorseProdotte.add(risorsa);
        }
    }
    
    public void rimuoviRisorsa(String risorsa) {
        risorseProdotte.remove(risorsa);
    }
    
    /**
     * Calcola il valore strategico del centro in base al tipo, importanza e difesa.
     * @return valore strategico calcolato
     */
    public int calcolaValoreStrategico() {
        return tipo.getImportanzaBase() * importanzaStrategica + livelloDifesa * 2;
    }
    
    /**
     * Calcola la popolazione effettiva, considerando la capacità del territorio.
     * @return popolazione effettiva
     */
    public long calcolaPopolazioneEffettiva() {
        return popolazione;
    }
    
    /**
     * Ogni tipo di Centro deve implementare un metodo per generare risorse.
     * @return quantità di risorse generate
     */
    public abstract int generaRisorse();
    
    /**
     * Ogni tipo di Centro deve implementare un metodo per specificare la propria influenza.
     * @return raggio di influenza in termini di celle
     */
    public abstract int raggioInfluenza();
    
    /**
     * Restituisce una descrizione testuale del centro.
     * @return stringa con la descrizione del centro
     */
    @Override
    public String toString() {
        return tipo.getNome() + " '" + nome + "' (" + statoPadrone + 
               ", Regione: " + regione + 
               ", Coordinate: [" + coordinataX + "," + coordinataY + "]" +
               ", Importanza: " + importanzaStrategica + 
               ", Difesa: " + livelloDifesa + 
               ", Popolazione: " + popolazione + ")";
    }
}
