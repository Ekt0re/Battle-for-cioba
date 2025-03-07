package battle_cioba;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Classe che rappresenta il Mondo di gioco, contenente più Stati.
 */
public class Mondo {

    /**
     * Lista degli stati presenti nel mondo.
     */
    private final LinkedList<Stato> stati;

    /**
     * Random per generazione casuale.
     */
    private final Random random;

    /**
     * Matrice di territori generati dal CSV.
     */
    private Territorio[][] mappaTerritori;

    /**
     * Costruttore base per il mondo.
     */
    public Mondo() {
        this.stati = new LinkedList<>();
        this.random = new Random();
    }

    /**
     * Restituisce la lista degli stati.
     *
     * @return LinkedList degli stati
     */
    public LinkedList<Stato> getStati() {
        return stati;
    }

    /**
     * Aggiunge uno stato alla lista.
     *
     * @param stato Stato da aggiungere
     */
    public void aggiungiStato(Stato stato) {
        stati.add(stato);
    }

    /**
     * Rimuove uno stato dalla lista.
     *
     * @param stato Stato da rimuovere
     * @return true se lo stato è stato rimosso, false altrimenti
     */
    public boolean rimuoviStato(Stato stato) {
        return stati.remove(stato);
    }

    /**
     * Genera statistiche per tutti gli stati.
     */
    public void stampaStatisticheStati() {
        System.out.println("=== STATISTICHE DEGLI STATI ===");

        for (Stato stato : stati) {
            System.out.println("\nStato: " + stato.getNome());
            System.out.println("Popolazione totale: " + stato.getPopolazione());
            System.out.println("Numero di regioni: " + stato.getRegioni().size());

            int territoriTotali = 0;
            int territoriMarini = 0;
            for (Regione regione : stato.getRegioni()) {
                territoriTotali += regione.getTerritori().size();
                for (Territorio territorio : regione.getTerritori()) {
                    if (territorio.isAcqua()) {
                        territoriMarini++;
                    }
                }
            }

            System.out.println("Territori totali: " + territoriTotali);
            System.out.println("Territori terrestri: " + (territoriTotali - territoriMarini));
            System.out.println("Territori marini: " + territoriMarini);
            System.out.println("Potenza militare: " + stato.getPotenza());
            System.out.println("Capitale: " + (stato.getCapitale() != null ? stato.getCapitale().getNome() : "Nessuna"));
        }
    }

    /**
     * Crea un nuovo territorio in base al tipo (acqua o terra).
     *
     * @param isAcqua indica se è un territorio acquatico o terrestre
     * @param riga    posizione riga nella matrice
     * @param colonna posizione colonna nella matrice
     * @return il territorio creato
     */
    private Territorio nuovoTerritorio(boolean isAcqua, int riga, int colonna) {
        String nome = "Territorio_" + riga + "_" + colonna;
        if (isAcqua) {
            return new TerritorioMarino(nome);
        } else {
            return new TerritorioTerrestre(nome);
        }
    }

    /**
     * Genera una mappa di territori basandosi su un file CSV. Nel file, T
     * rappresenta Terra e M rappresenta Mare.
     *
     * @param percorsoFile percorso del file CSV
     * @return matrice di territori generati
     * @throws IOException se ci sono problemi nella lettura del file
     */
    public Territorio[][] generaTerritori(String percorsoFile) throws IOException {
        List<String> righe = new ArrayList<>();

        // Legge il file CSV
        try (BufferedReader br = new BufferedReader(new FileReader(percorsoFile))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                righe.add(riga);
            }
        }

        if (righe.isEmpty()) {
            throw new IOException("Il file CSV è vuoto o non è stato possibile leggerlo");
        }

        int righeMatrice = righe.size();
        int colonneMatrice = righe.get(0).split(",").length;

        // Creazione della matrice di territori
        mappaTerritori = new Territorio[righeMatrice][colonneMatrice];

        // Popolamento della matrice
        for (int i = 0; i < righeMatrice; i++) {
            String[] celle = righe.get(i).split(",");
            for (int j = 0; j < celle.length; j++) {
                String tipo = celle[j].trim();
                boolean isAcqua = tipo.equalsIgnoreCase("M"); // M = Mare, T = Terra

                // Creazione di un territorio appropriato usando il metodo nuovoTerritorio
                mappaTerritori[i][j] = nuovoTerritorio(isAcqua, i, j);
            }
        }

