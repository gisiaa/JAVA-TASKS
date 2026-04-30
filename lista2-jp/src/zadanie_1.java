import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class zadanie_1 {

    public static void main(String[] args) {
        // przypadek '-h'

        // koniec programu i wyswietlenie bledu gdy uzytkownik poda za malo argumentow
        if (args.length < 3) {
            System.out.println("ERROR: Za malo argumentow.");
            return; // koniec
        }


        int N = 0; // N =  rozmiar listy

        try {
            N = Integer.parseInt(args[0]);
            // sprawdzenie czy 1-szy argument jest liczba calkowita
        } catch (NumberFormatException ex) {
            System.out.println("ERROR: Pierwszy argument musi byc liczba calkowita");
            return; // koniec
        }

        String Metoda = args[1]; // Zwyczajnie / Wyjatkowo
        String Kryterium = args[2]; // jedno z 4 zapytan

        if (!Metoda.equals("Zwyczajnie") && !Metoda.equals("Wyjatkowo")) {
            System.out.println("ERROR: Nieznana metoda");
            return;
        }

        if ( !Kryterium.equals("najstarszy") && !Kryterium.equals("najmlodszy") && !Kryterium.equals("nie starszy niz") && !Kryterium.equals("nie mlodszy niz") )
        {
            System.out.println("ERROR: Podane kryterium nie istnieje");
            return;
        }

        Random r = new Random(); // zmienna do generowania losowych liczb

        List<Samochod> wylosowaneSamochody = new ArrayList<>();

        for (int i = 0; i < N; i++) { // tworzy tyle samochodow ile zadeklarowal uzytkownik N
            Samochod marka = new Samochod(r);
            wylosowaneSamochody.add(marka);
        }

        //lista(wylosowaneSamochody); //wyswietlenie tablicy

        try {
            List<Samochod> SamochodySpelniajaceWarunki = new ArrayList<>();
            int rok;

            switch (Kryterium) {
                case "najstarszy":
                    if (args.length > 3){
                        System.out.println("ERROR: Za duzo argumentow dla tej funkcji");
                        return; //koniec, poniewaz jest to blad
                    }
                    SamochodySpelniajaceWarunki = najstarszy(wylosowaneSamochody);
                    break;

                case "najmlodszy": // gdyby ktos po najmlodszym, postanowil podac liczbe (taki bezsensowny ale realny przypadek)
                    if (args.length > 3){
                        System.out.println("ERROR: Za duzo argumentow dla tej funkcji");
                        return; //koniec, poniewaz jest to blad
                    }
                    SamochodySpelniajaceWarunki = najmlodszy(wylosowaneSamochody);
                    break;
                case "nie starszy niz":
                    if (args.length == 3) { // sprawdzanie roku
                        System.out.println("ERROR: Brak argumentu rok");
                        return;
                    }
                    rok = Integer.parseInt(args[3]);
                    SamochodySpelniajaceWarunki = nie_starszy_niz_rok(wylosowaneSamochody, rok);
                    break;

                case "nie mlodszy niz":
                    if (args.length == 3) {
                        System.out.println("ERROR: Brak argumentu rok");
                        return;
                    }
                    rok = Integer.parseInt(args[3]);
                    SamochodySpelniajaceWarunki = nie_mlodszy_niz_rok(wylosowaneSamochody, rok);
                    break;
            }

            if (Metoda.equals("Zwyczajnie")) {
                lista(SamochodySpelniajaceWarunki);
            } else if (Metoda.equals("Wyjatkowo")) {
                throw new Wyjatek(SamochodySpelniajaceWarunki);
            } else {
                System.out.println("ERROR: Nieznana metoda");
            }

        } catch (Wyjatek exp) {
            lista(exp.getlistaWyjatku()); // uruchomienie dla Wyjatku

        } catch (NumberFormatException e) {
            System.out.println("ERROR: Blad parametru ROK");
        }
    }

    public static void informacje() { // instrukcja do wpisywania wejscia
        System.out.println("Sposob wpisywania zapytania: zadanie_1 N Metoda Kryterium");
        System.out.println("gdzie 'N' odpowiada rozmiarowi listy, w formie liczby naturalnej (int)");
        System.out.println("'Metoda' - Zwyczajnie lub Wyjatkowo");
        System.out.println("'Kryterium' - jedno z czterech nastepujacych zapytan (koniecznie w cudzyslowie):");
        System.out.println(" - 'najstarszy'");
        System.out.println(" - 'najmlodszy'");
        System.out.println(" - 'nie starszy niz ROK'");
        System.out.println(" - 'nie mlodszy niz ROK'");
        System.out.println("za 'ROK' przyjmujemy liczbe naturalna - rocznik samochodu");
    }

    public static void lista(List<Samochod> lista) {
        if (lista.isEmpty()) { // gdyby N == 0, lub gdy funkcja nie znajdzie pasujacego samochodu
            System.out.println("Lista jest pusta, podany rok prawdpodobnie wychodzi poza zakres");
            return;
        }
        for (Samochod samochod : lista) {
            System.out.println(samochod);
        }
    }

    public static List<Samochod> najstarszy(List<Samochod> listaZwylosowanymi) {
        List<Samochod> listaKoncowa = new ArrayList<>();
        if (listaZwylosowanymi.isEmpty()) { // sprawdzenie czy lista jest pusta
            return listaKoncowa;
        }

        int najstarszyRocznik = listaZwylosowanymi.get(0).getRocznik();

        for (Samochod samochod : listaZwylosowanymi) {
            if (samochod.getRocznik() < najstarszyRocznik) {
                najstarszyRocznik = samochod.getRocznik();
            }
        }

        for (Samochod samochod : listaZwylosowanymi) {
            if (samochod.getRocznik() == najstarszyRocznik) {
                listaKoncowa.add(samochod);
            }
        }
        return listaKoncowa;
    }

    public static List<Samochod> najmlodszy(List<Samochod> listaZwylosowanymi) {
        List<Samochod> listaKoncowa = new ArrayList<>();
        if (listaZwylosowanymi.isEmpty()) {  // sprawdzenie czy lista jest pusta
            return listaKoncowa;
        }

        int najmlodszyRocznik = listaZwylosowanymi.get(0).getRocznik();

        for (Samochod samochod : listaZwylosowanymi) {
            if (samochod.getRocznik() > najmlodszyRocznik) {
                najmlodszyRocznik = samochod.getRocznik();
            }
        }

        for (Samochod samochod : listaZwylosowanymi) {
            if (samochod.getRocznik() == najmlodszyRocznik) {
                listaKoncowa.add(samochod);
            }
        }
        return listaKoncowa;
    }

    public static List<Samochod> nie_starszy_niz_rok(List<Samochod> listaZwylosowanymi, int rok) {
        List<Samochod> listaKoncowa = new ArrayList<>();
        if (listaZwylosowanymi.isEmpty()) {  // sprawdzenie czy lista jest pusta
            return listaKoncowa;
        }
        for (Samochod samochod : listaZwylosowanymi) {
            if (samochod.getRocznik() <= rok) {
                listaKoncowa.add(samochod);
            }
        }
        return listaKoncowa;
    }

    public static List<Samochod> nie_mlodszy_niz_rok(List<Samochod> listaZwylosowanymi, int rok) {
        List<Samochod> listaKoncowa = new ArrayList<>();
        if (listaZwylosowanymi.isEmpty()) {
            return listaKoncowa;
        }
        for (Samochod samochod : listaZwylosowanymi) {
            if (samochod.getRocznik() >= rok) {
                listaKoncowa.add(samochod);
            }
        }
        return listaKoncowa;
    }

}