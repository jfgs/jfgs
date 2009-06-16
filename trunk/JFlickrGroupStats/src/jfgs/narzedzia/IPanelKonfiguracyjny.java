/*
 */

package jfgs.narzedzia;

import javax.swing.JPanel;

/**
 * Interfejs dla wszystkich klas opisujących drugi panel
 * @author michalus
 */
public interface IPanelKonfiguracyjny {

    /**
     * Metoda wyszarzająca wszystkie komponenty na panelu
     * @param czyDoEdycji
     */
    public void stanKomponentow(boolean czyDoEdycji);

    /**
     * Metoda zwracająca panel do dodania
     * @return
     */
    public JPanel getPanel();

}
