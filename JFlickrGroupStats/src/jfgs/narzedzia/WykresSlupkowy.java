/*
 */

package jfgs.narzedzia;

import java.util.Arrays;
import java.util.Vector;

/**
 * Wykres słupkowy
 *
 * @author michalus
 */
public class WykresSlupkowy {

    /**
     * Opis wykresu
     */
    private class OpisWykresu implements Comparable<OpisWykresu> {

        private int y;
        private String x;
        private String opis;

        public OpisWykresu(String x, String opis, int y) {
            this.x = x;
            this.opis = opis;
            this.y = y;
        }

        /**
         * @return the y
         */
        public int getY() {
            return y;
        }

        /**
         * @return the x
         */
        public String getX() {
            return x;
        }

        /**
         * @return the opis
         */
        public String getOpis() {
            return opis;
        }

        /**
         * Po prostu porwnujemy wartość y
         * @param o
         * @return
         */
        public int compareTo(OpisWykresu o) {
            return
                new Integer(
                    this.getY()).compareTo(o.getY());
        }

    }

    /**
     * Tymczasowa struktura dla elementów wykresu
     */
    private Vector<OpisWykresu> opisWykresu;
    
    public WykresSlupkowy() {
        this.opisWykresu = new Vector<WykresSlupkowy.OpisWykresu>(20);
    }

    public void add(String x, String opis, int y) {
        this.opisWykresu.add(new OpisWykresu(x, opis, y));
    }

    public String get() {
        StringBuffer sb = new StringBuffer("");

        OpisWykresu[] wykres = new OpisWykresu[opisWykresu.size()];
        opisWykresu.toArray(wykres);

        // posortowane wg wartości y
        Arrays.sort(wykres);      

        // wykres po 10%
        int blok = 10;
        int wysokosc = 100 / blok;

        // każdy x będzie miał swój y
        String[] wiersze = new String[wysokosc];
        Arrays.fill(wiersze, "");

        // dla wiersza
        for (int j=wykres.length - 1; j>=0; j--) {

            // malujemy słupek
            for (int i=0; i<wiersze.length; i++) {

                // wysoki zgodnie z jego procentem maksymalnej wartości y
                if (                    
                    ((double) (wykres[0].getY() - wykres[j].getY())
                        / (wykres[0].getY() - wykres[wykres.length-1].getY())
                        * 100)
                    >= ((wiersze.length - 1 - i) * blok)
                ) {
                    wiersze[i] += "▓";
                } else {

                    wiersze[i] += "░";
                }

            }

        } // pionowe linie wykresu

        // kończymy każdą linię wykresu
        for (int i=0; i<wiersze.length; i++) {
            sb.append(wiersze[i]+"\n");
        }

        // początek stopki
        for (int i=0; i<wykres.length; i++) {
            sb.append("╫");
        }
        sb.append("\n");

        int szerokoscStopki = wykres.length - 1;
        int szerokoscWciecia = 2;

        // dodajemy stopkę z opisem
        for (int i=0; i<wykres.length; i++) {

            String stopka = "";

            // dokańczamy poniższe wiersze stopki
            for (int j=i; j<szerokoscStopki; j++) {
                stopka += "║";
            }

            // zawijak dla bieżącego wiersza stopki
            stopka += "╚";

            // bieżący wiersz stopki
            for (int j=0; j<i + szerokoscWciecia; j++) {
                stopka += "═";
            }

            // opis bieżacego wiersza
            stopka += " ";
            stopka += wykres[i].getX()+", ";
            stopka += wykres[i].getY()+" (";
            stopka += wykres[i].getOpis()+")";

            sb.append(stopka + "\n");

        } // wiersze stopki

        return sb.toString();
    }

//    public static void main(String[] args) {
//
//        WykresSlupkowy ws = new WykresSlupkowy();
//
//        ws.add("jeden", "opis jedynki", 1);
//        ws.add("dsd", "opis jedynki", 2);
//        ws.add("jen", "opis jedynki", 0);
//        ws.add("jsden", "opis jedynki", 22);
//        ws.add("jedddn", "opis jedynki", 5);
//        ws.add("jedddn", "opis jedynki", -6);
//        ws.add("dwa", "opis 222", 7);
//        ws.add("trzy", "opis 333", 8);
//        ws.add("cztery", "opis 444", 9);
//        ws.add("pięć", "opis 555", 10);
//
//        System.out.println(ws.get());
//
//    }

}
