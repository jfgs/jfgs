/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.zm;

import jfgs.narzedzia.DaneWyjsciowe;
import com.aetrion.flickr.groups.pools.PoolsInterface;
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
import jfgs.gui.KontrolerGUI;

/**
 * "Zdjęcie miesiąca" to logika pozwalająca podsumować dowolnie wybrany okres
 * kilku miesięcy dla danej grupy. Podsumowanie zdjęć, ich autorół oraz komentarzy
 * które dodali autorzy zdjęć.
 * 
 * @author michalus
 */
public class ZdjecieMiesiaca {
    
    /**
     * Wyłączam graficzny pasek podsumowania bo niemożliwe jest uzycie znaczników
     * <pre> w komentarzach na Flick, wszystko się rozjeżdża
     */
    private static final boolean graficznyPasekPodsumowania = false;
    
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
    
    private KontrolerGUI kgui;
    private DaneWyjsciowe dw;
    
    
    
    public ZdjecieMiesiaca(String groupId, KontrolerGUI kgui) {
        
        this.kgui = kgui;
        dw = new DaneWyjsciowe();        
        
        /*
         * Wszystkie zdjęcia w puli
         */ 
        int liczbaWszystkichZdjecPuli = 0;
        
        try {

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
            
            dw.drukujSeparator();
            
            

            PoolsInterface pi = kgui.getFlickr().getPoolsInterface();
            PhotoList listaZdjec = pi.getPhotos(groupId, new String[]{}, 500, 1);
            
            liczbaWszystkichZdjecPuli = listaZdjec.getTotal();

            /*
             * Kolekcja obiektów reprezentujących aktywność użytkownika, kluczem
             * jest identyfikator użytkownika
             */
            HashMap<String, StatystykaAutora> aktywnosc = new HashMap<String, StatystykaAutora>();
            
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
                    
                    String nazwaZdjecia = p.getTitle().trim();
                    if (nazwaZdjecia.length() == 0) {
                        nazwaZdjecia = "(...)";
                    }                

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
                        + dw.formatujDate(p.getDateAdded()));

                    /*
                     * Zliczanie komentarzy autora
                     */
                    while (ic.hasNext()) {

                        Comment komentarz = (Comment) ic.next();

                        if (p.getOwner().getId().equals(komentarz.getAuthor())) {
                            // Swoich komentarzy nie liczymy
                        } else {
                            if (aktywnosc.containsKey(komentarz.getAuthor())) {
                                StatystykaAutora s = aktywnosc.get(komentarz.getAuthor());
                                s.dodajKomentarz();
                                aktywnosc.put(komentarz.getAuthor(), s);
                            } else {
                                aktywnosc.put(
                                    komentarz.getAuthor(), 
                                    new StatystykaAutora(1, 0, komentarz.getAuthorName()));
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

            } // wszystkie zdjęcia w puli

            
            
            /*
             * Poniżej akcje wykonane po przeanalizowaniu całej puli zdjęć
             */
            
            
            
            /*
             * Pasek ustawiony do końca
             */
            kgui.ustawPostep(100);
            
            dw.drukujSeparator();
            
            /*
             * Wydruk wydruku "pasków" ocen
             */
            if (dodajPodsumowanieZbiorcze) {
            
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
                        StatystykaAutora s = aktywnosc.get(key);

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

                drukujNaglowekOcen();
                
                for (Object o : st) {                    
                    drukujPasekOcen(
                        (StatystykaAutora) o,
                        minimalnaWartosc, 
                        maksymalnaWartosc);                    
                }
                
                dw.drukujSeparator();
                
            }
            
            /*
             * Warunek wydruku kostki miniaturek
             */
            if (dodajKostkeMiniaturek) {
                
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
                
                dw.drukujSeparator();
                
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            dw.zamknijPlik();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     * Nagłówek dla słupka z punktacją     
     */
    private void drukujNaglowekOcen() {
        pasekOcen(null, 0, 0, true);
    }
    
    /**
     * Słupek z punktacją
     * @param s tablica ocen
     * @param minimalna wartość dla funkcji oceny
     * @param maksymalna wartość dla funkcji oceny
     */
    private void drukujPasekOcen(StatystykaAutora s, int minimalna, int maksymalna) {
        pasekOcen(s, minimalna, maksymalna, false);
    }    
    
    /*
     * Narzędziowa funkcja drukująca pasek lub nagłówek
     */
    private void pasekOcen(StatystykaAutora s, int minimalna, int maksymalna, boolean czyNaglowek) {
        
        /*
         * Użytkowników nie dodających zdjęć nie drukujemy
         */
        if (!czyNaglowek && s.dajLiczbeZdjec() == 0) {
            return;
        }
        
        final int dlugoscUyztkownika = 20;
        final int dlugoscBelki = 30;
        String linia = "";
        
        /*
         * Nazwa użytkownika
         */
        String uzytkownik = "";
        if (czyNaglowek) {
            uzytkownik = "~";
        } else {
            uzytkownik = s.dajNazwe().trim();
        }
        
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
        double p = 0;
        if (!czyNaglowek) {
            p = 
                ((double) (s.dajWartosc() - minimalna) 
                    / (double) (maksymalna - minimalna))
                * dlugoscBelki;
            p = Math.round(p);
        } else {
            p = 0;
        }
        
        if (graficznyPasekPodsumowania) {
            for (int i=0; i<dlugoscBelki; i++) {
                if (i<p) {
                    linia = linia + "#";
                } else {
                    linia = linia + ".";
                }                    
            }
        }
        
        if (!czyNaglowek) {
            /*
             * Punktacja
             */
            linia = linia 
                + " [" 
                + s.dajLiczbeKomentarzy() 
                + "/" 
                + s.dajLiczbeZdjec()
                + "/" 
                + s.dajWartosc()+"]";
        } else {
            linia = linia 
                + " [komentarze"                 
                + "/zdjęcia"                 
                + "/wartość" 
                +"]";
        }
        
        dw.drukujLinie(linia);
        
    }    
        
}
