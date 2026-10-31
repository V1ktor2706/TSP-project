package algorithms;

import model.City;
import model.TSPGraph;

import java.util.ArrayList;
import java.util.List;

public class BranchAndBoundTSP {

    private TSPGraph tspGraph;

    private List<City> bestRoute;
    private double bestDistance;

    private long exploredNodes;
    private long prunedBranches;

    public BranchAndBoundTSP(TSPGraph tspGraph) {
        this.tspGraph = tspGraph;
    }

    public List<City> solve() {
        List<City> allCities = tspGraph.getCities();

        bestRoute = new ArrayList<>();
        bestDistance = Double.POSITIVE_INFINITY;

        exploredNodes = 0;
        prunedBranches = 0;

        if (allCities.isEmpty()) {
            return bestRoute;
        }

        createInitialSolution();

        City startCity = allCities.remove(0);

        List<City> currentRoute = new ArrayList<>();
        currentRoute.add(startCity);

        search(
                startCity,
                startCity,
                allCities,
                currentRoute,
                0
        );

        return bestRoute;
    }

    private void createInitialSolution() {
        NearestNeighborTSP nearestNeighbor =
                new NearestNeighborTSP(tspGraph);

        bestRoute = nearestNeighbor.solve();

        bestDistance =
                tspGraph.calculateRouteDistance(bestRoute);
    }

    private void search(
            City startCity,
            City currentCity,
            List<City> remainingCities,
            List<City> currentRoute,
            double currentDistance
    ) {
        exploredNodes++;

        // Базов случай: всички градове са посетени
        if (remainingCities.isEmpty()) {
            double returnDistance =
                    tspGraph.distance(
                            currentCity,
                            startCity
                    );

            double totalDistance =
                    currentDistance + returnDistance;

            if (totalDistance < bestDistance) {
                bestDistance = totalDistance;

                bestRoute =
                        new ArrayList<>(currentRoute);

                bestRoute.add(startCity);
            }

            return;
        }

        // Пробваме всеки непосетен град
        for (int i = 0; i < remainingCities.size(); i++) {
            City nextCity =
                    remainingCities.remove(i);

            double distanceToNextCity =
                    tspGraph.distance(
                            currentCity,
                            nextCity
                    );

            double newDistance =
                    currentDistance + distanceToNextCity;

            currentRoute.add(nextCity);

            search(
                    startCity,
                    nextCity,
                    remainingCities,
                    currentRoute,
                    newDistance
            );

            // Backtracking
            currentRoute.remove(
                    currentRoute.size() - 1
            );

            remainingCities.add(i, nextCity);
        }
    }

    public double getBestDistance() {
        return bestDistance;
    }

    public long getExploredNodes() {
        return exploredNodes;
    }

    public long getPrunedBranches() {
        return prunedBranches;
    }
}