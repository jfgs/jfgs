/*
 *  This file is part of JFlickrGroupStats.
 *
 *  JFlickrGroupStats is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JFlickrGroupStats is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JFlickrGroupStats.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jfgs.narzedzia;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.JOptionPane;
import jfgs.gui.PodgladFrame;

/**
 * Klasa zarządzająca wypisaniem danych wyjściowych
 * @author michalus
 */
public class DaneWyjsciowe {

    /**
     * Czy drukować na standardowe wyjście
     */
    public static final boolean drukujNaEkran = false;

    private NumberFormat nf;
    private DateFormat df;
    
    private BufferedWriter out;
    
    public DaneWyjsciowe() {
        
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setMinimumIntegerDigits(3);
        
        df = DateFormat.getDateInstance();
        df.setCalendar(Calendar.getInstance(Locale.getDefault()));
        
    }
    
    /**
     * Drukowanie do pliku wynikowego
     * @param s
     */
    public void drukuj(String s) {
        
        try {

            if (drukujNaEkran) {
                System.out.print(s);
            }

            doPliku(s);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Drukowanie do pliku wynikowego zakończone znakiem nowej linii
     * @param s
     */
    public void drukujLinie(String s) {
        drukuj(s+"\n");
    }
    
    private void otworzPlik() throws IOException {
        out = new BufferedWriter(new FileWriter(Constants.output));
    }
    
    /**
     * Zapisanie do pliku wynikowego. Jeżeli plik nie jest otwraty otwiera go.
     * 
     * @param s
     * @throws java.io.IOException
     * @see drukuj
     */
    private void doPliku(String s) throws IOException {
        if (out == null) {
            otworzPlik();
        }
        out.write(s);
    }
    
    /**
     * Zamykanie pliku wynikowego
     * 
     * @throws java.io.IOException
     * @see drukuj
     */
    public void zamknijPlik() throws IOException {
        if (out != null) {
            out.flush();
            out.close();
        }
    }

    /**
     * Wydruk separatora graficznego z nagłówkiem
     * @param naglowek
     */
    public void drukujSeparator(String naglowek) {

        if ("".equals(naglowek)) {
            naglowek = "***" ;
        } else {
            naglowek = "* "+naglowek+" *";
        }

        drukujLinie(
            "\n(<b>"
            + naglowek
            + "</b>)\n");
    }

    /**
     * Wydruk separatora graficzny
     */
    public void drukujSeparator() {
        drukujSeparator("");
    }
    
    /**
     * Formatowanie daty
     * @param d
     * @return
     */
    public String formatujDate(Date d) {
        return df.format(d);
    }
    
    /**
     * Formatowanie liczby całkowitej
     * @param n
     * @return
     */
    public String formatujLiczbe(int n) {
        return nf.format(n);
    }

    /**
     * Pokazuje okienko podglądu
     */
    public void pokazOkno() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new PodgladFrame(Constants.output).setVisible(true);
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

}
