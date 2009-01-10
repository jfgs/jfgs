/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.narzedzia;

import jfgs.narzedzia.Constants;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Klasa zarządzająca wypisaniem danych wyjściowych
 * @author michalus
 */
public class DaneWyjsciowe {

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
            System.out.print(s);
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
        out.flush();
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
     * Wydruk separatora graficzny
     */
    public void drukujSeparator() {
        drukujLinie("\n(<b>***</b>)\n");
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
    
}
