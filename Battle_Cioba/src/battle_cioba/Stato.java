package battle_cioba;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta uno Stato, contenente più Regioni.
 */
public class Stato {

    /**
     * Contatore statico per ID univoci di Stato.
     */
    private static long globalIdCounter = 0;

    /**
     * ID univoco dello Stato.
     */
    private final long id;

    /**
     * Nome dello Stato.
     */
    private String nome;

    /**
     * Oggetto che rappresenta il presidente (può essere una classe dedicata).
     */
    private Object presidente;

    /**
     * Lista delle regioni che compongono lo Stato.
     */
    private final List<Regione> regioni;

    /**
     * Potenza complessiva dello Stato (metrica arbitraria).
     */
    private int potenza;

    /**
     * Popolazione totale dello Stato.
     */
    private long popolazione;

    /**
     * Regione che funge da capitale dello Stato (opzionale).
     */
    private Regione capitale;

    /**
     * Indice di "opposizione" all'interno dello Stato (metrica arbitraria).
     */
    private int opposizione;

    /**
     * Costruttore base per uno Stato.
     *
     * @param nome        nome dello Stato
     * @param presidente  oggetto presidente (es. una classe Persona)
     * @param potenza     valore di potenza iniziale
     * @param popolazione popolazione totale
     * @param capitale    regione designata come capitale (opzionale)
     */
    public Stato(String nome, Object presidente, int potenza, long popolazione, Regione capitale) {
        this.id = ++globalIdCounter;
        this.nome = nome;
        this.presidente = presidente;
        this.potenza = potenza;
        this.popolazione = popolazione;
        this.capitale = capitale;
        this.regioni = new LinkedList<>();
        if (capitale != null) {
            regioni.add(capitale);
        }
    }

    /* =========================== GETTER / SETTER =========================== */

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Object getPresidente() {
        return presidente;
    }

    public void setPresidente(Object presidente) {
        this.presidente = presidente;
    }

    public List<Regione> getRegioni() {
        return regioni;
    }

    public int getPotenza() {
        return potenza;
    }

    public void setPotenza(int potenza) {
        this.potenza = potenza;
    }

    public long getPopolazione() {
        return popolazione;
    }

    public void setPopolazione(long popolazione) {
        this.popolazione = popolazione;
    }

    public Regione getCapitale() {
        return capitale;
    }

    public void setCapitale(Regione capitale) {
        // Se c'era già una capitale precedente, rimuovi il flag
        if (this.capitale != null && this.capitale.getCapoluogo() != null) {
            this.capitale.getCapoluogo().setCapitale(false);
        }
        
        // Imposta la nuova capitale
        this.capitale = capitale;
        
        // Aggiungi la regione alla lista delle regioni se non è già presente
        if (capitale != null && !regioni.contains(capitale)) {
            regioni.add(capitale);
            
            // Imposta il capoluogo della regione come territorio capitale
            if (capitale.getCapoluogo() != null) {
                capitale.getCapoluogo().setCapitale(true);
            }
        }
    }

    public int getOpposizione() {
        return opposizione;
    }

    public void setOpposizione(int opposizione) {
        this.opposizione = opposizione;
    }

    /* =========================== METODI DI GESTIONE =========================== */

    /**
     * Aggiunge una regione allo Stato.
     *
     * @param regione la regione da aggiungere
     */
    public void addRegione(Regione regione) {
        if (regione != null && !regioni.contains(regione)) {
            regioni.add(regione);
        }
    }

    /**
     * Rimuove una regione dallo Stato.
     *
     * @param regione la regione da rimuovere
     * @return true se la regione è stata rimossa, false altrimenti
     */
    public boolean removeRegione(Regione regione) {
        if (regione == null) return false;
        // Se rimuovo la capitale, la setto a null
        if (Objects.equals(capitale, regione)) {
            capitale = null;
        }
        return regioni.remove(regione);
    }

    /**
     * Mostra informazioni di base sullo Stato.
     */
    public void stampaInfo() {
        System.out.println("Stato ID: " + id
                + " | Nome: " + nome
                + " | Presidente: " + (presidente != null ? presidente.toString() : "N/D")
                + " | Potenza: " + potenza
                + " | Popolazione: " + popolazione
                + " | Capitale: " + (capitale != null ? capitale.getNome() : "Nessuna")
                + " | Numero regioni: " + regioni.size()
                + " | Opposizione: " + opposizione);
    }

    public Territorio[] getTerritori() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTerritori'");
    }

 
}

