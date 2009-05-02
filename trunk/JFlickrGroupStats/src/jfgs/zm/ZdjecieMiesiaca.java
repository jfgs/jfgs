/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.zm;

import jfgs.narzedzia.DaneWyjsciowe;
import com.aetrion.flickr.groups.pools.PoolsInterface;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.PhotoList;
import java.io.IOException;
import java.util.Iterator;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.comments.CommentsInterface;
import java.util.Collection;
import java.util.HashMap;
import com.aetrion.flickr.photos.comments.Comment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import jfgs.gui.KontrolerGUI;
import jfgs.narzedzia.ILogika;
import jfgs.narzedzia.WykresSlupkowy;

/**
 * "Zdjęcie miesiąca" to logika pozwalająca podsumować dowolnie wybrany okres
 * kilku miesięcy dla danej grupy. Podsumowanie zdjęć, ich autorół oraz komentarzy
 * które dodali autorzy zdjęć.
 * 
 * @author michalus
 */
public class ZdjecieMiesiaca implements ILogika {
    
    /**
     * Czy po liście zdjęć dodać podsumowanie zbiorcze: użytkownik, liczba
     * komentarzy i bilans komentarze do zdjęć     
     */
    private static final boolean dodajPodsumowanieZbiorcze = true;
    
    /**
     * Czy generujemy kostkę z miniaturkami zdjęć
     */
    private static final boolean dodajKostkeMiniaturek = true;
    
    /**
     * Liczba miniatur w wierszu, trzeba zmieścić się w szerokości pola
     * do komentarzy
     */
    private static final int liczbaZdjecWierszaKostkiMiniaturek = 5;

    /**
     * Czy generujemy zestawienie zdjęć najbardziej oglądanych
     * @see liczbaZestawieniaNajbardziejPopularnych
     */
    private static final boolean dodajPodsumowaniePopularnosci = true;

    /**
     * Liczba zestawienia zdjęć najbardziej popularnych
     * @see dodajPodsumowaniePopularnosci
     */
    private static final int liczbaZestawieniaNajbardziejPopularnych = 10;
    
    private KontrolerGUI kgui;
    private DaneWyjsciowe dw;
    


    public ZdjecieMiesiaca() { }

