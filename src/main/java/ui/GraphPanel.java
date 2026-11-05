package ui;

import model.City;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private List<City> cities;
    private List<City> route;

    private static final int MARGIN = 40;
    private static final int CITY_RADIUS = 10;

    public GraphPanel() {
        cities = new ArrayList<>();
        route = new ArrayList<>();

        setOpaque(false);
        setPreferredSize(new Dimension(600, 500));
    }

    public void setCities(List<City> cities) {
        this.cities = new ArrayList<>(cities);
        this.route.clear();
        repaint();
    }

    public void setRoute(List<City> route) {
        this.route = new ArrayList<>(route);
        repaint();
    }

    public void clearRoute() {
        route.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        drawBackground(g2);
        drawGrid(g2);
        drawRoute(g2);
        drawCities(g2);

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(Theme.INPUT_BACKGROUND);

        g2.fillRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                16,
                16
        );
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(Theme.GRID);
        g2.setStroke(new BasicStroke(1));

        int gridSpacing = 40;

        for (int x = MARGIN; x < getWidth() - MARGIN; x += gridSpacing) {
            g2.drawLine(
                    x,
                    MARGIN,
                    x,
                    getHeight() - MARGIN
            );
        }

        for (int y = MARGIN; y < getHeight() - MARGIN; y += gridSpacing) {
            g2.drawLine(
                    MARGIN,
                    y,
                    getWidth() - MARGIN,
                    y
            );
        }
    }

    private void drawRoute(Graphics2D g2) {
        if (route.size() < 2) {
            return;
        }

        g2.setColor(Theme.ROUTE_BLUE);
        g2.setStroke(new BasicStroke(
                3,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        for (int i = 0; i < route.size() - 1; i++) {
            City firstCity = route.get(i);
            City secondCity = route.get(i + 1);

            int x1 = convertX(firstCity.getX());
            int y1 = convertY(firstCity.getY());

            int x2 = convertX(secondCity.getX());
            int y2 = convertY(secondCity.getY());

            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawCities(Graphics2D g2) {
        for (City city : cities) {
            int x = convertX(city.getX());
            int y = convertY(city.getY());

            boolean startingCity =
                    !route.isEmpty()
                            && route.get(0).getId() == city.getId();

            if (startingCity) {
                g2.setColor(Theme.START_GREEN);
            } else {
                g2.setColor(Theme.PRIMARY_BLUE);
            }

            g2.fillOval(
                    x - CITY_RADIUS,
                    y - CITY_RADIUS,
                    CITY_RADIUS * 2,
                    CITY_RADIUS * 2
            );

            g2.setColor(new Color(147, 197, 253));
            g2.setStroke(new BasicStroke(2));

            g2.drawOval(
                    x - CITY_RADIUS,
                    y - CITY_RADIUS,
                    CITY_RADIUS * 2,
                    CITY_RADIUS * 2
            );

            drawCityNumber(g2, city, x, y);
        }
    }

    private void drawCityNumber(
            Graphics2D g2,
            City city,
            int x,
            int y
    ) {
        String number = String.valueOf(city.getId());

        g2.setFont(new Font(
                "SansSerif",
                Font.BOLD,
                11
        ));

        FontMetrics fontMetrics = g2.getFontMetrics();

        int textWidth = fontMetrics.stringWidth(number);
        int textHeight = fontMetrics.getAscent();

        g2.setColor(Color.WHITE);

        g2.drawString(
                number,
                x - textWidth / 2,
                y + textHeight / 2 - 1
        );
    }

    private int convertX(double cityX) {
        int availableWidth = getWidth() - 2 * MARGIN;

        return MARGIN
                + (int) ((cityX / 100.0) * availableWidth);
    }

    private int convertY(double cityY) {
        int availableHeight = getHeight() - 2 * MARGIN;

        return MARGIN
                + (int) ((cityY / 100.0) * availableHeight);
    }
}