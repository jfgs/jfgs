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
    
    private static boolean sortByWartosc = true;
    
    /*
     * Sortuj wg liczby komentarzy i zdjęć
     */
    public static void sortByWartosc() {
        sortByWartosc = true;
    }

    /*
     * Sortuj wg liczby komentarzy
     */
    public static void sortByKomentarze() {
        sortByWartosc = false;
    }

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
        if (sortByWartosc) {
            if (o.dajWartosc() < dajWartosc()) {
                return -1;
            } else if (o.dajWartosc() > dajWartosc()) {
                return 1;
            } else {
                return 0;
            }
        } else {
            if (o.dajLiczbeKomentarzy() < dajLiczbeKomentarzy()) {
                return -1;
            } else if (o.dajLiczbeKomentarzy() > dajLiczbeKomentarzy()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    
}
