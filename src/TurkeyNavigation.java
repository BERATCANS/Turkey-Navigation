/**
 * @author Beratcan Dogan, Student ID: 2021400132
 * @date 03.04.2024
 *this code find distance of cities with a path and shows in canvas.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.awt.*;

public class TurkeyNavigation {
    /**
     * Main method come together all my methods and my project starts from here.
     * @param args I don't know why I write this parameter but my teacher said that don't forget write args.
     * @throws FileNotFoundException If my project cannot find files it gives this error.
     */
    public static void main(String[] args) throws FileNotFoundException {
        List<City> cities = new ArrayList<>(); //initially creating a list of cities
        File file = new File("city_names.txt"); //opening file
        Scanner scanner = new Scanner(file); //defining new scanner to read lines
        while (scanner.hasNextLine()) { //if file has next line
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            String cityName = parts[0]; //getting one by one city names
            double x = Double.parseDouble(parts[1]); //getting one by one city x coordinates
            double y = Double.parseDouble(parts[2]); //getting one by one city y coordinates
            City city = new City(cityName, x, y); //defining new city object
            cities.add(city);//adding cities as an object in city class
        }
        scanner.close();
        int size = cities.size(); //size of city number
        double[][] distanceMatrix = new  double[size][size]; // to implement an algorithm first creating matrix of distances

        File file1 = new File("city_connections.txt");
        Scanner scanner1 = new Scanner(file1);
        while (scanner1.hasNextLine()) {
            String line = scanner1.nextLine();
            String[] names = line.split(",");
            String name1 = names[0];
            String name2 = names[1];
            int cityIndex1 = findIndex(cities, name1);
            int cityIndex2 = findIndex(cities, name2);//finding indexes of cities name via findIndex method
            double distance = cities.get(cityIndex1).distanceTo(cities.get(cityIndex2));//calculating distance with distanceTo method
            distanceMatrix[cityIndex1][cityIndex2] = distance;
            distanceMatrix[cityIndex2][cityIndex1] = distance;
            //filling matrix with distances and reading this knowledge from text file and calculating by method which in city file
        }
        scanner.close();

        Scanner startingInput = new Scanner(System.in);
        System.out.print("Enter starting city: ");
        String source = startingInput.nextLine();
        int sourceIndex = findIndex(cities,source);
        while(sourceIndex == -1){
            System.out.printf("City named '%s' not found. Please enter a valid city name.%n",source);
            System.out.print("Enter starting city: ");
            source = startingInput.nextLine();
            sourceIndex = findIndex(cities,source);
        }
        //getting inputs of source and arrive city names if name is inappropriate ask user to write again
        Scanner goingInput = new Scanner(System.in);
        System.out.print("Enter arriving city: ");
        String arrive = goingInput.nextLine();
        int arriveIndex = findIndex(cities,arrive);
        while(arriveIndex == -1){
            System.out.printf("City named '%s' not found. Please enter a valid city name.%n",arrive);
            System.out.print("Enter arriving city: ");
            arrive = goingInput.nextLine();
            arriveIndex = findIndex(cities,arrive);
        }
        scanner.close();

        boolean[] visitedIndex = new boolean[size]; // it shows is that visited or not initially false
        double[] distances = new double[size]; // it shows distances of source to other cities
        int[] previousCityIndex = new int[size]; // it helps to keep path order
        for(int i=0;i<size;i++) {//giving initial value to lists
            previousCityIndex[i] = -1;
            distances[i] = Double.MAX_VALUE;
        }

        distances[sourceIndex] = 0;  //distance of itself is 0
        // main part of dijkstra algorithm
        for (int i = 0; i < size-1; i++) { // checks every city distance
            int u = calc_min_distance(distances, visitedIndex); // it calls method to find the vertex with the minimum distance among the vertices that have not been visited yet.
            if (u != -1) { // if it is -1 it means there is no path
                visitedIndex[u] = true; //it marks this vertex as visited to avoid revisiting it in subsequent iterations.
                for (int v = 0; v < size; v++) { //This loop iterates over all adjacent vertices 'v' of the current vertex 'u'.
                    if (!visitedIndex[v] && distanceMatrix[u][v] != 0 && (distances[u] + distanceMatrix[u][v] < distances[v])) { //checks appropriate conditions
                        distances[v] = distances[u] + distanceMatrix[u][v]; // updating new distance if conditions are met
                        previousCityIndex[v] = u; // updating last node
                    }
                }
            }
        }
        if (distances[arriveIndex] != Double.MAX_VALUE) { // checks the path exist or not
            List<Integer> path = getPath(previousCityIndex, arriveIndex); // getting list of path cities indexes
            List<String> pathCity = new ArrayList<>(); //this list shows name of cities of path by for loop
            for (Integer integer : path) { //converting path city numbers to city names array
                String name = cities.get(integer).cityName;
                pathCity.add(name);
            }
            //it writes path and distance on console
            System.out.printf("Total Distance: %.2f. ",distances[arriveIndex]);
            System.out.print("Path: " + String.join(" -> ", pathCity));
            //StdDraw part
            int width = 2377;
            int height = 1055;
            StdDraw.setCanvasSize(width/2, height/2);
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
            StdDraw.picture(width / 2.0, height / 2.0, "map.png", width, height);
            StdDraw.enableDoubleBuffering();
            //setting scale and drawing picture of map on canvas

            for(City city:cities) { //writing names of cities in canvas
                if(pathCity.contains(city.cityName)){ //it checks whether city is on the path if yes writing with blue color
                    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                }
                else{
                    StdDraw.setPenColor(StdDraw.GRAY);
                }
                StdDraw.setFont(new Font("Serif", Font.BOLD, 13));
                StdDraw.filledCircle(city.x,city.y,5);
                StdDraw.text(city.x, city.y+15, city.cityName);
                //setting font and writing names of cities
            }
                // to write connections opening file again
            File file2 = new File("city_connections.txt");
            Scanner scanner2 = new Scanner(file2);
            while (scanner2.hasNextLine()) {
                String line = scanner2.nextLine();
                String[] parts = line.split(",");
                String connection1 = parts[0];
                String connection2 = parts[1];
                int index1 = findIndex(cities, connection1);
                int index2 = findIndex(cities, connection2);
                for(int i = 0;i<path.size()-1;i++){ //it checks whether the connection is on the path
                    if((path.get(i)==index1 && path.get(i+1)==index2)|(path.get(i)==index2 && path.get(i+1)==index1)){ //it checks whether is path on the going path
                        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);//if yes color and pen radius change
                        StdDraw.setPenRadius(0.009);
                        break;
                    }
                    else{
                        StdDraw.setPenColor(StdDraw.GRAY);
                        StdDraw.setPenRadius(0.002);
                    }
                }
                StdDraw.line(cities.get(index1).x,cities.get(index1).y,cities.get(index2).x,cities.get(index2).y); // drawing lines between cities which have connection.

            }
            scanner.close();
            StdDraw.show();
        } else {
            System.out.println("No path could be found."); // if there is no path code is not writing stdDraw
        }


    }

    /**
     *to find the index of a city with a known name
     * @param cities city array
     * @param cityName name of cities which I want to find index
     * @return creation index of city
     */
    public static int findIndex(List<City> cities, String cityName) {
        for (int i=0;i<cities.size();i++){
            City city = cities.get(i);
            if (city.getName().equalsIgnoreCase(cityName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     *calculating distances of cities
     * @param distances list of source city distance to other cities
     * @param visitedIndex checks is it visited before
     * @return returning index of city which has min distance
     */
    public static int calc_min_distance(double[] distances, boolean[] visitedIndex) {
        double minDistance = Double.MAX_VALUE;
        int minDistanceVertex = -1;
        for (int i = 0; i < distances.length; i++) {
            if (!visitedIndex[i] && distances[i] < minDistance) {
                minDistance = distances[i];
                minDistanceVertex = i;
            }
        }
        return minDistanceVertex;
    }

    /**
     *helps to print the path .it keeps the names of the cities in an array in order
     * @param prev list of path order
     * @param dest arriving city index
     * @return list of city index of going path
     */
    public static List<Integer> getPath(int[] prev, int dest) {
        List<Integer> path = new ArrayList<>();
        buildPath(prev, dest, path);
        return path;
    }

    /**
     *helper method of getPath it works recursively to find path
     * @param prev list of path order
     * @param dest arriving city index
     * @param path my list of path initially free
     */
    public static void buildPath(int[] prev, int dest, List<Integer> path) {
        if (prev[dest] != -1) {
            buildPath(prev, prev[dest], path);
            path.add(dest);
        } else {
            path.add(dest);
        }
    }
}