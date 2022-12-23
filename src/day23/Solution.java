package day23;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
        var elfs = new HashSet<Point>();
        for(int j = 0; j < lines.length; j++) {
            for(int i = 0; i < lines[j].length(); i++) {
                if(lines[j].charAt(i) == '#') {
                    elfs.add(new Point(i, j));
                }
            }
        }

        for(int round = 0; round < 10; round++) {
//            visualize(elfs, round);
            elfs = updatePosition(elfs, round);
        }
        // 4336
        return calcScore(elfs);
    }

    void visualize(HashSet<Point> elfs, int round) {
        int minx = 100_000;
        int maxx = -100_000;
        int miny = 100_000;
        int maxy = -100_000;
        for (var p: elfs) {
            minx = Math.min(minx, p.x);
            miny = Math.min(miny, p.y);
            maxx = Math.max(maxx, p.x);
            maxy = Math.max(maxy, p.y);
        }
        System.out.println("round: " + round);
        for(int j = miny; j <= maxy; j++) {
            for(int i = minx; i <= maxx; i++) {
                if(elfs.contains(new Point(i, j))) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }

    int calcScore(Set<Point> elfs) {
        int minx = 100_000;
        int maxx = -100_000;
        int miny = 100_000;
        int maxy = -100_000;
        for (var p: elfs) {
            minx = Math.min(minx, p.x);
            miny = Math.min(miny, p.y);
            maxx = Math.max(maxx, p.x);
            maxy = Math.max(maxy, p.y);
        }
        return (maxx - minx +1) * (maxy - miny +1) - elfs.size();
    }

    private HashSet<Point> updatePosition(Set<Point> elfs, int round) {
        var chosenDest = new ConcurrentHashMap<Point, Integer>();
        elfs.parallelStream().forEach(elf -> chosenDest.merge(pickDest(elf, elfs, round), 1, Integer::sum));
        var newElfs = new HashSet<Point>();
        for(var elf: elfs) {
            var dest = pickDest(elf, elfs, round);
            if(chosenDest.get(dest) == 1) {
                newElfs.add(dest);
            } else if(chosenDest.get(dest) > 1) {
                newElfs.add(elf);
            } else {
                throw new RuntimeException();
            }
        }
        return newElfs;
    }

    Point pickDest(Point elf, Set<Point> elfs, int round) {
        Point[] directions = {new Point(0, -1), new Point(0,1), new Point(-1, 0), new Point(1,0)};
        Point[] allDir = {new Point(0, -1), new Point(0,1), new Point(-1, 0), new Point(1,0), new Point(-1, -1), new Point(1, -1), new Point(-1, 1), new Point(1, 1)};

        boolean allEmpty = true;
        for(var dir: allDir) {
            var real = add(elf, dir);
            if(elfs.contains(real)) {
                allEmpty = false;
                break;
            }
        }
        if(allEmpty) {
            return elf;
        }

        main: for(int d = 0; d < 4; d++) {
            for(var dir: checkDirection(directions[(round + d) % 4])) {
                var real = add(elf, dir);
                if(elfs.contains(real)) {
                    continue main;
                }
            }
            return add(elf, directions[(round + d) % 4]);
        }
        return elf;
    }

    List<Point> checkDirection(Point dir) {
        if(dir.x == 0) {
            return List.of(dir, new Point(-1, dir.y), new Point(1, dir.y));
        } else if(dir.y == 0)  {
            return List.of(dir, new Point(dir.x, -1), new Point(dir.x, 1));
        }
        throw new RuntimeException();
    }

    Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var elfs = new HashSet<Point>();
        for(int j = 0; j < lines.length; j++) {
            for(int i = 0; i < lines[j].length(); i++) {
                if(lines[j].charAt(i) == '#') {
                    elfs.add(new Point(i, j));
                }
            }
        }
        HashSet<Point> newElfs = elfs;
        int round = 0;
        do{
            elfs = newElfs;
            newElfs = updatePosition(elfs, round);
            round++;
            elfs.removeAll(newElfs);
        } while (elfs.size() > 0);

        // 1005
        return round;
    }
}

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