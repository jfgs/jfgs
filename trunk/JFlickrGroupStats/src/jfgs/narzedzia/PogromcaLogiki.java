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

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import jfgs.ak.AnalizaKomentarzy;
import jfgs.gui.KontrolerGUI;
import jfgs.zm.ZdjecieMiesiaca;

/**
 * Klasa zarządzająca sposobem działania aplikacji
 * 
 * @author michalus
 */
public class PogromcaLogiki {

    /**
     * Zdjęcie miesiąca
     * @see ZdjecieMiesiaca
     */
    public static final String LOGIKA_ZDJECIA_MIESIACA = "Zdjęcie miesiąca";

    /**
     * Analiza komentarzy
     * @see AnalizaKomentarzy
     */
    public static final String LOGIKA_ANALIZA_KOMENTARZY = "Analiza komentarzy (wersja alfa)";



    /**
     * Wykonuje w osobnym wątku logikę określoną parametrem
     *
     * @param rodzajLogiki
     * @param kontroler
     */
    public static void wykonaj(Object rodzajLogiki, final KontrolerGUI kontroler) {

        ILogika logika = null;

        if (LOGIKA_ZDJECIA_MIESIACA.equals(rodzajLogiki)) {
            logika = new ZdjecieMiesiaca();
        } else if (LOGIKA_ANALIZA_KOMENTARZY.equals(rodzajLogiki)) {
            logika = new AnalizaKomentarzy();
        } else {
            throw new RuntimeException(
                "Nieznany rodzaj logiki: "+rodzajLogiki+"!");
        }

        wykonaj(logika, kontroler);
    }

    public static IPanelKonfiguracyjny dajPanelKonfiguracyjny(Object rodzajLogiki) {

        ILogika logika = null;

        if (LOGIKA_ZDJECIA_MIESIACA.equals(rodzajLogiki)) {
            logika = new ZdjecieMiesiaca();
        } else if (LOGIKA_ANALIZA_KOMENTARZY.equals(rodzajLogiki)) {
            logika = new AnalizaKomentarzy();
        } else {
            throw new RuntimeException(
                "Nieznany rodzaj logiki: "+rodzajLogiki+"!");
        }

        return logika.dajPanelKonfiguracyjny();

    }

    private static void wykonaj(final ILogika logika, final KontrolerGUI kontroler) {

        Thread t = new Thread() {

            @Override
            public void run() {
                logika.podlaczGUI(kontroler);
                logika.wykonajZadanie();
            }

        };
        
        t.start();

    }

    /**
     * Model dla rodzaju logiki
     * @return
     */
    public static ComboBoxModel dajModelDlaRodzaju() {
        return
            new DefaultComboBoxModel(
                new Object[]{
                    LOGIKA_ZDJECIA_MIESIACA,
                    LOGIKA_ANALIZA_KOMENTARZY});
    }

}
