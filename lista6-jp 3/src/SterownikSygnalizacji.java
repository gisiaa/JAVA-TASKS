public class SterownikSygnalizacji implements Runnable {

    private static final int maxCzasPostoju = 60;

    private static final int MIN_ZIELONE = 1200;
    private static final int PUSTO = 600; //bez autka na pasie

    @Override
    public void run() {
        while (true) {
            try {
                if (Main.predkosc == 0) {
                    Thread.sleep(100);
                    continue;
                }

                // suma wag
                double priorytetPolnoc = sprawdzWage(0, 1) + sprawdzWage(0, 2) + sprawdzWage(2, 1) + sprawdzWage(2, 2);
                double priorytetWschod = sprawdzWage(0, 0) + sprawdzWage(2, 0);
                double priorytetPoludnie = sprawdzWage(1, 1) + sprawdzWage(1, 2) + sprawdzWage(3, 1) + sprawdzWage(3, 2);
                double priorytetZachod = sprawdzWage(1, 0) + sprawdzWage(3, 0);

                int faza = 0;
                double maxWaga = priorytetPolnoc;

                if (priorytetWschod > maxWaga) { maxWaga = priorytetWschod; faza = 1; }
                if (priorytetPoludnie > maxWaga) { maxWaga = priorytetPoludnie; faza = 2; }
                if (priorytetZachod > maxWaga) { maxWaga = priorytetZachod; faza = 3; }

                int wymuszona = wybierzFaze();
                if (wymuszona != -1) {
                    faza = wymuszona;
                    maxWaga = wagaFazy(wymuszona, priorytetPolnoc, priorytetWschod, priorytetPoludnie, priorytetZachod);
                }

                wylaczWszystkie();

                // przerwa miedzy zmianamim swiatel
                long startCzekania = System.currentTimeMillis();
                while (System.currentTimeMillis() - startCzekania < 3000) {
                    if (Main.predkosc > 2.0) {
                        break;
                    }
                    if (Main.predkosc == 0) {
                        break;
                    }
                    Thread.sleep(100);
                }

                while (!Main.tarczaPusta()) {
                    if (Main.predkosc == 0) {
                        break;
                    }
                    Thread.sleep(30);
                }

                if (!autoPrzedStopem(faza) && maxWaga <= 0.0001) {
                    Thread.sleep(80);
                    continue;
                }

                wybierzSwiatla(faza);

                int czasZielonego = (int) Math.min((maxWaga / 6.0) * 1000, 20000);
                if (czasZielonego < MIN_ZIELONE) {
                    czasZielonego = MIN_ZIELONE;
                }

                long startZielonego = System.currentTimeMillis();
                long ostatniMomentGdyBylyAuta = startZielonego;

                while (System.currentTimeMillis() - startZielonego < czasZielonego) {
                    if (Main.predkosc == 0) {
                        break;
                    }

                    long teraz = System.currentTimeMillis();

                    if (autoPrzedStopem(faza)) {
                        ostatniMomentGdyBylyAuta = teraz;
                    } else {
                        if (teraz - startZielonego >= MIN_ZIELONE && teraz - ostatniMomentGdyBylyAuta >= PUSTO) {
                            break;
                        }
                    }

                    if (Main.predkosc > 3.0) {
                        break;
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean autoPrzedStopem(int f) {
        if (f == 0) {
            return pasAutaPrzedStopem(0, 1) || pasAutaPrzedStopem(0, 2) || pasAutaPrzedStopem(2, 1) || pasAutaPrzedStopem(2, 2);
        }
        if (f == 1) {
            return pasAutaPrzedStopem(0, 0) || pasAutaPrzedStopem(2, 0);
        }
        if (f == 2) {
            return pasAutaPrzedStopem(1, 1) || pasAutaPrzedStopem(1, 2) || pasAutaPrzedStopem(3, 1) || pasAutaPrzedStopem(3, 2);
        }
        return pasAutaPrzedStopem(1, 0) || pasAutaPrzedStopem(3, 0);
    }

    private boolean pasAutaPrzedStopem(int wlot, int nrPasa) {
        for (PasRuchu p : Main.listaPasow) {
            if (p.numerWlotu == wlot && p.numerPasa == nrPasa) {
                for (Pojazd a : p.lista()) {
                    if (!a.naSrodku) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private double wagaFazy(int faza, double priorytetPolnoc, double priorytetWschod, double priorytetPoludnie, double priorytetZachod) {
        if (faza == 0) return priorytetPolnoc;
        if (faza == 1) return priorytetWschod;
        if (faza == 2) return priorytetPoludnie;
        return priorytetZachod;
    }

    private int wybierzFaze() {
        int faza = -1;
        int czas = maxCzasPostoju - 1;

        int w0 = maxCzekanieFazy0();
        if (w0 >= maxCzasPostoju && w0 > czas) { czas = w0; faza = 0; }

        int w1 = maxCzekanieFazyNr1();
        if (w1 >= maxCzasPostoju && w1 > czas) { czas = w1; faza = 1; }

        int w2 = maxCzekanieFazyNr2();
        if (w2 >= maxCzasPostoju && w2 > czas) { czas = w2; faza = 2; }

        int w3 = maxCzekanieFazyNr3();
        if (w3 >= maxCzasPostoju && w3 > czas) { faza = 3; }

        return faza;
    }

    private int maxCzekanieFazy0() {
        return max4(
                maxCzekanieNaPasie(0, 1),
                maxCzekanieNaPasie(0, 2),
                maxCzekanieNaPasie(2, 1),
                maxCzekanieNaPasie(2, 2)
        );
    }

    private int maxCzekanieFazyNr1() {
        return Math.max(
                maxCzekanieNaPasie(0, 0),
                maxCzekanieNaPasie(2, 0)
        );
    }

    private int maxCzekanieFazyNr2() {
        return max4(
                maxCzekanieNaPasie(1, 1),
                maxCzekanieNaPasie(1, 2),
                maxCzekanieNaPasie(3, 1),
                maxCzekanieNaPasie(3, 2)
        );
    }

    private int maxCzekanieFazyNr3() {
        return Math.max(
                maxCzekanieNaPasie(1, 0),
                maxCzekanieNaPasie(3, 0)
        );
    }

    private int maxCzekanieNaPasie(int wlot, int nrPasa) {
        for (PasRuchu p : Main.listaPasow) {
            if (p.numerWlotu == wlot && p.numerPasa == nrPasa) {
                int max = 0;
                for (Pojazd a : p.lista()) {
                    if (!a.naSrodku && a.czasStania > max) {
                        max = a.czasStania;
                    }
                }
                return max;
            }
        }
        return 0;
    }

    private int max4(int a, int b, int c, int d) {
        return Math.max(Math.max(a, b), Math.max(c, d));
    }

    private double sprawdzWage(int wlot, int nrPasa) {
        for (PasRuchu p : Main.listaPasow) {
            if (p.numerWlotu == wlot && p.numerPasa == nrPasa) {
                return p.priorytet;
            }
        }
        return 0;
    }

    private void wylaczWszystkie() {
        for (PasRuchu p : Main.listaPasow) {
            p.czyZielone = false;
        }
    }

    private void wybierzSwiatla(int f) {
        if (f == 0) {
            zielone(0, 1, 0, 2, 2, 1, 2, 2);
        }
        if (f == 1) {
            zielone(0, 0, -1, -1, 2, 0, -1, -1);
        }
        if (f == 2) {
            zielone(1, 1, 1, 2, 3, 1, 3, 2);
        }
        if (f == 3) {
            zielone(1, 0, -1, -1, 3, 0, -1, -1);
        }
    }

    private void zielone(int w1, int p1, int w2, int p2, int w3, int p3, int w4, int p4) {
        for (PasRuchu p : Main.listaPasow) {
            if ((p.numerWlotu == w1 && p.numerPasa == p1) ||
                    (p.numerWlotu == w2 && p.numerPasa == p2) ||
                    (p.numerWlotu == w3 && p.numerPasa == p3) ||
                    (p.numerWlotu == w4 && p.numerPasa == p4)) {
                p.czyZielone = true;
            }
        }
    }
}
