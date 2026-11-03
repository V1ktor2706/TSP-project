package algorithms;

import model.City;
import model.TSPGraph;

import java.util.ArrayList;
import java.util.List;

public class TwoOptTSP {

    private TSPGraph tspGraph;

    private double initialDistance;
    private double finalDistance;

    private long checkedSwaps;
    private int improvements;

    public TwoOptTSP(TSPGraph tspGraph) {
        this.tspGraph = tspGraph;
    }

    public List<City> solve() {
        NearestNeighborTSP nearestNeighbor =
                new NearestNeighborTSP(tspGraph);

        List<City> route =
                new ArrayList<>(nearestNeighbor.solve());

        if (route.size() < 4) {
            initialDistance =
                    tspGraph.calculateRouteDistance(route);

            finalDistance = initialDistance;

            return route;
        }

        initialDistance =
                tspGraph.calculateRouteDistance(route);

        checkedSwaps = 0;
        improvements = 0;

        boolean improved = true;

        while (improved) {
            improved = false;

            for (int i = 1; i < route.size() - 2; i++) {
                for (int k = i + 1;
                     k < route.size() - 1;
                     k++) {

                    checkedSwaps++;

                    City cityA = route.get(i - 1);
                    City cityB = route.get(i);
                    City cityC = route.get(k);
                    City cityD = route.get(k + 1);

                    double currentEdgesDistance =
                            tspGraph.distance(cityA, cityB) +
                                    tspGraph.distance(cityC, cityD);

                    double newEdgesDistance =
                            tspGraph.distance(cityA, cityC) +
                                    tspGraph.distance(cityB, cityD);

                    if (newEdgesDistance <
                            currentEdgesDistance) {

                        reverseRoutePart(route, i, k);

                        improvements++;
                        improved = true;
                    }
                }
            }
        }

        finalDistance =
                tspGraph.calculateRouteDistance(route);

        return route;
    }

    private void reverseRoutePart(
            List<City> route,
            int startIndex,
            int endIndex
    ) {
        while (startIndex < endIndex) {
            City temporaryCity =
                    route.get(startIndex);

            route.set(
                    startIndex,
                    route.get(endIndex)
            );

            route.set(
                    endIndex,
                    temporaryCity
            );

            startIndex++;
            endIndex--;
        }
    }

    public double getInitialDistance() {
        return initialDistance;
    }

    public double getFinalDistance() {
        return finalDistance;
    }

    public long getCheckedSwaps() {
        return checkedSwaps;
    }

    public int getImprovements() {
        return improvements;
    }
}