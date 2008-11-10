/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.logika;

import com.aetrion.flickr.groups.Group;
import com.aetrion.flickr.groups.GroupsInterface;
import com.aetrion.flickr.groups.pools.PoolsInterface;
import com.aetrion.flickr.photos.PhotoList;
import java.io.IOException;
import java.util.Iterator;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.comments.CommentsInterface;
import java.util.Collection;
import java.util.HashMap;
import com.aetrion.flickr.photos.comments.Comment;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.Arrays;
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
        
        /*
         * Wszystkie zdjęcia w puli
         */ 
        int liczbaWszystkichZdjecPuli = 0;
        
        try {

            GroupsInterface gi = kgui.getFlickr().getGroupsInterface();
            Group g = gi.getInfo(groupId);

            drukuj("Grupa: " + g.getName());
            
            g.getMembers();

            PoolsInterface pi = kgui.getFlickr().getPoolsInterface();
            PhotoList listaZdjec = pi.getPhotos(groupId, new String[]{}, 500, 1);
            
            liczbaWszystkichZdjecPuli = listaZdjec.getTotal();

            CommentsInterface ci = kgui.getFlickr().getCommentsInterface();

            HashMap<String, Stats> aktywnosc = new HashMap<String, Stats>();

            Iterator i = listaZdjec.iterator();
            
            /*
             * Numer zdjęcia w puli zdjęć
             */
            int numerZdjeciaWPuli = 0;
            
            /*
             * Numer zdjęcia w zakresie kryteriów przeszukiwania
             */
            int numerPrzetwarzanegoZdjecia = 0;

            while (i.hasNext()) {

                numerZdjeciaWPuli++;
                numerPrzetwarzanegoZdjecia++;
                
                /*
                 * Przesuwamy pasek postępu
                 */                
                if (liczbaWszystkichZdjecPuli == 0) {
                    kgui.ustawPostep(0);
                } else {                    
                    kgui.ustawPostep(
                        (int) Math.round(
                            (double) numerZdjeciaWPuli 
                                / (double) liczbaWszystkichZdjecPuli
                                * 100));
                }

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
                
                if (!p.getDateAdded().after(kgui.dajDataOd()) 
                        || !p.getDateAdded().before(kgui.dajDataDo()))
                {
                    
                    // zdjęcie poza zakresem badanych dat
                    
                } else {
                
                    drukuj(
                        nf.format(numerPrzetwarzanegoZdjecia) 
                        + ": " 
                        + "<a href=\"" 
                        + p.getUrl() 
                        + "\">" 
                        + nazwaZdjecia 
                        + "</a>" 
                        + " by " 
                        + p.getOwner().getUsername() 
                        + " (" 
                        + (komentarze.size() == 0 
                            ? "<b>" + komentarze.size() + "</b>" 
                            : "" + komentarze.size())
                        + ")" 
                        + ", "
                        + p.getDateAdded());

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
                                aktywnosc.put(
                                    komentarz.getAuthor(), 
                                    new Stats(1, 0, komentarz.getAuthorName()));
                            }
                        }

                    } // komentarze
                
                } // w zakresie dat

            } // zdjęcia

            /*
             * Przeszukanie wartości ocen wszystkich użytkowników, wyszukanie
             * najlepszej i najgorszej oceny do wydruku paska ocen
             */
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

            /*
             * Wydruk pasków ocen
             */
            {
                Object[] st = aktywnosc.values().toArray();
                Arrays.sort(st);

                for (Object o : st) {                    
                    printBar(
                        (Stats) o, 
                        minimalnaWartosc, 
                        maksymalnaWartosc);                    
                }
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
