/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.gui;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;
import jfgs.logika.Autoryzer;
import org.xml.sax.SAXException;

/**
 * Do sterowania GUI
 * 
 * @author michalus
 */
public class KontrolerGUI {
    
    private JProgressBar pasekPostepu;
    private JFrame owner;
    private Autoryzer autoryzer;
    private JLabel authLabel;
    private JTextField groupIdField;
    
    public KontrolerGUI(JProgressBar pasekPostepu, JFrame owner, 
        JLabel authLabel, JTextField groupIdField) 
        throws ParserConfigurationException 
    {
        this.pasekPostepu = pasekPostepu;
        this.owner = owner;
        this.authLabel = authLabel;
        this.groupIdField = groupIdField;
        this.autoryzer = Autoryzer.get();
    }
    
    public Flickr getFlickr() throws ParserConfigurationException {
        return autoryzer.getFlickr();
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
        return owner;
    }
    
    public void ustawAuth(String str) {
        authLabel.setText(str);
    }
    
    public String getGroupId() {
        return groupIdField.getText();
    }
    
}
