package ui;

import algorithms.BruteForceTSP;
import algorithms.NearestNeighborTSP;
import model.City;
import model.TSPGraph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainFrame extends JFrame {

    private TSPGraph tspGraph;
    private GraphPanel graphPanel;

    private JSpinner cityCountSpinner;
    private JButton generateButton;
    private JButton solveButton;
    private JLabel resultLabel;
    private JComboBox<String> algorithmComboBox;

    private Timer animationTimer;
    private List<City> fullRoute;
    private List<City> animatedRoute;

    public MainFrame() {
        tspGraph = new TSPGraph();
        graphPanel = new GraphPanel();

        setTitle("Traveling Salesman Problem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createControlPanel();

        add(graphPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createControlPanel() {
        JPanel controlContainer =
                new JPanel(new BorderLayout());

        JPanel controlPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.CENTER,
                        10,
                        8
                ));

        JLabel cityCountLabel =
                new JLabel("City Count:");

        cityCountSpinner = new JSpinner(
                new SpinnerNumberModel(
                        10,
                        2,
                        100,
                        1
                )
        );

        generateButton = new JButton("Generate");

        JLabel algorithmLabel =
                new JLabel("Algorithm:");

        algorithmComboBox = new JComboBox<>(
                new String[]{
                        "Nearest Neighbor",
                        "Brute Force"
                }
        );

        solveButton = new JButton("Solve");

        resultLabel =
                new JLabel(
                        "Generate cities first",
                        SwingConstants.CENTER
                );

        resultLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        10,
                        8,
                        10
                )
        );

        generateButton.addActionListener(
                e -> generateCities()
        );

        solveButton.addActionListener(
                e -> solveSelectedAlgorithm()
        );

        controlPanel.add(cityCountLabel);
        controlPanel.add(cityCountSpinner);
        controlPanel.add(generateButton);

        controlPanel.add(algorithmLabel);
        controlPanel.add(algorithmComboBox);
        controlPanel.add(solveButton);

        controlContainer.add(
                controlPanel,
                BorderLayout.CENTER
        );

        controlContainer.add(
                resultLabel,
                BorderLayout.SOUTH
        );

        add(controlContainer, BorderLayout.NORTH);
    }

    private void generateCities() {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        int cityCount =
                (int) cityCountSpinner.getValue();

        tspGraph = new TSPGraph();

        for (int i = 0; i < cityCount; i++) {
            tspGraph.addCity();
        }

        graphPanel.setCities(tspGraph.getCities());
        graphPanel.setAlgorithmState(null, null);

        setControlsEnabled(true);

        resultLabel.setText(
                cityCount + " cities generated"
        );
    }

    private void solveSelectedAlgorithm() {
        String selectedAlgorithm =
                (String) algorithmComboBox.getSelectedItem();

        if ("Nearest Neighbor".equals(selectedAlgorithm)) {
            solveNearestNeighbor();
        } else if ("Brute Force".equals(selectedAlgorithm)) {
            solveBruteForce();
        }
    }

    private void solveNearestNeighbor() {
        if (!hasCities()) {
            return;
        }

        NearestNeighborTSP algorithm =
                new NearestNeighborTSP(tspGraph);

        long startTime = System.nanoTime();

        List<City> route = algorithm.solve();

        long endTime = System.nanoTime();

        double totalDistance =
                tspGraph.calculateRouteDistance(route);

        double executionTime =
                (endTime - startTime) / 1_000_000.0;

        startRouteAnimation(
                route,
                totalDistance,
                executionTime,
                "Nearest city",
                ""
        );
    }

    private void solveBruteForce() {
        if (!hasCities()) {
            return;
        }

        int cityCount = tspGraph.getCities().size();

        if (cityCount > 10) {
            JOptionPane.showMessageDialog(
                    this,
                    "Brute Force is limited to 10 cities.\n" +
                            "Generate 10 or fewer cities.",
                    "Too many cities",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (animationTimer != null) {
            animationTimer.stop();
        }

        graphPanel.setRoute(new ArrayList<>());
        graphPanel.setAlgorithmState(null, null);

        setControlsEnabled(false);
        resultLabel.setText(
                "Calculating optimal route..."
        );

        SwingWorker<List<City>, Void> worker =
                new SwingWorker<>() {

                    private double bestDistance;
                    private double executionTime;
                    private long checkedRoutes;

                    @Override
                    protected List<City> doInBackground() {
                        BruteForceTSP algorithm =
                                new BruteForceTSP(tspGraph);

                        long startTime = System.nanoTime();

                        List<City> route = algorithm.solve();

                        long endTime = System.nanoTime();

                        bestDistance =
                                algorithm.getBestDistance();

                        checkedRoutes =
                                algorithm.getCheckedRoutes();

                        executionTime =
                                (endTime - startTime) /
                                        1_000_000.0;

                        return route;
                    }

                    @Override
                    protected void done() {
                        try {
                            List<City> route = get();

                            startRouteAnimation(
                                    route,
                                    bestDistance,
                                    executionTime,
                                    "Next city",
                                    " | Checked routes: " +
                                            checkedRoutes
                            );

                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();

                            setControlsEnabled(true);

                            resultLabel.setText(
                                    "Calculation interrupted."
                            );

                        } catch (ExecutionException exception) {
                            setControlsEnabled(true);

                            resultLabel.setText(
                                    "Error while calculating route."
                            );

                            exception.getCause().printStackTrace();
                        }
                    }
                };

        worker.execute();
    }

    private void startRouteAnimation(
            List<City> route,
            double totalDistance,
            double executionTime,
            String nextCityDescription,
            String additionalInformation
    ) {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        if (route == null || route.isEmpty()) {
            setControlsEnabled(true);
            resultLabel.setText("No route found.");
            return;
        }

        fullRoute = route;
        animatedRoute = new ArrayList<>();

        animatedRoute.add(fullRoute.get(0));

        graphPanel.setRoute(
                new ArrayList<>(animatedRoute)
        );

        graphPanel.setAlgorithmState(null, null);

        setControlsEnabled(false);

        resultLabel.setText("Starting animation...");

        animationTimer = new Timer(500, e -> {
            int nextCityIndex =
                    animatedRoute.size();

            if (nextCityIndex < fullRoute.size()) {
                City currentCity =
                        animatedRoute.get(
                                animatedRoute.size() - 1
                        );

                City nextCity =
                        fullRoute.get(nextCityIndex);

                graphPanel.setAlgorithmState(
                        currentCity,
                        nextCity
                );

                animatedRoute.add(nextCity);

                graphPanel.setRoute(
                        new ArrayList<>(animatedRoute)
                );

                resultLabel.setText(
                        "Step: " + nextCityIndex +
                                " / " +
                                (fullRoute.size() - 1) +
                                " | Current city: " +
                                currentCity.getId() +
                                " → " +
                                nextCityDescription +
                                ": " +
                                nextCity.getId()
                );
            }

            if (animatedRoute.size() ==
                    fullRoute.size()) {

                animationTimer.stop();

                graphPanel.setAlgorithmState(
                        null,
                        null
                );

                setControlsEnabled(true);

                resultLabel.setText(
                        String.format(
                                "Distance: %.2f | " +
                                        "Time: %.4f ms%s",
                                totalDistance,
                                executionTime,
                                additionalInformation
                        )
                );
            }
        });

        animationTimer.start();
    }

    private boolean hasCities() {
        if (tspGraph.getCities().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "First generate cities.",
                    "No cities",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        return true;
    }

    private void setControlsEnabled(boolean enabled) {
        generateButton.setEnabled(enabled);
        solveButton.setEnabled(enabled);
        cityCountSpinner.setEnabled(enabled);
        algorithmComboBox.setEnabled(enabled);
    }
}