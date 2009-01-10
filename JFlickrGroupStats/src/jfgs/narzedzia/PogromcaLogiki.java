/*
 */

package jfgs.narzedzia;

import jfgs.gui.KontrolerGUI;
import jfgs.zm.ZdjecieMiesiaca;

/**
 * Klasa zarządzająca sposobem działania aplikacji
 * 
 * @author michalus
 */
public class PogromcaLogiki {

    public static final int LOGIKA_ZDJECIA_MIESIACA = 1;

    /**
     * Wykonuje w osobnym wątku logikę określoną parametrem
     *
     * @param rodzajLogiki
     * @param kontroler
     */
    public static void wykonaj(int rodzajLogiki, final KontrolerGUI kontroler) {

        ILogika logika = null;

        if (rodzajLogiki == LOGIKA_ZDJECIA_MIESIACA) {
            logika = new ZdjecieMiesiaca();            
        } else {
            throw new RuntimeException(
                "Nieznany rodzaj logiki: "+rodzajLogiki+"!");
        }

        wykonaj(logika, kontroler);
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

}
