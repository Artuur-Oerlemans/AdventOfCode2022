/**
 * Not an advent of code challenge, just an experiment in preparation.
 */
package shortestPath;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class Solution {

    public static final String ANSI_BOLD = "\033[0;1m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String... input) {
        var solver = new Solver();
        String stringData = readData(input[0]);

        System.out.println("\nexecuting part 1");
        Instant start1 = Instant.now();
        System.out.format(ANSI_BOLD + "result of part 1: %s\n", solver.part1(stringData) + ANSI_RESET);
        Instant end1 = Instant.now();
        System.out.println("Time taken: " + Duration.between(start1, end1) + "\n");
    }

    private static String readData(String fileName) {
        System.out.format("Reading file: %s\n", fileName);
        Path path = Path.of(".", fileName);
        try{
            return Files.readString(path);
        } catch (IOException e) {
            System.err.format("An exception occurred while reading file '%s' ", fileName);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

record TravelPoint(TravelPoint travelPoint, Point xy, int fuelCost) {
    void printJourney() {
        System.out.println(xy);
        if(travelPoint != null) {
            travelPoint.printJourney();
        }
    }
}

class Solver {
    Object part1(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var costs = new HashMap<Point, Integer>();

        for(int i = 0; i < lines.length; i++) {
            for(int j = 0; j < lines[i].length(); j++) {
                costs.put(new Point(i,j), Integer.valueOf(lines[i].charAt(j) + ""));
            }
        }
        PriorityQueue<TravelPoint> pq = new PriorityQueue<>(100, Comparator.comparingInt(TravelPoint::fuelCost));
        Map<Point, Integer> alreadyBeen = new HashMap<>();
        pq.add(new TravelPoint(null, new Point(0, 0), 0));
        alreadyBeen.put(new Point(0, 0), 0);
        TravelPoint here;

        while(!(here = pq.remove()).xy().equals(new Point(lines.length -1, lines[0].length() -1))) {
            var xy = here.xy();
            addPoint(costs, alreadyBeen, pq, here, new Point((int) xy.getX() +1, (int) xy.getY()));
            addPoint(costs, alreadyBeen, pq, here, new Point((int) xy.getX(), (int) xy.getY() +1));
            addPoint(costs, alreadyBeen, pq, here, new Point((int) xy.getX() -1, (int) xy.getY()));
            addPoint(costs, alreadyBeen, pq, here, new Point((int) xy.getX(), (int) xy.getY() -1));
        }
        System.out.println(here);
        here.printJourney();

        return "reached";
    }

    void addPoint(HashMap<Point, Integer> costs, Map<Point, Integer> alreadyBeen, PriorityQueue<TravelPoint> pq, TravelPoint soFar, Point xy) {
        if (!costs.containsKey(xy))
            return;
        var cost = costs.get(xy);
        if(!alreadyBeen.containsKey(xy) || alreadyBeen.get(xy) > soFar.fuelCost() + cost) {
            pq.add(new TravelPoint(soFar, xy, soFar.fuelCost() + cost));
            alreadyBeen.put(xy, soFar.fuelCost() + cost);
        }
    }
}

class Helper {
    public static Set<Character> stringToCharSet(String s) {
        return new HashSet<>(s.chars().mapToObj(e->(char)e).toList());
    }

    public static List<Character> stringToList(String s) {
        return new ArrayList<>(s.chars().mapToObj(e->(char)e).toList());
    }
}