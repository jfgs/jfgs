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

package jfgs.ak;

/**
 *
 * @author michalus
 */
public class StatystykaKomentarza implements Comparable<StatystykaKomentarza> {

    private String idUzytkownika;
    private String idZdjecia;
    private String idKomentarza;
    private String komentarz;
    private int liczba;

    public StatystykaKomentarza() {
    }

    public String getIdUzytkownika() {
        return idUzytkownika;
    }

    public String getIdZdjecia() {
        return idZdjecia;
    }

    public String getIdKomentarza() {
        return idKomentarza;
    }

    public int getLiczba() {
        return liczba;
    }

    public String getKomentarz() {
        return komentarz;
    }

    public int compareTo(StatystykaKomentarza o) {
        if (this.liczba < o.liczba) {
            return -1;
        } else if (this.liczba < o.liczba) {
            return 1;
        } else {
            return (this.komentarz.compareTo(o.komentarz));
        }
    }

}
