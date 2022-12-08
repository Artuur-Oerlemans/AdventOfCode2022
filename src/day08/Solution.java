package day08;

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
        var data = Stream.of(lines)
                .map(s -> s.chars().mapToObj(c -> (char)c).map(Integer::valueOf).toList())
                .toList();
        var visibleTrees = new HashSet<Point>();
        for(int i = 0; i < data.size(); i++) {
            long heighest = data.get(i).get(0);
            visibleTrees.add(new Point(i ,0));
            for(int j = 1; j < data.get(0).size(); j++) {
                if(data.get(i).get(j) > heighest) {
                    visibleTrees.add(new Point(i ,j));
                    heighest = data.get(i).get(j);
                }
            }
        }

        for(int i = 0; i < data.get(0).size(); i++) {
            long heighest = data.get(0).get(i);
            visibleTrees.add(new Point(0 ,i));
            for(int j = 1; j < data.size(); j++) {
                if(data.get(j).get(i) > heighest) {
                    visibleTrees.add(new Point(j, i));
                    heighest = data.get(j).get(i);
                }
            }
        }

        for(int i = 0; i < data.size(); i++) {
            long heighest = data.get(i).get(data.get(0).size() -1);
            visibleTrees.add(new Point(i ,data.get(0).size() -1));
            for(int j = data.get(0).size() -2; j >=0; j--) {
                if(data.get(i).get(j) > heighest) {
                    visibleTrees.add(new Point(i ,j));
                    heighest = data.get(i).get(j);
                }
            }
        }

        for(int i = 0; i < data.get(0).size(); i++) {
            long heighest = data.get(data.size() -1).get(i);
            visibleTrees.add(new Point(data.size() -1 ,i));
            for(int j = data.size() -2; j >= 0; j--) {
                if(data.get(j).get(i) > heighest) {
                    visibleTrees.add(new Point(j, i));
                    heighest = data.get(j).get(i);
                }
            }
        }
        System.out.println(visibleTrees);
        return visibleTrees.size();
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var data = Stream.of(lines)
                .map(s -> s.chars().mapToObj(c -> (char)c).map(Integer::valueOf).toList())
                .toList();
        int highScore = 0;
        for(int i = 0; i < data.size(); i++) {
            for(int j = 0; j < data.get(0).size(); j++) {
                int baseHeight = data.get(i).get(j);
                int xr = 0;
                int x = i+1;
                while( x < data.size()) {
                    xr++;
                    if(baseHeight <= data.get(x).get(j)) {
                        break;
                    }
                    x++;
                }
                int yr = 0;
                int y = j+1;
                while( y < data.get(0).size()) {
                    yr++;

                    if(baseHeight <= data.get(i).get(y)) {
                        break;
                    }
                    y++;
                }
                int xl = 0;
                x = i-1;
                while( x >= 0) {
                    xl++;
                    if(baseHeight <= data.get(x).get(j)) {
                        break;
                    }
                    x--;
                }
                int yl = 0;
                y = j-1;
                while( y >= 0 ) {
                    yl++;
                    if(baseHeight <= data.get(i).get(y)) {
                        break;
                    }
                    y--;
                }

                int score = xr * yr* xl * yl ;
                if(score > highScore) {
                    highScore = score;
                }
            }
        }
        return highScore;
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