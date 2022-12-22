package day22;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        var inputs = stringData.split(System.lineSeparator() + System.lineSeparator());
        var mapLines = inputs[0].split(System.lineSeparator());
        var instructions = Helper.extractValues(inputs[1]);
        var coord = new HashMap<Point, Character>();
        for (int i = 0; i < mapLines.length; i++) {
            for (int j = 0; j < mapLines[i].length(); j++) {
                if (mapLines[i].charAt(j) != ' ') {
                    coord.put(new Point(j + 1, i + 1), mapLines[i].charAt(j));
                }
            }
        }
        var dir = new Point(1, 0);
        var loc = wrapAround(new Point(9001, 1), dir, coord);

        for (var ins : instructions) {
            if ("R".equals(ins) || "L".equals(ins)) {
                dir = turnAround(dir, ins);
            } else {
                for (int i = 0; i < Integer.parseInt(ins); i++) {
                    loc = takeStep(loc, dir, coord);
                }
            }
        }

        return calcScore(loc, dir);
    }

    Point turnAround(Point dir, String ins) {
        if ("R".equals(ins)) {
            return new Point(-dir.y, dir.x);
        } else {
            return new Point(dir.y, -dir.x);
        }
    }

    Point takeStep(Point location, Point dir, Map<Point, Character> coord) {
        var newLocation = add(location, dir);
        if (!coord.containsKey(newLocation)) {
            newLocation = wrapAround(newLocation, dir, coord);
        }
        if (coord.get(newLocation).equals('#')) {
            return location;
        } else {
            return newLocation;
        }
    }

    Point wrapAround(Point location, Point dir, Map<Point, Character> coord) {
        Point startSearch;
        if (dir.x > 0) {
            startSearch = new Point(1, location.y);
        } else if (dir.x < 0) {
            startSearch = new Point(160, location.y);
        } else if (dir.y < 0) {
            startSearch = new Point(location.x, 210);
        } else {
            startSearch = new Point(location.x, 1);
        }
        while (!coord.containsKey(startSearch)) {
            startSearch = add(startSearch, dir);
        }
        return startSearch;
    }

    Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    Object part2(String stringData) {
        var inputs = stringData.split(System.lineSeparator() + System.lineSeparator());
        var mapLines = inputs[0].split(System.lineSeparator());
        var instructions = Helper.extractValues(inputs[1]);
        var coord = new HashMap<Point, Character>();
        for (int i = 0; i < mapLines.length; i++) {
            for (int j = 0; j < mapLines[i].length(); j++) {
                if (mapLines[i].charAt(j) != ' ') {
                    coord.put(new Point(j + 1, i + 1), mapLines[i].charAt(j));
                }
            }
        }
        var dir = new Point(1, 0);
        var loc = wrapAround(new Point(9001, 1), dir, coord);

        for (var ins : instructions) {
            if ("R".equals(ins) || "L".equals(ins)) {
                dir = turnAround(dir, ins);
            } else {
                for (int i = 0; i < Integer.parseInt(ins); i++) {
                    var p = takeStepCube(loc, dir, coord);
                    loc = p.loc();
                    dir = p.dir();
                }
            }
        }

        return calcScore(loc, dir);
    }

    Pair takeStepCube(Point location, Point dir, Map<Point, Character> coord) {
        var newPair = new Pair(add(location, dir), dir);
        if (!coord.containsKey(newPair.loc())) {
            newPair = wrapAroundCube(newPair.loc(), dir, coord);
        }
        
        if (coord.get(newPair.loc()).equals('#')) {
            return new Pair(location, dir);
        } else {
            return newPair;
        }
    }

    Map<Pair, Pair> memo = new HashMap<>();

    Pair wrapAroundCube(Point loc, Point dir, Map<Point, Character> coord) {
        var start = new Pair(loc, dir);

        if(memo.containsKey(start)) {
            return memo.get(start);
        }

        Pair dest;
        if(50 < loc.x && loc.x < 101 & loc.y == 0) {
            dest = new Pair(new Point(1, loc.x -50 + 150), new Point(1, 0));
            // 1 -> 10
        } else if(100 < loc.x && loc.x < 151 & loc.y == 0) {
            dest = new Pair(new Point(loc.x - 100, 200), new Point(0, -1));
            // 2 -> 9
        }else if(loc.x == 151 & 0 < loc.y && loc.y < 51) {
            dest = new Pair(new Point(100, 151 - loc.y), new Point(-1, 0));
            // 3 -> 6
        }else if(100 < loc.x && loc.x < 151 & loc.y == 51) {
            dest = new Pair(new Point(100, loc.x -100 + 50), new Point(-1, 0));
            // 4 -> 5
        }else if(loc.x == 101 & 50 < loc.y && loc.y < 101) {
            dest = new Pair(new Point(loc.y - 50 + 100, 50), new Point(0, -1));
            // 5 -> 4
        }else if(loc.x == 101 & 100 < loc.y && loc.y < 151) {
            dest = new Pair(new Point(150, 51 - (loc.y -100)), new Point(-1, 0));
            // 6 -> 3
        }else if(50 < loc.x && loc.x < 101 & loc.y == 151) {
            dest = new Pair(new Point(50, loc.x -50 + 150), new Point(-1, 0));
            // 7 -> 8
        }else if(loc.x == 51 & 150 < loc.y && loc.y < 201) {
            dest = new Pair(new Point(loc.y - 150 + 50, 150), new Point(0, -1));
            // 8 -> 7
        }else if(0 < loc.x && loc.x < 51 & loc.y == 201) {
            dest = new Pair(new Point(loc.x + 100, 1), new Point(0, 1));
            // 9 -> 2
        }else if(loc.x == 0 & 150 < loc.y && loc.y < 201) {
            dest = new Pair(new Point(loc.y - 150 + 50, 1), new Point(0, 1));
            // 10 -> 1
        }else if(loc.x == 0 & 100 < loc.y && loc.y < 151) {
            dest = new Pair(new Point(51, 51 - (loc.y - 100)), new Point(1, 0));
            // 11 -> 14
        }else if(0 < loc.x && loc.x < 51 & loc.y == 100) {
            dest = new Pair(new Point(51, loc.x + 50), new Point(1, 0));
            // 12 -> 13
        }else if(loc.x == 50 & 50 < loc.y && loc.y < 101) {
            dest = new Pair(new Point(loc.y - 50, 101), new Point(0, 1));
            // 13 -> 12
        }else if(loc.x == 50 & 0 < loc.y && loc.y < 51) {
            dest = new Pair(new Point(1, 151 - loc.y), new Point(1, 0));
            // 14 -> 11
        } else {
            throw new RuntimeException();
        }
        debug(start, dest);
        memo.put(start, dest);
        return dest;
    }

    private void debug(Pair start, Pair dest) {
        visualize(start);
        visualize(dest);
        System.out.println(start + " --> " + dest);

        Console cnsl = System.console();
        cnsl.readLine("waiting for you : ");
    }

    void visualize(Pair p) {
        char[][] disp = {{' ', ' ', ' ', ' '}, {' ', ' ', '#', '#', ' '}, {' ', ' ', '#', ' '}, {' ', '#', '#', ' '}, {' ', '#', ' '}, {' ', ' ', ' '}};
        char symbol;
        if(p.dir().equals(new Point(1, 0))) {
            symbol = '>';
        } else if(p.dir().equals(new Point(0, 1))) {
            symbol = 'v';
        } else if(p.dir().equals(new Point(-1, 0))) {
            symbol = '<';
        } else  {
            symbol = '^';
        }
        disp[(p.loc().y -1) /50 +1][(p.loc().x -1) /50+1] = symbol;
        for(int line = 0; line < disp.length; line++) {
            for(int row = 0; row < disp[line].length; row++) {
                System.out.print(disp[line][row]);
            }
            System.out.println();
        }
        System.out.println(p.dir());
    }

    int calcScore(Point loc, Point dir) {
        var baseScore = 1000 * loc.y + 4 * loc.x;
        if (new Point(1, 0).equals(dir)) {
            return baseScore;
        } else if (new Point(0, 1).equals(dir)) {
            return baseScore + 1;
        } else if (new Point(-1, 0).equals(dir)) {
            return baseScore + 2;
        } else {
            return baseScore + 3;
        }
    }
}

record Pair(Point loc, Point dir) {}

class Helper {

    public static List<String> extractValues(String stringData) {
        Pattern p = Pattern.compile("([RL]|-?[\\d]+)");
        return p.matcher(stringData).results().map(MatchResult::group)
                .collect(Collectors.toList());
    }
}