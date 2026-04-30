import java.io.*;
import java.util.*;

public class Zadanie_2 {

    private static List<PrzypisanieDoLitery> alfabet = new ArrayList<>();
    //uzylam arraylist poniewaz tablica ta przeszukiwana bedzie bardzo duzo razy
    //natomiast linked list bardziej nadaje sie do modyfikowania
    public static void main(String[] args) {

        if (args.length != 3) {
            throw new IllegalArgumentException("ERROR: nalezy podac 3 nazwy plikow wejsciowych");
        }

        alfabet();
        //tworze 26 kontenerow z literami <=26


        for (int i = 0; i < args.length; i++) {
            przetworzPlik(args[i], i, args.length);
        }

        // wyswietlam wyniki
        wyswietlWyniki();
    }

    private static void alfabet() {
        for (char c = 'A'; c <= 'Z'; c++) { //od a do z za pomoca kodow ASCII
            alfabet.add(new PrzypisanieDoLitery(c));
        }
    }

    private static void przetworzPlik(String sciezka, int indeksPliku, int iloscPlikow) {
        File plik = new File(sciezka);
        if (!plik.exists()) {
            System.out.println("ERROR: zabraklo pliku");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(plik))) {
            String linia;
            while ((linia = br.readLine()) != null) {
                String[] slowa = linia.split("[ \\n\\r.,]+");

                for (String slowo : slowa) {
                    sprawdzSlowo(slowo.toUpperCase(), indeksPliku, iloscPlikow); //zamieniam na wielka litere, bo tak sa zapisane imiona w ENUMie
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: wystapil blad podczas czytania pliku");
        }
    }

    private static void sprawdzSlowo(String slowo, int indeksPliku, int iloscPlikow) {
        if (slowo.isEmpty()) return;
        try {
            Imiona znalezioneImie = Imiona.valueOf(slowo); // sprawdzenie czy slowo nalezy do enumu
            char pierwszaLitera = slowo.charAt(0); // jesli tak, przypisuje mu literke
            int indeksLitery = pierwszaLitera - 'A';

            if (indeksLitery >= 0 && indeksLitery < 26) {
                PrzypisanieDoLitery litera_przyp = alfabet.get(indeksLitery);
                litera_przyp.dodanieLubDopisanie(znalezioneImie, indeksPliku, iloscPlikow); // dopasowanie
            }

        } catch (IllegalArgumentException e) {
            //slowo nie jest imieniem
        }
    }

    private static void wyswietlWyniki() {
        for (PrzypisanieDoLitery k : alfabet) {
            // jesli do litery przypisane jest imie - wyswietlam
            if (!k.listaImion.isEmpty()) {
                System.out.println(k);
            }
        }
    }
}