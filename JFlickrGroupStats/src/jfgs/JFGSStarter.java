/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs;

import jfgs.zm.gui.StatsFrame;

/**
 * Załadowanie JFlickrGroupStats
 * 
 * @author michalus
 */
public class JFGSStarter {

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StatsFrame().setVisible(true);
            }
        });
    }
    
}
