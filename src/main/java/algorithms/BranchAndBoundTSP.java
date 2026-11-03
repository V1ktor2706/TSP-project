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

        double lowerBound = calculateLowerBound(
                startCity,
                currentCity,
                remainingCities,
                currentDistance
        );

        if (lowerBound >= bestDistance) {
            prunedBranches++;
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

    private double calculateLowerBound(
            City startCity,
            City currentCity,
            List<City> remainingCities,
            double currentDistance
    ) {
        if (remainingCities.isEmpty()) {
            return currentDistance +
                    tspGraph.distance(currentCity, startCity);
        }

        double lowerBound = currentDistance;

        // Най-малкото възможно разстояние
        // от текущия град до непосетен град
        double minimumFromCurrent =
                Double.POSITIVE_INFINITY;

        for (City city : remainingCities) {
            double distance =
                    tspGraph.distance(currentCity, city);

            if (distance < minimumFromCurrent) {
                minimumFromCurrent = distance;
            }
        }

        lowerBound += minimumFromCurrent;

        // За всеки непосетен град добавяме
        // най-малкото възможно изходящо разстояние
        for (City city : remainingCities) {
            double minimumOutgoingDistance =
                    tspGraph.distance(city, startCity);

            for (City otherCity : remainingCities) {
                if (city == otherCity) {
                    continue;
                }

                double distance =
                        tspGraph.distance(city, otherCity);

                if (distance < minimumOutgoingDistance) {
                    minimumOutgoingDistance = distance;
                }
            }

            lowerBound += minimumOutgoingDistance;
        }

        return lowerBound;
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