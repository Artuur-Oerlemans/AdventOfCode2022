package day24;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
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

class Solver {
    Object part1(String stringData) {
        var lines = stringData.split(System.lineSeparator());

        var initialState = new HashMap<Point, ArrayList<Character>>();
        int maxx = lines[0].length();
        int maxy = lines.length;
        var goalPoint = new Point(maxx -2, maxy -1);
        for(int j = 0; j < lines.length; j++) {
            for(int i = 0; i < lines[j].length(); i++) {
                if(lines[j].charAt(i) != '.') {
                    var l = new ArrayList<Character>();
                    l.add(lines[j].charAt(i));
                    initialState.merge(new Point(i, j), l, (a,b) -> {a.addAll(b); return a;});
                }
            }
        }
        var states = new ArrayList<>(List.of(initialState));

        var pq = new PriorityQueue<>(1000, Comparator.comparingInt(TravelPoint::time));
        var alreadyBeen = new HashSet<TravelPoint>();
        var startPoint = new TravelPoint(new Point(1, 0), 0, false, false);
        pq.add(startPoint);
        while( !goalPoint.equals(pq.peek().loc())) {
            var tp = pq.remove();
            while(states.size() <= tp.time() +1) {
                addState(states, maxx, maxy);
            }

            for(var neighbour: getNeighbours(tp)) {
                if(!alreadyBeen.contains(neighbour) && !states.get(neighbour.time()).containsKey(neighbour.loc()) && neighbour.loc().y >=0) {
                    pq.add(neighbour);
                    alreadyBeen.add(neighbour);
                }
            }
        }
        return pq.remove().time();
    }

    void addState(List<HashMap<Point, ArrayList<Character>>> states, int maxx, int maxy) {
        var last = states.get(states.size() -1);
        var newState = new HashMap<Point, ArrayList<Character>>();
        BiFunction<ArrayList<Character>, ArrayList<Character>, ArrayList<Character>> combineList = (a, b) -> {
            a.addAll(b);
            return a;
        };
        for(var entry: last.entrySet()) {
            for(var c : entry.getValue()) {
                switch (c) {
                    case '#' -> newState.put(entry.getKey(), new ArrayList<>(List.of('#')));
                    case '^' -> newState.merge(moveBliz(entry.getKey(), new Point(0, -1), maxx, maxy), new ArrayList<>(List.of(c)), combineList);
                    case '>' -> newState.merge(moveBliz(entry.getKey(), new Point(1, 0), maxx, maxy), new ArrayList<>(List.of(c)), combineList);
                    case 'v' -> newState.merge(moveBliz(entry.getKey(), new Point(0, 1), maxx, maxy), new ArrayList<>(List.of(c)), combineList);
                    case '<' -> newState.merge(moveBliz(entry.getKey(), new Point(-1, 0), maxx, maxy), new ArrayList<>(List.of(c)), combineList);
                }
            }
        }
        states.add(newState);
    }

    void visualize(TravelPoint tp, List<HashMap<Point, ArrayList<Character>>> states, int maxx, int maxy) {
        var state = states.get(tp.time());
        for(int y = 0; y < maxy; y++) {
            for(int x = 0; x < maxx; x++) {
                var p = new Point(x, y);
                if(tp.loc().equals(p)){
                    System.out.print('E');
                } else if (state.containsKey(p)){
                    if(state.get(p).size() > 1){
                        System.out.print(state.get(p).size());
                    } else {
                        System.out.print(state.get(p).get(0));
                    }
                } else {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
        System.out.println("====" + tp.time() + "=============");
        Console cnsl = System.console();
        cnsl.readLine("waiting for you : ");
    }

    Point moveBliz(Point p, Point dir, int maxx, int maxy) {
        var imagine = add(p, dir);

        if(imagine.x == 0) {
            return new Point(maxx -2, p.y);
        }
        if(imagine.x == maxx -1) {
            return new Point(1, p.y);
        }
        if(imagine.y == 0) {
            return new Point(p.x, maxy -2);
        }
        if(imagine.y == maxy -1) {
            return new Point(p.x, 1);
        }
        return imagine;
    }

    List<TravelPoint> getNeighbours(TravelPoint tp) {
        var time = tp.time() +1;
        var neighbours = new ArrayList<TravelPoint>();
        neighbours.add(new TravelPoint(tp.loc(), time, tp.reachedDest(), tp.returned()));
        neighbours.add(new TravelPoint(add(tp.loc(),new Point(1, 0)), time, tp.reachedDest(), tp.returned()));
        neighbours.add(new TravelPoint(add(tp.loc(),new Point(-1, 0)), time, tp.reachedDest(), tp.returned()));
        neighbours.add(new TravelPoint(add(tp.loc(),new Point(0, 1)), time, tp.reachedDest(), tp.returned()));
        neighbours.add(new TravelPoint(add(tp.loc(),new Point(0, -1)), time, tp.reachedDest(), tp.returned()));
        return neighbours;
    }

    Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());

        var initialState = new HashMap<Point, ArrayList<Character>>();
        int maxx = lines[0].length();
        int maxy = lines.length;
        var goalPoint = new Point(maxx -2, maxy -1);
        var startPoint = new Point(1, 0);
        for(int j = 0; j < lines.length; j++) {
            for(int i = 0; i < lines[j].length(); i++) {
                if(lines[j].charAt(i) != '.') {
                    var l = new ArrayList<Character>();
                    l.add(lines[j].charAt(i));
                    initialState.merge(new Point(i, j), l, (a,b) -> {a.addAll(b); return a;});
                }
            }
        }
        var states = new ArrayList<>(List.of(initialState));

        var pq = new PriorityQueue<>(1000, Comparator.comparingInt(TravelPoint::time));
        var alreadyBeen = new HashSet<TravelPoint>();
        var startTP = new TravelPoint(startPoint, 0, false, false);
        pq.add(startTP);
        while( !goalPoint.equals(pq.peek().loc()) || !pq.peek().returned()) {
            var tp = pq.remove();
            while(states.size() <= tp.time() +1) {
                addState(states, maxx, maxy);
            }

            for(var neighbour: getNeighbours(tp)) {
                if(!alreadyBeen.contains(neighbour) && !states.get(neighbour.time()).containsKey(neighbour.loc()) && neighbour.loc().y >=0 && neighbour.loc().y < maxy) {
                    if(goalPoint.equals(neighbour.loc())) {
                        neighbour = new TravelPoint(neighbour.loc(), neighbour.time(), true, neighbour.returned());
                    } else if(startPoint.equals(neighbour.loc()) && neighbour.reachedDest()) {
                        neighbour = new TravelPoint(neighbour.loc(), neighbour.time(), true, true);
                    }
                    pq.add(neighbour);
                    alreadyBeen.add(neighbour);
                }
            }
        }
        return pq.remove().time();
    }
}

record TravelPoint(Point loc, int time, boolean reachedDest, boolean returned) {}

class Helper {

    public static List<Integer> extractInts(String stringData) {
        Pattern p = Pattern.compile("(-?[\\d]+)");
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