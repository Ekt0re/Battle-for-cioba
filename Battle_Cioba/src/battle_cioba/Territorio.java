/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package battle_cioba;

import java.util.List;
import java.util.Random;

/**
 *
 * @author hp
 */
public abstract class Territorio {
    //private Random rnd = new Random();
    protected String name;
    protected static String id = "-1";
    
    private String statoPadrone;  //Nome Stato che possiede questo territorio
    protected boolean acqua = true;
    //protected boolean terra;  all'inizio tutto acqua
    protected List<String> risorse; //Nel costruttore quando territorio viene creato
    protected long numCivili;   //Long xchè int MAX è 2Miliardi
    protected String regione;
    protected int countBasiMilitari = 0;    //Numero basi militari
    
    
}
