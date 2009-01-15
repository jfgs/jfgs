/*
 */

package jfgs.narzedzia;

import jfgs.gui.KontrolerGUI;

/**
 * Logika podłączana do GUI
 *
 * @author michalus
 */
public interface ILogika {

    /**
     * Logika przetworzyła zadanie i zakończyło się ono sukcesem
     */
    public static final int WYKONANIE_POPRAWNE = 1;

    /**
     * Logika przetworzyła zadanie i zakończyło się ono błędem
     */
    public static final int WYKONANIE_BLEDNE = 0;

    /**
     * Podłączenie logiki z GUI tak aby można było zczytać wybór użytkownika
     * @return
     */
    public void podlaczGUI(KontrolerGUI kontroler);

    /**
     * Wykonanie akcj dla danej logiki
     * @return
     */
    public int wykonajZadanie();

}
