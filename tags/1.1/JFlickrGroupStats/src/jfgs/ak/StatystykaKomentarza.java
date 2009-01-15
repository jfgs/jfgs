/*
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
