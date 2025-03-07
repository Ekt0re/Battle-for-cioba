package battle_cioba;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta una Regione, contenente uno o più Territori.
 */
public class Regione {

    /**
     * Contatore statico per ID univoci di Regione.
     */
    private static long globalIdCounter = 0;

    /**
     * ID univoco della regione.
     */
    private final long id;

    /**
     * Nome della regione.
     */
    private String nome;

    /**
     * Territorio che funge da capoluogo (opzionale).
     */
    private Territorio capoluogo;

    /**
     * Lista dei territori appartenenti a questa regione.
     */
    private final List<Territorio> territori;

    /**
     * Indice di "consensi" associato alla regione (metrica arbitraria).
     */
    private int consensi;

    /**
     * Indice di "rivoltosi" associato alla regione (metrica arbitraria).
     */
    private int rivoltosi;

    /**
     * Costruttore base.
     *
     * @param nome      nome della regione
     * @param capoluogo territorio capoluogo (opzionale)
     */
    public Regione(String nome, Territorio capoluogo) {
        this.id = ++globalIdCounter;
        this.nome = nome;
        this.capoluogo = capoluogo;
        this.territori = new LinkedList<>();
        if (capoluogo != null) {
            this.territori.add(capoluogo);
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

    public Territorio getCapoluogo() {
        return capoluogo;
    }

    public void setCapoluogo(Territorio capoluogo) {
        // Se c'era già un capoluogo precedente, rimuovi il flag di capitale
        if (this.capoluogo != null) {
            this.capoluogo.setCapitale(false);
        }
        
        // Imposta il nuovo capoluogo
        this.capoluogo = capoluogo;
        
        // Marca il nuovo territorio come capitale
        if (capoluogo != null) {
            capoluogo.setCapitale(true);
        }
    }

    public int getConsensi() {
        return consensi;
    }

    public void setConsensi(int consensi) {
        this.consensi = consensi;
    }

    public int getRivoltosi() {
        return rivoltosi;
    }

    public void setRivoltosi(int rivoltosi) {
        this.rivoltosi = rivoltosi;
    }

    /**
     * Ritorna la lista di territori della regione (in sola lettura).
     */
    public List<Territorio> getTerritori() {
        return territori;
    }

    /* =========================== METODI DI GESTIONE =========================== */

    /**
     * Aggiunge un territorio alla regione.
     *
     * @param territorio territorio da aggiungere
     */
    public void addTerritorio(Territorio territorio) {
        if (territorio != null && !territori.contains(territorio)) {
            territori.add(territorio);
            
            // Imposta la regione di appartenenza
            territorio.setRegione(this.getNome());
        }
    }

    /**
     * Rimuove un territorio dalla regione.
     *
     * @param territorio territorio da rimuovere
     * @return true se il territorio è stato rimosso, false altrimenti
     */
    public boolean removeTerritorio(Territorio territorio) {
        if (territorio == null) return false;
        // Se rimuovo il capoluogo, setto capoluogo a null
        if (Objects.equals(capoluogo, territorio)) {
            capoluogo = null;
        }
        return territori.remove(territorio);
    }

    /**
     * Mostra informazioni sulla regione.
     */
    public void stampaInfo() {
        System.out.println("Regione ID: " + id
                + " | Nome: " + nome
                + " | Capoluogo: " + (capoluogo != null ? capoluogo.getName() : "Nessuno")
                + " | Consensi: " + consensi
                + " | Rivoltosi: " + rivoltosi
                + " | Numero territori: " + territori.size());
    }
}
