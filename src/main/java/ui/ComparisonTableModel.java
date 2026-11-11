package ui;

import model.AlgorithmResult;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComparisonTableModel extends AbstractTableModel {

    private final List<AlgorithmResult> results;

    private final String[] columnNames = {
            "Algorithm",
            "Distance",
            "Time",
            "Quality"
    };

    public ComparisonTableModel() {
        results = new ArrayList<>();
    }

    public void addOrUpdateResult(AlgorithmResult newResult) {
        for (int i = 0; i < results.size(); i++) {
            AlgorithmResult currentResult = results.get(i);

            if (currentResult.getAlgorithmName().equals(
                    newResult.getAlgorithmName()
            )) {
                results.set(i, newResult);
                fireTableDataChanged();
                return;
            }
        }

        results.add(newResult);
        fireTableDataChanged();
    }

    public void clearResults() {
        results.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return results.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        AlgorithmResult result = results.get(row);

        switch (column) {
            case 0:
                return result.getAlgorithmName();

            case 1:
                return String.format(
                        Locale.US,
                        "%.2f",
                        result.getDistance()
                );

            case 2:
                return String.format(
                        Locale.US,
                        "%.4f ms",
                        result.getExecutionTime()
                );

            case 3:
                return calculateQuality(result);

            default:
                return "";
        }
    }

    private String calculateQuality(AlgorithmResult result) {
        double optimalDistance = findOptimalDistance();

        if (optimalDistance < 0) {
            return "Run exact algorithm";
        }

        if (result.isExact()) {
            return "Optimal";
        }

        double difference =
                ((result.getDistance() - optimalDistance)
                        / optimalDistance) * 100;

        return String.format(
                Locale.US,
                "+%.2f%%",
                difference
        );
    }

    private double findOptimalDistance() {
        double optimalDistance = Double.POSITIVE_INFINITY;

        for (AlgorithmResult result : results) {
            if (result.isExact()
                    && result.getDistance() < optimalDistance) {

                optimalDistance = result.getDistance();
            }
        }

        if (optimalDistance == Double.POSITIVE_INFINITY) {
            return -1;
        }

        return optimalDistance;
    }
}