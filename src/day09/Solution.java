package day09;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

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
        var data = Stream.of(lines).toList();

        var visited = new HashSet<Point>();

        int hx = 0;
        int hy = 0;
        int tx = 0;
        int ty = 0;

        visited.add(new Point(tx, ty));

        for(var line: data) {
            var values = line.split(" ");

            for(int i = 0; i < Integer.parseInt(values[1]); i++) {
                switch (values[0]) {
                    case "R" -> hx += 1;
                    case "L" -> hx -= 1;
                    case "U" -> hy += 1;
                    case "D" -> hy -= 1;
                }

                if(Math.abs(hx - tx) > 1) {
                    tx += (hx - tx) / 2;
                    if(Math.abs(hy - ty) == 1) {
                        ty += (hy - ty);
                    }
                }
                else if(Math.abs(hy - ty) > 1) {
                    ty += (hy - ty) / 2;
                    if(Math.abs(hx - tx) == 1) {
                        tx += (hx - tx);
                    }
                }
                visited.add(new Point(tx, ty));
            }
        }

        return visited.size();
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var data = Stream.of(lines).toList();

        var visited = new HashSet<Point>();

        var tails = new ArrayList<int[]>();
        for(int i = 0; i < 10; i++) {
            tails.add(new int[]{0 ,0});
        }

        addVisited(tails, visited);

        for(var line: data) {

            System.out.println(line);
            var values = line.split(" ");

            for(int i = 0; i < Integer.parseInt(values[1]); i++) {
                var h = tails.get(0);
                switch (values[0]) {
                    case "R" -> h[0] += 1;
                    case "L" -> h[0] -= 1;
                    case "U" -> h[1] += 1;
                    case "D" -> h[1] -= 1;
                }

                System.out.println("h" +Arrays.toString(tails.get(0)));
                for(int j = 1; j < tails.size(); j++) {
                    var ht = tails.get(j-1);
                    var tt = tails.get(j);

                    if(Math.abs(ht[0] - tt[0]) > 1) {
                        tt[0] += Math.signum(ht[0] - tt[0]);
                        if(Math.abs(ht[1] - tt[1]) >= 1) {
                            tt[1] += Math.signum(ht[1] - tt[1]);
                        }
                    }
                    else if(Math.abs(ht[1] - tt[1]) > 1) {
                        tt[1] += Math.signum(ht[1] - tt[1]);
                        if(Math.abs(ht[0] - tt[0]) >= 1) {
                            tt[0] += Math.signum(ht[0] - tt[0]);
                        }
                    }
                }

                addVisited(tails, visited);
            }
        }

        return visited.size();
    }

    void addVisited(List<int[]> tails, Set<Point> visited) {
        visited.add(new Point(tails.get(9)[0], tails.get(9)[1]));
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