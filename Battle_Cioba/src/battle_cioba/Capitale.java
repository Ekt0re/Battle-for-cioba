package battle_cioba;

/**
 * Rappresenta la capitale di uno stato.
 * È il centro più importante di uno stato e determina la sua amministrazione centrale.
 */
public class Capitale extends Centro {
    
    /**
     * Livello di potere politico (da 1 a 10)
     */
    private int livellopPolitico;
    
    /**
     * Stato a cui appartiene questa capitale
     */
    private Stato stato;
    
    /**
     * Indicatore di stabilità politica (da 0 a 100)
     */
    private int stabilitaPolitica;
    
    /**
     * Costruttore principale.
     * 
     * @param nome Nome della capitale
     * @param importanzaStrategica Importanza strategica (1-10)
     * @param livelloDifesa Livello di difesa (1-10)
     * @param popolazione Popolazione
     * @param territorio Territorio su cui si trova
     * @param statoPadrone Stato a cui appartiene
     * @param regione Regione a cui appartiene
     * @param livellopPolitico Livello di potere politico
     * @param stato Oggetto Stato associato
     */
    public Capitale(String nome, int importanzaStrategica, int livelloDifesa, 
                   long popolazione, Territorio territorio, String statoPadrone, 
                   String regione, int livellopPolitico, Stato stato) {
        super(nome, TipoCentro.CAPITALE, importanzaStrategica, livelloDifesa, 
             popolazione, territorio, statoPadrone, regione);
        
        this.livellopPolitico = Math.max(1, Math.min(10, livellopPolitico));
        this.stato = stato;
        this.stabilitaPolitica = 50; // Valore predefinito di stabilità media
    }

    public int getLivellopPolitico() {
        return livellopPolitico;
    }

    public void setLivellopPolitico(int livellopPolitico) {
        this.livellopPolitico = Math.max(1, Math.min(10, livellopPolitico));
    }

    public Stato getStato() {
        return stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    public int getStabilitaPolitica() {
        return stabilitaPolitica;
    }

    public void setStabilitaPolitica(int stabilitaPolitica) {
        this.stabilitaPolitica = Math.max(0, Math.min(100, stabilitaPolitica));
    }
    
    /**
     * Le capitali generano risorse in base alla popolazione e al livello politico.
     */
    @Override
    public int generaRisorse() {
        return (int) (getPopolazione() / 1000 * getLivellopPolitico() / 2);
    }
    
    /**
     * Le capitali hanno un raggio di influenza molto ampio.
     */
    @Override
    public int raggioInfluenza() {
        return 10 + getLivellopPolitico();
    }
    
    /**
     * Metodo specifico per rappresentare una decisione politica nazionale.
     * 
     * @param decisione Descrizione della decisione
     * @return Effetto stimato sulla stabilità politica
     */
    public int prendiDecisioneNazionale(String decisione) {
        // Simulazione semplice: decisioni hanno effetti casuali sulla stabilità
        int effetto = (int) (Math.random() * 20) - 10; // Effetto tra -10 e +10
        
        stabilitaPolitica = Math.max(0, Math.min(100, stabilitaPolitica + effetto));
        
        return effetto;
    }
    
    /**
     * Verifica se la capitale è a rischio di ribellione.
     * 
     * @return true se la stabilità è sotto il 20%
     */
    public boolean aRischioDiRibellione() {
        return stabilitaPolitica < 20;
    }
    
    @Override
    public String toString() {
        return super.toString() + " (Livello Politico: " + livellopPolitico + 
               ", Stabilità: " + stabilitaPolitica + "%)";
    }
} 