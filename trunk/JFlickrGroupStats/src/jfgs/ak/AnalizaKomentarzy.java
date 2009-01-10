/*
 */

package jfgs.ak;

import jfgs.gui.KontrolerGUI;
import jfgs.narzedzia.ILogika;

/**
 * Odczytanie wydźwięku komentarzy pod zdjęciem
 * @author michalus
 */
public class AnalizaKomentarzy implements ILogika {

    private KontrolerGUI kgui;

    public void podlaczGUI(KontrolerGUI kontroler) {
        this.kgui = kontroler;
    }

    public int wykonajZadanie() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
