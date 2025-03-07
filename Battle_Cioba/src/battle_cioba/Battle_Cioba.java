/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package battle_cioba;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Classe principale dell'applicazione Battle Cioba.
 * @author hp
 */
public class Battle_Cioba {

    /**
     * Metodo main per avviare l'applicazione.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Avvia l'interfaccia grafica nell'Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BattleCiobaGUI();
            }
        });
    }
    
}