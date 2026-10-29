package algorithms;

import model.City;
import model.TSPGraph;

import java.util.ArrayList;
import java.util.List;

public class BruteForceTSP {
    private TSPGraph tspGraph;

    private List<City> bestRoute;
    private double bestDistance;
    private long checkedRoutes;

    public BruteForceTSP(TSPGraph tspGraph) {
        this.tspGraph = tspGraph;
    }

    public List<City> solve() {
        List<City> allCities = tspGraph.getCities();

        bestRoute = new ArrayList<>();
        bestDistance = Double.POSITIVE_INFINITY;
        checkedRoutes = 0;

        if(allCities.isEmpty())
            return bestRoute;

        City startCity = allCities.remove(0);

        List<City> currRoute = new ArrayList<>();
        currRoute.add(startCity);

        generateRoutes(
                startCity,
                allCities,
                currRoute
        );


        return bestRoute;
    }

    private void generateRoutes(City startCity, List<City> remainingCities, List<City> currRoute) {
        if (remainingCities.isEmpty()) {
            List<City> completedRoute =
                    new ArrayList<>(currRoute);

            completedRoute.add(startCity);

            double distance =
                    tspGraph.calculateRouteDistance(completedRoute);

            checkedRoutes++;

            if (distance < bestDistance) {
                bestDistance = distance;
                bestRoute = completedRoute;
            }

            return;
        }

        for(int i = 0; i < remainingCities.size(); i++){
            City selectedCity = remainingCities.remove(i);

            currRoute.add(selectedCity);

            generateRoutes(
                    startCity,
                    remainingCities,
                    currRoute
            );

            //Backtracking
            currRoute.remove(currRoute.size() - 1);
            remainingCities.add(i, selectedCity);
        }
    }

    public double getBestDistance() {
        return bestDistance;
    }

    public long getCheckedRoutes() {
        return checkedRoutes;
    }

}
