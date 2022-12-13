package day12;


import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Solution {

    public static final String ANSI_BOLD = "\033[0;1m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String... input) {
        var solver = new Solver();
        String stringData = readData(input[0]);

        System.out.println("\nexecuting part 1");
        Instant start1 = Instant.now();
        System.out.format(ANSI_BOLD + "result of part 1: %s\n" + ANSI_RESET, solver.part1(stringData));
        Instant end1 = Instant.now();
        System.out.println("Time taken: " + Duration.between(start1, end1));

        System.out.println("\nexecuting part 2");
        Instant start2 = Instant.now();
        System.out.format(ANSI_BOLD + "result of part 2: %s\n" + ANSI_RESET, solver.part2(stringData));
        Instant end2 = Instant.now();
        System.out.println("Time taken: " + Duration.between(start2, end2));
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

record TravelPoint(TravelPoint previous, Point xy, int fuelCost) {
    void printJourney() {
        System.out.println(xy);
        if(previous != null) {
            previous.printJourney();
        }
    }
}

class Solver {
    Object part1(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var height = new HashMap<Point, Integer>();

        var S = new Point(0, 0);
        var E = new Point(0, 0);

        for(int i = 0; i < lines.length; i++) {
            for(int j = 0; j < lines[i].length(); j++) {
                if(lines[i].charAt(j) == 'S') {
                    height.put(new Point(i,j), (int) 'a' );
                    S = new Point(i, j);
                } else if(lines[i].charAt(j) == 'E') {
                    height.put(new Point(i,j), (int) 'z' );
                    E = new Point(i, j);
                } else {
                    height.put(new Point(i,j), (int) lines[i].charAt(j) );
                }
            }
        }
        Map<Point, Integer> alreadyBeen = new HashMap<>();
        return extracted(S, height, E, alreadyBeen);
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var height = new HashMap<Point, Integer>();

        var E = new Point(0, 0);
        var startingPoints = new ArrayList<Point>();

        for(int i = 0; i < lines.length; i++) {
            for(int j = 0; j < lines[i].length(); j++) {
                if(lines[i].charAt(j) == 'S') {
                    height.put(new Point(i,j), (int) 'a' );
                } else if(lines[i].charAt(j) == 'E') {
                    height.put(new Point(i,j), (int) 'z' );
                    E = new Point(i, j);
                } else {
                    height.put(new Point(i,j), (int) lines[i].charAt(j) );
                }
                if(height.get(new Point(i,j)) == (int) 'a') {
                    startingPoints.add( new Point(i, j));
                }
            }
        }

        final var EFinal = E;
        Map<Point, Integer> alreadyBeen = new HashMap<>();

        return startingPoints.stream()
                .map(S -> extracted(S, height, EFinal, alreadyBeen))
                .mapToInt(i -> i)
                .sorted()
                .findFirst().getAsInt();
    }

    private int extracted(Point S, HashMap<Point, Integer> height, Point E, Map<Point, Integer> alreadyBeen) {
        PriorityQueue<TravelPoint> pq = new PriorityQueue<>(10000, Comparator.comparingInt(TravelPoint::fuelCost));
        pq.add(new TravelPoint(null, S, 0));
        alreadyBeen.put(S, 0);
        TravelPoint here;

        try {
            while(!(here = pq.remove()).xy().equals(E)) {
                var xy = here.xy();
                addPoint(height, alreadyBeen, pq, here, new Point((int) xy.getX() +1, (int) xy.getY()));
                addPoint(height, alreadyBeen, pq, here, new Point((int) xy.getX(), (int) xy.getY() +1));
                addPoint(height, alreadyBeen, pq, here, new Point((int) xy.getX() -1, (int) xy.getY()));
                addPoint(height, alreadyBeen, pq, here, new Point((int) xy.getX(), (int) xy.getY() -1));
            }
        } catch (NoSuchElementException e) {
            return Integer.MAX_VALUE;
        }

        return here.fuelCost();
    }

    void addPoint(HashMap<Point, Integer> height, Map<Point, Integer> alreadyBeen, PriorityQueue<TravelPoint> pq, TravelPoint soFar, Point xy) {
        if (!height.containsKey(xy))
            return;
        var heightSoFar = height.get(soFar.xy());
        var heightXY = height.get(xy);
        if(!alreadyBeen.containsKey(xy) || alreadyBeen.get(xy) > soFar.fuelCost() + 1) {
            if(heightSoFar +1>=heightXY ) {
                pq.add(new TravelPoint(soFar, xy, soFar.fuelCost() + 1));
                alreadyBeen.put(xy, soFar.fuelCost() + 1);
            }
        }
    }
}

class Helper {

    public static List<Integer> extractInts(String stringData) {
        Pattern p = Pattern.compile("([\\d]+)");
        return p.matcher(stringData).results().map(MatchResult::group)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public static Set<Character> stringToCharSet(String s) {
        return new HashSet<>(s.chars().mapToObj(e->(char)e).toList());
    }

    public static List<Character> stringToList(String s) {
        return new ArrayList<>(s.chars().mapToObj(e->(char)e).toList());
    }
}