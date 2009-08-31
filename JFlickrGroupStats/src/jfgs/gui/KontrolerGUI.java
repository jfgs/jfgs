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

package jfgs.gui;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.groups.Group;
import com.aetrion.flickr.groups.GroupsInterface;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;
import jfgs.narzedzia.Autoryzer;
import jfgs.narzedzia.IPanelKonfiguracyjny;
import org.xml.sax.SAXException;

/**
 * Do sterowania GUI
 * 
 * @author michalus
 */
public class KontrolerGUI {
    
    private JProgressBar pasekPostepu;
    private IStats stats;
    private Autoryzer autoryzer;
    private JLabel authLabel;
    private JTextField groupIdField;
    private IPanelKonfiguracyjny panelKonfiguracyjny;
    
    public KontrolerGUI(
        JProgressBar pasekPostepu,
        IStats stats,
        JLabel authLabel,
        JTextField groupIdField
    ) throws ParserConfigurationException
    {
        this.pasekPostepu = pasekPostepu;
        this.stats = stats;
        this.authLabel = authLabel;
        this.groupIdField = groupIdField;        
        this.autoryzer = Autoryzer.get();
        this.panelKonfiguracyjny = null;
    }
    
    public Flickr getFlickr() throws ParserConfigurationException {
        return autoryzer.getFlickr();
    }

    public void setPanelKoniguracyjny(IPanelKonfiguracyjny panel) {
        this.panelKonfiguracyjny = panel;
    }

    public IPanelKonfiguracyjny getPanelKonfiguracyjny() {
        return panelKonfiguracyjny;
    }
    
    /**
     * Nazwa grupy wybrana w menu
     * @return
     */
    public String getNazwaGrupy() {
        if ("".equals(getGroupId())) {
            return "";
        } else {
            try {
                GroupsInterface gi = getFlickr().getGroupsInterface();
                Group g = gi.getInfo(getGroupId());
                return g.getName();
            } catch(Exception e) {
                e.printStackTrace();
                return "";
            }            
        }
    }
    
    public void autoryzuj() 
        throws IOException, SAXException, FlickrException, URISyntaxException 
    {
        autoryzer.autoryzuj(this);
    }

    /**
     * Wartość maksymalna dla paska postępu
     * @param wartoscMaksymalna
     */
    public void ustawPostepMax(int wartoscMaksymalna) {
        pasekPostepu.setMaximum(wartoscMaksymalna);
    }

    /**
     * Zmiana wartości dla paska postępu
     * @param postep
     */
    public void ustawPostep(int postep) {
        pasekPostepu.setValue(postep);
        pasekPostepu.repaint();
    }

    /**
     * Zmiana opisu dla paska postępu
     * @param opis
     */
    public void ustawPostepStr(String opis) {
        pasekPostepu.setString(opis);
    }
    
    /**
     * Okno głównego interfejsu
     * @return
     */
    public JFrame getOwner() {
        return stats.dajOwner();
    }
    
    public void ustawAuth(String str) {
        authLabel.setText(str);
    }
    
    public String getGroupId() {
        return groupIdField.getText();
    }
    
    public Date dajDataOd() throws ParseException {
        return stats.dajDataOd();
    }
    
    public Date dajDataDo() throws ParseException {
        return stats.dajDataDo();
    }
    
}
