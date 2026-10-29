package algorithms;

import model.City;
import model.TSPGraph;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborTSP {
    private TSPGraph tspGraph;

    public NearestNeighborTSP(TSPGraph tspGraph) {
        this.tspGraph = tspGraph;
    }

    public List<City> solve(){
        //Get a copy of cities and create the route
        List<City> allCities = tspGraph.getCities();
        List<City> route = new ArrayList<>();

        //Handle an empty graph
        if(allCities.isEmpty()){
            return route;
        }

        //Choose the starting city
        City startCity = allCities.get(0);
        City currentCity = startCity;

        //Visit the starting city
        route.add(startCity);
        allCities.remove(startCity);

        //Continue until there are no unvisited cities
        while(!allCities.isEmpty()){
            City nearestCity = null;
            double shortestDistance = Double.POSITIVE_INFINITY;

            //Find the closest unvisited city
            for(City city : allCities){
                double distance = tspGraph.distance(currentCity, city);

                if(distance < shortestDistance){
                    shortestDistance = distance;
                    nearestCity = city;
                }
            }

            //Visit that city
            route.add(nearestCity);
            allCities.remove(nearestCity);
            currentCity = nearestCity;
        }

        //Return to the starting city
        route.add(startCity);

        return route;
    }
}
