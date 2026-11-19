# TSP-project

TSP Visualizer is a Java Swing application for demonstrating different solutions to the Traveling Salesman Problem. It generates a set of cities and runs the selected algorithm on the same data, which makes it possible to compare the resulting routes and execution times.


The application includes four approaches:
* Nearest Neighbor – starts from the first city and repeatedly visits the nearest unvisited city.

* Nearest Neighbor + 2-opt – improves the Nearest Neighbor route by replacing pairs of edges when this shortens the route.

* Brute Force – checks every possible route and returns the shortest one.

* Branch and Bound – searches for the optimal route while skipping branches that cannot improve the current best solution.

* Brute Force and Branch and Bound are exact algorithms. Nearest Neighbor and 2-opt are heuristic methods and are more suitable for a larger number of cities.

Features:

* generation of random cities;

* selection of an algorithm;

* animated route visualization;

* comparison of algorithms on the same set of cities;

* total route distance and execution time;

* solution quality compared with the optimal result;

* additional statistics for 2-opt and Branch and Bound;

* dark interface created with FlatLaf.

Technologies:

* Java 17;

* Java Swing;

* FlatLaf;

* Swing Timer for route animation;

* SwingWorker for longer calculations.

# How to run the project

* Clone the repository:

* git clone https://github.com/USERNAME/REPOSITORY.git

* Open the project folder in IntelliJ IDEA.

* Select JDK 17 as the project SDK.

* Make sure the FlatLaf dependency is loaded.

* Run Main.java.

* Replace USERNAME/REPOSITORY with the actual GitHub repository address.

How to use the application:

* Select the number of cities with the slider.

* Press Generate Cities.

* Select an algorithm from the drop-down menu.

* Press Run Algorithm.

* Wait for the route animation to finish.

* Run another algorithm to add its result to the comparison table.
