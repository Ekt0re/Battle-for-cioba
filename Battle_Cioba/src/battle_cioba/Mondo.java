package battle_cioba;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

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
     * Crea un nuovo stato con un nome e un presidente specificati.
     *
     * @param nome nome dello stato
     * @param presidente presidente dello stato
     * @return lo stato creato
     */
    public Stato creaStato(String nome, Presidente presidente) {
        // Creiamo un nuovo stato con il presidente
        Stato nuovoStato = new Stato(nome, presidente, 100, 0, null);
        stati.add(nuovoStato);
        return nuovoStato;
    }

    /**
     * Genera un certo numero di stati sulla mappa.
     * Ogni stato avrà un insieme di regioni contigue o separate da territori marittimi.
     *
     * @param numStati numero di stati da generare
     * @return numero di stati effettivamente creati
     */
    public int generaStati(int numStati) {
        // Chiamata al nuovo metodo con liste vuote (per retrocompatibilità)
        return generaStati(numStati, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Genera un certo numero di stati sulla mappa.
     * Ogni stato avrà un insieme di regioni contigue o separate da territori marittimi.
     * Utilizza nomi personalizzati per gli stati, presidenti, capitali e capoluoghi.
     *
     * @param numStati numero di stati da generare
     * @param nomiStati lista di nomi da utilizzare per gli stati
     * @param nomiPresidenti lista di nomi da utilizzare per i presidenti
     * @param cognomiPresidenti lista di cognomi da utilizzare per i presidenti
     * @param nomiCapitali lista di nomi da utilizzare per le capitali
     * @param nomiCapoluoghi lista di nomi da utilizzare per i capoluoghi
     * @return numero di stati effettivamente creati
     */
    public int generaStati(int numStati, List<String> nomiStati, List<String> nomiPresidenti, 
                          List<String> cognomiPresidenti, List<String> nomiCapitali, List<String> nomiCapoluoghi) {
        if (mappaTerritori == null) {
            System.out.println("Errore: la mappa dei territori non è stata generata.");
            return 0;
        }
        
        // Debug: stampa le liste di nomi ricevuti
        System.out.println("DEBUG - generaStati ricevuti " + nomiCapoluoghi.size() + " elementi per capoluoghi:");
        for (int i = 0; i < nomiCapoluoghi.size(); i++) {
            System.out.println(" - Capoluogo " + i + ": '" + nomiCapoluoghi.get(i) + "'");
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
        int statiDaCreare = Math.min(numStati, puntidiPartenza.size() / 3); // Considera che ogni stato potrebbe richiedere più tentativi
        int statiCreati = 0;
        
        System.out.println("Generazione di " + statiDaCreare + " stati con una dimensione media di " + 
                          dimensioneMediaStato + " territori ciascuno (totale territori terrestri: " + 
                          totaleTerritoriTerrestri + ")");
        
        // Numero massimo di tentativi per stato
        final int MAX_TENTATIVI_PER_STATO = 3;
        
        // Crea gli stati
        for (int i = 0; i < statiDaCreare; i++) {
            // Nome dello stato (usa il nome dalla lista se disponibile)
            String nomeStato = "Stato_" + (i + 1);
            if (i < nomiStati.size()) {
                nomeStato = nomiStati.get(i);
            }
            
            // Crea il presidente con il nome e cognome dalla lista
            Presidente presidente = null;
            if (i < nomiPresidenti.size()) {
                String nomePresidente = nomiPresidenti.get(i);
                String cognomePresidente = "";
                if (i < cognomiPresidenti.size()) {
                    cognomePresidente = cognomiPresidenti.get(i);
                }
                // Crea il presidente con nome e cognome separati
                presidente = creaPresidente(nomePresidente, cognomePresidente);
            }
            
            // Recupera i nomi personalizzati per capitali e capoluoghi
            String nomeCapitale = (i < nomiCapitali.size()) ? nomiCapitali.get(i) : "Capitale_" + nomeStato;
            String nomeCapoluogo = (i < nomiCapoluoghi.size()) ? nomiCapoluoghi.get(i) : "Capoluogo_" + nomeStato;
            
            // Crea lo stato con il nome personalizzato e il presidente
            Stato nuovoStato = presidente != null ? creaStato(nomeStato, presidente) : creaStato(nomeStato);
            
            // Calcola la dimensione target per questo stato
            // Variazione casuale del ±20% rispetto alla media
            double fattoreVariazione = 0.8 + (random.nextDouble() * 0.4);
            int dimensioneTarget = (int)(dimensioneMediaStato * fattoreVariazione);
            
            boolean success = false;
            int tentativi = 0;
            Set<Integer> indiciProvati = new HashSet<>(); // Per tenere traccia dei punti di partenza già provati
            
            // Tenta più volte se necessario
            while (!success && tentativi < MAX_TENTATIVI_PER_STATO && !puntidiPartenza.isEmpty()) {
                tentativi++;
                
                // Seleziona casualmente un punto di partenza non ancora tentato
                int indexPunto = -1;
                do {
                    if (puntidiPartenza.isEmpty()) {
                        break;
                    }
                    indexPunto = random.nextInt(puntidiPartenza.size());
                } while (indiciProvati.contains(indexPunto) && indiciProvati.size() < puntidiPartenza.size());
                
                if (puntidiPartenza.isEmpty()) {
                    System.out.println("Terminati i punti di partenza disponibili");
                    break;
                }
                
                indiciProvati.add(indexPunto);
                Point puntoIniziale = puntidiPartenza.get(indexPunto);
                
                System.out.println("Tentativo " + tentativi + " per lo stato " + nomeStato + " dal punto " + puntoIniziale.x + "," + puntoIniziale.y);
                
                // Genera il nucleo iniziale dello stato con la dimensione calcolata
                success = generaNucleoStatoMigliorato(nuovoStato, puntoIniziale, visited, dimensioneTarget, nomeCapitale, nomeCapoluogo);
                
                if (success) {
                    // Rimuove il punto usato con successo
                    puntidiPartenza.remove(indexPunto);
                    System.out.println("Stato " + nomeStato + " creato con successo al tentativo " + tentativi);
                } else {
                    System.out.println("Tentativo " + tentativi + " fallito per lo stato " + nomeStato);
                }
            }
            
            if (success) {
                statiCreati++;
            } else {
                rimuoviStato(nuovoStato);
                System.out.println("Impossibile creare lo stato " + nomeStato + " dopo " + tentativi + " tentativi.");
            }
        }
        
        System.out.println("GENERAZIONE STATI COMPLETATA: Creati " + statiCreati + " stati su " + statiDaCreare + " richiesti");
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
     * Trova il territorio più centrale in una lista di territori.
     *
     * @param territori lista di territori da cui trovare il territorio centrale
     * @return il territorio centrale
     */
    private Territorio trovaTerritorioCentrale(List<Territorio> territori) {
        if (territori.isEmpty()) {
            return null;
        }
        
        // Alternativa 1: usa semplicemente il territorio a metà della lista
        if (territori.size() <= 5) {
            return territori.get(territori.size() / 2);
        }
        
        // Alternativa 2: trova le coordinate di ciascun territorio e calcola il centro
        List<Point> punti = new ArrayList<>();
        for (Territorio t : territori) {
            Point p = trovaCoordinateTerritorio(t);
            if (p != null) {
                punti.add(p);
            }
        }
        
        if (punti.isEmpty()) {
            // Se non riusciamo a trovare coordinate, usa il metodo semplice
            return territori.get(territori.size() / 2);
        }
        
        // Calcola il centro geometrico
        Point centro = new Point(0, 0);
        for (Point p : punti) {
            centro.x += p.x;
            centro.y += p.y;
        }
        centro.x /= punti.size();
        centro.y /= punti.size();
        
        // Trova il territorio più vicino al centro
        Territorio territorioPiuVicino = null;
        double distanzaMinima = Double.MAX_VALUE;
        
        for (int i = 0; i < territori.size(); i++) {
            if (i >= punti.size()) continue; // Salta se ci sono più territori che punti
            
            Point p = punti.get(i);
            double distanza = Math.sqrt(Math.pow(p.x - centro.x, 2) + Math.pow(p.y - centro.y, 2));
            
            if (distanza < distanzaMinima) {
                distanzaMinima = distanza;
                territorioPiuVicino = territori.get(i);
            }
        }
        
        return territorioPiuVicino != null ? territorioPiuVicino : territori.get(territori.size() / 2);
    }

    /**
     * Aggiorna la lista dei territori disponibili rimuovendo quelli già visitati.
     *
     * @param punti lista di punti di partenza
     * @param visited matrice di territori già visitati
     */
    private void aggiornaTerreniDisponibili(List<Point> punti, boolean[][] visited) {
        punti.removeIf(p -> visited[(int)p.getX()][(int)p.getY()]);
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
     * Applica l'algoritmo di flood fill per creare una regione di territori contigui.
     * Versione migliorata che previene la frammentazione e assicura maggiore contiguità.
     *
     * @param startRow riga di partenza
     * @param startCol colonna di partenza
     * @param maxSize dimensione massima della regione
     * @param visited matrice di territori già visitati
     * @return lista di territori che formano la regione
     */
    private List<Territorio> floodFillRegionMigliorato(int startRow, int startCol, int maxSize, boolean[][] visited) {
        List<Territorio> region = new ArrayList<>();
        LinkedList<Point> queue = new LinkedList<>();
        queue.add(new Point(startRow, startCol));
        
        // Teniamo traccia dei territori marini attraversati
        int contatoreMare = 0;
        // Massimo di territori marini consentiti (ridotto per favorire la contiguità terrestre)
        int maxTerritoriMarini = Math.max(3, maxSize / 4);
        
        // Lista di priorità per i territori terrestri
        PriorityQueue<Point> tierreTerrestri = new PriorityQueue<>((p1, p2) -> {
            int countViciniVisitati1 = contaViciniVisitati(p1.x, p1.y, visited);
            int countViciniVisitati2 = contaViciniVisitati(p2.x, p2.y, visited);
            // Priorità ai territori con più vicini già visitati (per favorire la contiguità)
            return Integer.compare(countViciniVisitati2, countViciniVisitati1);
        });
        
        // Matrice per tenere traccia di territori visitati solo durante il flood fill corrente
        boolean[][] tempVisited = new boolean[mappaTerritori.length][mappaTerritori[0].length];
        
        // Mappa per tenere traccia dei "buchi" (territori circondati completamente)
        boolean[][] potenzialiBuchi = new boolean[mappaTerritori.length][mappaTerritori[0].length];
        
        // Primo passaggio: espansione principale
        while ((!queue.isEmpty() || !tierreTerrestri.isEmpty()) && region.size() < maxSize) {
            Point p;
            
            // Preferisci espandere prima su tierra che su mare
            if (!tierreTerrestri.isEmpty() && region.size() > 0) {
                p = tierreTerrestri.poll();
            } else if (!queue.isEmpty()) {
                p = queue.poll();
            } else {
                break;
            }
            
            int i = p.x;
            int j = p.y;

            if (!isValid(i, j) || visited[i][j])
                continue;

            Territorio t = mappaTerritori[i][j];
            if (t == null || t.getStatoPadrone() != null)
                continue;

            visited[i][j] = true;
            tempVisited[i][j] = true;
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
                        Point nuovoPunto = new Point(ni, nj);
                        
                        // Dividi in base al tipo di territorio
                        if (!vicino.isAcqua()) {
                            // I territori terrestri hanno priorità
                            tierreTerrestri.add(nuovoPunto);
                        } else if (contatoreMare < maxTerritoriMarini) {
                            // I territori marini hanno meno priorità
                            queue.add(nuovoPunto);
                        }
                    }
                }
            }
            
            // Ridotto il "salto" attraverso il mare per favorire la contiguità
            // Lo facciamo solo se abbiamo una regione molto piccola e abbiamo bisogno di espanderci
            if (!t.isAcqua() && region.size() < maxSize/3 && region.size() % 15 == 0) {
                // Cerchiamo territori terrestri non connessi che potrebbero essere raggiunti attraverso il mare
                List<Point> puntiTerraNonConnessi = trovaPuntiTerraNonConnessi(visited, tempVisited, maxTerritoriMarini - contatoreMare);
                
                // Limitiamo a un solo punto non connesso per volta per mantenere la contiguità
                if (!puntiTerraNonConnessi.isEmpty()) {
                    Point puntoTerra = puntiTerraNonConnessi.get(0);
                    if (!visited[puntoTerra.x][puntoTerra.y]) {
                        tierreTerrestri.add(puntoTerra);
                        System.out.println("Aggiunto punto terra non connesso: " + puntoTerra.x + "," + puntoTerra.y);
                    }
                }
            }
        }
        
        // Secondo passaggio: identifica e riempi i "buchi" (territori circondati completamente da territori dello stato)
        if (region.size() < maxSize) {
            // Identifica potenziali buchi (territori non visitati circondati da territori visitati)
            for (int i = 1; i < mappaTerritori.length - 1; i++) {
                for (int j = 1; j < mappaTerritori[0].length - 1; j++) {
                    if (!isValid(i, j) || visited[i][j])
                        continue;
                        
                    Territorio t = mappaTerritori[i][j];
                    if (t == null || t.getStatoPadrone() != null)
                        continue;
                        
                    // Controlla se è circondato su tutti e quattro i lati da territori visitati o bordi della mappa
                    boolean circondatoDaNord = !isValid(i-1, j) || visited[i-1][j];
                    boolean circondatoDaSud = !isValid(i+1, j) || visited[i+1][j];
                    boolean circondatoDaOvest = !isValid(i, j-1) || visited[i][j-1];
                    boolean circondatoDaEst = !isValid(i, j+1) || visited[i][j+1];
                    
                    // Se è circondato su almeno 3 lati, lo segniamo come potenziale buco
                    int latiCircondati = 0;
                    if (circondatoDaNord) latiCircondati++;
                    if (circondatoDaSud) latiCircondati++;
                    if (circondatoDaOvest) latiCircondati++;
                    if (circondatoDaEst) latiCircondati++;
                    
                    // Riempie più aggressivamente i buchi, riducendo la frammentazione
                    // Considera anche territori circondati da 2 lati se sono territori terrestri
                    boolean èBuco = latiCircondati >= 3 || (!t.isAcqua() && latiCircondati >= 2);
                    
                    if (èBuco) {
                        potenzialiBuchi[i][j] = true;
                        
                        // Se abbiamo ancora spazio, aggiungiamo subito il territorio alla regione
                        if (region.size() < maxSize && (!t.isAcqua() || contatoreMare < maxTerritoriMarini)) {
                            visited[i][j] = true;
                            region.add(t);
                            if (t.isAcqua()) contatoreMare++;
                            System.out.println("Riempito buco in posizione: " + i + "," + j);
                        }
                    }
                }
            }
            
            // Se abbiamo ancora spazio, cerchiamo di riempire ulteriori buchi
            if (region.size() < maxSize) {
                // Priorità per i territori terrestri circondati parzialmente
                PriorityQueue<Point> buchiQueue = new PriorityQueue<>((p1, p2) -> {
                    boolean t1Terra = !mappaTerritori[p1.x][p1.y].isAcqua();
                    boolean t2Terra = !mappaTerritori[p2.x][p2.y].isAcqua();
                    if (t1Terra != t2Terra) {
                        return t1Terra ? -1 : 1; // Territori terrestri hanno priorità
                    }
                    // Altrimenti, priorità in base al numero di vicini visitati
                    return Integer.compare(contaViciniVisitati(p2.x, p2.y, visited), 
                                          contaViciniVisitati(p1.x, p1.y, visited));
                });
                
                // Aggiungiamo alla coda tutti i territori adiacenti ai buchi già riempiti
                for (int i = 0; i < mappaTerritori.length; i++) {
                    for (int j = 0; j < mappaTerritori[0].length; j++) {
                        if (potenzialiBuchi[i][j]) {
                            // Controlla i vicini
                            int[][] direzioni = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                            for (int[] dir : direzioni) {
                                int ni = i + dir[0];
                                int nj = j + dir[1];
                                
                                if (isValid(ni, nj) && !visited[ni][nj] && !potenzialiBuchi[ni][nj]) {
                                    Territorio vicino = mappaTerritori[ni][nj];
                                    if (vicino != null && vicino.getStatoPadrone() == null) {
                                        buchiQueue.add(new Point(ni, nj));
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Riempi i buchi rimanenti finché c'è spazio
                while (!buchiQueue.isEmpty() && region.size() < maxSize) {
                    Point p = buchiQueue.poll();
                    int i = p.x;
                    int j = p.y;
                    
                    if (visited[i][j]) continue;
                    
                    Territorio t = mappaTerritori[i][j];
                    if (t == null || t.getStatoPadrone() != null) continue;
                    
                    // Se è un territorio acquatico, controlliamo il limite
                    if (t.isAcqua() && contatoreMare >= maxTerritoriMarini) continue;
                    
                    visited[i][j] = true;
                    region.add(t);
                    if (t.isAcqua()) contatoreMare++;
                    
                    // Aggiungi i vicini alla coda
                    int[][] direzioni = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                    for (int[] dir : direzioni) {
                        int ni = i + dir[0];
                        int nj = j + dir[1];
                        
                        if (isValid(ni, nj) && !visited[ni][nj]) {
                            Territorio vicino = mappaTerritori[ni][nj];
                            if (vicino != null && vicino.getStatoPadrone() == null) {
                                buchiQueue.add(new Point(ni, nj));
                            }
                        }
                    }
                }
            }
        }
        
        // Verifica finale: rimuovi territori isolati per migliorare la contiguità
        if (region.size() > 5) {
            rimuoviTerritoriIsolati(region, visited);
        }
        
        return region;
    }
    
    /**
     * Conta quanti territori vicini sono già stati visitati.
     * Utile per dare priorità ai territori che formano regioni più compatte.
     * 
     * @param row riga del territorio
     * @param col colonna del territorio
     * @param visited matrice di territori già visitati
     * @return numero di vicini visitati
     */
    private int contaViciniVisitati(int row, int col, boolean[][] visited) {
        int count = 0;
        int[][] direzioni = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int[] dir : direzioni) {
            int ni = row + dir[0];
            int nj = col + dir[1];
            
            if (isValid(ni, nj) && visited[ni][nj]) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Rimuove territori isolati (quelli che hanno meno di 2 vicini nella stessa regione).
     * Questo migliora la contiguità delle regioni.
     * 
     * @param region lista di territori della regione
     * @param visited matrice dei territori visitati da aggiornare
     */
    private void rimuoviTerritoriIsolati(List<Territorio> region, boolean[][] visited) {
        List<Territorio> toRemove = new ArrayList<>();
        
        for (Territorio territorio : region) {
            Point coords = trovaCoordinateTerritorio(territorio);
            if (coords == null) continue;
            
            int viciniNellaRegione = contaViciniVisitati(coords.x, coords.y, visited);
            
            // Se è un territorio terrestre con meno di 2 connessioni, lo consideriamo isolato
            if (!territorio.isAcqua() && viciniNellaRegione < 2) {
                toRemove.add(territorio);
                visited[coords.x][coords.y] = false;
            }
        }
        
        // Rimuovi i territori isolati dalla regione
        if (!toRemove.isEmpty()) {
            System.out.println("Rimossi " + toRemove.size() + " territori isolati per migliorare la contiguità");
            region.removeAll(toRemove);
        }
    }

    /**
     * Trova punti terrestri non connessi che potrebbero essere raggiunti attraverso il mare.
     * Questi punti sono potenziali candidati per espandere uno stato attraverso il mare.
     *
     * @param visited matrice di territori già visitati globalmente
     * @param tempVisited matrice di territori visitati solo nell'operazione corrente
     * @param maxMareDisponibile quanti territori marini possiamo ancora attraversare
     * @return lista di punti terrestri non connessi raggiungibili attraverso il mare
     */
    private List<Point> trovaPuntiTerraNonConnessi(boolean[][] visited, boolean[][] tempVisited, int maxMareDisponibile) {
        List<Point> puntiCandidati = new ArrayList<>();
        
        // Fattore di distanza massima per saltare attraverso il mare (proporzionale a maxMareDisponibile)
        int distanzaMax = Math.min(10, maxMareDisponibile);
        
        // Cerca territori terrestri non visitati
        for (int i = 0; i < mappaTerritori.length; i++) {
            for (int j = 0; j < mappaTerritori[0].length; j++) {
                if (isValid(i, j) && !visited[i][j] && mappaTerritori[i][j] != null && 
                    !mappaTerritori[i][j].isAcqua() && mappaTerritori[i][j].getStatoPadrone() == null) {
                    
                    // Trova il territorio visitato più vicino
                    Point puntoVicino = trovaPuntoVisitatoPiuVicino(i, j, tempVisited, distanzaMax);
                    
                    if (puntoVicino != null) {
                        // Calcola la distanza tra i punti
                        int distanza = Math.abs(i - puntoVicino.x) + Math.abs(j - puntoVicino.y);
                        
                        // Se la distanza è accettabile, aggiungi il punto come candidato
                        if (distanza <= distanzaMax) {
                            puntiCandidati.add(new Point(i, j));
                        }
                    }
                }
            }
        }
        
        // Ordina i punti candidati per distanza al bordo più vicino (meno distanza = migliore)
        // E limita il numero di candidati
        if (puntiCandidati.size() > 10) {
            // Prendiamo solo 10 punti casuali per evitare calcoli pesanti
            Collections.shuffle(puntiCandidati, random);
            return puntiCandidati.subList(0, 10);
        }
        
        return puntiCandidati;
    }
    
    /**
     * Trova il punto già visitato più vicino a un dato punto.
     *
     * @param row riga del punto di riferimento
     * @param col colonna del punto di riferimento
     * @param tempVisited matrice di territori già visitati
     * @param maxDistanza distanza massima da considerare
     * @return il punto visitato più vicino entro la distanza massima, o null se non trovato
     */
    private Point trovaPuntoVisitatoPiuVicino(int row, int col, boolean[][] tempVisited, int maxDistanza) {
        Point puntoVicino = null;
        int distanzaMinima = Integer.MAX_VALUE;
        
        // Cerca in un'area limitata attorno al punto dato
        int startRow = Math.max(0, row - maxDistanza);
        int endRow = Math.min(mappaTerritori.length - 1, row + maxDistanza);
        int startCol = Math.max(0, col - maxDistanza);
        int endCol = Math.min(mappaTerritori[0].length - 1, col + maxDistanza);
        
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                if (tempVisited[i][j]) {
                    // Calcola la distanza (Manhattan distance)
                    int distanza = Math.abs(row - i) + Math.abs(col - j);
                    
                    if (distanza < distanzaMinima && distanza > 0) {
                        distanzaMinima = distanza;
                        puntoVicino = new Point(i, j);
                    }
                }
            }
        }
        
        return puntoVicino;
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
     * Seleziona un territorio adatto come capoluogo tra i territori forniti.
     * Preferibilmente un territorio terrestre e non ai bordi.
     *
     * @param territori lista di territori candidati
     * @return il territorio scelto come capoluogo, null se non ci sono territori terrestri
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
     * Genera il nucleo territoriale di uno stato a partire da un punto sulla mappa con dimensione migliorata.
     * 
     * @param stato stato da generare
     * @param puntoIniziale punto di partenza
     * @param visited matrice di territori già visitati
     * @param dimensioneTarget dimensione target dello stato
     * @param nomeCapitaleCustom nome personalizzato per la capitale (se fornito)
     * @param nomeCapoluogoCustom nome personalizzato per i capoluoghi (se fornito)
     * @return true se lo stato è stato generato con successo, false altrimenti
     */
    private boolean generaNucleoStatoMigliorato(Stato stato, Point puntoIniziale, boolean[][] visited, 
                                             int dimensioneTarget, String nomeCapitaleCustom, String nomeCapoluogoCustom) {
        // Lista dei nomi di capoluoghi disponibili per questo stato
        // Dividiamo la stringa del capoluogo che potrebbe contenere più nomi separati da ";"
        List<String> nomiCapoluoghiDisponibili = new ArrayList<>();
        
        // Debug: stampa i nomi dei capoluoghi ricevuti
        System.out.println("DEBUG - Nomi capoluoghi ricevuti per lo stato " + stato.getNome() + ": " + nomeCapoluogoCustom);
        
        if (nomeCapoluogoCustom != null && !nomeCapoluogoCustom.isEmpty()) {
            // Se il nome contiene ";" lo dividiamo in più nomi
            if (nomeCapoluogoCustom.contains(";")) {
                String[] nomi = nomeCapoluogoCustom.split(";");
                for (String nome : nomi) {
                    nomiCapoluoghiDisponibili.add(nome.trim());
                }
            } else {
                nomiCapoluoghiDisponibili.add(nomeCapoluogoCustom);
            }
        }
        
        // Debug: stampa i nomi dei capoluoghi disponibili dopo il parsing
        System.out.println("DEBUG - Nomi capoluoghi disponibili dopo parsing: " + nomiCapoluoghiDisponibili);
        
        // Numero di regioni da creare per lo stato (da 3 a 6 per stati più realistici)
        int numRegioni = 3 + random.nextInt(4);
        
        // Se non abbiamo abbastanza nomi di capoluoghi, ne aggiungiamo alcuni generici
        while (nomiCapoluoghiDisponibili.size() < numRegioni) {
            nomiCapoluoghiDisponibili.add("Capoluogo_" + stato.getNome() + "_" + (nomiCapoluoghiDisponibili.size() + 1));
        }
        
        // DEBUG: stampa tutti i nomi disponibili
        System.out.println("DEBUG - Lista completa nomi capoluoghi per " + stato.getNome() + ":");
        for (int i = 0; i < nomiCapoluoghiDisponibili.size(); i++) {
            System.out.println("  " + i + ": " + nomiCapoluoghiDisponibili.get(i));
        }
        
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
        
        // Usa il nome personalizzato per la capitale se fornito
        String nomeCapitale = nomeCapitaleCustom != null && !nomeCapitaleCustom.isEmpty() 
                            ? nomeCapitaleCustom 
                            : "Capitale_" + stato.getNome();
        
        // Crea un oggetto Capitale con i parametri richiesti
        int importanzaStrategica = 10; // Massima importanza
        int livelloDifesa = 9; // Alta difesa
        long popolazione = 50000 + random.nextInt(150000); // Popolazione significativa
        int livelloPolitico = 10; // Massimo livello politico
        
        Capitale capitaleOggetto = new Capitale(
            nomeCapitale,
            importanzaStrategica,
            livelloDifesa,
            popolazione,
            territorioCapitale,
            stato.getNome(),
            regioneCapitale.getNome(),
            livelloPolitico,
            stato
        );
        
        // Assegna la Capitale al territorio
        territorioCapitale.setCentro(capitaleOggetto);
        
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
                           "' con " + territoriCapitale.size() + " territori e Capitale '" + 
                           nomeCapitale + "' posizionata");
        
        // Trova territori adiacenti per le altre regioni
        List<Point> frontiera = new ArrayList<>();
        for (Territorio t : territoriCapitale) {
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
        
        // Genera le altre regioni
        int regioniCreate = 1; // La capitale è già stata creata
        int tentativi = 0; // Teniamo traccia dei tentativi per evitare loop infiniti
        int maxTentativi = numRegioni * 5; // Numero massimo di tentativi
        
        // Tieni traccia degli indici di nomi di capoluoghi già utilizzati
        Set<Integer> indiciNomiUtilizzati = new HashSet<>();
        indiciNomiUtilizzati.add(0); // Il primo nome è per la capitale (seppur non utilizzato come capoluogo)
        
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
            
            if (territoriRegione.size() >= 5) { // Accetta solo regioni con almeno 5 territori
                Territorio capoluogoRegione = scegliCapoluogo(territoriRegione);
                Regione nuovaRegione = new Regione(nomeRegione, capoluogoRegione);
                
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
                
                // Crea un capoluogo per la regione
                if (capoluogoRegione != null) {
                    // Trova un nome di capoluogo non ancora utilizzato
                    String nomeCapoluogo;
                    int indiceNome = -1;
                    
                    // Prova a trovare un nome non ancora utilizzato
                    for (int i = 0; i < nomiCapoluoghiDisponibili.size(); i++) {
                        if (!indiciNomiUtilizzati.contains(i)) {
                            indiceNome = i;
                            indiciNomiUtilizzati.add(i);
                            break;
                        }
                    }
                    
                    // Se non ne abbiamo trovato uno, ne generiamo uno nuovo
                    if (indiceNome == -1) {
                        nomeCapoluogo = "Capoluogo_" + nuovaRegione.getNome();
                        System.out.println("DEBUG - Nessun nome disponibile, generato: " + nomeCapoluogo);
                    } else {
                        nomeCapoluogo = nomiCapoluoghiDisponibili.get(indiceNome);
                        System.out.println("DEBUG - Utilizzato nome all'indice " + indiceNome + ": " + nomeCapoluogo);
                    }
                    
                    // Assicuriamoci che il nome non sia un numero
                    if (nomeCapoluogo.matches("\\d+")) {
                        nomeCapoluogo = "Città_" + nomeCapoluogo;
                        System.out.println("DEBUG - Il nome era solo un numero, modificato in: " + nomeCapoluogo);
                    }
                    
                    Capoluogo capoluogoOggetto = new Capoluogo(
                        nomeCapoluogo,
                        6 + random.nextInt(3),    // Importanza media-alta
                        5 + random.nextInt(3),    // Difesa media
                        20000 + random.nextInt(80000), // Popolazione
                        capoluogoRegione,        // Territorio
                        stato.getNome(),         // Nome stato
                        nuovaRegione.getNome(),  // Nome regione
                        6 + random.nextInt(3),   // Livello economico
                        5 + random.nextInt(4),   // Livello culturale
                        nuovaRegione             // Oggetto regione
                    );
                    capoluogoRegione.setCentro(capoluogoOggetto);
                    // Il capoluogo ha più civili
                    capoluogoRegione.setNumCivili(20000 + random.nextInt(80000));
                    
                    System.out.println("Creato capoluogo '" + nomeCapoluogo + "' per la regione " + 
                                      nuovaRegione.getNome());
                }
                
                stato.addRegione(nuovaRegione);
                regioniCreate++;
                
                System.out.println("Creata regione '" + nuovaRegione.getNome() + 
                                   "' con " + territoriRegione.size() + " territori e Capoluogo posizionato");
                
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
            }
        }
        
        return regioniCreate >= numRegioni / 2; // Considera un successo se almeno metà delle regioni sono state create
    }

    /**
     * Crea un nuovo oggetto Presidente con nome e cognome separati.
     *
     * @param nome nome del presidente
     * @param cognome cognome del presidente
     * @return oggetto Presidente creato
     */
    private Presidente creaPresidente(String nome, String cognome) {
        // Se entrambi nome e cognome sono forniti, li passiamo come stringa combinata
        if (!nome.isEmpty() && !cognome.isEmpty()) {
            return new Presidente(nome + " " + cognome);
        } else if (!nome.isEmpty()) {
            return new Presidente(nome);
        } else if (!cognome.isEmpty()) {
            return new Presidente(cognome);
        } else {
            return new Presidente("Presidente");
        }
    }

    /**
     * Genera un certo numero di stati sulla mappa.
     * Ogni stato avrà un insieme di regioni contigue o separate da territori marittimi.
     * Utilizza nomi personalizzati per gli stati e i presidenti.
     *
     * @param numStati numero di stati da generare
     * @param nomiStati lista di nomi da utilizzare per gli stati
     * @param nomiPresidenti lista di nomi da utilizzare per i presidenti
     * @return numero di stati effettivamente creati
     */
    public int generaStati(int numStati, List<String> nomiStati, List<String> nomiPresidenti) {
        // Chiamata al nuovo metodo con liste vuote per i parametri aggiuntivi
        return generaStati(numStati, nomiStati, nomiPresidenti, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}