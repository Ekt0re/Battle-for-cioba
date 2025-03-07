package battle_cioba;

/**
 * Rappresenta un capoluogo di regione.
 * Centro amministrativo e culturale di una regione.
 */
public class Capoluogo extends Centro {
    
    /**
     * Livello di sviluppo economico (da 1 a 10)
     */
    private int livelloEconomico;
    
    /**
     * Livello culturale e di istruzione (da 1 a 10)
     */
    private int livelloCulturale;
    
    /**
     * Regione a cui appartiene questo capoluogo
     */
    private Regione regione;
    
    /**
     * Costruttore principale.
     * 
     * @param nome Nome del capoluogo
     * @param importanzaStrategica Importanza strategica (1-10)
     * @param livelloDifesa Livello di difesa (1-10)
     * @param popolazione Popolazione
     * @param territorio Territorio su cui si trova
     * @param statoPadrone Stato a cui appartiene
     * @param regione Regione a cui appartiene (nome)
     * @param livelloEconomico Livello di sviluppo economico
     * @param livelloCulturale Livello culturale
     * @param regioneObj Oggetto Regione associato
     */
    public Capoluogo(String nome, int importanzaStrategica, int livelloDifesa, 
                    long popolazione, Territorio territorio, String statoPadrone, 
                    String regione, int livelloEconomico, int livelloCulturale, Regione regioneObj) {
        super(nome, TipoCentro.CAPOLUOGO, importanzaStrategica, livelloDifesa, 
             popolazione, territorio, statoPadrone, regione);
        
        this.livelloEconomico = Math.max(1, Math.min(10, livelloEconomico));
        this.livelloCulturale = Math.max(1, Math.min(10, livelloCulturale));
        this.regione = regioneObj;
    }

    public int getLivelloEconomico() {
        return livelloEconomico;
    }

    public void setLivelloEconomico(int livelloEconomico) {
        this.livelloEconomico = Math.max(1, Math.min(10, livelloEconomico));
    }

    public int getLivelloCulturale() {
        return livelloCulturale;
    }

    public void setLivelloCulturale(int livelloCulturale) {
        this.livelloCulturale = Math.max(1, Math.min(10, livelloCulturale));
    }

    public Regione getRegioneObj() {
        return regione;
    }

    public void setRegioneObj(Regione regione) {
        this.regione = regione;
    }
    
    /**
     * I capoluoghi generano risorse in base al livello economico.
     */
    @Override
    public int generaRisorse() {
        return (int) (getPopolazione() / 2000 * livelloEconomico);
    }
    
    /**
     * I capoluoghi hanno un raggio di influenza basato sul livello culturale.
     */
    @Override
    public int raggioInfluenza() {
        return 5 + livelloCulturale / 2;
    }
    
    /**
     * Genera un indice di produttività basato sull'economia e la popolazione.
     * 
     * @return valore di produttività
     */
    public int calcolaProduttivita() {
        return (int) (getPopolazione() / 1000 * livelloEconomico / 2);
    }
    
    /**
     * Simula l'effetto di un investimento culturale.
     * 
     * @param investimento ammontare dell'investimento
     * @return incremento del livello culturale
     */
    public int investiInCultura(int investimento) {
        // Calcolo dell'incremento basato sull'investimento
        int incremento = investimento / 1000;
        incremento = Math.min(incremento, 3); // Massimo 3 livelli alla volta
        
        // Applica l'incremento
        int vecchioLivello = livelloCulturale;
        livelloCulturale = Math.min(10, livelloCulturale + incremento);
        
        return livelloCulturale - vecchioLivello;
    }
    
    @Override
    public String toString() {
        return super.toString() + " (Economia: " + livelloEconomico + 
               "/10, Cultura: " + livelloCulturale + "/10)";
    }
} 