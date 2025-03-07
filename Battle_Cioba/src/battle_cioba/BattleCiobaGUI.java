package battle_cioba;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BattleCiobaGUI extends JFrame {
    private Mondo mondo;
    private MapPanel mapPanel;
    private JPanel controlPanel;
    private int cellSize = 20;
    private Map<String, Color> coloriStati;
    
    // Variables for panning
    private Point startPoint;
    private Point viewPosition = new Point(0, 0);
    
    // Variables for zooming
    private double zoomFactor = 1.0;
    private static final double ZOOM_STEP = 0.1;
    private static final double MIN_ZOOM = 0.5;
    private static final double MAX_ZOOM = 3.0;
    
    // Variabili per modalità di visualizzazione
    private boolean mostraStati = true;
    private boolean mostraRegioni = false;
    private boolean mostraTerritori = false;
    private boolean mostraCentri = true;
    
    // Flag per il debug - mostra una griglia di base anche senza territori
    private boolean debugMode = true;
    
    // Icone per i diversi tipi di centri
    private BufferedImage iconeCapitale;
    private BufferedImage iconeCapoluogo;
    private BufferedImage iconeBMilitare;
    
    // Dimensione delle icone rispetto alla cella
    private static final double ICON_SIZE_RATIO = 0.8;
    
    /**
     * Pannello personalizzato per la visualizzazione della mappa
     * Implementa correttamente paintComponent
     * Classe static per facilitare il debugging
     */
    static class MapPanel extends JPanel {
        // Riferimento alla classe principale
        private final BattleCiobaGUI gui;
        
        public MapPanel(BattleCiobaGUI gui) {
            this.gui = gui;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Debug output
            System.out.println("DEBUG: paintComponent chiamato");
            
            Graphics2D g2d = (Graphics2D) g;
            
            // Apply transformations for panning and zooming
            g2d.translate(gui.viewPosition.x, gui.viewPosition.y);
            g2d.scale(gui.zoomFactor, gui.zoomFactor);
            
            // Disegna una griglia di base in modalità debug
            if (gui.debugMode) {
                g2d.setColor(Color.LIGHT_GRAY);
                int gridSize = 20;
                int rows = this.getHeight() / gridSize;
                int cols = this.getWidth() / gridSize;
                
                // Disegna le linee orizzontali
                for (int i = 0; i <= rows; i++) {
                    g2d.drawLine(0, i * gridSize, cols * gridSize, i * gridSize);
                }
                
                // Disegna le linee verticali
                for (int i = 0; i <= cols; i++) {
                    g2d.drawLine(i * gridSize, 0, i * gridSize, rows * gridSize);
                }
                
                // Disegna un testo di debug per verificare che il pannello funzioni
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("DEBUG: Pannello di visualizzazione", 50, 50);
                g2d.drawString("Se vedi questo testo, paintComponent funziona", 50, 70);
                g2d.drawString("Premi 'Genera Mappa' per caricare i territori", 50, 90);
            }
            
            // Chiamata al metodo di disegno della mappa effettiva
            gui.disegnaMappa(g2d);
        }
    }

    /**
     * Costruttore della GUI.
     */
    public BattleCiobaGUI() {
        setTitle("Battle Cioba");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        // Inizializzazione del mondo
        mondo = new Mondo();
        
        // Carica le icone
        caricaIcone();
        
        // Crea il pannello della mappa con una classe esplicita invece di una classe anonima
        mapPanel = new MapPanel(this);
        mapPanel.setBackground(Color.WHITE);
        setupMapInteractions(mapPanel);
        
        // Wrapping mapPanel in a JScrollPane for better navigation
        JScrollPane scrollPane = new JScrollPane(mapPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        add(scrollPane, BorderLayout.CENTER);

        // Pannello dei controlli (in basso)
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        
        // Bottone per generare i territori
        JButton generaMappaButton = new JButton("Genera Mappa");
        generaMappaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generaMappa();
            }
        });
        controlPanel.add(generaMappaButton);
        
        // Campo per il numero di stati
        JTextField numStatiField = new JTextField("5", 5);
        controlPanel.add(new JLabel("Numero Stati:"));
        controlPanel.add(numStatiField);
        
        // Bottone per generare gli stati
        JButton generaStatiButton = new JButton("Genera Stati");
        generaStatiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int numStati = Integer.parseInt(numStatiField.getText());
                    generaStati(numStati);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(BattleCiobaGUI.this, 
                            "Inserire un numero valido di stati", 
                            "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        controlPanel.add(generaStatiButton);
        
        // Bottone per visualizzare le statistiche
        JButton statisticheButton = new JButton("Mostra Statistiche");
        statisticheButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraStatistiche();
            }
        });
        controlPanel.add(statisticheButton);
        
        // Checkbox per le modalità di visualizzazione
        JCheckBox mostraStatiBox = new JCheckBox("Mostra Stati", mostraStati);
        mostraStatiBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraStati = mostraStatiBox.isSelected();
                mapPanel.repaint();
            }
        });
        controlPanel.add(mostraStatiBox);
        
        JCheckBox mostraRegioniBox = new JCheckBox("Mostra Regioni", mostraRegioni);
        mostraRegioniBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraRegioni = mostraRegioniBox.isSelected();
                mapPanel.repaint();
            }
        });
        controlPanel.add(mostraRegioniBox);
        
        JCheckBox mostraTerritori = new JCheckBox("Mostra Territori", this.mostraTerritori);
        mostraTerritori.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BattleCiobaGUI.this.mostraTerritori = mostraTerritori.isSelected();
                mapPanel.repaint();
            }
        });
        controlPanel.add(mostraTerritori);
        
        JCheckBox mostraCentriBox = new JCheckBox("Mostra Centri", mostraCentri);
        mostraCentriBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraCentri = mostraCentriBox.isSelected();
                mapPanel.repaint();
            }
        });
        controlPanel.add(mostraCentriBox);
        
        // Debug checkbox
        JCheckBox debugModeBox = new JCheckBox("Modalità Debug", debugMode);
        debugModeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                debugMode = debugModeBox.isSelected();
                mapPanel.repaint();
            }
        });
        controlPanel.add(debugModeBox);
        
        // Button for resetting zoom and position
        JButton resetViewButton = new JButton("Reset Vista");
        resetViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetView();
            }
        });
        controlPanel.add(resetViewButton);
        
        // Bottone per testare la visualizzazione delle capitali
        JButton testCapitaliButton = new JButton("Test Capitali");
        testCapitaliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testVisualizzazioneCapitali();
            }
        });
        controlPanel.add(testCapitaliButton);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        // Inizializza i colori per gli stati (ora mondo è inizializzato)
        generaColoriPerStati();
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Forza il ridisegno del pannello
        mapPanel.repaint();
    }
    
    /**
     * Configura le interazioni del mouse per panning e zooming.
     * @param panel il pannello a cui aggiungere le interazioni
     */
    private void setupMapInteractions(JPanel panel) {
        // Mouse listener for panning (right mouse button)
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    startPoint = e.getPoint();
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    panel.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        
        // Mouse motion listener for panning
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (startPoint != null) {
                        Point currentPoint = e.getPoint();
                        int dx = currentPoint.x - startPoint.x;
                        int dy = currentPoint.y - startPoint.y;
                        
                        viewPosition.x += dx;
                        viewPosition.y += dy;
                        
                        startPoint = currentPoint;
                        panel.repaint();
                    }
                }
            }
        });
        
        // Mouse wheel listener for zooming
        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Get the mouse position for zoom center
                Point mousePoint = e.getPoint();
                
                // Calculate zoom factor
                double oldZoom = zoomFactor;
                zoomFactor -= e.getWheelRotation() * ZOOM_STEP;
                zoomFactor = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoomFactor));
                
                // Adjust view position to zoom at mouse position
                if (oldZoom != zoomFactor) {
                    // Transform the mouse position relative to the view position
                    double relX = (mousePoint.x - viewPosition.x) / oldZoom;
                    double relY = (mousePoint.y - viewPosition.y) / oldZoom;
                    
                    // Calculate the new view position
                    viewPosition.x = mousePoint.x - (int)(relX * zoomFactor);
                    viewPosition.y = mousePoint.y - (int)(relY * zoomFactor);
                    
                    panel.repaint();
                }
            }
        });
        
        // Make panel focusable to receive keyboard events
        panel.setFocusable(true);
    }
    
    /**
     * Reset the zoom and position to default values.
     */
    private void resetView() {
        viewPosition = new Point(0, 0);
        zoomFactor = 1.0;
        mapPanel.repaint();
    }
    
    /**
     * Genera colori casuali per gli stati.
     */
    private void generaColoriPerStati() {
        coloriStati = new HashMap<>();
        if (mondo != null && mondo.getStati() != null) {
            Random random = new Random();
            for (Stato stato : mondo.getStati()) {
                // Crea un colore casuale sufficientemente brillante
                int r = 100 + random.nextInt(156);
                int g = 100 + random.nextInt(156);
                int b = 100 + random.nextInt(156);
                coloriStati.put(stato.getNome(), new Color(r, g, b));
            }
        }
    }
    
    /**
     * Genera la mappa dei territori dal file CSV.
     */
    private void generaMappa() {
        System.out.println("DEBUG: Metodo generaMappa chiamato");
        
        // Verifica che il file CSV esista nella directory corrente
        try {
            // Verifica il percorso del file
            String percorsoCorrente = System.getProperty("user.dir");
            System.out.println("DEBUG: Directory corrente: " + percorsoCorrente);
            
            String percorsoFile = "mondoT.csv";
            java.io.File file = new java.io.File(percorsoFile);
            
            if (!file.exists()) {
                System.err.println("DEBUG: File '" + percorsoFile + "' non trovato!");
                
                // Chiediamo all'utente se vuole creare un file di esempio
                int risposta = JOptionPane.showConfirmDialog(this,
                        "Il file '" + percorsoFile + "' non esiste!\n" +
                        "Vuoi creare un file di esempio?",
                        "File non trovato", JOptionPane.YES_NO_OPTION);
                
                if (risposta == JOptionPane.YES_OPTION) {
                    // Crea un file di esempio
                    if (creaFileCSVEsempio(file)) {
                        System.out.println("DEBUG: File di esempio creato con successo");
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Impossibile creare il file di esempio.",
                                "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    return;
                }
            }
            
            System.out.println("DEBUG: File trovato, dimensione: " + file.length() + " byte");
            
            // Verifichiamo che il file abbia il formato corretto
            boolean formatoCorretto = verificaFormatoCSV(file);
            if (!formatoCorretto) {
                System.err.println("DEBUG: Formato CSV non valido!");
                JOptionPane.showMessageDialog(this,
                        "Il file CSV non ha il formato corretto.\n" +
                        "Deve contenere righe con valori 'M' (mare) o 'T' (terra) separati da virgole.",
                        "Formato file non valido", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            System.out.println("DEBUG: Formato CSV valido, procedo con il caricamento");
            System.out.println("DEBUG: Tentativo di caricare il file: " + file.getAbsolutePath());
            
            // Ora carica effettivamente il file attraverso il metodo del mondo
            System.out.println("DEBUG: Chiamata a mondo.generaTerritori...");
            mondo.generaTerritori(percorsoFile);
            
            System.out.println("DEBUG: Mappa generata con successo");
            if (mondo.getMappaTerritori() != null) {
                System.out.println("DEBUG: Dimensioni mappa: " + 
                        mondo.getMappaTerritori().length + "x" + 
                        mondo.getMappaTerritori()[0].length);
                
                // Stampa un esempio di contenuto della mappa
                System.out.println("DEBUG: Esempio contenuto:");
                sampleMapContent();
            } else {
                System.err.println("DEBUG: La mappa è stata creata ma è null!");
            }
            
            // Forza l'aggiornamento della vista
            mapPanel.repaint(); 
            
            // Aggiorna la dimensione preferita del pannello della mappa
            updateMapPanelSize();
            
            JOptionPane.showMessageDialog(this, "Mappa generata con successo!");
        } catch (Exception e) {
            System.err.println("DEBUG: Eccezione durante il caricamento del file: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(this, 
                    "Errore nel caricamento del file: " + e.getMessage(), 
                    "Errore", JOptionPane.ERROR_MESSAGE);
            
            // Suggerimento per risolvere il problema
            JOptionPane.showMessageDialog(this, 
                    "SUGGERIMENTO: Assicurati che il file 'mondoT.csv' sia nella directory principale del progetto.\n" +
                    "Directory corrente: " + System.getProperty("user.dir") + "\n" +
                    "Formato atteso: valori 'M' o 'T' separati da virgole, es:\n" +
                    "T,T,M,M\n" +
                    "T,T,T,M\n" +
                    "M,M,M,M",
                    "Suggerimento", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Crea un file CSV di esempio.
     * @param file il file da creare
     * @return true se la creazione è avvenuta con successo, false altrimenti
     */
    private boolean creaFileCSVEsempio(java.io.File file) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            // Crea una mappa 20x20 con una combinazione di terra e acqua
            // Creiamo un'isola centrale circondata da acqua
            for (int i = 0; i < 20; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < 20; j++) {
                    // Se siamo nella zona centrale (5-15 sia x che y) mettiamo terra
                    // altrimenti acqua
                    if (i >= 5 && i <= 15 && j >= 5 && j <= 15) {
                        row.append("T");
                    } else {
                        row.append("M");
                    }
                    
                    // Aggiungi la virgola se non è l'ultimo elemento
                    if (j < 19) {
                        row.append(",");
                    }
                }
                writer.println(row.toString());
            }
            return true;
        } catch (java.io.IOException e) {
            System.err.println("DEBUG: Errore durante la creazione del file CSV: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verifica che il file CSV abbia il formato corretto.
     * @param file il file da verificare
     * @return true se il formato è corretto, false altrimenti
     */
    private boolean verificaFormatoCSV(java.io.File file) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            int lineNumber = 0;
            int expectedColumns = -1;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] values = line.split(",");
                
                // Controlla che ci sia almeno un valore
                if (values.length == 0) {
                    System.err.println("DEBUG: Riga " + lineNumber + " vuota");
                    return false;
                }
                
                // Imposta il numero di colonne attese dalla prima riga
                if (expectedColumns == -1) {
                    expectedColumns = values.length;
                } else if (values.length != expectedColumns) {
                    // Tutte le righe devono avere lo stesso numero di colonne
                    System.err.println("DEBUG: Riga " + lineNumber + " ha un numero diverso di colonne: " 
                            + values.length + " invece di " + expectedColumns);
                    return false;
                }
                
                // Verifica che ogni valore sia 'M' o 'T'
                for (String value : values) {
                    String trimmed = value.trim();
                    if (!trimmed.equalsIgnoreCase("M") && !trimmed.equalsIgnoreCase("T")) {
                        System.err.println("DEBUG: Valore non valido '" + trimmed + "' alla riga " + lineNumber);
                        return false;
                    }
                }
            }
            
            // Il file deve avere almeno una riga
            if (lineNumber == 0) {
                System.err.println("DEBUG: File vuoto");
                return false;
            }
            
            return true;
        } catch (java.io.IOException e) {
            System.err.println("DEBUG: Errore durante la lettura del file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mostra un esempio del contenuto della mappa per debug.
     */
    private void sampleMapContent() {
        Territorio[][] mappaTerritori = mondo.getMappaTerritori();
        if (mappaTerritori == null) return;
        
        int rows = Math.min(5, mappaTerritori.length);
        int cols = Math.min(5, mappaTerritori[0].length);
        
        System.out.println("DEBUG: Esempio contenuto mappa (prime " + rows + "x" + cols + " celle):");
        for (int i = 0; i < rows; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                Territorio t = mappaTerritori[i][j];
                if (t == null) {
                    row.append("null");
                } else {
                    row.append(t.isAcqua() ? "M" : "T");
                }
                if (j < cols - 1) row.append(", ");
            }
            System.out.println("Riga " + i + ": [" + row.toString() + "]");
        }
    }
    
    /**
     * Aggiorna la dimensione preferita del pannello della mappa in base alla mappa attuale.
     */
    private void updateMapPanelSize() {
        if (mondo.getMappaTerritori() != null) {
            int righe = mondo.getMappaTerritori().length;
            int colonne = mondo.getMappaTerritori()[0].length;
            int width = colonne * cellSize;
            int height = righe * cellSize;
            mapPanel.setPreferredSize(new Dimension(width, height));
            mapPanel.revalidate();
        }
    }
    
    /**
     * Genera gli stati sulla mappa.
     * 
     * @param numStati numero di stati da generare
     */
    private void generaStati(int numStati) {
        if (mondo.getMappaTerritori() == null) {
            JOptionPane.showMessageDialog(this, 
                    "Prima genera la mappa dei territori!", 
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int statiCreati = mondo.generaStati(numStati);
        generaColoriPerStati(); // Aggiorna i colori per i nuovi stati
        mapPanel.repaint(); // Ridisegna la GUI
        
        JOptionPane.showMessageDialog(this, 
                "Creati " + statiCreati + " stati su " + numStati + " richiesti.");
    }
    
    /**
     * Mostra le statistiche degli stati.
     */
    private void mostraStatistiche() {
        if (mondo.getStati().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Non ci sono stati da visualizzare!", 
                    "Informazione", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Crea una stringa con le statistiche
        StringBuilder stats = new StringBuilder();
        stats.append("=== STATISTICHE DEGLI STATI ===\n\n");
        
        for (Stato stato : mondo.getStati()) {
            stats.append("Stato: ").append(stato.getNome()).append("\n");
            stats.append("Popolazione: ").append(stato.getPopolazione()).append("\n");
            stats.append("Regioni: ").append(stato.getRegioni().size()).append("\n");
            stats.append("Potenza militare: ").append(stato.getPotenza()).append("\n");
            stats.append("Capitale: ").append(stato.getCapitale() != null ? 
                    stato.getCapitale().getNome() : "Nessuna").append("\n\n");
        }
        
        // Mostra le statistiche in una finestra di dialogo con scrolling
        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
                "Statistiche degli Stati", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Disegna la mappa sulla GUI.
     * 
     * @param g Graphics object
     */
    private void disegnaMappa(Graphics g) {
        // DEBUG: Stampa informazioni per capire se il metodo è chiamato e qual è lo stato del mondo
        System.out.println("DEBUG: Metodo disegnaMappa chiamato");
        
        if (mondo == null) {
            System.out.println("DEBUG: mondo è null");
            return;
        }
        
        if (mondo.getMappaTerritori() == null) {
            System.out.println("DEBUG: La mappa dei territori è null. Devi generare la mappa prima!");
            
            // Disegniamo comunque qualcosa per mostrare che il metodo funziona
            if (debugMode) {
                g.setColor(Color.ORANGE);
                g.fillOval(100, 100, 50, 50);
                g.setColor(Color.BLACK);
                g.drawString("Mappa non ancora generata", 100, 170);
            }
            return;
        }
        
        // Se arriviamo qui, abbiamo una mappa valida da disegnare
        Territorio[][] mappaTerritori = mondo.getMappaTerritori();
        
        // Prima verifica se la mappa è effettivamente inizializzata e ha le dimensioni corrette
        if (mappaTerritori == null) {
            System.err.println("DEBUG: mappaTerritori è null anche se getMappaTerritori() non ha restituito null!");
            return;
        }
        
        if (mappaTerritori.length == 0) {
            System.err.println("DEBUG: La mappa ha 0 righe!");
            return;
        }
        
        if (mappaTerritori[0].length == 0) {
            System.err.println("DEBUG: La mappa ha 0 colonne!");
            return;
        }
        
        int righe = mappaTerritori.length;
        int colonne = mappaTerritori[0].length;
        
        System.out.println("DEBUG: Dimensioni mappa: " + righe + "x" + colonne);
        
        // Contatore di elementi disegnati per debug
        int contatoreCelle = 0;
        int contatoreAcqua = 0;
        int contatoreTerra = 0;
        
        // Verifica quali celle sono null
        int contatoreNull = 0;
        
        try {
            for (int i = 0; i < righe; i++) {
                for (int j = 0; j < colonne; j++) {
                    Territorio t = mappaTerritori[i][j];
                    if (t == null) {
                        contatoreNull++;
                        continue;
                    }
                    
                    contatoreCelle++;
                    if (t.isAcqua()) {
                        contatoreAcqua++;
                    } else {
                        contatoreTerra++;
                    }
                    
                    int x = j * cellSize;
                    int y = i * cellSize;
                    
                    // Colora il territorio in base alla modalità di visualizzazione
                    if (t.isAcqua()) {
                        g.setColor(Color.BLUE);
                    } else if (mostraStati && t.getStatoPadrone() != null) {
                        g.setColor(coloriStati.getOrDefault(t.getStatoPadrone(), Color.GRAY));
                    } else {
                        g.setColor(Color.GREEN);
                    }
                    
                    // Disegna il territorio di base
                    g.fillRect(x, y, cellSize, cellSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, cellSize, cellSize);
                    
                    // Se mostraRegioni è attivato, evidenzia i confini delle regioni
                    if (mostraRegioni && !t.isAcqua() && t.getRegione() != null) {
                        // Controllo dei territori adiacenti per disegnare i confini regionali
                        g.setColor(Color.RED);
                        
                        // Controlla i quattro lati per vedere se appartengono a regioni diverse
                        // Lato superiore
                        if (i > 0 && mappaTerritori[i-1][j] != null && 
                            !mappaTerritori[i-1][j].isAcqua() && 
                            !t.getRegione().equals(mappaTerritori[i-1][j].getRegione())) {
                            g.drawLine(x, y, x + cellSize, y);
                        }
                        
                        // Lato inferiore
                        if (i < righe - 1 && mappaTerritori[i+1][j] != null && 
                            !mappaTerritori[i+1][j].isAcqua() && 
                            !t.getRegione().equals(mappaTerritori[i+1][j].getRegione())) {
                            g.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
                        }
                        
                        // Lato sinistro
                        if (j > 0 && mappaTerritori[i][j-1] != null && 
                            !mappaTerritori[i][j-1].isAcqua() && 
                            !t.getRegione().equals(mappaTerritori[i][j-1].getRegione())) {
                            g.drawLine(x, y, x, y + cellSize);
                        }
                        
                        // Lato destro
                        if (j < colonne - 1 && mappaTerritori[i][j+1] != null && 
                            !mappaTerritori[i][j+1].isAcqua() && 
                            !t.getRegione().equals(mappaTerritori[i][j+1].getRegione())) {
                            g.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
                        }
                    }
                    
                    // Se mostraTerritori è attivato, mostra il nome del territorio
                    if (mostraTerritori && zoomFactor > 1.5) {
                        g.setColor(Color.BLACK);
                        String nome = t.getName();
                        // Mostra solo un ID breve per evitare sovrapposizioni
                        String id = String.valueOf(t.getIdUnivoco());
                        FontMetrics fm = g.getFontMetrics();
                        g.drawString(id, x + (cellSize - fm.stringWidth(id)) / 2, 
                                     y + (cellSize + fm.getAscent()) / 2);
                    }
                    
                    // Se questo territorio ha un centro, mostralo con l'icona corrispondente
                    if (mostraCentri && t.getCentro() != null) {
                        // Dimensione dell'icona in base allo zoom
                        // La dimensione dell'icona è adattata in base allo zoom, ma con un limite minimo e massimo
                        int minIconSize = 12; // Dimensione minima dell'icona
                        int maxIconSize = 40; // Dimensione massima dell'icona
                        
                        // Calcola la dimensione dell'icona in base al cellSize e allo zoom
                        int baseIconSize = (int)(cellSize * ICON_SIZE_RATIO);
                        int iconSize = Math.min(maxIconSize, Math.max(minIconSize, baseIconSize));
                        
                        // Se lo zoom è molto piccolo, le icone potrebbero essere più grandi delle celle
                        // In questo caso, manteniamo le icone in proporzione alla dimensione delle celle
                        if (zoomFactor < 1.0 && iconSize > cellSize) {
                            iconSize = Math.max(minIconSize, cellSize);
                        }
                        
                        // Posiziona l'icona al centro della cella
                        int iconX = x + (cellSize - iconSize) / 2;
                        int iconY = y + (cellSize - iconSize) / 2;
                        
                        Centro centro = t.getCentro();
                        BufferedImage iconaDaUsare = null;
                        
                        // Scegli l'icona in base al tipo di centro
                        if (centro instanceof Capitale) {
                            iconaDaUsare = iconeCapitale;
                        } else if (centro instanceof Capoluogo) {
                            iconaDaUsare = iconeCapoluogo;
                        } else if (centro instanceof BaseMilitare) {
                            iconaDaUsare = iconeBMilitare;
                        }
                        
                        if (iconaDaUsare != null) {
                            // Disegna l'icona
                            g.drawImage(iconaDaUsare, iconX, iconY, iconSize, iconSize, null);
                            
                            // Se lo zoom è sufficiente, mostra anche il nome del centro
                            if (zoomFactor > 1.5) {
                                g.setColor(Color.BLACK);
                                g.setFont(new Font("Arial", Font.BOLD, 10));
                                String nomeCentro = centro.getNome();
                                // Limita la lunghezza del nome per evitare sovrapposizioni
                                if (nomeCentro.length() > 10) {
                                    nomeCentro = nomeCentro.substring(0, 8) + "...";
                                }
                                FontMetrics fm = g.getFontMetrics();
                                int textWidth = fm.stringWidth(nomeCentro);
                                
                                // Posiziona il testo sotto l'icona
                                g.drawString(nomeCentro, 
                                           x + (cellSize - textWidth) / 2, 
                                           y + cellSize + fm.getAscent());
                            }
                        }
                    }
                    // Se questo territorio è una capitale, marcalo (per compatibilità con codice esistente)
                    else if (t.isCapitale()) {
                        g.setColor(Color.RED);
                        int starSize = cellSize / 2;
                        int centerX = x + cellSize / 2;
                        int centerY = y + cellSize / 2;
                        
                        g.fillOval(centerX - starSize/2, centerY - starSize/2, starSize, starSize);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Eccezione durante il disegno della mappa: " + e.getMessage());
            e.printStackTrace();
            
            // Disegna un messaggio di errore direttamente sulla mappa
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("ERRORE: " + e.getMessage(), 50, 50);
        }
        
        System.out.println("DEBUG: Statistiche mappa - Totale celle: " + contatoreCelle 
                + ", Celle null: " + contatoreNull 
                + ", Acqua: " + contatoreAcqua 
                + ", Terra: " + contatoreTerra);
    }
    
    /**
     * Metodo di test per verificare la visualizzazione di tutti i tipi di centri.
     */
    private void testVisualizzazioneCapitali() {
        if (mondo.getMappaTerritori() == null) {
            JOptionPane.showMessageDialog(this, 
                    "Prima genera la mappa dei territori!", 
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Crea uno stato di test
            Stato statoTest = new Stato("Stato di Test", null, 100, 1000, null);
            mondo.aggiungiStato(statoTest);
            
            // Trova tre territori terrestri per posizionare i diversi centri
            Territorio territorioCapitale = null;
            Territorio territorioCapoluogo = null;
            Territorio territorioBaseMilitare = null;
            
            Territorio[][] mappaTerritori = mondo.getMappaTerritori();
            for (int i = 0; i < mappaTerritori.length; i++) {
                for (int j = 0; j < mappaTerritori[0].length; j++) {
                    Territorio t = mappaTerritori[i][j];
                    if (t != null && !t.isAcqua() && t.getStatoPadrone() == null) {
                        if (territorioCapitale == null) {
                            territorioCapitale = t;
                        } else if (territorioCapoluogo == null && (i > 5 || j > 5)) {
                            territorioCapoluogo = t;
                        } else if (territorioBaseMilitare == null && (i > 10 || j > 10)) {
                            territorioBaseMilitare = t;
                            break;
                        }
                    }
                }
                if (territorioCapitale != null && territorioCapoluogo != null && territorioBaseMilitare != null) {
                    break;
                }
            }
            
            if (territorioCapitale == null) {
                JOptionPane.showMessageDialog(this, 
                        "Nessun territorio terrestre trovato nella mappa!", 
                        "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crea una regione capitale
            Regione regioneCapitale = new Regione("Regione Capitale", territorioCapitale);
            
            // Crea un oggetto Capitale e posizionalo nel territorio
            Capitale capitale = new Capitale(
                "Capitale di Test",
                10, // Importanza strategica
                10, // Livello difesa
                100000, // Popolazione
                territorioCapitale,
                statoTest.getNome(),
                regioneCapitale.getNome(),
                10, // Livello politico
                statoTest
            );
            territorioCapitale.setCentro(capitale);
            territorioCapitale.setStatoPadrone(statoTest.getNome());
            territorioCapitale.setRegione(regioneCapitale.getNome());
            regioneCapitale.addTerritorio(territorioCapitale);
            
            // Imposta la regione come capitale dello stato
            statoTest.addRegione(regioneCapitale);
            statoTest.setCapitale(regioneCapitale);
            
            // Se abbiamo trovato un territorio per il capoluogo, crea una seconda regione
            if (territorioCapoluogo != null) {
                Regione regioneSecondaria = new Regione("Regione Secondaria", territorioCapoluogo);
                
                // Crea un oggetto Capoluogo e posizionalo nel territorio
                Capoluogo capoluogo = new Capoluogo(
                    "Capoluogo di Test",
                    8, // Importanza strategica
                    7, // Livello difesa
                    50000, // Popolazione
                    territorioCapoluogo,
                    statoTest.getNome(),
                    regioneSecondaria.getNome(),
                    8, // Livello economico
                    7, // Livello culturale
                    regioneSecondaria
                );
                territorioCapoluogo.setCentro(capoluogo);
                territorioCapoluogo.setStatoPadrone(statoTest.getNome());
                territorioCapoluogo.setRegione(regioneSecondaria.getNome());
                regioneSecondaria.addTerritorio(territorioCapoluogo);
                
                statoTest.addRegione(regioneSecondaria);
            }
            
            // Se abbiamo trovato un territorio per la base militare
            if (territorioBaseMilitare != null) {
                // Imposta lo stato e la regione del territorio
                territorioBaseMilitare.setStatoPadrone(statoTest.getNome());
                territorioBaseMilitare.setRegione(regioneCapitale.getNome());
                regioneCapitale.addTerritorio(territorioBaseMilitare);
                
                // Crea un oggetto BaseMilitare e posizionalo nel territorio
                BaseMilitare baseMilitare = new BaseMilitare(
                    "Base Militare di Test",
                    9, // Importanza strategica
                    9, // Livello difesa
                    5000, // Popolazione (personale militare)
                    territorioBaseMilitare,
                    statoTest.getNome(),
                    regioneCapitale.getNome(),
                    5000, // Numero truppe
                    9, // Livello armamento
                    BaseMilitare.TipoBase.TERRESTRE // Tipo di base
                );
                territorioBaseMilitare.setCentro(baseMilitare);
            }
            
            // Aggiorna i colori per gli stati
            generaColoriPerStati();
            
            // Forza il ridisegno
            mapPanel.repaint();
            
            // Messaggio di successo con informazioni sui territori
            StringBuilder message = new StringBuilder("Test completato:\n\n");
            message.append("Capitale creata in ").append(territorioCapitale.getName()).append("\n");
            
            if (territorioCapoluogo != null) {
                message.append("Capoluogo creato in ").append(territorioCapoluogo.getName()).append("\n");
            } else {
                message.append("Capoluogo non creato (territorio non trovato)\n");
            }
            
            if (territorioBaseMilitare != null) {
                message.append("Base Militare creata in ").append(territorioBaseMilitare.getName()).append("\n");
            } else {
                message.append("Base Militare non creata (territorio non trovato)\n");
            }
            
            message.append("\nLe icone dovrebbero ora essere visibili sulla mappa.");
            
            JOptionPane.showMessageDialog(this, message.toString(), "Test Centri", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println("DEBUG: Errore durante il test: " + e.getMessage());
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(this, 
                    "Errore durante il test: " + e.getMessage(), 
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Carica le icone per i centri dalle risorse.
     */
    private void caricaIcone() {
        try {
            // Percorsi relativi e alternativi per migliorare la compatibilità
            String baseDir = System.getProperty("user.dir");
            
            // Prova diversi percorsi possibili per trovare le icone
            File capitaleFile = new File(baseDir + "/Assets/Capitale.png");
            if (!capitaleFile.exists()) {
                capitaleFile = new File(baseDir + "/Battle_Cioba/Assets/Capitale.png");
                if (!capitaleFile.exists()) {
                    capitaleFile = new File("Assets/Capitale.png");
                }
            }
            
            File capoluogoFile = new File(baseDir + "/Assets/Capoluogo.png");
            if (!capoluogoFile.exists()) {
                capoluogoFile = new File(baseDir + "/Battle_Cioba/Assets/Capoluogo.png");
                if (!capoluogoFile.exists()) {
                    capoluogoFile = new File("Assets/Capoluogo.png");
                }
            }
            
            File bmilitareFile = new File(baseDir + "/Assets/BMilitare.png");
            if (!bmilitareFile.exists()) {
                bmilitareFile = new File(baseDir + "/Battle_Cioba/Assets/BMilitare.png");
                if (!bmilitareFile.exists()) {
                    bmilitareFile = new File("Assets/BMilitare.png");
                }
            }
            
            // Carica le immagini dai file trovati
            iconeCapitale = ImageIO.read(capitaleFile);
            iconeCapoluogo = ImageIO.read(capoluogoFile);
            iconeBMilitare = ImageIO.read(bmilitareFile);
            
            System.out.println("Icone caricate con successo da: " + capitaleFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Errore nel caricamento delle icone: " + e.getMessage());
            e.printStackTrace();
            
            // Crea icone segnaposto in caso di errore
            iconeCapitale = creaIconaSegnaposto(Color.RED);
            iconeCapoluogo = creaIconaSegnaposto(Color.ORANGE);
            iconeBMilitare = creaIconaSegnaposto(Color.BLUE);
        }
    }
    
    /**
     * Crea un'icona segnaposto in caso di errore nel caricamento delle immagini.
     * 
     * @param color colore dell'icona
     * @return BufferedImage icona segnaposto
     */
    private BufferedImage creaIconaSegnaposto(Color color) {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillOval(4, 4, 24, 24);
        g.setColor(Color.BLACK);
        g.drawOval(4, 4, 24, 24);
        g.dispose();
        return img;
    }
}