    public int wykonajZadanie() {

        if (kgui == null) {
            throw new RuntimeException("Brak kontrolera GUI!");
        }

        dw = new DaneWyjsciowe();

        /*
         * Wszystkie zdjęcia w puli
         */
        int liczbaWszystkichZdjecPuli = 0;

        /*
         * Kolekcja obiektów reprezentujących aktywność użytkownika, kluczem
         * jest identyfikator użytkownika
         */
        HashMap<String, StatystykaAutora> aktywnosc = null;

        /*
         * Kolekcja zdjęć w zakresie kryteriów
         */
        ArrayList<Photo> zdjecia = null;

        try {

            /*
             * Nagłówek
             */
            {

                dw.drukujSeparator();

                dw.drukujLinie(
                    "Grupa: "
                    + kgui.getNazwaGrupy());

                dw.drukujLinie(
                    "Zdjęcia dodane po "
                    + dw.formatujDate(kgui.dajDataOd())
                    + " i przed "
                    + dw.formatujDate(kgui.dajDataDo())
                    + ".");

            }

            /*
             * Główna pętla, analiza zdjęć i wypisanie ich na ekran
             */
            {
                dw.drukujSeparator("Analizowane zdjęcia");

                PoolsInterface pi = kgui.getFlickr().getPoolsInterface();

                Set dodatkoweParametry = new HashSet();
                dodatkoweParametry.add(Extras.VIEWS);

                /*
                 * Ustawiamy wszystkie "extras" bo chcemy mieć informacje o
                 * liczbie odsłąnięć zdjęcia
                 */
                PhotoList 
                    listaZdjec = pi.getPhotos(
                        kgui.getGroupId(),
                        new String[]{},
                        dodatkoweParametry,
                        500,
                        1);

                liczbaWszystkichZdjecPuli = listaZdjec.getTotal();

                aktywnosc = new HashMap<String, StatystykaAutora>();
                zdjecia = new ArrayList<Photo>();

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

                        /*
                         * Zliczanie zdjęć autora
                         */
                        if (aktywnosc.containsKey(p.getOwner().getId())) {
                            StatystykaAutora s = aktywnosc.get(p.getOwner().getId());
                            s.dodajZdjecie();
                            aktywnosc.put(
                                p.getOwner().getId(), s);
                        } else {
                            aktywnosc.put(
                                p.getOwner().getId(),
                                new StatystykaAutora(0, 1, p.getOwner().getUsername()));
                        }

                        CommentsInterface ci = kgui.getFlickr().getCommentsInterface();
                        Collection komentarze = ci.getList(p.getId());
                        Iterator ic = komentarze.iterator();

                        dw.drukujLinie(
                            dw.formatujLiczbe(numerPrzetwarzanegoZdjecia)
                            + ": "
                            + "<a href=\""
                            + p.getUrl()
                            + "\">"
                            + dajNazweZdjecia(p.getTitle())
                            + "</a>"
                            + " by "
                            + p.getOwner().getUsername()
                            + " ("
                            + (komentarze.size() == 0
                                ? "<b>" + komentarze.size() + "</b>"
                                : "" + komentarze.size())
                            + ")"
                            + ", "
                            + dw.formatujDate(p.getDateAdded()));

                        // musimy zapamiętać kto już komentował to zdjęcie
                        Vector<String> komentowaliJuz = new Vector<String>(10);

                        // premiujemy pierwszy komentarz pod cudzym zdjęciem
                        final double pierwszyKomentarz = 1;

                        // słabiej premiujemy kolejny komentarz pod cudzym zdjęciem
                        final double kolejnyKomentarz = 0.1;

                        /*
                         * Zliczanie komentarzy autora
                         */
                        while (ic.hasNext()) {

                            Comment komentarz = (Comment) ic.next();
                            
                            if (p.getOwner().getId().equals(komentarz.getAuthor())) {
                                
                                // Komentarze pod zdjęciem autora nie liczymy

                            } else {

                                // kolejne komentarze liczymy mniej hojnie

                                boolean komentowalJuz = false;

                                for (int j=0; j<komentowaliJuz.size(); j++) {
                                    if (komentowaliJuz.get(j).equals(komentarz.getAuthor())) {
                                        komentowalJuz = true;
                                        break;
                                    }
                                }

                                if (!komentowalJuz) {
                                    komentowaliJuz.add(komentarz.getAuthor());                                    
                                }

                                if (aktywnosc.containsKey(komentarz.getAuthor())) {

                                    // aktualizujemy klucz

                                    StatystykaAutora s = aktywnosc.get(komentarz.getAuthor());

                                    s.dodajKomentarz(
                                            (komentowalJuz
                                                ? kolejnyKomentarz
                                                : pierwszyKomentarz));

                                    aktywnosc.put(komentarz.getAuthor(), s);

                                } else {

                                    // dodajemy nowy klucz

                                    aktywnosc.put(
                                        komentarz.getAuthor(),
                                        new StatystykaAutora(
                                            (komentowalJuz 
                                                ? kolejnyKomentarz
                                                : pierwszyKomentarz),
                                            0,
                                            komentarz.getAuthorName())
                                    );
                                }

                            } // komentarz inny niż autora zdjęcia

                        } // komentarze

                    } // w zakresie dat

                    /*
                     * Optymalizacja, zdjęć za datą końcową nie analizujemy
                     */
                    if (p.getDateAdded().before(kgui.dajDataOd())) {
                        break;
                    }

                } // wszystkie zdjęcia w puli

                /*
                 * Pasek ustawiony do końca
                 */
                kgui.ustawPostep(100);
            }

            /*
             * Poniżej akcje wykonane po przeanalizowaniu całej puli zdjęć
             */

            /*
             * Wydruk wydruku "pasków" ocen
             */
            if (dodajPodsumowanieZbiorcze) {

                dw.drukujSeparator("Podsumowanie");

                dw.drukujLinie(
                      "Wykres poniżej prezentuje liczbę dodanych szczerych komentarzy"
                    + " do liczby zdjęć dodanych do grupy. Komentarze autora"
                    + " pod własnym zdjęciem nie są liczone. Kolejne "
                    + "komentarze pod cudzym zdjęciem liczone są jako 1/10 "
                    + "punkta.");
                dw.drukujLinie("");

                /*
                 * Przeszukanie wartości ocen wszystkich użytkowników, wyszukanie
                 * najlepszej i najgorszej oceny do wydruku paska ocen
                 */
                double maksymalnaWartosc = Double.MIN_VALUE;
                double minimalnaWartosc = Double.MAX_VALUE;
                {
                    Iterator is = aktywnosc.keySet().iterator();
                    while (is.hasNext()) {

                        String key = (String) is.next();
                        StatystykaAutora s = aktywnosc.get(key);

                        if (s.dajWartosc() < minimalnaWartosc) {
                            minimalnaWartosc = s.dajWartosc();
                        }
                        if (s.dajWartosc() > maksymalnaWartosc) {
                            maksymalnaWartosc = s.dajWartosc();
                        }
                    }
                }

                StatystykaAutora[] sat = new StatystykaAutora[aktywnosc.size()];
                aktywnosc.values().toArray(sat);
                Arrays.sort(sat);

                {
                    WykresSlupkowy ws = new WykresSlupkowy();

                    for (StatystykaAutora sa : sat) {

                        // Użytkowników nie dodających zdjęć nie drukujemy
                        if (sa.dajLiczbeZdjec() != 0) {
                            ws.add(
                                sa.dajNazwe(),
                                ws.getFormat().format(
                                    sa.dajLiczbeZdjec())
                                + "/"
                                + ws.getFormat().format(
                                    sa.dajLiczbeKomentarzy()),
                                sa.dajWartosc());
                        }
                        
                    }

                    dw.drukuj(ws.get());

                }
                
            }

