import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelSymulacji extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // tlo
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // asfalt
        g2.setColor(Color.BLACK);
        g2.fillRect(400, 0, 200, Main.WYSOKOSC_MAPY);
        g2.fillRect(0, 400, Main.SZEROKOSC_MAPY, 200);

        // linie podwojne ciagle
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(497, 0, 497, 390);
        g2.drawLine(503, 0, 503, 390);
        g2.drawLine(497, 610, 497, Main.WYSOKOSC_MAPY);
        g2.drawLine(503, 610, 503, Main.WYSOKOSC_MAPY);
        g2.drawLine(0, 497, 390, 497);
        g2.drawLine(0, 503, 390, 503);
        g2.drawLine(610, 497, Main.SZEROKOSC_MAPY, 497);
        g2.drawLine(610, 503, Main.SZEROKOSC_MAPY, 503);

        // linie stop
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(400, 390, 500, 390);
        g2.drawLine(500, 610, 600, 610);
        g2.drawLine(610, 400, 610, 500);
        g2.drawLine(390, 500, 390, 600);

        // linie przerywane
        Stroke przerywana = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2.setStroke(przerywana);
        int[] pionoweX = {433, 466, 533, 566};
        for (int x : pionoweX) {
            g2.drawLine(x, 0, x, 400);
            g2.drawLine(x, 600, x, Main.WYSOKOSC_MAPY);
        }
        int[] poziomeY = {433, 466, 533, 566};
        for (int y : poziomeY) {
            g2.drawLine(0, y, 400, y);
            g2.drawLine(600, y, Main.SZEROKOSC_MAPY, y);
        }

        // numery pasow (by mozna bylo latwo zweryfikowac poprawnosc przyjtego przeze mnie sposobu liczenia na podstawie czasu i ilosci-wagi)
        g2.setColor(new Color(255, 255, 255, 100));
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
        g2.drawString("1", 478, 350); g2.drawString("2", 445, 350); g2.drawString("3", 412, 350);
        g2.drawString("1", 512, 650); g2.drawString("2", 545, 650); g2.drawString("3", 578, 650);
        g2.drawString("1", 650, 490); g2.drawString("2", 650, 457); g2.drawString("3", 650, 424);
        g2.drawString("1", 340, 524); g2.drawString("2", 340, 557); g2.drawString("3", 340, 590);

        g2.setStroke(new BasicStroke(2));
        for (PasRuchu p : Main.listaPasow) {
            for (Trasa t : p.dostepneTrasy) {
                g2.setColor(t.kolor);
                if (t.kierunek == 1) {
                    g2.drawLine(t.punktStart.x, t.punktStart.y, t.punktKoniec.x, t.punktKoniec.y);
                } else {
                    luk(g2, t);
                }
            }
        }

        // auta i sygnalizacja
        g2.setStroke(new BasicStroke(5));
        for (PasRuchu p : Main.listaPasow) {
            sygnalizator(g2, p);
            List<Pojazd> kopia = p.lista();
            for (Pojazd auto : kopia) {
                samochod(g2, auto);
            }
        }

        statystyki(g2);
        panelBoczny(g2);
        statystykiPanelBoczny(g2);
    }

    private void panelBoczny(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(Main.SZEROKOSC_MAPY, 0, Math.max(0, getWidth() - Main.SZEROKOSC_MAPY), getHeight());
    }

    private void luk(Graphics2D g2, Trasa t) {
        int kroki = 60;
        double poprzednieX = t.punktStart.x;
        double poprzednieY = t.punktStart.y;

        for (int i = 1; i <= kroki; i++) {
            double pos = (double) i / kroki;
            double u = 1 - pos;

            double x = u * u * u * t.punktStart.x + 3 * u * u * pos * t.punkt1.x + 3 * u * pos * pos * t.punkt2.x + pos * pos * pos * t.punktKoniec.x;
            double y = u * u * u * t.punktStart.y + 3 * u * u * pos * t.punkt1.y + 3 * u * pos * pos * t.punkt2.y + pos * pos * pos * t.punktKoniec.y;

            g2.drawLine((int) poprzednieX, (int) poprzednieY, (int) x, (int) y);
            poprzednieX = x;
            poprzednieY = y;
        }
    }

    private void samochod(Graphics2D g2, Pojazd p) {
        Graphics2D gKopia = (Graphics2D) g2.create();
        gKopia.translate(p.x, p.y);
        gKopia.rotate(p.kat);

        gKopia.setColor(p.kolorAuta);
        gKopia.fillRoundRect(-11, -7, 22, 14, 6, 6);
        gKopia.setColor(p.kolorAuta.darker());
        gKopia.setStroke(new BasicStroke(1));
        gKopia.drawRoundRect(-11, -7, 22, 14, 6, 6);
        gKopia.dispose();

        if (!p.naSrodku) {
            g2.setColor(Color.BLACK);
            if (p.kolorAuta.equals(Color.BLUE)) {
                g2.setColor(Color.WHITE);
            }
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));

            String s = String.valueOf(p.czasStania);
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(s);
            int h = fm.getAscent();
            g2.drawString(s, (int) p.x - w / 2, (int) p.y + h / 2 - 2);
        }
    }

    private void sygnalizator(Graphics2D g2, PasRuchu p) {
        int x = p.stopX;
        int y = p.stopY;

        if (p.numerWlotu == 0) {
            if (p.numerPasa == 0) {
                x = 466;
            } else if (p.numerPasa == 1) {
                x = 433;
            } else {
                x = 400;
            }
        } else if (p.numerWlotu == 2) {
            if (p.numerPasa == 0) {
                x = 533;
            } else if (p.numerPasa == 1) {
                x = 566;
            } else {
                x = 600;
            }
        } else if (p.numerWlotu == 1) {
            if (p.numerPasa == 0) {
                y = 466;
            } else if (p.numerPasa == 1) {
                y = 433;
            } else {
                y = 400;
            }
        } else if (p.numerWlotu == 3) {
            if (p.numerPasa == 0) {
                y = 533;
            } else if (p.numerPasa == 1) {
                y = 566;
            } else {
                y = 600;
            }
        }

        int r = 10;
        // czerwone swiatlo
        if (p.czyZielone) {
            g2.setColor(Color.darkGray);
        } else {
            g2.setColor(Color.RED);
        }

        if (p.numerWlotu == 0 || p.numerWlotu == 2) {
            g2.fillOval(x - 5, y - 14, r, r);
        } else {
            g2.fillOval(x - 14, y - 5, r, r);
        }

        // zielone swiatlo
        if (p.czyZielone) {
            g2.setColor(Color.GREEN);
        } else {
            g2.setColor(Color.DARK_GRAY);
        }

        if (p.numerWlotu == 0 || p.numerWlotu == 2) {
            g2.fillOval(x - 5, y + 2, r, r);
        } else {
            g2.fillOval(x + 2, y - 5, r, r);
        }
    }

    private void statystyki(Graphics2D g2) {
        int boxW = 200;
        int boxH = 220;

        // pozycjonowanie okienek
        int left0 = 0;
        int left1 = 400;
        int right0 = 600;
        int right1 = Main.SZEROKOSC_MAPY;

        int top0 = 0;
        int top1 = 400;
        int bot0 = 600;
        int bot1 = Main.WYSOKOSC_MAPY;

        int xNW = left0 + ((left1 - left0) - boxW) / 2;
        int yNW = top0 + ((top1 - top0) - boxH) / 2;

        int xNE = right0 + ((right1 - right0) - boxW) / 2;
        int yNE = yNW;

        int xSW = xNW;
        int ySW = bot0 + ((bot1 - bot0) - boxH) / 2;

        int xSE = xNE;
        int ySE = ySW;

        okno(g2, xNW, yNW, "POLNOC", 0);
        okno(g2, xNE, yNE, "WSCHOD", 1);
        okno(g2, xSW, ySW, "ZACHOD", 3);
        okno(g2, xSE, ySE, "POLUDNIE", 2);
    }

    private void statystykiPanelBoczny(Graphics2D g2) {
        int w = 240;
        int h = 120;

        int panelW = Math.max(0, getWidth() - Main.SZEROKOSC_MAPY);
        int x = Main.SZEROKOSC_MAPY + (panelW - w) / 2;
        int y = (getHeight() - h) / 2;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(x, y, w, h, 15, 15);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        wysrodkowanie(g2, "SKRZYZOWANIE", x, w, y + 25);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));

        long przej = Main.pobierzLiczbeAut();
        double sr = Main.obliczSredniCzas();
        double przep = Main.obliczPrzepustowosc();

        wysrodkowanie(g2, "Lacznie przejechalo: " + przej, x, w, y + 55);
        wysrodkowanie(g2, String.format("Sredni czas: %.2f s", sr), x, w, y + 75);

        g2.setColor(Color.YELLOW);
        wysrodkowanie(g2, String.format("Przepustowosc: %.2f aut/s", przep), x, w, y + 95);
        g2.setColor(Color.WHITE);
    }

    private void okno(Graphics2D g2, int x, int y, String tytul, int wlot) {
        int w = 200;
        int h = 220;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(x, y, w, h, 15, 15);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        wysrodkowanie(g2, tytul, x, w, y + 25);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));

        int maxCzas = 0;
        int suma = 0;
        int yp = y + 55; //kursor
        wysrodkowanie(g2, "Liczba aut:", x, w, yp);
        yp += 20;

        for (int i = 0; i < 3; i++) {
            int c = 0;
            for (PasRuchu p : Main.listaPasow) {
                if (p.numerWlotu == wlot && p.numerPasa == i) {
                    c = p.ileAut();
                    for (Pojazd a : p.lista()) {
                        if (!a.naSrodku && a.czasStania > maxCzas) {
                            maxCzas = a.czasStania;
                        }
                    }
                }
            }
            suma += c;
            wysrodkowanie(g2, "Pas " + (i + 1) + ": " + c, x, w, yp);
            yp += 18;
        }
        g2.setColor(Color.YELLOW);
        wysrodkowanie(g2, "Suma: " + suma, x, w, yp + 5);
        g2.setColor(Color.WHITE);
        yp += 30;
        wysrodkowanie(g2, "Najdluzej stojace auto:", x, w, yp);
        wysrodkowanie(g2, maxCzas + " s", x, w, yp + 18);
        yp += 45;

        double sek = (System.currentTimeMillis() - Main.czasRozpoczecia) / 1000.0;
        double przepustowosc = 0;
        if (sek > 0) {
            przepustowosc = (double) Main.licznikiWlotow[wlot] / sek;
        }
        wysrodkowanie(g2, String.format("Przepustowosc: %.2f aut/s", przepustowosc), x, w, yp);
    }

    private void wysrodkowanie(Graphics2D g2, String t, int x, int w, int y) {
        int tw = g2.getFontMetrics().stringWidth(t);
        g2.drawString(t, x + (w - tw) / 2, y);
    }
}
