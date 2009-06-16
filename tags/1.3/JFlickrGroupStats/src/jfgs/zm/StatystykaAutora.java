/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.zm;

/**
 * Strukturka poboczna do przechowywania informacji o statystyce użytkownika
 * 
 * @author michalus
 */
public class StatystykaAutora implements Comparable<StatystykaAutora> {

    private final String nazwaUzytkownika;
    private double dodanychZdjec;
    private double dodanychKomentarzy;
    
    public StatystykaAutora(double k, double z, String nazwa) {
        dodanychKomentarzy = k;
        dodanychZdjec = z;
        nazwaUzytkownika = nazwa;
    }
    
    public StatystykaAutora() {
        this(0, 0, "");
    }

    public void dodajKomentarz(double wartosc) {
        dodanychKomentarzy += wartosc;
    }

    public void dodajKomentarz() {
        dodanychKomentarzy++;
    }
    
    public void dodajZdjecie() {
        dodanychZdjec++;
    }
    
    public double dajLiczbeKomentarzy() {
        return dodanychKomentarzy;
    }
    
    public double dajLiczbeZdjec() {
        return dodanychZdjec;
    }
    
    public String dajNazwe() {
        return nazwaUzytkownika;
    }

    public double dajWartosc() {
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
