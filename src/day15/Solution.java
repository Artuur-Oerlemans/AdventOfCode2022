package day15;

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
        try {
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
        int y = 2000000;
        var visiblePointsLine = new HashSet<Point>();
        var beacons = new HashSet<Point>();
        Stream.of(lines)
                .map(Helper::extractInts)
                .forEach(l -> beacons.add(new Point(l.get(2), l.get(3))));
        Stream.of(lines)
                .map(Helper::extractInts)
                .forEach(l -> calcVisiblePointsSensor(new Point(l.get(0), l.get(1)), new Point(l.get(2), l.get(3)), y, visiblePointsLine));

        var noBeacons = new HashSet<>(visiblePointsLine);
        noBeacons.removeAll(beacons);
        return noBeacons.size();
    }

    void calcVisiblePointsSensor(Point sensor, Point beacon, int y, HashSet<Point> visiblePointsLine) {
        int distance = calcDistance(sensor, beacon);
        int xLeeway = distance - Math.abs(sensor.y - y);
        for (int x = sensor.x - xLeeway; x <= sensor.x + xLeeway; x++) {
            visiblePointsLine.add(new Point(x, y));
        }
    }

    int calcDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        int maxCoor = 4000000;
        var borderIntersections = new HashSet<Point>();
        var sensors = Stream.of(lines)
                .map(Helper::extractInts)
                .map(l -> new Sensor(
                        new Point(l.get(0), l.get(1)),
                        calcDistance(new Point(l.get(0), l.get(1)), new Point(l.get(2), l.get(3)))
                ))
                .toList();

        for(int i = 0; i < sensors.size(); i++) {
            for(int j = i + 1; j < sensors.size(); j++) {
                calcBorderIntersections(sensors.get(i), sensors.get(j), borderIntersections);
            }
        }

        for (var point : borderIntersections) {
            if (0 > point.x || point.x > maxCoor || 0 > point.y || point.y > maxCoor) {
                continue;
            }
            if(notSeen(point, sensors)) {
                System.out.println(point);
                return 4000000L * point.x + (long)point.y;
            }
        }

        return -1;
    }

    void calcBorderIntersections(Sensor sensor1, Sensor sensor2, HashSet<Point> borderIntersections) {
        int distanceBetween = calcDistance(sensor1.location(), sensor2.location());

        if(distanceBetween == sensor1.range() + sensor2.range() + 2) {
            int toBorder1 = sensor1.range() + 1;
            int dirX = (int) Math.signum(sensor2.location().x - sensor1.location().x);
            int dirY = (int) Math.signum(sensor2.location().y - sensor1.location().y);

            int startDistanceTo2 = calcDistance(new Point(sensor1.location().x, sensor1.location().y + dirY * toBorder1), sensor2.location());
            int startDx;
            if(startDistanceTo2 == sensor2.range() + 1) {
                startDx = 0;
            } else {
                startDx = (startDistanceTo2 - (sensor2.range() + 1))/2;
            }

            int endDistanceTo2 = calcDistance(new Point(sensor1.location().x + dirX * toBorder1, sensor1.location().y), sensor2.location());
            int endDx;
            if(endDistanceTo2 == sensor2.range() + 1) {
                endDx = toBorder1;
            } else {
                endDx = toBorder1 - (endDistanceTo2 - (sensor2.range() + 1))/2;
            }

            for (int dx = startDx; dx <= endDx; dx++) {
                var dy = toBorder1 - dx;
                borderIntersections.add(new Point(sensor1.location().x + dirX * dx, sensor1.location().y + dirY * dy));
            }
        }
    }

    boolean notSeen(Point point, List<Sensor> sensors) {
        for (var sensor : sensors) {
            int distance = calcDistance(point, sensor.location());
            if (distance <= sensor.range()) {
                return false;
            }
        }
        return true;
    }
}

record Sensor(Point location, int range) {}

class Helper {

    public static List<Integer> extractInts(String stringData) {
        Pattern p = Pattern.compile("(-?[\\d]+)");
        return p.matcher(stringData).results().map(MatchResult::group)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public static Set<Character> stringToCharSet(String s) {
        return new HashSet<>(s.chars().mapToObj(e -> (char) e).toList());
    }

    public static List<Character> stringToList(String s) {
        return new ArrayList<>(s.chars().mapToObj(e -> (char) e).toList());
    }
}