package day16;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        var valveMap = Stream.of(lines).map(this::createValve).collect(Collectors.toMap(Valve::name, v->v));
        var canBeOpened = valvesThatCanBeOpened(valveMap);
        var shortestPath = CalculateShortestPaths(valveMap, canBeOpened);

        int highScore = 0;
        var journeys = new LinkedList<Journey>();
        journeys.add(new Journey(0, "AA", new HashSet<>(), 0));
        while(journeys.size() > 0) {
            var journey = journeys.remove();
            var canBeOpenedHere = new HashSet<>(canBeOpened);
            canBeOpenedHere.removeAll(journey.opened());
            int flowPerMinute = 0;
            for(var flowing: journey.opened()) {
                flowPerMinute += valveMap.get(flowing).flowRate();
            }
            for(var dest: canBeOpenedHere) {
                int travelTime = shortestPath.get(journey.location() + dest) + 1;
                if(journey.time() + travelTime< 30) {

                    var nowOpen = new HashSet<>(journey.opened());
                    nowOpen.add(dest);
                    journeys.add(new Journey(
                            journey.time() + travelTime,
                            dest,
                            nowOpen,
                            journey.score() + travelTime * flowPerMinute));
                }
            }

            int scoreIfDoNothing = journey.score() + (30 - journey.time()) * flowPerMinute;
            if(scoreIfDoNothing > highScore) {
                highScore = scoreIfDoNothing;
            }
        }

        return highScore;
    }

    Set<String> valvesThatCanBeOpened(Map<String,Valve> valveMap) {
        var result = new HashSet<String>();
        for(var valve : valveMap.values()) {
            if(valve.flowRate() > 0) {
                result.add(valve.name());
            }
        }
        return result;
    }

    HashMap<String, Integer> CalculateShortestPaths(Map<String,Valve> valveMap, Set<String> canBeOpened) {
        var shortestPath = new HashMap<String, Integer>();
        var startingValves = new HashSet<>(canBeOpened);
        startingValves.add("AA");
        for(var valveName: startingValves) {
            var valve = valveMap.get(valveName);
            for(var dest: valveMap.values()) {
                shortestPath.put(valveName + dest.name(), 999);
            }
            shortestPath.put(valveName + valveName, 0);
            PriorityQueue<Journey> pq = new PriorityQueue<>(100, Comparator.comparingInt(Journey::time));
            pq.add(new Journey(0, valveName, null, 0));

            while(pq.size() >0) {
                Journey here = pq.remove();
                for(var destName: valveMap.get(here.location()).leads()) {
                    var routeName = valveName + destName;
                    if(here.time() + 1 < 30 && shortestPath.get(routeName) > here.time() + 1) {
                        shortestPath.put(routeName, here.time() + 1);
                        pq.add(new Journey(here.time() + 1, destName, null, 0));
                    }
                }
            }
        }
        return shortestPath;
    }

    Valve createValve(String valveString) {
        int flowRate = Helper.extractInts(valveString).get(0);
        Pattern p = Pattern.compile("([A-Z]{2})");
        List<String> names = p.matcher(valveString).results().map(MatchResult::group)
                .collect(Collectors.toList());
        return new Valve(names.get(0), flowRate, names.subList(1,names.size()));
    }

    Object part2First(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var valveMap = Stream.of(lines).map(this::createValve).collect(Collectors.toMap(Valve::name, v->v));
        var canBeOpened = valvesThatCanBeOpened(valveMap);
        var shortestPath = CalculateShortestPaths(valveMap, canBeOpened);

        int highScore = 0;
        var journeys = new Stack<JourneyWithElephant>();
        journeys.add(new JourneyWithElephant(new int[]{0,0}, new String[]{"AA","AA"}, new HashMap<>()));
        while(journeys.size() > 0) {
            var journey = journeys.pop();
            var canBeOpenedNow = new HashSet<>(canBeOpened);
            canBeOpenedNow.removeAll(journey.opened().keySet());
            for(var dest: canBeOpenedNow) {
                int travelTime0 = shortestPath.get(journey.locations()[0] + dest) + 1;
                if(journey.times()[0] + travelTime0< 26) {

                    var nowOpen = new HashMap<>(journey.opened());
                    nowOpen.put(dest, journey.times()[0] + travelTime0);
                    journeys.push(new JourneyWithElephant(
                            new int[]{journey.times()[0] + travelTime0, journey.times()[1]},
                            new String[]{dest, journey.locations()[1]},
                            nowOpen));
                }

                int travelTime1 = shortestPath.get(journey.locations()[1] + dest) + 1;
                if(journey.times()[1] + travelTime1< 26) {

                    var nowOpen = new HashMap<>(journey.opened());
                    nowOpen.put(dest, journey.times()[1] + travelTime1);
                    journeys.push(new JourneyWithElephant(
                            new int[]{journey.times()[0], journey.times()[1] + travelTime1},
                            new String[]{journey.locations()[0], dest},
                            nowOpen));
                }
            }

            int scoreIfDoNothing = 0;
            for(var flowingEntry: journey.opened().entrySet()) {
                scoreIfDoNothing += (26 - flowingEntry.getValue()) * valveMap.get(flowingEntry.getKey()).flowRate();
            }

            if(scoreIfDoNothing > highScore) {
                highScore = scoreIfDoNothing;
                System.out.println(highScore);
            }
        }

        return highScore;
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var valveMap = Stream.of(lines).map(this::createValve).collect(Collectors.toMap(Valve::name, v->v));
        var canBeOpened = valvesThatCanBeOpened(valveMap);
        var shortestPath = CalculateShortestPaths(valveMap, canBeOpened);

        var scoreForOpened = new HashMap<String, Score>();
        int highScore = 0;
        var journeys = new LinkedList<Journey>();
        journeys.add(new Journey(0, "AA", new HashSet<>(), 0));
        while(journeys.size() > 0) {
            var journey = journeys.remove();
            var canBeOpenedHere = new HashSet<>(canBeOpened);
            canBeOpenedHere.removeAll(journey.opened());
            int flowPerMinute = 0;
            for(var flowing: journey.opened()) {
                flowPerMinute += valveMap.get(flowing).flowRate();
            }
            for(var dest: canBeOpenedHere) {
                int travelTime = shortestPath.get(journey.location() + dest) + 1;
                if(journey.time() + travelTime< 26) {

                    var nowOpen = new HashSet<>(journey.opened());
                    nowOpen.add(dest);
                    journeys.add(new Journey(
                            journey.time() + travelTime,
                            dest,
                            nowOpen,
                            journey.score() + travelTime * flowPerMinute));
                }
            }

            int scoreIfDoNothing = journey.score() + (26 - journey.time()) * flowPerMinute;

            String openedStr = String.join(",", journey.opened().stream().sorted().toList());
            if(!scoreForOpened.containsKey(openedStr) || scoreForOpened.get(openedStr).score() < scoreIfDoNothing) {
                scoreForOpened.put(openedStr, new Score(journey.opened(), scoreIfDoNothing));
            }
        }

        System.out.println(scoreForOpened.size());
        var scoreForOpenedList = new ArrayList<>(scoreForOpened.values());
        for(int i = 0; i < scoreForOpenedList.size(); i++) {
            var score1 = scoreForOpenedList.get(i);
            for(int j = i + 1; j < scoreForOpenedList.size(); j++) {
                var score2 = scoreForOpenedList.get(j);
                var intersection = new HashSet<>(score1.opened());
                intersection.retainAll(score2.opened());
                if (intersection.isEmpty() && score1.score() + score2.score() > highScore) {
                    highScore = score1.score() + score2.score();
                }
            }
        }

        return highScore;
    }
}

record Score(Set<String> opened, int score) {}

record JourneyWithElephant(int[] times, String[] locations, Map<String, Integer> opened) {}

record Journey(int time, String location, Set<String> opened, int score) {}

record Valve(String name, int flowRate, List<String> leads) {}

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