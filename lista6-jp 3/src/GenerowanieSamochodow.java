import java.util.Random;

public class GenerowanieSamochodow implements Runnable {
    @Override
    public void run() {
        Random r = new Random();
        while (true) {
            try {
                long baza = r.nextInt(900) + 400;

                double dzielnik = 1;
                if (Main.predkosc > 0.1) {
                    dzielnik = Main.predkosc;
                }

                long opoznienie = (long) (baza / dzielnik);

                if (opoznienie < 50) { //by nie wygenerowal zbyt wielu aut
                    Thread.sleep(50);
                } else {
                    Thread.sleep(opoznienie);
                }

                if (Main.predkosc == 0) {
                    continue;
                }

                PasRuchu p = Main.listaPasow.get(r.nextInt(Main.listaPasow.size()));
                if (p.ileAut() < 8) {
                    Pojazd auto = new Pojazd(p);
                    p.dodajPojazd(auto);
                    new Thread(auto).start();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
