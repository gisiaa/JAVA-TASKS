import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pojazd implements Runnable {
    PasRuchu pas;
    double x, y;
    double bazowaPredkosc = 3.5;
    long momentPojawienia;
    int czasStania = 0;

    double czasPostoju = 0.0; // w ms
    long ostatniTick = System.currentTimeMillis();

    boolean zglosilWjazdNaTarcze = false;
    boolean zglosilWyjazdZTarczy = false;

    boolean skonczyl = false; // czy dojechal do konca
    boolean naSrodku = false; // czy minal linie stop

    // double postep = 0.0;
    double kat = 0.0;
    Color kolorAuta;
    Trasa trasa;

    List<Point2D.Double> sciezka = null;
    double[] dlugosci = null;
    double dlugoscCalkowita = 0.0;
    double dystans = 0.0;
    int indeks = 0;

    boolean tarcza = false;
    boolean zaTarcza = false;

    boolean bylWTarczy = false;
    boolean zapisDoStatystyk = false;

    public Pojazd(PasRuchu pas) {
        this.pas = pas;
        this.x = pas.startX;
        this.y = pas.startY;
        this.momentPojawienia = System.currentTimeMillis();

        if (pas.numerWlotu == 0) {
            kat = Math.PI / 2;
        } else if (pas.numerWlotu == 2) {
            kat = -Math.PI / 2;
        } else if (pas.numerWlotu == 1) {
            kat = Math.PI;
        } else if (pas.numerWlotu == 3) {
            kat = 0;
        }

        // losowanie
        if (!pas.dostepneTrasy.isEmpty()) {
            this.trasa = pas.dostepneTrasy.get(new Random().nextInt(pas.dostepneTrasy.size()));

            if (trasa.kierunek == 0) {
                this.kolorAuta = Color.RED;
            } else if (trasa.kierunek == 1) {
                this.kolorAuta = Color.YELLOW;
            } else {
                this.kolorAuta = Color.BLUE;
            }
        }
    }

    @Override
    public void run() {
        try {
            while (!skonczyl) {
                Thread.sleep(30);

                //czas przed wjazdem na skrzyzowanie
                long teraz = System.currentTimeMillis();
                long roznica = teraz - ostatniTick;
                if (roznica < 0) roznica = 0;
                ostatniTick = teraz;

                if (Main.predkosc == 0) {
                    continue;
                }

                double deltaSym = roznica * Main.predkosc;
                double krok = bazowaPredkosc * Main.predkosc;

                boolean blokada = false;

                if (!czyJechac(55)) {
                    blokada = true;
                } else if (!naSrodku) {
                    double dystansDoStop = Point.distance(x, y, pas.stopX, pas.stopY);
                    if (dystansDoStop < 10 && !pas.czyZielone) {
                        blokada = true;
                    }
                }

                if (blokada) {
                    czasPostoju += deltaSym;
                    czasStania = (int) (czasPostoju / 1000.0);
                    continue;
                }

                // dojazd do linii (miala symbolizowac stop)
                if (!naSrodku) {
                    double dystansDoStop = Point.distance(x, y, pas.stopX, pas.stopY);

                    if (dystansDoStop < 10) {
                        // czerwone swiatlo
                        if (!pas.czyZielone) {
                            continue;
                        }
                        naSrodku = true; // zielone
                        x = trasa.punktStart.x;
                        y = trasa.punktStart.y;
                        zbudujSciezke();
                    } else {
                        double katObrotu = Math.atan2(pas.stopY - y, pas.stopX - x);
                        x += Math.cos(katObrotu) * krok;
                        y += Math.sin(katObrotu) * krok;
                        kat = katObrotu;
                    }
                } else {
                    if (sciezka == null || dlugosci == null) {
                        zbudujSciezke();
                    }

                    double stareX = x;
                    double stareY = y;

                    dystans += krok;
                    if (dystans > dlugoscCalkowita) {
                        dystans = dlugoscCalkowita;
                    }

                    while (indeks < dlugosci.length - 2 && dlugosci[indeks + 1] < dystans) {
                        indeks++;
                    }

                    double d0 = dlugosci[indeks];
                    double d1 = dlugosci[indeks + 1];
                    double alpha = 0.0;
                    if (d1 > d0) {
                        alpha = (dystans - d0) / (d1 - d0);
                    }

                    Point2D.Double p0 = sciezka.get(indeks);
                    Point2D.Double p1 = sciezka.get(indeks + 1);

                    x = p0.x + (p1.x - p0.x) * alpha;
                    y = p0.y + (p1.y - p0.y) * alpha;

                    // obrot auta
                    if (Point.distance(stareX, stareY, x, y) > 0.0001) {
                        kat = Math.atan2(y - stareY, x - stareX);
                    }

                    boolean wTarczyNow = wProstokacieTarczy(x, y);
                    boolean wTarczyPrev = tarcza;
                    boolean przelot = (!wTarczyPrev && !wTarczyNow) && odcinekPrzecinaTarcze(stareX, stareY, x, y);

                    if (wTarczyNow || przelot) {
                        bylWTarczy = true;
                    }

                    if ((wTarczyNow && !wTarczyPrev) || przelot) {
                        if (!zglosilWjazdNaTarcze) {
                            zglosilWjazdNaTarcze = true;
                            Main.wjechalNaTarcze();
                        }
                    }

                    if (((!wTarczyNow && wTarczyPrev) || przelot) && !zapisDoStatystyk && bylWTarczy) {
                        zapisDoStatystyk = true;
                        long czasCalkowity = System.currentTimeMillis() - momentPojawienia;
                        Main.autoZakonczyloTrase(pas.numerWlotu, czasCalkowity);

                        if (!zglosilWyjazdZTarczy && zglosilWjazdNaTarcze) {
                            zglosilWyjazdZTarczy = true;
                            Main.wyjechalZTarczy();
                        }
                    }

                    tarcza = wTarczyNow;

                    if (dystans >= dlugoscCalkowita) {
                        skonczyl = true;
                        pas.usunPojazd(this);

                        if (!zapisDoStatystyk) {
                            zapisDoStatystyk = true;
                            long czasCalkowity = System.currentTimeMillis() - momentPojawienia;
                            Main.autoZakonczyloTrase(pas.numerWlotu, czasCalkowity);
                        }

                        if (!zglosilWyjazdZTarczy && zglosilWjazdNaTarcze) {
                            zglosilWyjazdZTarczy = true;
                            Main.wyjechalZTarczy();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean wProstokacieTarczy(double px, double py) {
        return px >= Main.TARCZA_MIN_X && px <= Main.TARCZA_MAX_X && py >= Main.TARCZA_MIN_Y && py <= Main.TARCZA_MAX_Y;
    }

    private boolean odcinekPrzecinaTarcze(double x0, double y0, double x1, double y1) {
        double minX = Main.TARCZA_MIN_X;
        double maxX = Main.TARCZA_MAX_X;
        double minY = Main.TARCZA_MIN_Y;
        double maxY = Main.TARCZA_MAX_Y;

        if (wProstokacieTarczy(x0, y0) || wProstokacieTarczy(x1, y1)) return true;

        double dx = x1 - x0;
        double dy = y1 - y0;

        double[] tt = new double[]{0.0, 1.0};

        if (!clip(-dx, x0 - minX, tt)) return false;
        if (!clip(dx, maxX - x0, tt)) return false;
        if (!clip(-dy, y0 - minY, tt)) return false;
        if (!clip(dy, maxY - y0, tt)) return false;

        return tt[0] <= tt[1];
    }

    private boolean clip(double p, double q, double[] tt) {
        double t0 = tt[0];
        double t1 = tt[1];

        if (Math.abs(p) < 1e-12) {
            if (q < 0) return false;
            return true;
        }

        double r = q / p;
        if (p < 0) {
            if (r > t1) return false;
            if (r > t0) t0 = r;
        } else {
            if (r < t0) return false;
            if (r < t1) t1 = r;
        }

        tt[0] = t0;
        tt[1] = t1;
        return true;
    }

    private void zbudujSciezke() {
        sciezka = new ArrayList<>();
        int kroki = 220;

        if (trasa.kierunek == 1) {
            sciezka.add(new Point2D.Double(trasa.punktStart.x, trasa.punktStart.y));
            sciezka.add(new Point2D.Double(trasa.punktKoniec.x, trasa.punktKoniec.y));
        } else {
            for (int i = 0; i <= kroki; i++) {
                double t = (double) i / kroki;
                double u = 1 - t;

                double px = u * u * u * trasa.punktStart.x + 3 * u * u * t * trasa.punkt1.x + 3 * u * t * t * trasa.punkt2.x + t * t * t * trasa.punktKoniec.x;

                double py = u * u * u * trasa.punktStart.y + 3 * u * u * t * trasa.punkt1.y + 3 * u * t * t * trasa.punkt2.y + t * t * t * trasa.punktKoniec.y;

                sciezka.add(new Point2D.Double(px, py));
            }
        }

        dlugosci = new double[sciezka.size()];
        dlugosci[0] = 0.0;
        for (int i = 1; i < sciezka.size(); i++) {
            Point2D.Double a = sciezka.get(i - 1);
            Point2D.Double b = sciezka.get(i);
            dlugosci[i] = dlugosci[i - 1] + a.distance(b);
        }

        dlugoscCalkowita = dlugosci[dlugosci.length - 1];
        dystans = 0.0;
        indeks = 0;
        tarcza = false;
        zaTarcza = false;

        bylWTarczy = false;
        zapisDoStatystyk = false;

        zglosilWjazdNaTarcze = false;
        zglosilWyjazdZTarczy = false;
    }

    private boolean czyJechac(double odstep) {
        List<Pojazd> kopia;
        synchronized (pas) {
            kopia = new ArrayList<>(pas.listaPojazdow);
        }
        int indeks = kopia.indexOf(this);
        if (indeks <= 0) {
            return true; //pierwsy
        }
        try {
            Pojazd pojazdPrzed = kopia.get(indeks - 1);
            return Point.distance(x, y, pojazdPrzed.x, pojazdPrzed.y) > odstep;
        } catch (Exception e) {
            return true;
        }
    }
}
