package battle_cioba;

/**
 * Classe che rappresenta il Presidente di uno Stato.
 */
public class Presidente extends Persona {
    
    /**
     * Nome del Presidente.
     */
    private String nome;
    
    /**
     * Cognome del Presidente.
     */
    private String cognome;
    
    /**
     * Soprannome del Presidente.
     */
    private String soprannome;
    
    /**
     * Età del Presidente.
     */
    private int eta;
    
    /**
     * Costruttore per il Presidente con nome completo.
     * 
     * @param nomeCompleto Nome completo del Presidente
     */
    public Presidente(String nomeCompleto) {
        // Gestisce il caso in cui il nome completo contenga più parti
        String[] parti = nomeCompleto.trim().split("\\s+");
        
        if (parti.length >= 2) {
            this.nome = parti[0];
            this.cognome = parti[1];
            
            // Se ci sono più di due parti, consideriamo le altre come soprannome
            if (parti.length > 2) {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < parti.length; i++) {
                    sb.append(parti[i]);
                    if (i < parti.length - 1) {
                        sb.append(" ");
                    }
                }
                this.soprannome = sb.toString();
            } else {
                this.soprannome = "";
            }
        } else if (parti.length == 1) {
            this.nome = parti[0];
            this.cognome = "";
            this.soprannome = "";
        } else {
            this.nome = "Presidente";
            this.cognome = "";
            this.soprannome = "";
        }
        
        // Età casuale tra 35 e 80 anni
        this.eta = 35 + (int)(Math.random() * 45);
    }
    
    /**
     * Restituisce il nome del Presidente.
     * 
     * @return Nome del Presidente
     */
    public String getNome() {
        return nome;
    }
    
    /**
     * Restituisce il cognome del Presidente.
     * 
     * @return Cognome del Presidente
     */
    public String getCognome() {
        return cognome;
    }
    
    /**
     * Restituisce il soprannome del Presidente.
     * 
     * @return Soprannome del Presidente
     */
    public String getSoprannome() {
        return soprannome;
    }
    
    /**
     * Restituisce l'età del Presidente.
     * 
     * @return Età del Presidente
     */
    public int getEta() {
        return eta;
    }
    
    /**
     * Restituisce una rappresentazione stringa del Presidente.
     * 
     * @return Stringa che rappresenta il Presidente
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nome);
        
        if (!cognome.isEmpty()) {
            sb.append(" ").append(cognome);
        }
        
        if (!soprannome.isEmpty()) {
            sb.append(" \"").append(soprannome).append("\"");
        }
        
        sb.append(", ").append(eta).append(" anni");
        
        return sb.toString();
    }
} 