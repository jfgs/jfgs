/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.logika;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.groups.Group;
import com.aetrion.flickr.groups.GroupsInterface;
import com.aetrion.flickr.groups.pools.PoolsInterface;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.util.AuthStore;
import java.io.IOException;
import com.aetrion.flickr.util.FileAuthStore;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import org.xml.sax.SAXException;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.comments.CommentsInterface;
import java.util.Collection;
import java.util.HashMap;
import com.aetrion.flickr.photos.comments.Comment;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URI;
import java.text.NumberFormat;
import java.util.Arrays;
import javax.swing.JOptionPane;
import jfgs.gui.KontrolerGUI;

/**
 * Logika liczenia statystyk
 * 
 * @author michalus
 */
public class JFlickrGroupStats {
    
    private KontrolerGUI kgui;
    
    private NumberFormat nf;    
    private BufferedWriter out;
    
    /**
     * Jednoczesne drukowanie na ekran oraz do pliku
     * @param s
     */
    private void drukuj(String s) {
        try {
            System.out.println(s);
            doPliku(s+"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Drukowanie do pliku. Jeżeli plik nie jest otwraty otwiera go.
     * 
     * @param s
     * @throws java.io.IOException
     * @see drukuj
     */
    private void doPliku(String s) throws IOException {
        if (out == null) {
            out = new BufferedWriter(new FileWriter(Constants.output));
        }
        out.write(s);
        out.flush();
    }
    
    /**
     * Zamykanie pliku z logiem
     * 
     * @throws java.io.IOException
     * @see drukuj
     */
    private void zamknijPlik() throws IOException {
        if (out != null) {
            out.flush();
            out.close();
        }
    }
    
    public JFlickrGroupStats(String groupId, KontrolerGUI kgui) {
        
        this.kgui = kgui;
        
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setMinimumIntegerDigits(3);
        
        try {

            GroupsInterface gi = kgui.getFlickr().getGroupsInterface();
            Group g = gi.getInfo(groupId);

            drukuj("Grupa: " + g.getName());

            g.getMembers();

            PoolsInterface pi = kgui.getFlickr().getPoolsInterface();
            PhotoList listaZdjec = pi.getPhotos(groupId, new String[]{}, 500, 1);

            CommentsInterface ci = kgui.getFlickr().getCommentsInterface();

            HashMap<String, Stats> aktywnosc = new HashMap<String, Stats>();

            Iterator i = listaZdjec.iterator();
            int numerZdjecia = 0;

            while (i.hasNext()) {

                numerZdjecia++;

                Photo p = (Photo) i.next();

                String nazwaZdjecia = p.getTitle().trim();
                if (nazwaZdjecia.length() == 0) {
                    nazwaZdjecia = "(...)";
                }                

                if (aktywnosc.containsKey(p.getOwner().getId())) {
                    Stats s = aktywnosc.get(p.getOwner().getId());
                    s.dodajZdjecie();
                    aktywnosc.put(p.getOwner().getId(), s);
                } else {
                    aktywnosc.put(p.getOwner().getId(), new Stats(0, 1, p.getOwner().getUsername()));
                }

                Collection komentarze = ci.getList(p.getId());
                Iterator ic = komentarze.iterator();
                
//                {
//                    Date d = p.getDateAdded();
//                    
//                    boolean drukujDate = false;
//                    int miesiac = d.getMonth();
//                    int rok = d.getYear();
//                    
//                    if (ostatniRok == -1 && ostatniMiesiac == -1) {
//                        drukujDate = true;
//                    } else if (ostatniMiesiac != miesiac && ostatniRok != rok) {
//                        drukujDate = true;
//                        ostatniMiesiac = miesiac;
//                        ostatniRok = rok;
//                    }
//
//                    if (drukujDate) {
//                        drukuj(
//                            "<i>Miesiąc: "
//                            + rok
//                            + "/" 
//                            + miesiac
//                            + "</i>\n");
//                    }
//                }
                
                drukuj(
                    nf.format(numerZdjecia) 
                    + ": " 
                    + "<a href=\"" 
                    + p.getUrl() 
                    + "\">" 
                    + nazwaZdjecia 
                    + "</a>" 
                    + " by " 
                    + p.getOwner().getUsername() 
                    + " (" 
                    + (komentarze.size() == 0 ? "<b>" + komentarze.size() + "</b>" : "" + komentarze.size())
                    + ")" );

                while (ic.hasNext()) {
                    Comment komentarz = (Comment) ic.next();

                    if (p.getOwner().getId().equals(komentarz.getAuthor())) {
                        // Swoich komentarzy nie liczymy
                    } else {
                        if (aktywnosc.containsKey(komentarz.getAuthor())) {
                            Stats s = aktywnosc.get(komentarz.getAuthor());
                            s.dodajKomentarz();
                            aktywnosc.put(komentarz.getAuthor(), s);
                        } else {
                            aktywnosc.put(komentarz.getAuthor(), new Stats(1, 0, komentarz.getAuthorName()));
                        }
                    }
                }

                //if (numerZdjecia>10) {
                //    break;
                //}
            }

            int maksymalnaWartosc = Integer.MIN_VALUE;
            int minimalnaWartosc = Integer.MAX_VALUE;
            {
                Iterator is = aktywnosc.keySet().iterator();
                while (is.hasNext()) {

                    String key = (String) is.next();
                    Stats s = aktywnosc.get(key);

                    if (s.dajWartosc() < minimalnaWartosc) {
                        minimalnaWartosc = s.dajWartosc();
                    }
                    if (s.dajWartosc() > maksymalnaWartosc) {
                        maksymalnaWartosc = s.dajWartosc();
                    }
                }
            }

            Object[] st = aktywnosc.values().toArray();
            Arrays.sort(st);

            for (Object o : st) {
                printBar((Stats) o, minimalnaWartosc, maksymalnaWartosc);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            zamknijPlik();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     * Słupek z punktacją
     * @param s
     * @param minimalna
     * @param maksymalna
     */
    private void printBar(Stats s, int minimalna, int maksymalna) {
        
        /*
         * Użytkowników nie dodających zdjęć nie drukujemy
         */
        if (s.dajLiczbeZdjec() == 0) {
            return;
        }
        
        final int dlugoscUyztkownika = 15;
        final int dlugoscBelki = 30;
        String linia = "";
        
        /*
         * Nazwa użytkownika
         */
        String uzytkownik = s.dajNazwe().trim();
        if (uzytkownik.length()>dlugoscUyztkownika) {
            uzytkownik = uzytkownik.substring(0, dlugoscUyztkownika);
        }
        while (uzytkownik.length()<dlugoscUyztkownika) {
            uzytkownik = uzytkownik + " ";
        }
        
        linia = uzytkownik+" ";
        
        /*
         * Długość słupka i rysowanie słupka
         */
        double p = 
            ((double) (s.dajWartosc() - minimalna) 
                / (double) (maksymalna - minimalna))
            * dlugoscBelki;
        p = Math.round(p);
        
        for (int i=0; i<dlugoscBelki; i++) {
            if (i<p) {
                linia = linia + "#";
            } else {
                linia = linia + ".";
            }                    
        }
        
        /*
         * Punktacja
         */
        linia = linia 
            + " [" 
            + s.dajLiczbeKomentarzy() 
            + "/" 
            + s.dajLiczbeZdjec()
            + ":" 
            + s.dajWartosc()+"]";        
        
        drukuj(linia);
        
    }    
        
}
