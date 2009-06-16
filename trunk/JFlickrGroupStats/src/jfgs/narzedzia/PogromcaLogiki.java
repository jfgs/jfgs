/*
 */

package jfgs.narzedzia;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
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