            /*
             * Warunek zestawienia TOP wg popularności
             */
            if (dodajPodsumowaniePopularnosci) {
                
                Photo[] top = new Photo[liczbaZestawieniaNajbardziejPopularnych];
                Iterator<Photo> ip = zdjecia.iterator();

                while(ip.hasNext()) {

                    Photo p = ip.next();

                    /*
                     * Czy zdjęcie wchodzi na ostatnią pozycję listy TOP
                     */
                    if (top[top.length-1] == null 
                        || p.getViews() >= top[top.length-1].getViews())
                    {

                        top[top.length-1] = p;

                        /*
                         * Czy trzeba sortować zdjęcia powyżej
                         */
                        for(int i=top.length-2; i>=0; i--) {

                            Photo tmpT = null;

                            if (top[i] == null
                                || top[i].getViews() <= top[i+1].getViews())
                            {
                                tmpT = top[i];
                                top[i] = top[i+1];
                                top[i+1] = tmpT;
                            }
                            
                        } // wszystkie zdjęcia powyżej ostatniego

                    } // jeżeli przedostatnie nieposortowane

                } // wszystkie zdjęcia

                dw.drukujSeparator("TOP"+liczbaZestawieniaNajbardziejPopularnych);

                for(int i=0; i<top.length; i++) {

                    if (top[i] != null) {
                        dw.drukujLinie(
                            dw.formatujLiczbe(i+1)
                            + ": "
                            + "<a href=\""
                            + top[i].getUrl()
                            + "\">"
                            + dajNazweZdjecia(top[i].getTitle())
                            + "</a>"
                            + " by "
                            + top[i].getOwner().getUsername()
                            + " "
                            + top[i].getViews()
                            );
                    } else {
                        dw.drukujLinie(
                            dw.formatujLiczbe(i)
                            + ": "
                            );
                    }

                } // podsumowanie całego TOP

            }

            /*
             * Warunek wydruku kostki miniaturek
             */
            if (dodajKostkeMiniaturek) {

                dw.drukujSeparator("Podgląd zdjęć");

                Iterator<Photo> ip = zdjecia.iterator();
                int zdjecieWKostce = 1;

                while(ip.hasNext()) {

                    Photo zdjecie = ip.next();

                    dw.drukuj(
                        "<a href=\""
                        + zdjecie.getUrl()
                        + "\" "
                        + "title=\""
                        + zdjecie.getTitle()
                        + " by "
                        + zdjecie.getOwner().getUsername()
                        + ", on Flickr\">"
                        + "<img src=\""
                        + zdjecie.getSmallSquareUrl()
                        + "\" "
                        + "width=\"75\" "
                        + "height=\"75\" "
                        + "alt=\""
                        + zdjecie.getTitle()
                        + "\" /></a>");

                    /*
                     * Na koniec wiersza i po ostatnim zdjęciu chcemy mieć
                     * znak nowej linii
                     */
                    if (zdjecieWKostce % liczbaZdjecWierszaKostkiMiniaturek == 0
                        || !ip.hasNext())
                    {
                        dw.drukujLinie("");
                    }

                    zdjecieWKostce++;

                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /*
         * Ostatni separator przed zamknięciem pliku
         */
        dw.drukujSeparator();

        try {
            dw.zamknijPlik();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        dw.pokazOkno();

        return ILogika.WYKONANIE_POPRAWNE;
    }

    public void podlaczGUI(KontrolerGUI kontroler) {
        this.kgui = kontroler;
    }

    /**
     * Zamienia pustę nazwę zdjęcia
     * @param nazwa
     * @return
     */
    private String dajNazweZdjecia(String nazwa) {
        String n = nazwa.trim();
        if ("".equals(n)) {
            n = "(...)";
        }
        return n;
    }
        
}