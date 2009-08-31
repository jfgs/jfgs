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

    /**
     * Zwraca panel konfiguracyjny dla GUI, jeżeli brak null
     * @return
     */
    public IPanelKonfiguracyjny dajPanelKonfiguracyjny();

}
