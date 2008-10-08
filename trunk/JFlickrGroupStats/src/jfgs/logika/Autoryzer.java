/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.logika;

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
    
    public Flickr getFlickr() throws ParserConfigurationException {        
        return flickr;
    }
    
    public void autoryzuj(KontrolerGUI kgui) 
        throws IOException, SAXException, FlickrException, URISyntaxException 
    {
        przywrocAutoryzacje(kgui);
    }
    
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
    
    public boolean czyAutoryzowany() {
        return autoryzowany;
    }
    
}
