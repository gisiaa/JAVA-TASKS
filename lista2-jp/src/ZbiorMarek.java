import java.util.Random;

enum ZbiorMarek {

    POLONEZ(10000.0, 2000.0, 1978, 2002),
    FIAT(20000.0, 3000.0, 1973, 2000),
    SYRENA(25000.0, 3300.0, 1957, 1983);

    /*
    POLONEZ(1978-2002),
    FIAT(1973-2000),
    SYRENA(1957-1983)
    */

    //zmienne

    private final double sredniaCena;
    private final double odchylenieCeny;
    private final int minRocznik;
    private final int maxRocznik;


    // konstruktor

    ZbiorMarek(double sredniaCena, double odchylenieCeny, int minRocznik, int maxRocznik) {
        this.sredniaCena = sredniaCena;
        this.odchylenieCeny = odchylenieCeny;
        this.minRocznik = minRocznik;
        this.maxRocznik = maxRocznik;
    }

    // gettery

    public double getSredniaCena() {
        return sredniaCena;
    }

    public double getOdchylenieCeny() {
        return odchylenieCeny;
    }

    public int getMaxRocznik() {
        return maxRocznik;
    }

    public int getMinRocznik() {
        return minRocznik;
    }

    public static ZbiorMarek losowanieMarki(Random random) {
        ZbiorMarek[] marki = values(); // marki

        int zakres = marki.length; // zakres losowania

        int losowaMarka = random.nextInt(zakres); // losowanie liczby z podanego zakresu losowania

        ZbiorMarek wylosowanaMarka = marki[losowaMarka]; //indeks - wylosowana liczba z zakresu

        return wylosowanaMarka;
    }
}