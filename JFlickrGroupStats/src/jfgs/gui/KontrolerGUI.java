/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    
    public KontrolerGUI(JProgressBar pasekPostepu, IStats stats, 
        JLabel authLabel, JTextField groupIdField) 
        throws ParserConfigurationException 
    {
        this.pasekPostepu = pasekPostepu;
        this.stats = stats;
        this.authLabel = authLabel;
        this.groupIdField = groupIdField;        
        this.autoryzer = Autoryzer.get();
    }
    
    public Flickr getFlickr() throws ParserConfigurationException {
        return autoryzer.getFlickr();
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
     * Zmiana wartości dla paska postępu
     * @param postep
     */
    public void ustawPostep(int postep) {
        pasekPostepu.setValue(postep);
        pasekPostepu.repaint();
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
