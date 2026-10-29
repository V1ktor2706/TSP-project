package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TSPGraph {
    private List<City> cities;

    public TSPGraph() {
        this.cities = new ArrayList<>();
    }

    public List<City> getCities() {
        return new ArrayList<>(cities);
    }

    public void addCity() {
        Random rand = new Random();

        double x = rand.nextDouble() * 100;
        double y = rand.nextDouble() * 100;

        int id = cities.size() + 1;//tekushiq broi gradove + 1

        City newCity = new City(x, y, id);
        cities.add(newCity);
    }

    public double distance(City city1, City city2) {
        double x1 = city1.getX();
        double y1 = city1.getY();

        double x2 = city2.getX();
        double y2 = city2.getY();

        double dist = Math.sqrt(
                Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)
        );

        return dist;
    }

    public double calculateRouteDistance(List<City> route){
        double totalDistance = 0;

        for(int i = 0; i < route.size() - 1; i++){
            City currCity =  route.get(i);
            City nextCity =  route.get(i+1);

            totalDistance += distance(currCity, nextCity);
        }

        return totalDistance;
    }
}
