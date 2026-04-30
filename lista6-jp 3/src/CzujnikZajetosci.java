import java.awt.*;
import java.util.List;

public class CzujnikZajetosci implements Runnable {
    private final PasRuchu pas;

    public CzujnikZajetosci(PasRuchu pas) {
        this.pas = pas;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Pojazd> auta = pas.lista();

                int ileCzeka = 0;
                int maxCzas = 0;

                for (Pojazd a : auta) {
                    if (!a.naSrodku) {
                        ileCzeka++;
                        if (a.czasStania > maxCzas) {
                            maxCzas = a.czasStania;
                        }
                    }
                }

                double waga;
                if (maxCzas > 120) {
                    waga = 99999;
                } else {
                    waga = ileCzeka * 0.35 + maxCzas * 0.65;
                }

                pas.czyKtosCzeka = (ileCzeka > 0);
                pas.priorytet = pas.czyKtosCzeka ? waga : 0;

                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
