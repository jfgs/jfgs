/*
 *  This file is part of JFlickrGroupStats.
 *
 *  JFlickrGroupStats is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JFlickrGroupStats is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JFlickrGroupStats.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jfgs.zm;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.groups.members.Member;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import jfgs.gui.KontrolerGUI;
import jfgs.narzedzia.Autoryzer;
import jfgs.narzedzia.ILogika;
import jfgs.narzedzia.IPanelKonfiguracyjny;
import jfgs.narzedzia.PhotoComparatorWgAutora;
import jfgs.narzedzia.WykresSlupkowy;
import org.xml.sax.SAXException;

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
    private boolean dodajPodsumowanieZbiorcze = false;

    /**
     * Jako wykres słupkowy
     * @see dodajPodsumowanieZbiorcze
     */
    private boolean wykresSlupkowy = false;

    /**
     * Jako lista
     * @see dodajPodsumowanieZbiorcze
     */
    private boolean wykresLista = false;

    /**
     * Kod HTML dla analizowanych zdjęć
     */
    private boolean dodajKodHTML = false;
    
    /**
     * Czy generujemy kostkę z miniaturkami zdjęć
     */
    private boolean dodajKostkeMiniaturek = false;
    
    /**
     * Liczba miniatur w wierszu, trzeba zmieścić się w szerokości pola
     * do komentarzy
     */
    private static final int LICZBA_ZDJEC_WIERSZA_KOSTKI_MINIATUR = 5;

    /**
     * Czy generujemy zestawienie zdjęć najbardziej oglądanych
     * @see liczbaZestawieniaNajbardziejPopularnych
     */
    private boolean dodajPodsumowaniePopularnosci = false;

    /**
     * Liczba zestawienia zdjęć najbardziej popularnych
     * @see dodajPodsumowaniePopularnosci
     */
    private static final int LICZBA_ZESTAWIENIA_NAJBARDZIEJ_POPULARNYCH = 10;

    /**
     * Premiujemy pierwszy komentarz pod cudzym zdjęciem
     */
    private static final double VAL_PIERWSZY_KOMENTARZ = 1;

    /**
     * Słabiej premiujemy kolejny komentarz pod cudzym zdjęciem
     */
    private static final double VAL_KOLEJNY_KOMENTARZ = 0.1;

    /**
     * Liczba zdjęć na stronie, preferujemy większą liczbę ponieważ
     * pobieranie kolejnych stron (odpowiedź na szersze zapytanie)
     * lokalnie nic nas nie kosztuje, koszt odrzucenia zdjęć w obrębie
     * strony jest i tak liniowy i szybki
     */
    private static final int ZDJEC_NA_STRONE = 333;

    /**
     * Stała używana podczas drukowania dat ostatnich komentarzy i zdjęć
     */
    final String POZA_ZAKRESEM = "(<i>poza zakresem</i>)";

    /**
     * Szukanie osób, którzy nie dodali zdjęć
     */
    private boolean drukujBrakZdjec = false;

    /**
     * Szukanie osób, którzy nie komentowali
     */
    private boolean drukujBrakKomentarzy = false;
    
    /*
     * Ile miesięcy bez zdjęc jest do zaakceptowania
     */
    private int mcBezZdjec = 0;

    /**
     * Ile miesięcy bez komentarzy jest do zaakceptowanie
     */
    private int mcBezKomentarzy = 0;

    /**
     * Czy drukujemy nowym układem strony, narazie bez GUI
     */
    private boolean nowyUkladStrony = true;
    
    private KontrolerGUI kgui;
    private DaneWyjsciowe dw;    

    public ZdjecieMiesiaca() { }

    /**
     * Zwraca wyszukane wg kryteriów strony z zdjęciami
     * 
     * @param f
     * @param groupID
     * @param dataOd
     * @param dataDo
     * @param extDataOd rozszerzona data początku zakresu
     * @return
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws com.aetrion.flickr.FlickrException
     */
    private ArrayList<PhotoList> dajStrony(
        final Flickr f,
        final String groupID,
        final Date dataOd,
        final Date dataDo,
        final Date extDataOd
    ) throws IOException, SAXException, FlickrException
    {

        dw.drukujSeparator("Analizowane zdjęcia");

        PoolsInterface pi = f.getPoolsInterface();

        Set dodatkoweParametry = new HashSet();
        dodatkoweParametry.add(Extras.VIEWS);

        ArrayList<PhotoList> strony = new ArrayList<PhotoList>(10);

        /*
         * Pobranie listy wszystkich zdjęć
         */
        {
            // modyfikujemy zakres interesujących nas zdjęć jeżeli potrzeba
            Date poczatek = (drukujBrakKomentarzy || drukujBrakZdjec) ? extDataOd : dataOd;
            Date koniec = dataDo;

            /*
             * Najpierw pobieramy strony z interesującego nas zakresu dat,
             * potem będziemy je analizowali - po pierwsze szybko oszacujemy
             * ile zdjęć jest do analizy, po drugie zabezpieczymy się przed
             * zmianą stanu grupy (dodanie/usunięcie zdjęć) co spowodowało by
             * przesunięcie się "okienka" strony i zaburzenie porządku analizowania
             * zdjęć
             */
            {

                kgui.ustawPostep(0);

                boolean analizujNastepnaStrone = true;
                int numerAnalizowanejStrony = 1;

                while(analizujNastepnaStrone) {

                    kgui.ustawPostepStr(
                        "Analizowanie strony "+numerAnalizowanejStrony);

                    PhotoList
                        analizowana = pi.getPhotos(
                            groupID,
                            new String[]{},
                            dodatkoweParametry,
                            ZDJEC_NA_STRONE,
                            numerAnalizowanejStrony);

                    numerAnalizowanejStrony++;

                    if (analizowana.size() == 0) {
                        analizujNastepnaStrone = false;

                        break;
                    }

                    Date dataPierwszego =
                        ((Photo) analizowana.get(0)).getDateAdded();
                    Date dataOstatniego =
                        ((Photo) analizowana.get(
                            analizowana.size()-1)).getDateAdded();

                    // poza zakresem dat dla zakresu rozszerzonego lub podstawowego
                    if (dataPierwszego.before(poczatek) && dataOstatniego.before(koniec))
                    {

                            /*
                             * Jeżeli początek i koniec strony jest
                             * starszy niż data początku zakresu to
                             * nie ma sensu analizować zdjęć wcześniejszych
                             */
                            analizujNastepnaStrone = false;
                            break;

                    }

                    if (dataPierwszego.after(koniec) && dataOstatniego.after(koniec)) {

                        /*
                         * Jeżeli początek i koniec strony jest młodszy
                         * niż data końca zakresu to trzeba pobrać
                         * kolejną stronę
                         */

                        analizujNastepnaStrone = true;
                        continue;

                    }

                    strony.add(analizowana);

                } // analizowanie kolejnych stron
            }
        }

        return strony;

    }

    /**
     * Dla wyszukanej listy stron zwraca zdjęcia wg kryteriów
     *
     * @see dajStrony
     * @param strony
     * @param dataOd
     * @param dataDo
     * @return
     */
    private ArrayList<Photo> dajListeZdjec(
        final ArrayList<PhotoList> strony,
        final Date dataOd, 
        final Date dataDo,
        final Date extDataOd,
        final HashMap<String, Date> ostatnieZdjecieAutora)
    {

        ArrayList<Photo> 
            zal = new ArrayList<Photo>(ZDJEC_NA_STRONE * strony.size());

        /*
         * Wydłubanie interesujących nas zdjęć z stron, odrzucenie
         * początku i końca poza zakresem dat
         */
        {

            Date poczatek = (drukujBrakKomentarzy || drukujBrakZdjec) ? extDataOd : dataOd;
            Date koniec = dataDo;

            kgui.ustawPostepStr(
                "Analizowanie zdjęć wyszukanych stron");

            for (PhotoList s : strony) {

                Iterator is = s.iterator();

                while(is.hasNext()) {

                    Photo p = (Photo) is.next();

                    /**
                     * Szukamy ostatniego zdjęcia autora
                     */
                    if (drukujBrakZdjec
                        && !p.getDateAdded().before(extDataOd)
                        && (!ostatnieZdjecieAutora.containsKey(p.getOwner().getId())
                            || p.getDateAdded().after(ostatnieZdjecieAutora.get(p.getOwner().getId()))))
                    {
                        ostatnieZdjecieAutora.put(p.getOwner().getId(), p.getDateAdded());
                    }

                    /*
                     * Warunek na kryteria wyboru zdjęć
                     */
                    if (!p.getDateAdded().after(poczatek)
                        || !p.getDateAdded().before(koniec))
                    {

                        // zdjęcie poza zakresem badanych dat
                        // na razie analizujemy także rozszerzony początek

                    } else {

                        zal.add(p);

                    }

                } // zdjęcia strony

            } // analizowane strony

        }

        return zal;

    }

    /**
     * Zwraca tablicę zdjęć do analizy
     *
     * @param f
     * @param groupID
     * @param dataOd
     * @param dataDo
     * @return
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws com.aetrion.flickr.FlickrException
     */
    private Photo[] dajTabliceZdjec(
        final Flickr f,
        final String groupID,
        final Date dataOd,
        final Date dataDo,
        final Date extDataOd,
        final HashMap<String, Date> ostatnieZdjecieAutora
    ) throws IOException, SAXException, FlickrException
    {

        ArrayList<Photo>
            zal = dajListeZdjec(
                dajStrony(f, groupID, dataOd, dataDo, extDataOd),
                dataOd,
                dataDo,
                extDataOd,
                ostatnieZdjecieAutora);
        
        Photo[] z = new Photo[zal.size()];
        zal.toArray(z);

        return z;

    }

    /**
     * Zwraca listę unikalnych autorów zdjęć oraz inicjalizuje tablicę
     * aktywności zgodnie z liczną zdjęć
     * 
     * @param zdjecia
     * @param aktywnosc
     * @return
     */
    private HashMap<String, String> dajAutorow(
        final Photo[] zdjecia,
        final HashMap<String, StatystykaAutora> aktywnosc,
        final Date dataOd)
    {

        HashMap<String, String> autorzy = new HashMap<String, String>();

        /*
         * Wybranie unikalnych autorów
         */
        {
            
            String poprzedniId = null;
            String poprzUserName = null;
            int zdjecPoprzedniego = 0;

            for(int i=0; i<zdjecia.length; i++) {

                /**
                 * Teoretycznie mogą być tutaj zdjęcia z roszerzonego zakresu,
                 * pomijamy je
                 */
                if (!zdjecia[i].getDateAdded().before(dataOd)) {
                    
                    if (poprzedniId == null
                        || !poprzedniId.equals(zdjecia[i].getOwner().getId()))
                    {

                        if (poprzedniId != null) {

                            /*
                             * Zmiana autora, dodajemy podsumowanie liczby zdjęć
                             */
                            aktywnosc.put(
                                poprzedniId,
                                new StatystykaAutora(
                                    0,
                                    zdjecPoprzedniego,
                                    poprzUserName));

                        }

                        /*
                         * Kolejny autor
                         */
                        poprzedniId = zdjecia[i].getOwner().getId();
                        poprzUserName = zdjecia[i].getOwner().getUsername();
                        zdjecPoprzedniego = 0;

                        autorzy.put(poprzedniId, poprzUserName);

                    }

                    zdjecPoprzedniego++;

                }

            }

            // trzeba pamiętać aby dodać ostatniego do aktywności
            aktywnosc.put(
                poprzedniId,
                new StatystykaAutora(
                    0,
                    zdjecPoprzedniego,
                    poprzUserName));

        }

        return autorzy;

    }

    /**
     * Liczy komentarze zdjęcia, aktualizuje aktywność autorów o liczbę komentarzy
     * 
     * @param photoId
     * @param ownerId
     * @param f
     * @param autorzy
     * @param aktywnosc
     * @return
     * @throws com.aetrion.flickr.FlickrException
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     */
    private int policzKomentarzeZdjecia(
        final String photoId,
        final Date photoDateAdded,
        final String ownerId,
        final CommentsInterface ci,
        final HashMap<String, String> autorzy,
        final HashMap<String, StatystykaAutora> aktywnosc,
        final HashMap<String, Date> ostatniKomentarzAutora,
        final Date dataOd,
        final Date extDataOd,
        final HashMap<String, String> uzytkownicy
    ) throws FlickrException, IOException, SAXException
    {
        
        Collection komentarze = ci.getList(photoId);

        /*
         * Optymalizacja, tam dalej nic się nie dzieje jak nie
         * ma komentarzy
         */
        if (komentarze.size() == 0) {
            return 0;
        }

        Iterator ic = komentarze.iterator();

        // musimy zapamiętać kto już komentował to zdjęcie
        HashMap<String, String>
            komentowaliJuz = new HashMap<String, String>();

        /*
         * Zliczanie komentarzy autora
         */
        while (ic.hasNext()) {

            Comment komentarz = (Comment) ic.next();

            if (ownerId.equals(komentarz.getAuthor())) {

                // Komentarze pod zdjęciem autora nie liczymy

            } else if(!uzytkownicy.containsKey(komentarz.getAuthor())) {

                // Komentarze ludzi spoza grupy ignorujemy

            } else {

                /**
                 * Szukamy ostatniego komentarza autora (pod nieswoim zdjęciem)
                 */
                if (drukujBrakKomentarzy
                    && !photoDateAdded.before(extDataOd)
                    && (!ostatniKomentarzAutora.containsKey(komentarz.getAuthor())
                        || komentarz.getDateCreate().after(ostatniKomentarzAutora.get(komentarz.getAuthor()))))
                {
                    ostatniKomentarzAutora.put(komentarz.getAuthor(), komentarz.getDateCreate());
                } 

                if (photoDateAdded.before(dataOd)) {

                    // zdjęcie poza prawdziwym obszarem zainteresowań,
                    // analizujemy komentarze, ale nie liczymy już aktywności
                    // danego autora

                } else {

                    if (!autorzy.containsKey(komentarz.getAuthor())) {

                        /*
                         * Komentarz napisany przez użytkownika nie
                         * posiadającego zdjęcia w interesującym nas okresie
                         * więc go pomijamy
                         */
                        continue;

                    } else {

                        /*
                         * Kolejne komentarze liczymy mniej hojnie
                         */
                        boolean
                            komentowalJuz = komentowaliJuz.containsKey(
                                komentarz.getAuthor());

                        /*
                         * Zapisujemy kto już komentował
                         */
                        if (!komentowalJuz) {
                            komentowaliJuz.put(komentarz.getAuthor(), "");
                        }

                        /*
                         * Aktualizujemy klucz, wszystkie klucze dodane
                         * są już wyżej
                         */
                        StatystykaAutora s = aktywnosc.get(komentarz.getAuthor());

                        s.dodajKomentarz(
                                (komentowalJuz
                                    ? VAL_KOLEJNY_KOMENTARZ
                                    : VAL_PIERWSZY_KOMENTARZ));

                        aktywnosc.put(komentarz.getAuthor(), s);

                    } // komentarz spoza grupy autorów zdjęć

                } // komentarze zdjęcia przed datą początku obowiązywania

            } // komentarz inny niż autora zdjęcia

        } // komentarze

        return komentarze.size();

    }

    /**
     * Dla przekazanej tablicy zdjęć liczy komentarze i drukuje główne zestawienie
     * @param f
     * @param zdjecia
     * @param aktywnosc
     * @param autorzy
     * @param kodhtml
     * @throws com.aetrion.flickr.FlickrException
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     */
    private void wykonajLiczenieKomentarzy(
        final Flickr f,
        final Photo[] zdjecia,
        final HashMap<String, StatystykaAutora> aktywnosc,
        final HashMap<String, String> autorzy,
        final StringBuffer kodhtml,
        final HashMap<String, Date> ostatniKomentarzAutora,
        final Date dataOd,
        final Date extDataOd,
        final HashMap<String, String> uzytkownicy
    ) throws FlickrException, IOException, SAXException 
    {

        /*
         * Główna pętla, analiza zdjęć i wypisanie ich na ekran
         */
        {

            int kz = 0;

            final CommentsInterface ci = f.getCommentsInterface();

            kgui.ustawPostepMax(zdjecia.length - 1);

            // nie wszystkie zdjęcia drukujemy
            int drukowaneZdjecie = 1;

            /*
             * Główna pętla po wszystkich zdjęciach
             */
            for(int noZdjecia=0; noZdjecia<zdjecia.length; noZdjecia++) {

                /*
                 * Optymalizacja, ustawienie postępu to 1% całej operacji
                 * wystarczy jak wywołamy ją raz na pięć razy
                 */
                if (noZdjecia%5 == 1) {

                    kgui.ustawPostep(noZdjecia);

                    kgui.ustawPostepStr(
                        "Zdjęcie " +
                        noZdjecia +
                        "/" +
                        (zdjecia.length - 1));

                }

                kz =
                    policzKomentarzeZdjecia(
                        zdjecia[noZdjecia].getId(),
                        zdjecia[noZdjecia].getDateAdded(),                        
                        zdjecia[noZdjecia].getOwner().getId(),
                        ci,
                        autorzy,
                        aktywnosc,
                        ostatniKomentarzAutora,
                        dataOd,
                        extDataOd,
                        uzytkownicy);

                if (zdjecia[noZdjecia].getDateAdded().before(dataOd)) {

                    // to zdjęcie jest z roszerzonego zakresu i nie
                    // będzie drukowane, było użyte tylko do analizy
                    // dat komentarzy

                } else {

                    if (!nowyUkladStrony) {

                        kodhtml.append(
                            dw.formatujLiczbe(drukowaneZdjecie)
                            + ": &lt;a href=&quot;"
                            + zdjecia[noZdjecia].getUrl()
                            + "&quot;&gt;&lt;img src=&quot;"
                            + zdjecia[noZdjecia].getSmallUrl()
                            + "&quot;&gt;&lt;/a&gt;"
                            + "\n"
                        );

                        dw.drukujLinie(
                            dw.formatujLiczbe(drukowaneZdjecie)
                            + ": "
                            + "<a href=\""
                            + zdjecia[noZdjecia].getUrl()
                            + "\">"
                            + dajNazweZdjecia(zdjecia[noZdjecia].getTitle())
                            + "</a>"
                            + " by "
                            + zdjecia[noZdjecia].getOwner().getUsername()
                            + " ("
                            + (kz == 0
                                ? "<b>" + kz + "</b>"
                                : "" + kz)
                            + ")"
                            + ", "
                            + dw.formatujDate(zdjecia[noZdjecia].getDateAdded())
                        );

                    } else {

                        dw.drukuj(
                            "<a href=\""
                            + zdjecia[noZdjecia].getUrl()
                            + "\" "
                            + "title=\""
                            + zdjecia[noZdjecia].getTitle()
                            + " by "
                            + zdjecia[noZdjecia].getOwner().getUsername()
                            + ", on Flickr\">"
                            + "<img src=\""
                            + zdjecia[noZdjecia].getMediumUrl()
                            + "\" "
                            + "alt=\""
                            + zdjecia[noZdjecia].getTitle()
                            + "\" /></a>");

                        dw.drukujLinie("<blockquote>");

                        dw.drukujLinie(
                            "Lp. "
                            + dw.formatujLiczbe(drukowaneZdjecie)
                            + ", \"<u>"
                            + dajNazweZdjecia(zdjecia[noZdjecia].getTitle())                            
                            + "</u>\" by <u>"
                            + zdjecia[noZdjecia].getOwner().getUsername()
                            + "</u>"
                        );
                        
                        dw.drukujLinie("");

                        dw.drukujLinie(
                            "<i>&lt;a href=&quot;"
                            + zdjecia[noZdjecia].getUrl()
                            + "&quot;&gt;\n&nbsp;&nbsp;&nbsp;&nbsp;&lt;img src=&quot;"
                            + zdjecia[noZdjecia].getSmallUrl()
                            + "&quot;&gt;\n&lt;/a&gt;</i>"
                        );

                        dw.drukujLinie("</blockquote>");

                    }

                    drukowaneZdjecie++;

                }

            } // wszystkie wybrane zdjęcia

            /*
             * Pasek ustawiony do końca
             */
            kgui.ustawPostepMax(1);
            kgui.ustawPostep(1);
            kgui.ustawPostepStr("OK");

        }

    }

    /**
     * Drukowanie podsumowania zbibiorczego
     * 
     * @param aktywnosc
     */
    private void drukujPodsumowanieZbiorcze(
        final HashMap<String, StatystykaAutora> aktywnosc)
    {

        /*
         * Wydruk wydruku "pasków" ocen
         */
        if (dodajPodsumowanieZbiorcze) {

            dw.drukujNaglowek("Podsumowanie");

            dw.drukujLinie(
                  "Wykres poniżej prezentuje liczbę dodanych do grupy zdjęć do"
                + " liczby szczerych komentarzy (<i>zdjęcia</i> / <i>komentarze</i>). "
                + "Komentarze autora"
                + " pod własnym zdjęciem nie są liczone. Kolejne "
                + "komentarze pod cudzym zdjęciem liczone są jako 1/10 "
                + "punkta. Jeżeli wynik końcowy jest ujemny oznacza to, że"
                + " dany użytkownik dodał więcej zdjęć niż szczerych komentarzy.");
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

            if (wykresSlupkowy) {
                
                final WykresSlupkowy ws = new WykresSlupkowy();

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

            } else if(wykresLista) {

                final String separator = "; ";
                final WykresSlupkowy ws = new WykresSlupkowy();

                dw.drukujLinie(
                    "Nazwa" + separator +
                    "Liczba zdjęć" + separator +
                    "Liczba komentarzy" + separator +
                    "Wartość");

                for (StatystykaAutora sa : sat) {

                    dw.drukujLinie(
                        sa.dajNazwe()
                        + separator
                        + ws.getFormat().format(sa.dajLiczbeZdjec())
                        + separator
                        + ws.getFormat().format(sa.dajLiczbeKomentarzy())
                        + separator
                        + "<b>"
                        + ws.getFormat().format(sa.dajWartosc())
                        + "</b>"
                    );

                }

            } else {

                throw new RuntimeException("niy!");

            }

        }

    }

    /**
     * Drukowanie podsumowania popularnosci
     *
     * @param zdjecia
     */
    private void drukujPodsumowaniePopularnosci(
        final Photo[] zdjecia,
        final Date dataOd)
    {

        /*
         * Warunek zestawienia TOP wg popularności
         */
        if (dodajPodsumowaniePopularnosci) {

            Photo[] top = new Photo[LICZBA_ZESTAWIENIA_NAJBARDZIEJ_POPULARNYCH];

            for(int ip=0; ip<zdjecia.length; ip++) {

                if (zdjecia[ip].getDateAdded().before(dataOd)) {
                    continue;
                }

                /*
                 * Czy zdjęcie wchodzi na ostatnią pozycję listy TOP
                 */
                if (top[top.length-1] == null
                    || zdjecia[ip].getViews() >= top[top.length-1].getViews())
                {

                    top[top.length-1] = zdjecia[ip];

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

            dw.drukujSeparator("TOP"+LICZBA_ZESTAWIENIA_NAJBARDZIEJ_POPULARNYCH);

            dw.drukujLinie("Poniżej najbardziej popularne zdjęcia wg liczby odsłon.");
            dw.drukujLinie("");

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
                        + " - "
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

    }

    /**
     * Drukowanie kostki miniaturek
     *
     * @param zdjecia
     * @param kodhtml
     */
    private void drukujKostkeMiniaturek(
        final Photo[] zdjecia,
        final StringBuffer kodhtml,
        final Date dataOd)
    {
        /*
         * Warunek wydruku kostki miniaturek
         */
        if (!dodajKostkeMiniaturek) {

            dw.drukujSeparator("Podgląd zdjęć");

            int zdjecieWKostce = 1;

            for(int ip=0; ip<zdjecia.length; ip++) {

                if (zdjecia[ip].getDateAdded().before(dataOd)) {
                    continue;
                }

                dw.drukuj(
                    "<a href=\""
                    + zdjecia[ip].getUrl()
                    + "\" "
                    + "title=\""
                    + zdjecia[ip].getTitle()
                    + " by "
                    + zdjecia[ip].getOwner().getUsername()
                    + ", on Flickr\">"
                    + "<img src=\""
                    + zdjecia[ip].getSmallSquareUrl()
                    + "\" "
                    + "width=\"75\" "
                    + "height=\"75\" "
                    + "alt=\""
                    + zdjecia[ip].getTitle()
                    + "\" /></a>");

                /*
                 * Na koniec wiersza i po ostatnim zdjęciu chcemy mieć
                 * znak nowej linii
                 */
                if (zdjecieWKostce % LICZBA_ZDJEC_WIERSZA_KOSTKI_MINIATUR == 0
                    || (ip == zdjecia.length - 1))
                {
                    dw.drukujLinie("");
                }

                zdjecieWKostce++;

            }

            dw.drukujLinie("");
            
        }

        if (!nowyUkladStrony && dodajKodHTML) {

            dw.drukujSeparator("Kod HTML");

            dw.drukujLinie(
                "Poniżej kod HTML gotowy do skopiowania. Kod " +
                "zawiera odnośnik do zdjęcia wraz z jego miniaturą.");
            dw.drukujLinie("");

            dw.drukujLinie("<s>" + kodhtml.toString() + "</s>");

        }

    }

    /**
     * Drukowanie braku zdjęć w analizowanym zakresie
     *
     * @param dataDo data końca analizowanego okresu
     * @param uzytkownicy użytkownicy grupy
     * @param ostatnieZdjecieAutora statystyka ostatnich zdjęć wg autora
     */
    private void drukujBrakZdjec(
        final Date dataDo,
        final HashMap uzytkownicy,
        final HashMap<String, Date> ostatnieZdjecieAutora)
    {

        if (drukujBrakZdjec) {

            dw.drukujSeparator("***");

            Calendar c = Calendar.getInstance();
            c.setTime(dataDo);
            c.add(Calendar.MONTH, -mcBezZdjec);
            Date granica = c.getTime();

            dw.drukujLinie("Użytkownicy, którzy dodali swoje ostatnie "
                + "zdjęcia przed "+dw.formatujDate(granica)+"\n");

            Iterator<String> i = uzytkownicy.keySet().iterator();
            boolean rekordPozaZakresem = false;

            // wszyscy użytkownicy
            while(i.hasNext()) {

                String id = i.next();

                /*
                 * Musimy uważać, na tych co już się wypisali z grupy
                 */
                if (!uzytkownicy.containsKey(id)) {
                    continue;
                }

                // zapisana data ostatniego zdjęcia
                if (ostatnieZdjecieAutora.containsKey(id)) {

                    // zdjęcie poza granicą
                    if(granica.after(ostatnieZdjecieAutora.get(id))) {

                        dw.drukujLinie(
                            "*) "
                            + uzytkownicy.get(id)
                            + ", "
                            + dw.formatujDate(ostatnieZdjecieAutora.get(id)));

                        rekordPozaZakresem = true;

                    }

                } else {

                    dw.drukujLinie(
                            "*) "
                            + uzytkownicy.get(id)
                            + ", "
                            + POZA_ZAKRESEM);

                    rekordPozaZakresem = true;

                }

            }

            if (!rekordPozaZakresem) {

                dw.drukujLinie(
                    "*) Brak");

            }

            dw.drukujLinie("\npozostali analizowani\n");

            i = ostatnieZdjecieAutora.keySet().iterator();
            
            // wszyscy użytkownicy
            while(i.hasNext()) {

                String id = i.next();

                /*
                 * Musimy uważać, na tych co już się wypisali z grupy
                 */
                if (!uzytkownicy.containsKey(id)) {
                    continue;
                }

                // zdjęcie w granicach zakresu
                if(!granica.after(ostatnieZdjecieAutora.get(id))) {
                    dw.drukujLinie(
                        "*) "
                        + uzytkownicy.get(id)
                        + ", "
                        + dw.formatujDate(ostatnieZdjecieAutora.get(id)));
                }

            }

        }

    }

    /**
     * Drukowanie braku komentarzy w analizowanym zakresie
     *
     * @param dataDo data końca analizowanego okresu
     * @param uzytkownicy użytkownicy grupy
     * @param ostatniKomentarzAutora statystyka ostatnich komentarzy wg autora
     */
    private void drukujBrakKomentarzy(
        final Date dataDo,
        final HashMap uzytkownicy,
        final HashMap<String, Date> ostatniKomentarzAutora,
        final HashMap<String, Date> ostatnieZdjecieAutora)
    {

        if (drukujBrakKomentarzy) {

            dw.drukujSeparator("***");

            Calendar c = Calendar.getInstance();
            c.setTime(dataDo);
            c.add(Calendar.MONTH, -mcBezKomentarzy);
            Date granica = c.getTime();

            dw.drukujLinie("Użytkownicy, którzy ostatni raz komentowali cudze "
                + "zdjęcia przed "+dw.formatujDate(granica)+"\n");

            Iterator<String> i = uzytkownicy.keySet().iterator();
            boolean rekordPozaZakresem = false;

            // wszyscy użytkownicy
            while(i.hasNext()) {

                String id = i.next();

                /*
                 * Musimy uważać, na tych co już się wypisali z grupy
                 */
                if (!uzytkownicy.containsKey(id)) {
                    continue;
                }

                // zapisana data ostatniego komentarza
                if (ostatniKomentarzAutora.containsKey(id)) {

                    // data ostatniego komentarza przed zakresem
                    if(granica.after(ostatniKomentarzAutora.get(id))) {

                        dw.drukujLinie(
                            "*) "
                            + uzytkownicy.get(id)
                            + ", "
                            + dw.formatujDate(ostatniKomentarzAutora.get(id)));
                    
                        rekordPozaZakresem = true;

                    }

                } else {

                    dw.drukujLinie(
                        "*) "
                        + uzytkownicy.get(id)
                        + ", "
                        + POZA_ZAKRESEM);

                    rekordPozaZakresem = true;

                }

            }

            if (!rekordPozaZakresem) {

                dw.drukujLinie(
                    "*) Brak");

            }

            dw.drukujLinie("\npozostali analizowani\n");

            i = ostatniKomentarzAutora.keySet().iterator();

            // wszyscy użytkownicy
            while(i.hasNext()) {

                String id = i.next();

                /*
                 * Musimy uważać, na tych co już się wypisali z grupy
                 */
                if (!uzytkownicy.containsKey(id)) {
                    continue;
                }

                String dodatkoweInfo = "";
                if (drukujBrakZdjec) {

                    if (ostatniKomentarzAutora.get(id).before(ostatnieZdjecieAutora.get(id))) {
                        dodatkoweInfo =
                            " (przed zdjęciem z dnia "
                            + dw.formatujDate(ostatnieZdjecieAutora.get(id))
                            + ")";
                    }

                }

                // data ostatniego komentarza przed zakresem
                if(!granica.after(ostatniKomentarzAutora.get(id))) {
                    dw.drukujLinie(
                        "*) "
                        + uzytkownicy.get(id)
                        + ", "
                        + dw.formatujDate(ostatniKomentarzAutora.get(id))
                        + dodatkoweInfo);
                }

            }

        }

    }

    public int wykonajZadanie() {

        if (kgui == null) {
            throw new RuntimeException("Brak kontrolera GUI!");
        }

        dw = new DaneWyjsciowe();

        /*
         * Kod html do głosowania
         */
        final StringBuffer kodhtml = new StringBuffer("");

        final HashMap<String, Date> ostatnieZdjecieAutora = new HashMap<String, Date>();
        final HashMap<String, Date> ostatniKomentarzAutora = new HashMap<String, Date>();
        final HashMap<String, String> uzytkownicy;

        try {

            /*
             * Główne stałe algorytmu
             */
            final Date dataOd = kgui.dajDataOd();
            final Date dataDo = kgui.dajDataDo();
            final String groupID = kgui.getGroupId();
            final Flickr f = kgui.getFlickr();
            
            uzytkownicy = dajListeUzytkownikowGrupy(f, groupID);

            /*
             * Nagłówek
             */
            {

                dw.drukujNaglowek(kgui.getNazwaGrupy());

                dw.drukujLinie(
                    "Zdjęcia dodane od "
                    + dw.formatujDate(dataOd)
                    + " i przed "
                    + dw.formatujDate(dataDo)
                    + ".");

                dw.drukujSeparator();

                dw.drukujLinie(
                    "Jak co miesiąc zapraszam do głosowania na zdjęcie " +
                    "miesiąca. Poprzednie głosowania można zobaczyć w <a href" +
                    "=\"http://www.flickr.com/search/groups/?q=Podsumowanie" +
                    "&m=discuss&w=71956997%40N00&s=act\">archiwum grupy</a>. " +
                    "Regulamin głosowania jest dostępny w <a href=\"http://www." +
                    "flickr.com/groups/71956997@N00/discuss/72157622705055642/" +
                    "\">osobnym wątku</a>. " +
                    "Wybrane zdjęcia poprzednich miesięcy można oglądać w " +
                    "zdjęciach grupy SK z tagiem: \"<a " +
                    "href=\"http://www.flickr.com/groups/71956997@N00/pool/tags" +
                    "/zdj%C4%99ciemiesi%C4%85cagrupyszczerekomentarze/\">Zdjęcie miesiąca grupy " +
                    "Szczere komentarze</a>\".");

                dw.drukujSeparator();

            }

            /*
             * Kolekcja obiektów reprezentujących aktywność użytkownika, kluczem
             * jest identyfikator użytkownika
             */
            final HashMap<String, StatystykaAutora>
                aktywnosc = new HashMap<String, StatystykaAutora>();

            /*
             * Data początku rozszerzonego zakresu do analizy
             */
            Date extDataOd = null;

            /*
             * Przesuwamy w ukryciu datę końca aby móc zrobić statystyki
             */
            if ((drukujBrakKomentarzy || drukujBrakZdjec)) {

                Calendar c = Calendar.getInstance();
                c.setTime(dataOd);

                // minus jeden aby zobaczyć także ciut przed zakresem
                c.add(Calendar.MONTH, -Math.max(mcBezZdjec, mcBezKomentarzy)-1);

                extDataOd = c.getTime();

            } else {

                /*
                 * Jawnie, ta data może być pusta gdy nie potrzebujemy badać
                 * rozszerzonego zakresu
                 */
                extDataOd = null;

            }
            
            /*
             * Kolekcja zdjęć w zakresie kryteriów
             */
            final Photo[] zdjecia = dajTabliceZdjec(
                f, groupID, dataOd, dataDo, extDataOd, ostatnieZdjecieAutora);

            /*
             * Główna pętla, analiza zdjęć i wypisanie ich na ekran
             */
            {

                /*
                 * Sortowanie wg autora
                 */
                {

                    final PhotoComparatorWgAutora 
                        pcwa = new PhotoComparatorWgAutora();

                    Arrays.sort(zdjecia, pcwa);

                }

                /*
                 * Tablica unikalnych ID autorów
                 */
                final HashMap<String, String>
                    autorzy =
                        dajAutorow(zdjecia, aktywnosc, dataOd);

                /*
                 * Mieszanie zdjęć
                 */
                {
                    int n = 0;

                    for (int i=0; i<zdjecia.length; i++) {

                        n = (int) Math.round(
                            Math.random()*(zdjecia.length - 1));

                        Photo pp = zdjecia[i];
                        zdjecia[i] = zdjecia[n];
                        zdjecia[n] = pp;

                    }

                }

                wykonajLiczenieKomentarzy(
                    f,
                    zdjecia,
                    aktywnosc,
                    autorzy,
                    kodhtml,
                    ostatniKomentarzAutora,
                    dataOd,
                    extDataOd,
                    uzytkownicy);

            }

            /*
             * Poniżej akcje wykonane po przeanalizowaniu całej puli zdjęć
             */
            {

                drukujPodsumowanieZbiorcze(aktywnosc);

                drukujPodsumowaniePopularnosci(zdjecia, dataOd);

                drukujKostkeMiniaturek(zdjecia, kodhtml, dataOd);

                drukujBrakZdjec(dataDo, uzytkownicy, ostatnieZdjecieAutora);

                drukujBrakKomentarzy(dataDo, uzytkownicy, 
                    ostatniKomentarzAutora, ostatnieZdjecieAutora);

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

    /**
     * Zwraca listę użytkowników danej grupy
     * @return
     */
    public HashMap<String, String> dajListeUzytkownikowGrupy(
        final Flickr f,
        final String groupID
    ) throws Exception {

        /*
         * Autoryzacja przebiega dzięki klasie ThreadLocal gdzie zapisane jest to
         * co wczytujemy w GUI, każdy oddzielny wątek musi być ponownie autoryzowany
         */
        Autoryzer.get().autoryzuj(kgui);

        HashMap<String, String> u = new HashMap<String, String>();

        int strona = 1;
        int uzytkownikowStrony = 0;
        final int uzytkownikowNaStrone = 5;
        boolean pobierzStrone = true;

        /*
         * Do pobrania wszystkie strony użytkowników
         */
        while(pobierzStrone) {

            uzytkownikowStrony = 0;

            Iterator i = f.getMembersInterface().getList(groupID, null, uzytkownikowNaStrone, strona).iterator();
            while(i.hasNext()) {
                uzytkownikowStrony++;
                Member m = (Member) i.next();
                u.put(m.getId(), m.getUserName());
            }

            /*
             * Jeżeli na tej stronie był komplet to trzeba pobrać kolejną
             */
            if (uzytkownikowStrony == uzytkownikowNaStrone) {
                pobierzStrone = true;
                strona++;
            } else {
                pobierzStrone = false;
            }

        }

        return u;

    }

    public void podlaczGUI(KontrolerGUI kontroler) {

        this.kgui = kontroler;

        if (kontroler.getPanelKonfiguracyjny() != null) {

            if (kontroler.getPanelKonfiguracyjny() instanceof PanelKonfiguracji) {

                PanelKonfiguracji pk = (PanelKonfiguracji) kontroler.getPanelKonfiguracyjny();

                dodajKostkeMiniaturek = pk.dajKostkeMiniatur();
                dodajPodsumowaniePopularnosci = pk.dajPodsumowaniePopularnosci();
                dodajPodsumowanieZbiorcze = pk.dajPodsumowanieZbiorcze();
                dodajKodHTML = pk.dajKodHTML();
                wykresLista = pk.dajWykresLista();
                wykresSlupkowy = pk.dajWykresSlupkowy();
                drukujBrakKomentarzy = pk.dajDrukujBezKomentarzy();
                drukujBrakZdjec = pk.dajDrukujBezZdjec();
                mcBezZdjec = pk.dajLiczbeMcBezZdjec().intValue();
                mcBezKomentarzy = pk.dajLiczbeMcBezKomentarzy().intValue();

            } else {
                throw new RuntimeException("Zły panel konfiguracyjny!");
            }

        }

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

    public IPanelKonfiguracyjny dajPanelKonfiguracyjny() {
        return new PanelKonfiguracji();
    }

}
