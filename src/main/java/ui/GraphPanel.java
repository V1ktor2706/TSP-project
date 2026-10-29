package ui;

import model.City;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public class GraphPanel extends JPanel {
    private List<City> cities;
    private List<City> route;

    private City currentCity;
    private City selectedCity;


    public GraphPanel() {
        this.cities = new ArrayList<>();
        this.route = new ArrayList<>();

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
        this.route = new ArrayList<>();

        this.currentCity = null;
        this.selectedCity = null;

        repaint();
    }

    public void setRoute(List<City> route) {
        this.route = route;
        repaint();
    }

    public void setAlgorithmState(City currentCity, City selectedCity) {
        this.currentCity = currentCity;
        this.selectedCity = selectedCity;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int padding = 30;
        int citySize = 14;

        if(route.size() > 1){
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));

            for(int i = 0; i < route.size() - 1; i++){
                City currCity = route.get(i);
                City nextCity = route.get(i + 1);

                int x1 = padding + (int) (
                        currCity.getX()/ 100 * (getWidth() - 2 * padding)
                );

                int y1 = padding + (int) (
                        currCity.getY()/ 100 * (getHeight() - 2 * padding)
                );

                int x2 = padding + (int) (
                        nextCity.getX()/ 100 * (getWidth() - 2 * padding)
                );

                int y2 = padding + (int) (
                        nextCity.getY()/ 100 * (getHeight() - 2 * padding)
                );

                g2d.drawLine(x1, y1, x2, y2);
            }
        }

        for (City city : cities) {
            int x = padding + (int) (
                    city.getX() / 100 * (getWidth() - 2 * padding)
            );

            int y = padding + (int) (
                    city.getY() / 100 * (getHeight() - 2 * padding)
            );

           if(city == currentCity)
               g2d.setColor(Color.ORANGE);
           else if(city == selectedCity)
               g2d.setColor(new Color(34, 177, 76));
           else
               g2d.setColor(Color.BLUE);


            g2d.fillOval(
                    x - citySize / 2,
                    y - citySize / 2,
                    citySize,
                    citySize
            );

            g2d.setColor(Color.BLACK);

            g2d.drawString(
                    String.valueOf(city.getId()),
                    x + 10,
                    y - 10
            );
        }
    }
}
