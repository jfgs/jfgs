package jfgs.test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.util.IOUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author michalus
 */
public class Autoryzacja {

    Flickr f;
    RequestContext requestContext;
    String frob = "";
    String token = "";
    Properties properties = null;

    public Autoryzacja() throws ParserConfigurationException, IOException, SAXException {
        InputStream in = null;
        /*try {
            in = getClass().getResourceAsStream("/setup.properties");
            properties = new Properties();
            properties.load(in);
        } finally {
            IOUtilities.close(in);
        }*/
        f = new Flickr(
            /*properties.getProperty("apiKey")*/ "669b6ffa41c6994677c3f20a02d1db59",
            /*properties.getProperty("secret")*/ "5c04af7e59798cc6",
            new REST()
        );
        Flickr.debugStream = false;
        requestContext = RequestContext.getRequestContext();
        AuthInterface authInterface = f.getAuthInterface();
        try {
            frob = authInterface.getFrob();
        } catch (FlickrException e) {
            e.printStackTrace();
        }
        System.out.println("frob: " + frob);
        URL url = authInterface.buildAuthenticationUrl(Permission.READ, frob);
        System.out.println("Press return after you granted access at this URL:");
        System.out.println(url.toExternalForm());
        BufferedReader infile =
          new BufferedReader ( new InputStreamReader (System.in) );
        String line = infile.readLine();
        try {
            Auth auth = authInterface.getToken(frob);
            System.out.println("Authentication success");
            // This token can be used until the user revokes it.
            System.out.println("Token: " + auth.getToken());
            System.out.println("nsid: " + auth.getUser().getId());
            System.out.println("Realname: " + auth.getUser().getRealName());
            System.out.println("Username: " + auth.getUser().getUsername());
            System.out.println("Permission: " + auth.getPermission().getType());
        } catch (FlickrException e) {
            System.out.println("Authentication failed");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Autoryzacja t = new Autoryzacja();
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
