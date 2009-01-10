/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.zm.logika;

/**
 * Strukturka poboczna do przechowywania informacji o statystyce użytkownika
 * 
 * @author michalus
 */
public class StatystykaAutora implements Comparable<StatystykaAutora> {

    private String nazwaUzytkownika;
    private int dodanychZdjec;
    private int dodanychKomentarzy;
    
    public StatystykaAutora(int k, int z, String nazwa) {
        dodanychKomentarzy = k;
        dodanychZdjec = z;
        nazwaUzytkownika = nazwa;
    }
    
    public StatystykaAutora() {
        this(0, 0, "");
    }
    
    public void dodajKomentarz() {
        dodanychKomentarzy++;
    }
    
    public void dodajZdjecie() {
        dodanychZdjec++;
    }
    
    public int dajLiczbeKomentarzy() {
        return dodanychKomentarzy;
    }
    
    public int dajLiczbeZdjec() {
        return dodanychZdjec;
    }
    
    public String dajNazwe() {
        return nazwaUzytkownika;
    }

    public int dajWartosc() {        
        return (dodanychKomentarzy - dodanychZdjec);
    }
    
    public int compareTo(StatystykaAutora o) {
        if (o.dajWartosc() < dajWartosc()) {
            return -1;
        } else if (o.dajWartosc() > dajWartosc()) {
            return 1;
        } else {
            return 0;
        }
    }
    
}
