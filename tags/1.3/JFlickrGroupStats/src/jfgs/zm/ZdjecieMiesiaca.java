/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jfgs.zm;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import jfgs.gui.KontrolerGUI;
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
    private static final int liczbaZdjecWierszaKostkiMiniaturek = 5;

    /**
     * Czy generujemy zestawienie zdjęć najbardziej oglądanych
     * @see liczbaZestawieniaNajbardziejPopularnych
     */
    private boolean dodajPodsumowaniePopularnosci = false;

    /**
     * Liczba zestawienia zdjęć najbardziej popularnych
     * @see dodajPodsumowaniePopularnosci
     */
    private static final int liczbaZestawieniaNajbardziejPopularnych = 10;

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
    private static final int zdjecNaStrone = 333;
    
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
     * @return
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws com.aetrion.flickr.FlickrException
     */
    private ArrayList<PhotoList> dajStrony(
        final Flickr f,
        final String groupID,
        final Date dataOd,
        final Date dataDo) throws IOException, SAXException, FlickrException
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
            
            ArrayList<Photo> zal = null;

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
                            zdjecNaStrone,
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

                    if (dataPierwszego.before(dataOd) && dataOstatniego.before(dataOd)) {

                        /*
                         * Jeżeli początek i koniec strony jest
                         * starszy niż data początku zakresu to
                         * nie ma sensu analizować zdjęć wcześniejszych
                         */
                        analizujNastepnaStrone = false;
                        break;

                    }

                    if (dataPierwszego.after(dataDo) && dataOstatniego.after(dataDo)) {

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
        final Date dataDo)
    {

        ArrayList<Photo> 
            zal = new ArrayList<Photo>(zdjecNaStrone * strony.size());

        /*
         * Wydłubanie interesujących nas zdjęć z stron, odrzucenie
         * początku i końca poza zakresem dat
         */
        {

            kgui.ustawPostepStr(
                "Analizowanie zdjęć wyszukanych stron");

            for (PhotoList s : strony) {

                Iterator is = s.iterator();

                while(is.hasNext()) {

                    Photo p = (Photo) is.next();

                    /*
                     * Warunek na kryteria wyboru zdjęć
                     */
                    if (!p.getDateAdded().after(dataOd)
                        || !p.getDateAdded().before(dataDo))
                    {

                        // zdjęcie poza zakresem badanych dat

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
        final Date dataDo
    ) throws IOException, SAXException, FlickrException
    {

        ArrayList<Photo>
            zal = dajListeZdjec(
                dajStrony(f, groupID, dataOd, dataDo),
                dataOd,
                dataDo);
        
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
        final HashMap<String, StatystykaAutora> aktywnosc)
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
        final String ownerId,
        final CommentsInterface ci,
        final HashMap<String, String> autorzy,
        final HashMap<String, StatystykaAutora> aktywnosc
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
        final StringBuffer kodhtml
    ) throws FlickrException, IOException, SAXException 
    {

        /*
         * Główna pętla, analiza zdjęć i wypisanie ich na ekran
         */
        {

            int kz = 0;

            final CommentsInterface ci = f.getCommentsInterface();

            kgui.ustawPostepMax(zdjecia.length - 1);

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

                kodhtml.append(
                    dw.formatujLiczbe(noZdjecia)
                    + ": &lt;a href=&quot;"
                    + zdjecia[noZdjecia].getUrl()
                    + "&quot;&gt;&lt;img src=&quot;"
                    + zdjecia[noZdjecia].getSmallUrl()
                    + "&quot;&gt;&lt;/a&gt;"
                    + "\n"
                );

                kz =
                    policzKomentarzeZdjecia(
                        zdjecia[noZdjecia].getId(),
                        zdjecia[noZdjecia].getOwner().getId(),
                        ci,
                        autorzy,
                        aktywnosc);

                dw.drukujLinie(
                    dw.formatujLiczbe(noZdjecia)
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

            dw.drukujSeparator("Podsumowanie");

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

            } else if(wykresLista) {

                final String separator = ";";

                dw.drukujLinie(
                    "Nazwa" + separator +
                    "Liczba zdjęć" + separator +
                    "Liczba komentarzy" + separator +
                    "Wartość");

                for (StatystykaAutora sa : sat) {

                    dw.drukujLinie(
                        sa.dajNazwe() + separator +
                        sa.dajLiczbeZdjec() + separator +
                        sa.dajLiczbeKomentarzy() + separator +
                        sa.dajWartosc());

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
        final Photo[] zdjecia)
    {

        /*
         * Warunek zestawienia TOP wg popularności
         */
        if (dodajPodsumowaniePopularnosci) {

            Photo[] top = new Photo[liczbaZestawieniaNajbardziejPopularnych];

            for(int ip=0; ip<zdjecia.length; ip++) {

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

            dw.drukujSeparator("TOP"+liczbaZestawieniaNajbardziejPopularnych);

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
        final StringBuffer kodhtml)
    {
        /*
         * Warunek wydruku kostki miniaturek
         */
        if (dodajKostkeMiniaturek) {

            dw.drukujSeparator("Podgląd zdjęć");

            int zdjecieWKostce = 1;

            for(int ip=0; ip<zdjecia.length; ip++) {

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
                if (zdjecieWKostce % liczbaZdjecWierszaKostkiMiniaturek == 0
                    || (ip == zdjecia.length - 1))
                {
                    dw.drukujLinie("");
                }

                zdjecieWKostce++;

            }

            dw.drukujLinie("");
            dw.drukujLinie(
                "Wybrane zdjęcia poprzednich miesięcy można oglądać <a " +
                "href=\"http://www.flickr.com/groups/71956997@N00/pool/tags" +
                "/zdj%C4%99ciemiesi%C4%85cagrupyszczerekomentarze/\">w " +
                "zdjęciach grupy SK z tagiem <i>Zdjęcie miesiąca grupy " +
                "Szczere komentarze</i></a>.");

        }

        if (dodajKodHTML) {

            dw.drukujSeparator("Kod HTML");

            dw.drukujLinie(
                "Poniżej kod HTML gotowy do skopiowania. Kod " +
                "zawiera odnośnik do zdjęcia wraz z jego miniaturą.");
            dw.drukujLinie("");

            dw.drukujLinie("<s>" + kodhtml.toString() + "</s>");

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
       
        try {

            /*
             * Główne stałe algorytmu
             */
            final Date dataOd = kgui.dajDataOd();
            final Date dataDo = kgui.dajDataDo();
            final String groupID = kgui.getGroupId();
            final Flickr f = kgui.getFlickr();

            /*
             * Nagłówek
             */
            {

                dw.drukujSeparator();

                dw.drukujLinie(
                    "Jak co miesiąc zapraszam do głosowania na zdjęcie " +
                    "miesiąca. Poprzednie głosowania można zobaczyć w <a href" +
                    "=\"http://www.flickr.com/search/groups/?q=Podsumowanie" +
                    "&m=discuss&w=71956997%40N00&s=act\">archiwum grupy</a>.");

                dw.drukujSeparator();

                dw.drukujLinie(
                    "Grupa: "
                    + kgui.getNazwaGrupy());

                dw.drukujLinie(
                    "Zdjęcia dodane po "
                    + dw.formatujDate(dataOd)
                    + " i przed "
                    + dw.formatujDate(dataDo)
                    + ".");

            }

            /*
             * Kolekcja obiektów reprezentujących aktywność użytkownika, kluczem
             * jest identyfikator użytkownika
             */
            final HashMap<String, StatystykaAutora>
                aktywnosc = new HashMap<String, StatystykaAutora>();

            /*
             * Kolekcja zdjęć w zakresie kryteriów
             */
            final Photo[] zdjecia = dajTabliceZdjec(f, groupID, dataOd, dataDo);

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
                        dajAutorow(zdjecia, aktywnosc);

                wykonajLiczenieKomentarzy(
                    f, zdjecia, aktywnosc, autorzy, kodhtml);

            }

            /*
             * Poniżej akcje wykonane po przeanalizowaniu całej puli zdjęć
             */

            drukujPodsumowanieZbiorcze(aktywnosc);

            drukujPodsumowaniePopularnosci(zdjecia);

            drukujKostkeMiniaturek(zdjecia, kodhtml);

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

        if (kontroler.getPanelKonfiguracyjny() != null) {

            if (kontroler.getPanelKonfiguracyjny() instanceof PanelKonfiguracji) {

                PanelKonfiguracji pk = (PanelKonfiguracji) kontroler.getPanelKonfiguracyjny();

                dodajKostkeMiniaturek = pk.dajKostkeMiniatur();
                dodajPodsumowaniePopularnosci = pk.dajPodsumowaniePopularnosci();
                dodajPodsumowanieZbiorcze = pk.dajPodsumowanieZbiorcze();
                dodajKodHTML = pk.dajKodHTML();
                wykresLista = pk.dajWykresLista();
                wykresSlupkowy = pk.dajWykresSlupkowy();

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
