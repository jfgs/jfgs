/*
 */

package jfgs.ak;

import com.aetrion.flickr.groups.pools.PoolsInterface;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.comments.Comment;
import com.aetrion.flickr.photos.comments.CommentsInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import jfgs.gui.KontrolerGUI;
import jfgs.narzedzia.ILogika;

/**
 * Odczytanie wydźwięku komentarzy pod zdjęciem
 * @author michalus
 */
public class AnalizaKomentarzy implements ILogika {

    private KontrolerGUI kgui;
    private int liczbaWszystkichZdjecPuli;


    public void podlaczGUI(KontrolerGUI kontroler) {
        this.kgui = kontroler;
    }

    public int wykonajZadanie() {

        PoolsInterface pi = null;
        PhotoList listaZdjec = null;

        try {
            pi = kgui.getFlickr().getPoolsInterface();
            listaZdjec = pi.getPhotos(kgui.getGroupId(), new String[]{}, 500, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return ILogika.WYKONANIE_BLEDNE;
        }

        liczbaWszystkichZdjecPuli = listaZdjec.getTotal();

        /*
         * Kolekcja zdjęć w zakresie kryteriów
         */
        ArrayList<Photo> zdjecia = new ArrayList<Photo>();

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

            /*
             * Następne zdjęcie w puli zdjęć
             */
            Photo p = (Photo) i.next();

            System.err.println(p.getTitle());                                   //Do wyrzucenia

            try {

                /*
                 * Warunek na kryteria wyboru zdjęć
                 */
                if (!p.getDateAdded().after(kgui.dajDataOd())
                        || !p.getDateAdded().before(kgui.dajDataDo()))
                {

                    // zdjęcie poza zakresem badanych dat

                } else {

                    numerPrzetwarzanegoZdjecia++;

                    /*
                     * Zapamiętujemy przetwarzane zdjęcia
                     */
                    zdjecia.add(p);

                    CommentsInterface ci = null;
                    Collection komentarze = null;
                    try {
                        ci = kgui.getFlickr().getCommentsInterface();
                        komentarze = ci.getList(p.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ILogika.WYKONANIE_BLEDNE;
                    }

                    Iterator ic = komentarze.iterator();

                    /*
                     * Zliczanie komentarzy autora
                     */
                    while (ic.hasNext()) {

                        Comment komentarz = (Comment) ic.next();

                        StringTokenizer st = new StringTokenizer(komentarz.getText());
                        while(st.hasMoreTokens()) {
                            String s = st.nextToken();
                            s = s.replaceAll("[-!\"#$%&'()*+,./:;<=>?@[\\\\]_`{|}~]", "").toLowerCase().trim();
                            if (!"".equals(s) && s.length()>2 && s.length()<20) {
                                //System.out.println("\""+s+"\"");
                            }
                        }

                    } // komentarze

                } // w zakresie dat

                /*
                 * Optymalizacja, zdjęć za datą końcową nie analizujemy
                 */
            
                if (p.getDateAdded().before(kgui.dajDataOd())) {
                    break;
                }
                
            } catch(Exception e) {
                e.printStackTrace();
                return ILogika.WYKONANIE_BLEDNE;
            }

        } // wszystkie zdjęcia w puli

        System.err.println("EOF");

        return ILogika.WYKONANIE_POPRAWNE;

    }

}