        System.out.println("Generata mappa di territori " + righeMatrice + "x" + colonneMatrice);
        return mappaTerritori;
    }

    /**
     * Crea un nuovo stato con un nome specificato.
     *
     * @param nome nome dello stato
     * @return lo stato creato
     */
    public Stato creaStato(String nome) {
        // Creiamo un nuovo stato (per ora senza regioni)
        Stato nuovoStato = new Stato(nome, null, 100, 0, null);
        stati.add(nuovoStato);
        return nuovoStato;
    }

    /**
     * Genera automaticamente un numero specifico di stati sulla mappa.
     * Ogni stato avrà un insieme di regioni contigue.
     *
     * @param numStati numero di stati da generare
     * @return numero di stati effettivamente creati
     */
    public int generaStati(int numStati) {
        if (mappaTerritori == null) {
            System.out.println("Errore: la mappa dei territori non è stata generata.");
            return 0;
        }

        // Conteggio totale dei territori terrestri disponibili
        int totaleTerritoriTerrestri = 0;
        for (int i = 0; i < mappaTerritori.length; i++) {
            for (int j = 0; j < mappaTerritori[0].length; j++) {
                Territorio t = mappaTerritori[i][j];
                if (t != null && !t.isAcqua() && t.getStatoPadrone() == null) {
                    totaleTerritoriTerrestri++;
                }
            }
        }
        
        // Calcolo della dimensione media per stato in base al numero di stati da generare
        // Più stati ci sono, minore sarà la dimensione di ciascuno
        int dimensioneMediaStato = totaleTerritoriTerrestri / numStati;
        
        // Matrice per tenere traccia dei territori già visitati nella ricerca di regioni
        boolean[][] visited = new boolean[mappaTerritori.length][mappaTerritori[0].length];
        
        // Lista di punti di partenza potenziali per gli stati (territori terrestri)
        List<Point> puntidiPartenza = trovaTerreniDisponibili();
        
        if (puntidiPartenza.isEmpty()) {
            System.out.println("Errore: non ci sono territori terrestri disponibili.");
            return 0;
        }
        
        // Limita il numero di stati se non ci sono abbastanza punti di partenza
        int statiDaCreare = Math.min(numStati, puntidiPartenza.size());
        int statiCreati = 0;
        
        System.out.println("Generazione di " + statiDaCreare + " stati con una dimensione media di " + 
                          dimensioneMediaStato + " territori ciascuno (totale territori terrestri: " + 
                          totaleTerritoriTerrestri + ")");
        
        // Crea gli stati
        for (int i = 0; i < statiDaCreare; i++) {
            // Seleziona casualmente un punto di partenza
            int index = random.nextInt(puntidiPartenza.size());
            Point puntoIniziale = puntidiPartenza.remove(index);
            
            // Nome dello stato
            String nomeStato = "Stato_" + (i + 1);
            Stato nuovoStato = creaStato(nomeStato);
            
            // Calcola la dimensione target per questo stato
            // Variazione casuale del ±20% rispetto alla media
            double fattoreVariazione = 0.8 + (random.nextDouble() * 0.4);
            int dimensioneTarget = (int)(dimensioneMediaStato * fattoreVariazione);
            
            // Genera il nucleo iniziale dello stato con la dimensione calcolata
            boolean success = generaNucleoStatoMigliorato(nuovoStato, puntoIniziale, visited, dimensioneTarget);
            
            if (success) {
                statiCreati++;
                System.out.println("Stato " + nomeStato + " creato con successo.");
            } else {
                rimuoviStato(nuovoStato);
                System.out.println("Impossibile creare lo stato " + nomeStato + ".");
            }
        }
        
        System.out.println("Creati " + statiCreati + " stati su " + numStati + " richiesti.");
        return statiCreati;
    }
    
    /**
     * Trova tutti i territori terrestri disponibili sulla mappa.
     *
     * @return lista di punti corrispondenti a territori terrestri
     */
    private List<Point> trovaTerreniDisponibili() {
        List<Point> punti = new ArrayList<>();
        
        for (int i = 0; i < mappaTerritori.length; i++) {
            for (int j = 0; j < mappaTerritori[0].length; j++) {
                Territorio t = mappaTerritori[i][j];
                if (t != null && !t.isAcqua() && t.getStatoPadrone() == null) {
                    punti.add(new Point(i, j));
                }
            }
        }
        
        return punti;
    }
    
    /**
     * Genera il nucleo territoriale di uno stato a partire da un punto sulla mappa con dimensione migliorata.
     *
     * @param stato stato da generare
     * @param puntoIniziale punto di partenza
     * @param visited matrice di territori già visitati
     * @param dimensioneTarget dimensione target dello stato
     * @return true se lo stato è stato generato con successo, false altrimenti
     */
    private boolean generaNucleoStatoMigliorato(Stato stato, Point puntoIniziale, boolean[][] visited, int dimensioneTarget) {
        // Numero di regioni da creare per lo stato (da 3 a 6 per stati più realistici)
        int numRegioni = 3 + random.nextInt(4);
        
        // Dimensione totale approssimativa dello stato (territori totali)
        int dimensioneStato = dimensioneTarget;
        
        // Dimensione media per regione
        int dimensioneMediaRegione = dimensioneStato / numRegioni;
        
        // Primo territorio e regione capitale
        int startRow = puntoIniziale.x;
        int startCol = puntoIniziale.y;
        
        // Verifica che il punto sia ancora disponibile
        if (visited[startRow][startCol] || mappaTerritori[startRow][startCol].getStatoPadrone() != null) {
            return false;
        }
        
        System.out.println("Generazione stato '" + stato.getNome() + "' con " + numRegioni + 
                           " regioni e dimensione target di " + dimensioneStato + " territori");
        
        // Genera la regione capitale
        String nomeRegioneCapitale = "Regione_Capitale_di_" + stato.getNome();
        // La regione capitale è significativamente più grande
        int dimensioneCapitale = (int)(dimensioneMediaRegione * 1.4);
        List<Territorio> territoriCapitale = floodFillRegionMigliorato(startRow, startCol, dimensioneCapitale, visited);
        
        if (territoriCapitale.isEmpty()) {
            System.out.println("Impossibile creare la regione capitale per lo stato " + stato.getNome());
            return false;
        }
        
        // Scegli un territorio come capoluogo della regione capitale (preferibilmente non sul bordo)
        Territorio territorioCapitale = scegliCapoluogo(territoriCapitale);
        
        // Verifica che sia stato trovato un capoluogo terrestre
        if (territorioCapitale == null) {
            System.out.println("Impossibile trovare un territorio terrestre per la capitale dello stato " + stato.getNome());
            return false;
        }
        
        Regione regioneCapitale = new Regione(nomeRegioneCapitale, territorioCapitale);
        
        // Crea un oggetto Capitale e posizionalo nel territorio capitale
        Capitale capitaleOggetto = new Capitale(
            "Capitale_" + stato.getNome(),
            10 + random.nextInt(10), // Importanza strategica alta
            8 + random.nextInt(7),   // Livello difesa alto
            50000 + random.nextInt(150000), // Popolazione significativa
            territorioCapitale,
            stato.getNome(),
            regioneCapitale.getNome(),
            8 + random.nextInt(3),   // Livello politico alto
            stato
        );
        
        // Assegna la Capitale al territorio
        territorioCapitale.setCentro(capitaleOggetto);
        
        // Assegna un numero di civili significativo alla capitale
        territorioCapitale.setNumCivili(50000 + random.nextInt(150000));
        // Il capoluogo ha più basi militari
        territorioCapitale.setCountBasiMilitari(3 + random.nextInt(4));
        
        // Aggiorna i territori della regione capitale
        for (Territorio terr : territoriCapitale) {
            terr.setRegione(regioneCapitale.getNome());
            terr.setStatoPadrone(stato.getNome());
            
            // Assegna un numero di civili se non già assegnato
            if (terr.getNumCivili() == 0) {
                terr.setNumCivili(2000 + random.nextInt(8000));
            }
            
            // Possibilità di avere basi militari
            if (random.nextDouble() < 0.25) {
                terr.setCountBasiMilitari(1 + random.nextInt(2));
            }
            
            regioneCapitale.addTerritorio(terr);
        }
        
        stato.addRegione(regioneCapitale);
        stato.setCapitale(regioneCapitale);
        
        System.out.println("Creata regione capitale '" + regioneCapitale.getNome() + 
                           "' con " + territoriCapitale.size() + " territori e Capitale posizionata");
        
        // Trova territori adiacenti per le altre regioni, inclusi i territori terrestri occupati da altri stati
        List<Point> frontiera = trovaPuntiFrontieraMigliorata(territoriCapitale, visited);
        
        // Genera le altre regioni
        int regioniCreate = 1; // La capitale è già stata creata
        int tentativi = 0; // Teniamo traccia dei tentativi per evitare loop infiniti
        int maxTentativi = numRegioni * 5; // Numero massimo di tentativi
        
        while (regioniCreate < numRegioni && !frontiera.isEmpty() && tentativi < maxTentativi) {
            tentativi++;
            
            // Sceglie un punto di frontiera per la nuova regione
            int index = random.nextInt(frontiera.size());
            Point puntoRegione = frontiera.get(index);
            frontiera.remove(index);
            
            // Evita di visitare territori già visitati
            if (visited[puntoRegione.x][puntoRegione.y]) continue;
            
            String nomeRegione = "Regione_" + (regioniCreate + 1) + "_di_" + stato.getNome();
            
            // Varia significativamente la dimensione delle regioni per permettere maggiore espansione
            // Più lontane sono le regioni dalla capitale, più tendono ad essere grandi
            double fattoreDimensione = 0.7 + (regioniCreate * 0.1) + random.nextDouble() * 0.5; // Tra 0.7 e 2.5 in base alla distanza
            int dimensioneRegione = (int)(dimensioneMediaRegione * fattoreDimensione);
            
            List<Territorio> territoriRegione = floodFillRegionMigliorato(puntoRegione.x, puntoRegione.y, 
                                                dimensioneRegione, visited);
            
            if (territoriRegione.size() >= dimensioneMediaRegione / 2) { // Accetta solo regioni di dimensione significativa
                // Scegli un territorio casuale come capoluogo
                Territorio capoluogoTerritorio = scegliCapoluogo(territoriRegione);
                
                // Verifica che sia stato trovato un capoluogo terrestre
                if (capoluogoTerritorio == null) {
                    System.out.println("Impossibile trovare un territorio terrestre per il capoluogo della regione " + nomeRegione);
                    continue; // Passa alla prossima regione
                }
                
                Regione nuovaRegione = new Regione(nomeRegione, capoluogoTerritorio);
                
                // Crea un oggetto Capoluogo e posizionalo nel territorio capoluogo
                Capoluogo capoluogoOggetto = new Capoluogo(
                    "Capoluogo_" + nomeRegione,
                    6 + random.nextInt(8),  // Importanza strategica media
                    5 + random.nextInt(5),  // Livello difesa medio
                    20000 + random.nextInt(80000), // Popolazione media
                    capoluogoTerritorio,
                    stato.getNome(),
                    nuovaRegione.getNome(),
                    5 + random.nextInt(5),  // Livello economico
                    5 + random.nextInt(5),  // Livello culturale
                    nuovaRegione
                );
                
                // Assegna il Capoluogo al territorio
                capoluogoTerritorio.setCentro(capoluogoOggetto);
                
                // Il capoluogo ha più civili e possibilmente basi militari
                capoluogoTerritorio.setNumCivili(20000 + random.nextInt(80000));
                if (random.nextDouble() < 0.7) {
                    capoluogoTerritorio.setCountBasiMilitari(1 + random.nextInt(3));
                }
                
                // Aggiorna i territori della nuova regione
                for (Territorio terr : territoriRegione) {
                    terr.setRegione(nuovaRegione.getNome());
                    terr.setStatoPadrone(stato.getNome());
                    
                    // Assegna un numero di civili
                    if (terr.getNumCivili() == 0) {
                        terr.setNumCivili(800 + random.nextInt(5000));
                    }
                    
                    // Possibilità di avere basi militari (più alta nelle regioni periferiche)
                    double probabilitaBaseMilitare = 0.1 + (regioniCreate * 0.02); // Aumenta con la distanza dalla capitale
                    if (random.nextDouble() < probabilitaBaseMilitare) {
                        terr.setCountBasiMilitari(1);
                    }
                    
                    nuovaRegione.addTerritorio(terr);
                }
                
                stato.addRegione(nuovaRegione);
                regioniCreate++;
                
                System.out.println("Creata regione '" + nuovaRegione.getNome() + 
                                   "' con " + territoriRegione.size() + " territori e Capoluogo posizionato");
                
                // Aggiorna la frontiera con i nuovi punti
                List<Point> nuoviFrontiera = trovaPuntiFrontieraMigliorata(territoriRegione, visited);
                frontiera.addAll(nuoviFrontiera);
            }
        }
        
        return regioniCreate >= numRegioni / 2; // Considera un successo se almeno metà delle regioni sono state create
    }
    
    /**
     * Seleziona un territorio adatto come capoluogo tra i territori forniti.
     * Assicura che il capoluogo sia sempre su un territorio terrestre (non acquatico).
     *
     * @param territori lista di territori candidati
     * @return il territorio scelto come capoluogo, null se non ci sono territori terrestri disponibili
     */
    private Territorio scegliCapoluogo(List<Territorio> territori) {
        if (territori.isEmpty()) return null;
        
        // Filtriamo solo i territori terrestri
        List<Territorio> territoriTerrestri = new ArrayList<>();
        for (Territorio t : territori) {
            if (!t.isAcqua()) {
                territoriTerrestri.add(t);
            }
        }
        
        // Se non ci sono territori terrestri, restituisci null
        if (territoriTerrestri.isEmpty()) {
            System.out.println("ATTENZIONE: Non sono disponibili territori terrestri per il capoluogo");
            return null;
        }
        
        // Se ci sono pochi territori, scegliamo semplicemente il primo
        if (territoriTerrestri.size() <= 3) return territoriTerrestri.get(0);
        
        // Altrimenti, evitiamo i bordi prendendo un territorio dal secondo quarto della lista
        int indice = territoriTerrestri.size() / 4 + random.nextInt(territoriTerrestri.size() / 2);
        indice = Math.min(indice, territoriTerrestri.size() - 1);
        
        return territoriTerrestri.get(indice);
    }
    
    /**
     * Trova i punti di frontiera attorno a un insieme di territori con supporto migliorato
     * per l'espansione su territori terrestri occupati da altri stati.
     *
     * @param territori lista di territori da cui trovare la frontiera
     * @param visited matrice di territori già visitati
     * @return lista di punti sulla frontiera
     */
    private List<Point> trovaPuntiFrontieraMigliorata(List<Territorio> territori, boolean[][] visited) {
        List<Point> frontiera = new ArrayList<>();
        
        for (Territorio t : territori) {
            // Trova le coordinate del territorio
            Point p = trovaCoordinateTerritorio(t);
            if (p == null) continue;
            
            int i = p.x;
            int j = p.y;
            
            // Solo le quattro direzioni cardinali
            int[][] direzioni = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}  // Nord, Sud, Ovest, Est
            };
            
            for (int[] dir : direzioni) {
                int ni = i + dir[0];
                int nj = j + dir[1];
                
                if (isValid(ni, nj) && !visited[ni][nj]) {
                    Territorio vicino = mappaTerritori[ni][nj];
                    if (vicino != null && vicino.getStatoPadrone() == null) {
                        // Includiamo sia territori terrestri che acquatici nella frontiera
                        frontiera.add(new Point(ni, nj));
                    }
                }
            }
        }
        
        return frontiera;
    }
    
    /**
     * Trova le coordinate di un territorio nella mappa.
     *
     * @param territorio territorio da localizzare
     * @return punto contenente le coordinate, o null se non trovato
     */
    private Point trovaCoordinateTerritorio(Territorio territorio) {
        for (int i = 0; i < mappaTerritori.length; i++) {
            for (int j = 0; j < mappaTerritori[0].length; j++) {
                if (mappaTerritori[i][j] == territorio) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    /**
     * Crea regioni all'interno di uno stato, partendo dalla regione capitale.
     *
     * @param stato lo stato per cui creare le regioni
     * @return numero di regioni create
     */
    public int creaRegioniPerStato(Stato stato) {
        if (stato == null || mappaTerritori == null) {
            return 0;
        }
        
        // Verifica che la regione capitale esista
        if (stato.getCapitale() == null) {
            System.out.println("Lo stato non ha una regione capitale.");
            return 0;
        }
        
        // Ottieni capoluogo della regione capitale
        Territorio capoluogoCapitale = stato.getCapitale().getCapoluogo();
        if (capoluogoCapitale == null) {
            System.out.println("La regione capitale non ha un capoluogo.");
            return 0;
        }
        
        // Trova coordinate del capoluogo
        Point coordCapitale = trovaCoordinateTerritorio(capoluogoCapitale);
        if (coordCapitale == null) {
            System.out.println("Impossibile trovare le coordinate del capoluogo della capitale.");
            return 0;
        }
        
        // Matrice per tenere traccia dei territori già visitati
        boolean[][] visited = new boolean[mappaTerritori.length][mappaTerritori[0].length];
        
        // Marca tutti i territori dello stato come visitati
        for (Regione regione : stato.getRegioni()) {
            for (Territorio t : regione.getTerritori()) {
                Point p = trovaCoordinateTerritorio(t);
                if (p != null) {
                    visited[p.x][p.y] = true;
                }
            }
        }
        
        // Numero di regioni da creare (variabile in base alla dimensione dello stato)
        // Calcoliamo il numero totale di territori
        int numTerritori = 0;
        for (Regione regione : stato.getRegioni()) {
            numTerritori += regione.getTerritori().size();
        }
        
        // In base al numero di territori, calcoliamo quante regioni aggiuntive creare
        int numRegioniDaCreare = 1 + numTerritori / 50; // Una regione ogni 50 territori circa
        
        System.out.println("Creazione di " + numRegioniDaCreare + " regioni aggiuntive per lo stato " + 
                           stato.getNome() + " con " + numTerritori + " territori");
        
        // Trova territori di frontiera della regione capitale
        List<Point> frontiera = new ArrayList<>();
        for (Territorio t : stato.getCapitale().getTerritori()) {
            Point p = trovaCoordinateTerritorio(t);
            if (p == null) continue;
            
            // Controlla i vicini in tutte le 4 direzioni
            int[][] direzioni = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : direzioni) {
                int ni = p.x + dir[0];
                int nj = p.y + dir[1];
                
                if (isValid(ni, nj) && !visited[ni][nj]) {
                    Territorio vicino = mappaTerritori[ni][nj];
                    if (vicino != null && vicino.getStatoPadrone() == null) {
                        frontiera.add(new Point(ni, nj));
                    }
                }
            }
        }
        
        if (frontiera.isEmpty()) {
            System.out.println("Non ci sono territori di frontiera disponibili per nuove regioni.");
            return 1; // Rimane solo la regione capitale
        }
        
        // Crea le regioni aggiuntive
        int regioniCreate = 1; // Contando la capitale
        
        for (int i = 0; i < numRegioniDaCreare && !frontiera.isEmpty(); i++) {
            // Scegli un punto di frontiera casuale
            int index = random.nextInt(frontiera.size());
            Point puntoRegione = frontiera.get(index);
            frontiera.remove(index);
            
            if (visited[puntoRegione.x][puntoRegione.y]) continue;
            
            String nomeRegione = "Regione_" + (i + 2) + "_di_" + stato.getNome();
            int dimensioneRegione = 20 + random.nextInt(30); // Da 20 a 50 territori circa
            
            // Utilizzo floodFillRegionMigliorato invece di floodFillRegion
            List<Territorio> territoriRegione = floodFillRegionMigliorato(puntoRegione.x, puntoRegione.y, 
                                              dimensioneRegione, visited);
            
            if (territoriRegione.size() >= 5) { // Accetta solo regioni con almeno 5 territori
                Territorio capoluogoRegione = scegliCapoluogo(territoriRegione);
                Regione nuovaRegione = new Regione(nomeRegione, capoluogoRegione);
                
                // Aggiorna i territori della nuova regione
                for (Territorio terr : territoriRegione) {
                    terr.setRegione(nuovaRegione.getNome());
                    terr.setStatoPadrone(stato.getNome());
                    
                    // Assegna un numero di civili se non già assegnato
                    if (terr.getNumCivili() == 0) {
                        terr.setNumCivili(800 + random.nextInt(5000));
                    }
                    
                    nuovaRegione.addTerritorio(terr);
                }
                
                stato.addRegione(nuovaRegione);
                regioniCreate++;
                
                // Aggiorna la frontiera
                for (Territorio t : territoriRegione) {
                    Point p = trovaCoordinateTerritorio(t);
                    if (p == null) continue;
                    
                    // Controlla i vicini in tutte le 4 direzioni
                    int[][] direzioni = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                    for (int[] dir : direzioni) {
                        int ni = p.x + dir[0];
                        int nj = p.y + dir[1];
                        
                        if (isValid(ni, nj) && !visited[ni][nj]) {
                            Territorio vicino = mappaTerritori[ni][nj];
                            if (vicino != null && vicino.getStatoPadrone() == null) {
                                frontiera.add(new Point(ni, nj));
                            }
                        }
                    }
                }
                
                System.out.println("Creata regione '" + nuovaRegione.getNome() + "' con " + 
                                  territoriRegione.size() + " territori");
            }
        }
        
        return regioniCreate;
    }

    /**
     * Versione migliorata del metodo floodFill che permette l'espansione su territori terra.
     *
     * @param startRow riga di partenza
     * @param startCol colonna di partenza
     * @param maxSize dimensione massima della regione
     * @param visited matrice di territori già visitati
     * @return lista di territori nella regione
     */
    private List<Territorio> floodFillRegionMigliorato(int startRow, int startCol, int maxSize, boolean[][] visited) {
        List<Territorio> region = new ArrayList<>();
        LinkedList<Point> queue = new LinkedList<>();
        queue.add(new Point(startRow, startCol));
        
        // Teniamo traccia dei territori marini attraversati
        int contatoreMare = 0;
        // Massimo di territori marini consentiti (un quarto della dimensione totale)
        int maxTerritoriMarini = maxSize / 4;

        while (!queue.isEmpty() && region.size() < maxSize) {
            Point p = queue.poll();
            int i = p.x;
            int j = p.y;

            if (!isValid(i, j) || visited[i][j])
                continue;

            Territorio t = mappaTerritori[i][j];
            if (t == null || t.getStatoPadrone() != null)
                continue;

            visited[i][j] = true;
            region.add(t);
            
            // Solo le quattro direzioni cardinali per un'espansione più controllata
            int[][] direzioni = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}  // Nord, Sud, Ovest, Est
            };
            
            // Se è un territorio acquatico, incrementiamo il contatore
            if (t.isAcqua()) {
                contatoreMare++;
                if (contatoreMare > maxTerritoriMarini) {
                    // Salta l'aggiunta di nuovi territori marini se abbiamo superato il limite
                    continue;
                }
            }
            
            // Aggiungiamo i vicini alla coda
            for (int[] dir : direzioni) {
                int ni = i + dir[0];
                int nj = j + dir[1];
                
                if (isValid(ni, nj) && !visited[ni][nj]) {
                    Territorio vicino = mappaTerritori[ni][nj];
                    if (vicino != null && vicino.getStatoPadrone() == null) {
                        // Per i territori acquatici, li aggiungiamo solo se non abbiamo superato il limite
                        if (!vicino.isAcqua() || contatoreMare < maxTerritoriMarini) {
                            queue.add(new Point(ni, nj));
                        }
                    }
                }
            }
        }
        
        return region;
    }

    /**
     * Verifica che le coordinate (i, j) siano valide nella matrice dei territori.
     *
     * @param i indice di riga
     * @param j indice di colonna
     * @return true se (i, j) sono all'interno dei limiti della matrice, false altrimenti
     */
    private boolean isValid(int i, int j) {
        return i >= 0 && i < mappaTerritori.length && j >= 0 && j < mappaTerritori[0].length;
    }

    /**
     * Restituisce la matrice dei territori generata.
     *
     * @return matrice dei territori
     */
    public Territorio[][] getMappaTerritori() {
        return mappaTerritori;
    }
}