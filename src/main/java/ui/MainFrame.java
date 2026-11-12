package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import model.TSPGraph;
import model.City;
import model.AlgorithmResult;

import algorithms.NearestNeighborTSP;
import algorithms.TwoOptTSP;
import algorithms.BruteForceTSP;
import algorithms.BranchAndBoundTSP;

import java.util.concurrent.ExecutionException;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainFrame extends JFrame {

    private Timer animationTimer;
    private List<City> fullRoute;
    private List<City> animatedRoute;

    private TSPGraph tspGraph;
    private GraphPanel graphPanel;
    private ComparisonPanel comparisonPanel;

    private StatusBadge statusLabel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private JPanel mainPanel;
    private JLabel resultDescriptionLabel;

    private JComboBox<String> algorithmComboBox;
    private JSlider cityCountSlider;
    private JLabel cityCountValueLabel;

    private JButton runButton;
    private JButton generateButton;

    private JLabel distanceValueLabel;
    private JLabel executionTimeValueLabel;
    private JLabel visitedCitiesValueLabel;

    private JTextArea algorithmDescriptionArea;


    public MainFrame() {
        tspGraph = new TSPGraph();

        configureFrame();
        createLayout();

        setVisible(true);
    }

    private void configureFrame() {
        setTitle("TSP Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setSize(1200, 780);
        setLocationRelativeTo(null);

        java.net.URL iconUrl =
                getClass().getResource("/icons/tsp-icon.png");

        if (iconUrl != null) {
            ImageIcon appIcon = new ImageIcon(iconUrl);
            setIconImage(appIcon.getImage());
        }

        getContentPane().setBackground(Theme.BACKGROUND);
    }

    private void createLayout() {
        contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Theme.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        headerPanel = createHeaderPanel();

        mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setOpaque(false);

        RoundedPanel configurationPanel =
                createConfigurationPanel();

        RoundedPanel visualizationPanel =
                createVisualizationPanel();

        comparisonPanel = new ComparisonPanel();

        JPanel rightPanel = new JPanel(
                new BorderLayout(0, 15)
        );

        rightPanel.setOpaque(false);

        rightPanel.add(
                visualizationPanel,
                BorderLayout.CENTER
        );

        rightPanel.add(
                comparisonPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                configurationPanel,
                BorderLayout.WEST
        );

        mainPanel.add(
                rightPanel,
                BorderLayout.CENTER
        );

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        setContentPane(contentPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TSP Visualizer");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel iconLabel = new JLabel();

        java.net.URL iconUrl =
                getClass().getResource("/icons/tsp-icon.png");

        if (iconUrl != null) {
            ImageIcon originalIcon = new ImageIcon(iconUrl);

            Image scaledImage = originalIcon.getImage()
                    .getScaledInstance(
                            42,
                            42,
                            Image.SCALE_SMOOTH
                    );

            iconLabel.setIcon(new ImageIcon(scaledImage));
        }

        JLabel subtitleLabel =
                new JLabel("Traveling Salesman Problem Simulator");

        subtitleLabel.setFont(Theme.SMALL_FONT);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(subtitleLabel);

        JPanel leftHeaderPanel = new JPanel();

        leftHeaderPanel.setLayout(
                new BoxLayout(
                        leftHeaderPanel,
                        BoxLayout.X_AXIS
                )
        );

        leftHeaderPanel.setOpaque(false);

        leftHeaderPanel.add(iconLabel);
        leftHeaderPanel.add(Box.createHorizontalStrut(12));
        leftHeaderPanel.add(titlePanel);

        statusLabel = new StatusBadge("Ready");

        header.add(leftHeaderPanel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);

        return header;
    }

    private RoundedPanel createConfigurationPanel() {
        RoundedPanel panel = new RoundedPanel(20);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 16, 20, 16));
        panel.setPreferredSize(new Dimension(305, 0));

        JLabel configurationTitle =
                createSectionTitle("Configuration");

        panel.add(configurationTitle);
        panel.add(Box.createVerticalStrut(16));

        JLabel algorithmLabel = createNormalLabel("Algorithm");
        panel.add(algorithmLabel);
        panel.add(Box.createVerticalStrut(7));

        algorithmComboBox = new JComboBox<>(new String[]{
                "Nearest Neighbor",
                "Nearest Neighbor + 2-opt",
                "Brute Force",
                "Branch and Bound"
        });

        algorithmComboBox.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );

        algorithmComboBox.setFont(Theme.NORMAL_FONT);
        algorithmComboBox.setBackground(Theme.INPUT_BACKGROUND);
        algorithmComboBox.setForeground(Theme.TEXT_PRIMARY);

        panel.add(algorithmComboBox);
        panel.add(Box.createVerticalStrut(8));

        algorithmDescriptionArea = new JTextArea();
        algorithmDescriptionArea.setEditable(false);
        algorithmDescriptionArea.setFocusable(false);
        algorithmDescriptionArea.setOpaque(false);

        algorithmDescriptionArea.setLineWrap(true);
        algorithmDescriptionArea.setWrapStyleWord(true);

        algorithmDescriptionArea.setFont(Theme.SMALL_FONT);
        algorithmDescriptionArea.setForeground(Theme.TEXT_SECONDARY);

        algorithmDescriptionArea.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 58)
        );

        algorithmDescriptionArea.setPreferredSize(
                new Dimension(270, 58)
        );

        panel.add(algorithmDescriptionArea);
        panel.add(Box.createVerticalStrut(12));

        algorithmComboBox.addActionListener(
                e -> updateAlgorithmDescription()
        );

        updateAlgorithmDescription();

        JPanel cityHeaderPanel = new JPanel(new BorderLayout());
        cityHeaderPanel.setOpaque(false);
        cityHeaderPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 25)
        );

        JLabel citiesLabel = createNormalLabel("Cities");

        cityCountValueLabel = new JLabel("10");
        cityCountValueLabel.setFont(Theme.NORMAL_FONT);
        cityCountValueLabel.setForeground(Theme.TEXT_PRIMARY);

        cityHeaderPanel.add(citiesLabel, BorderLayout.WEST);
        cityHeaderPanel.add(cityCountValueLabel, BorderLayout.EAST);

        panel.add(cityHeaderPanel);
        panel.add(Box.createVerticalStrut(5));

        cityCountSlider = new JSlider(2, 100, 10);
        cityCountSlider.setOpaque(false);
        cityCountSlider.setForeground(Theme.PRIMARY_BLUE);
        cityCountSlider.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 35)
        );

        cityCountSlider.addChangeListener(e ->
                cityCountValueLabel.setText(
                        String.valueOf(cityCountSlider.getValue())
                )
        );

        panel.add(cityCountSlider);
        panel.add(Box.createVerticalStrut(14));

        runButton = new JButton("▶  Run Algorithm");
        configureMainButton(runButton);

        runButton.addActionListener(e -> runSelectedAlgorithm());

        panel.add(runButton);
        panel.add(Box.createVerticalStrut(10));

        generateButton = new JButton("↻  Generate Cities");
        configureSecondaryButton(generateButton);

        generateButton.addActionListener(e -> generateCities());

        panel.add(generateButton);
        panel.add(Box.createVerticalStrut(24));

        JSeparator separator = new JSeparator();
        separator.setForeground(Theme.BORDER);
        separator.setBackground(Theme.BORDER);
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 1)
        );

        panel.add(separator);
        panel.add(Box.createVerticalStrut(24));

        JLabel resultsTitle = createSectionTitle("Results");

        panel.add(resultsTitle);
        panel.add(Box.createVerticalStrut(16));

        JLabel distanceTitle =
                createSecondaryLabel("Total Distance");

        distanceValueLabel = new JLabel("—");
        distanceValueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 25)
        );
        distanceValueLabel.setForeground(Theme.ROUTE_BLUE);
        distanceValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(distanceTitle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(distanceValueLabel);
        panel.add(Box.createVerticalStrut(8));

        resultDescriptionLabel =
                createSecondaryLabel("");

        panel.add(resultDescriptionLabel);
        panel.add(Box.createVerticalStrut(18));

        JPanel executionTimeRow = createResultRow(
                "Execution time",
                "—"
        );

        executionTimeValueLabel =
                (JLabel) executionTimeRow.getComponent(1);

        panel.add(executionTimeRow);
        panel.add(Box.createVerticalStrut(22));

        JPanel visitedCitiesRow = createResultRow(
                "Visited cities",
                "—"
        );

        visitedCitiesValueLabel =
                (JLabel) visitedCitiesRow.getComponent(1);

        panel.add(visitedCitiesRow);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);

        label.setFont(Theme.SECTION_TITLE_FONT);
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JLabel createNormalLabel(String text) {
        JLabel label = new JLabel(text);

        label.setFont(Theme.NORMAL_FONT);
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JLabel createSecondaryLabel(String text) {
        JLabel label = new JLabel(text);

        label.setFont(Theme.NORMAL_FONT);
        label.setForeground(Theme.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private void configureMainButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(Theme.PRIMARY_BLUE);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
        ));

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );

        button.setPreferredSize(
                new Dimension(270, 42)
        );

        button.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void configureSecondaryButton(JButton button) {
        button.setFont(Theme.NORMAL_FONT);
        button.setForeground(Theme.TEXT_PRIMARY);
        button.setBackground(Theme.BORDER);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
        ));

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );

        button.setPreferredSize(
                new Dimension(270, 42)
        );

        button.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JPanel createResultRow(
            String title,
            String initialValue
    ) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        row.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 25)
        );

        JLabel titleLabel = createSecondaryLabel(title);

        JLabel valueLabel = new JLabel(initialValue);
        valueLabel.setFont(Theme.NORMAL_FONT);
        valueLabel.setForeground(Theme.TEXT_PRIMARY);

        row.add(titleLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }

    private RoundedPanel createVisualizationPanel() {
        RoundedPanel panel = new RoundedPanel(20);
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(new EmptyBorder(20, 20, 16, 20));

        JLabel titleLabel =
                createSectionTitle("Route Visualization");

        graphPanel = new GraphPanel();

        JPanel legendPanel = createLegendPanel();

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(graphPanel, BorderLayout.CENTER);
        panel.add(legendPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 18, 0)
        );

        legendPanel.setOpaque(false);

        JLabel startingCityLabel =
                new JLabel("●  Starting city");

        startingCityLabel.setFont(Theme.SMALL_FONT);
        startingCityLabel.setForeground(Theme.START_GREEN);

        JLabel routeLabel = new JLabel("●  Route");
        routeLabel.setFont(Theme.SMALL_FONT);
        routeLabel.setForeground(Theme.ROUTE_BLUE);

        legendPanel.add(startingCityLabel);
        legendPanel.add(routeLabel);

        return legendPanel;
    }

    private void generateCities() {
        int cityCount = cityCountSlider.getValue();

        statusLabel.setText("Generating...");

        tspGraph = new TSPGraph();

        for (int i = 0; i < cityCount; i++) {
            tspGraph.addCity();
        }

        graphPanel.setCities(tspGraph.getCities());
        graphPanel.clearRoute();

        distanceValueLabel.setText("—");
        executionTimeValueLabel.setText("—");
        visitedCitiesValueLabel.setText("0 / " + cityCount);
        resultDescriptionLabel.setText(
                ""
        );

        comparisonPanel.clearResults();

        statusLabel.setText("Ready");
    }

    private void startRouteAnimation(
            List<City> route,
            double totalDistance,
            double executionTime
    ) {
        if (route == null || route.isEmpty()) {
            statusLabel.setText("No route found");

            setControlsEnabled(true);
            return;
        }

        animatedRoute = new ArrayList<>();
        animatedRoute.add(route.get(0));

        graphPanel.setRoute(animatedRoute);

        distanceValueLabel.setText("0.00");
        executionTimeValueLabel.setText(
                String.format(Locale.US, "%.4f ms", executionTime)
        );

        visitedCitiesValueLabel.setText(
                "1 / " + tspGraph.getCities().size()
        );

        statusLabel.setText("Animating...");

        animationTimer = new Timer(400, e -> {
            int nextCityIndex = animatedRoute.size();

            if (nextCityIndex < route.size()) {
                City nextCity = route.get(nextCityIndex);

                animatedRoute.add(nextCity);
                graphPanel.setRoute(animatedRoute);

                double currentDistance =
                        tspGraph.calculateRouteDistance(animatedRoute);

                distanceValueLabel.setText(
                        String.format(
                                Locale.US,
                                "%.2f",
                                currentDistance
                        )
                );

                int visitedCities = Math.min(
                        animatedRoute.size(),
                        tspGraph.getCities().size()
                );

                visitedCitiesValueLabel.setText(
                        visitedCities
                                + " / "
                                + tspGraph.getCities().size()
                );
            }

            if (animatedRoute.size() >= route.size()) {
                animationTimer.stop();

                distanceValueLabel.setText(
                        String.format(
                                Locale.US,
                                "%.2f",
                                totalDistance
                        )
                );

                visitedCitiesValueLabel.setText(
                        tspGraph.getCities().size()
                                + " / "
                                + tspGraph.getCities().size()
                );

                statusLabel.setText("Finished");

                setControlsEnabled(true);
            }
        });

        animationTimer.start();
    }

    private void setControlsEnabled(boolean enabled) {
        generateButton.setEnabled(enabled);
        runButton.setEnabled(enabled);
        cityCountSlider.setEnabled(enabled);
        algorithmComboBox.setEnabled(enabled);
    }

    private void runSelectedAlgorithm() {
        String selectedAlgorithm =
                (String) algorithmComboBox.getSelectedItem();

        if ("Nearest Neighbor".equals(selectedAlgorithm)) {
            runNearestNeighbor();
        } else if ("Nearest Neighbor + 2-opt".equals(selectedAlgorithm)) {
            runTwoOpt();
        } else if ("Brute Force".equals(selectedAlgorithm)) {
            runBruteForce();
        } else if ("Branch and Bound".equals(selectedAlgorithm)) {
            runBranchAndBound();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    selectedAlgorithm + " will be connected next.",
                    "Algorithm",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void runNearestNeighbor() {
        if (tspGraph.getCities().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Generate cities first.",
                    "No cities",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (animationTimer != null) {
            animationTimer.stop();
        }

        setControlsEnabled(false);

        statusLabel.setText("Calculating...");

        NearestNeighborTSP algorithm =
                new NearestNeighborTSP(tspGraph);

        long startTime = System.nanoTime();

        fullRoute = algorithm.solve();

        long endTime = System.nanoTime();

        double executionTime =
                (endTime - startTime) / 1_000_000.0;

        double totalDistance =
                tspGraph.calculateRouteDistance(fullRoute);

        comparisonPanel.addResult(
                new AlgorithmResult(
                        "Nearest Neighbor",
                        totalDistance,
                        executionTime,
                        false
                )
        );

        resultDescriptionLabel.setText(
                "Nearest Neighbor result:"
        );

        startRouteAnimation(
                fullRoute,
                totalDistance,
                executionTime
        );
    }

    private void runTwoOpt() {
        if (tspGraph.getCities().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Generate cities first.",
                    "No cities",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (animationTimer != null) {
            animationTimer.stop();
        }

        setControlsEnabled(false);

        statusLabel.setText("Calculating...");

        SwingWorker<List<City>, Void> worker =
                new SwingWorker<>() {

                    private TwoOptTSP algorithm;
                    private double executionTime;

                    @Override
                    protected List<City> doInBackground() {
                        algorithm = new TwoOptTSP(tspGraph);

                        long startTime = System.nanoTime();

                        List<City> route = algorithm.solve();

                        long endTime = System.nanoTime();

                        executionTime =
                                (endTime - startTime) / 1_000_000.0;

                        return route;
                    }

                    @Override
                    protected void done() {
                        try {
                            List<City> route = get();

                            double totalDistance =
                                    tspGraph.calculateRouteDistance(route);

                            comparisonPanel.addResult(
                                    new AlgorithmResult(
                                            "Nearest Neighbor + 2-opt",
                                            totalDistance,
                                            executionTime,
                                            false
                                    )
                            );

                            resultDescriptionLabel.setText(
                                    "2-opt improvements: "
                                            + algorithm.getImprovements()
                            );

                            startRouteAnimation(
                                    route,
                                    totalDistance,
                                    executionTime
                            );

                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                            showAlgorithmError();

                        } catch (ExecutionException exception) {
                            exception.printStackTrace();
                            showAlgorithmError();
                        }
                    }
                };

        worker.execute();
    }

    private void runBruteForce() {
        int cityCount = tspGraph.getCities().size();

        if (cityCount == 0) {
            showNoCitiesMessage();
            return;
        }

        if (cityCount > 10) {
            JOptionPane.showMessageDialog(
                    this,
                    "Brute Force is limited to 10 cities because its complexity is O(n!).",
                    "Too many cities",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        prepareForCalculation();

        SwingWorker<List<City>, Void> worker =
                new SwingWorker<>() {

                    private BruteForceTSP algorithm;
                    private double executionTime;

                    @Override
                    protected List<City> doInBackground() {
                        algorithm = new BruteForceTSP(tspGraph);

                        long startTime = System.nanoTime();
                        List<City> route = algorithm.solve();
                        long endTime = System.nanoTime();

                        executionTime =
                                (endTime - startTime) / 1_000_000.0;

                        return route;
                    }

                    @Override
                    protected void done() {
                        try {
                            List<City> route = get();

                            double totalDistance =
                                    tspGraph.calculateRouteDistance(route);

                            comparisonPanel.addResult(
                                    new AlgorithmResult(
                                            "Brute Force",
                                            totalDistance,
                                            executionTime,
                                            true
                                    )
                            );

                            resultDescriptionLabel.setText(
                                    "Routes checked: "
                                            + algorithm.getCheckedRoutes()
                            );

                            startRouteAnimation(
                                    route,
                                    totalDistance,
                                    executionTime
                            );

                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                            showAlgorithmError();

                        } catch (ExecutionException exception) {
                            exception.printStackTrace();
                            showAlgorithmError();
                        }
                    }
                };

        worker.execute();
    }

    private void runBranchAndBound() {
        int cityCount = tspGraph.getCities().size();

        if (cityCount == 0) {
            showNoCitiesMessage();
            return;
        }

        if (cityCount > 12) {
            JOptionPane.showMessageDialog(
                    this,
                    "Branch and Bound is limited to 12 cities to avoid a very long calculation.",
                    "Too many cities",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        prepareForCalculation();

        SwingWorker<List<City>, Void> worker =
                new SwingWorker<>() {

                    private BranchAndBoundTSP algorithm;
                    private double executionTime;

                    @Override
                    protected List<City> doInBackground() {
                        algorithm = new BranchAndBoundTSP(tspGraph);

                        long startTime = System.nanoTime();
                        List<City> route = algorithm.solve();
                        long endTime = System.nanoTime();

                        executionTime =
                                (endTime - startTime) / 1_000_000.0;

                        return route;
                    }

                    @Override
                    protected void done() {
                        try {
                            List<City> route = get();

                            double totalDistance =
                                    tspGraph.calculateRouteDistance(route);

                            comparisonPanel.addResult(
                                    new AlgorithmResult(
                                            "Branch and Bound",
                                            totalDistance,
                                            executionTime,
                                            true
                                    )
                            );

                            resultDescriptionLabel.setToolTipText(
                                    "Explored nodes: " + algorithm.getExploredNodes()
                                            + " | Pruned branches: "
                                            + algorithm.getPrunedBranches()
                            );

                            startRouteAnimation(
                                    route,
                                    totalDistance,
                                    executionTime
                            );

                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                            showAlgorithmError();

                        } catch (ExecutionException exception) {
                            exception.printStackTrace();
                            showAlgorithmError();
                        }
                    }
                };

        worker.execute();
    }

    private void prepareForCalculation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        setControlsEnabled(false);

        statusLabel.setText("Calculating...");
    }

    private void showNoCitiesMessage() {
        JOptionPane.showMessageDialog(
                this,
                "Generate cities first.",
                "No cities",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showAlgorithmError() {
        statusLabel.setText("Error");

        setControlsEnabled(true);

        JOptionPane.showMessageDialog(
                this,
                "An error occurred while running the algorithm.",
                "Algorithm error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void updateAlgorithmDescription() {
        String selectedAlgorithm =
                (String) algorithmComboBox.getSelectedItem();

        if ("Nearest Neighbor".equals(selectedAlgorithm)) {
            algorithmDescriptionArea.setText(
                    "Approximate greedy algorithm. Visits the nearest "
                            + "unvisited city. Complexity: O(n²)."
            );

        } else if ("Nearest Neighbor + 2-opt".equals(selectedAlgorithm)) {
            algorithmDescriptionArea.setText(
                    "Improves the Nearest Neighbor route by reversing "
                            + "route segments. Approximate algorithm."
            );

        } else if ("Brute Force".equals(selectedAlgorithm)) {
            algorithmDescriptionArea.setText(
                    "Checks every possible route and guarantees the "
                            + "optimal solution. Complexity: O(n!)."
            );

        } else if ("Branch and Bound".equals(selectedAlgorithm)) {
            algorithmDescriptionArea.setText(
                    "Exact algorithm that removes branches which cannot "
                            + "improve the best solution. Worst case: O(n!)."
            );
        }
    }
}



