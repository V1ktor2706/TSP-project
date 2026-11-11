package model;

public class AlgorithmResult {

    private String algorithmName;
    private double distance;
    private double executionTime;
    private boolean exact;

    public AlgorithmResult(
            String algorithmName,
            double distance,
            double executionTime,
            boolean exact
    ) {
        this.algorithmName = algorithmName;
        this.distance = distance;
        this.executionTime = executionTime;
        this.exact = exact;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public double getDistance() {
        return distance;
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public boolean isExact() {
        return exact;
    }
}