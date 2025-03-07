package battle_cioba;

/**
 * Rappresenta una base militare.
 * Fornisce protezione militare e capacità offensive.
 */
public class BaseMilitare extends Centro {
    
    /**
     * Numero di truppe presenti nella base
     */
    private int numeroTruppe;
    
    /**
     * Livello di armamento (da 1 a 10)
     */
    private int livelloArmamento;
    
    /**
     * Tipo di base militare
     */
    private TipoBase tipoBase;
    
    /**
     * Costruttore principale.
     * 
     * @param nome Nome della base
     * @param importanzaStrategica Importanza strategica (1-10)
     * @param livelloDifesa Livello di difesa (1-10)
     * @param popolazione Personale militare
     * @param territorio Territorio su cui si trova
     * @param statoPadrone Stato a cui appartiene
     * @param regione Regione a cui appartiene
     * @param numeroTruppe Numero di truppe stanziate
     * @param livelloArmamento Livello di armamento
     * @param tipoBase Tipo di base militare
     */
    public BaseMilitare(String nome, int importanzaStrategica, int livelloDifesa, 
                       long popolazione, Territorio territorio, String statoPadrone, 
                       String regione, int numeroTruppe, int livelloArmamento, TipoBase tipoBase) {
        super(nome, TipoCentro.BASE_MILITARE, importanzaStrategica, livelloDifesa, 
             popolazione, territorio, statoPadrone, regione);
        
        this.numeroTruppe = numeroTruppe;
        this.livelloArmamento = Math.max(1, Math.min(10, livelloArmamento));
        this.tipoBase = tipoBase;
    }
    
    /**
     * Enum che rappresenta i vari tipi di basi militari.
     */
    public enum TipoBase {
        TERRESTRE("Base Terrestre"),
        NAVALE("Base Navale"),
        AEREA("Base Aerea"),
        INTEGRATA("Base Integrata");
        
        private final String nome;
        
        TipoBase(String nome) {
            this.nome = nome;
        }
        
        public String getNome() {
            return nome;
        }
    }

    public int getNumeroTruppe() {
        return numeroTruppe;
    }

    public void setNumeroTruppe(int numeroTruppe) {
        this.numeroTruppe = numeroTruppe;
    }

    public int getLivelloArmamento() {
        return livelloArmamento;
    }

    public void setLivelloArmamento(int livelloArmamento) {
        this.livelloArmamento = Math.max(1, Math.min(10, livelloArmamento));
    }

    public TipoBase getTipoBase() {
        return tipoBase;
    }

    public void setTipoBase(TipoBase tipoBase) {
        this.tipoBase = tipoBase;
    }
    
    /**
     * Le basi militari generano risorse (equipaggiamento) in base al livello di armamento.
     */
    @Override
    public int generaRisorse() {
        return livelloArmamento * 10;
    }
    
    /**
     * Le basi militari hanno un raggio di influenza basato sul tipo e livello di armamento.
     */
    @Override
    public int raggioInfluenza() {
        int raggioBase;
        
        switch (tipoBase) {
            case AEREA:
                raggioBase = 8; // Le basi aeree hanno il raggio più ampio
                break;
            case NAVALE:
                raggioBase = 6; // Le basi navali hanno un buon raggio
                break;
            case INTEGRATA:
                raggioBase = 5; // Le basi integrate hanno un raggio medio
                break;
            case TERRESTRE:
            default:
                raggioBase = 3; // Le basi terrestri hanno il raggio più ristretto
                break;
        }
        
        return raggioBase + livelloArmamento / 2;
    }
    
    /**
     * Calcola la potenza militare della base.
     * 
     * @return valore numerico della potenza militare
     */
    public int calcolaPotenzaMilitare() {
        return numeroTruppe * livelloArmamento / 100 + getLivelloDifesa() * 5;
    }
    
    /**
     * Simula un'azione militare.
     * 
     * @param obiettivo Descrizione dell'obiettivo
     * @return Potenza dell'attacco
     */
    public int eseguiAzioneMilitare(String obiettivo) {
        // L'efficacia dell'azione dipende dalle truppe e dall'armamento
        int potenzaAttacco = numeroTruppe * livelloArmamento / 200;
        
        // Simulazione di una variazione casuale nell'efficacia
        double fattoreCasuale = 0.8 + Math.random() * 0.4; // Tra 0.8 e 1.2
        potenzaAttacco = (int) (potenzaAttacco * fattoreCasuale);
        
        return potenzaAttacco;
    }
    
    @Override
    public String toString() {
        return super.toString() + " (" + tipoBase.getNome() + 
               ", Truppe: " + numeroTruppe + 
               ", Armamento: " + livelloArmamento + "/10)";
    }
} 