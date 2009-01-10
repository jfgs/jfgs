/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.narzedzia;

import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.awt.Desktop;
import java.net.URI;
import javax.swing.JOptionPane;
import jfgs.gui.KontrolerGUI;
import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.util.AuthStore;
import com.aetrion.flickr.util.FileAuthStore;
import java.io.File;

/**
 * Klasa potrzebna do autoryzowania się w Flickr API.
 * 
 * @author michalus
 */
public class Autoryzer {

    private Flickr flickr;
    private AuthStore authStore;
    private boolean autoryzowany = false;
    
    public static Autoryzer get() throws ParserConfigurationException {
        return new Autoryzer();
    }
    
    protected Autoryzer() throws ParserConfigurationException {
        flickr = new Flickr(Constants.apiKey, Constants.sharedSecret, new REST());
    }
    
    /**
     * Podstawowy obiekt API
     * 
     * @return
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public Flickr getFlickr() throws ParserConfigurationException {        
        return flickr;
    }
    
    /**
     * Autoryzuj - nowa autoryzacja lub autoryzacja zapisana na dysku
     * 
     * @param kgui
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws com.aetrion.flickr.FlickrException
     * @throws java.net.URISyntaxException
     */
    public void autoryzuj(KontrolerGUI kgui) 
        throws IOException, SAXException, FlickrException, URISyntaxException 
    {
        przywrocAutoryzacje(kgui);
    }
    
    /**
     * Odczytanie autoryzacji zapisanej na dysku 
     * 
     * @param kgui
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws com.aetrion.flickr.FlickrException
     * @throws java.net.URISyntaxException
     */
    private void przywrocAutoryzacje(KontrolerGUI kgui) 
        throws IOException, SAXException, FlickrException, URISyntaxException 
    {
        File authsDir = new File(Constants.dir);

        if (authsDir != null) {
            this.authStore = new FileAuthStore(authsDir);
        }

        Auth auth = authStore.retrieve(Constants.nsid);
        RequestContext rc = RequestContext.getRequestContext();

        if (auth == null) {
            nowaAutoryzacja(kgui);
            auth = authStore.retrieve(Constants.nsid);
        } else {
            rc.setAuth(auth);            
        }
        
        autoryzowany = true;
        
        kgui.ustawAuth("Zalogowany jako "+auth.getUser().getUsername());
    }
    
    /**
     * Nowa autoryzacja
     * 
     * @param kgui
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws com.aetrion.flickr.FlickrException
     * @throws java.net.URISyntaxException
     */
    private void nowaAutoryzacja(KontrolerGUI kgui) 
        throws IOException, SAXException, FlickrException, URISyntaxException 
    {        
		String frob = this.flickr.getAuthInterface().getFrob();
		URL authUrl = this.flickr.getAuthInterface().buildAuthenticationUrl(Permission.READ, frob);
        
        Desktop.getDesktop().browse(new URI(authUrl.toExternalForm()));
        
        JOptionPane.showInputDialog(
            kgui.getOwner(), 
            "<html>" +
            "W celu autoryzacji powinna zostać otworzona strona WWW o poniższym adresie." +
            "<br>" +
            "Po autoryzacji zamknij to okno." +
            "</html>",
            authUrl.toExternalForm());        
		
		Auth token = this.flickr.getAuthInterface().getToken(frob);
		RequestContext.getRequestContext().setAuth(token);
		this.authStore.store(token);
        
        JOptionPane.showMessageDialog(
            kgui.getOwner(), 
            "Autoryzacja przebiegła pomyślnie i zostanie zapisana.");
	}
    
    /**
     * Czy poprawnie autoryzowany
     * 
     * @return
     */
    public boolean czyAutoryzowany() {
        return autoryzowany;
    }
    
}
