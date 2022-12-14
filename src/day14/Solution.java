package day14;

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
    int lowestY = 0;
    Object part1(String stringData) {
        var lines = stringData.split(System.lineSeparator());

        var stone = new HashSet<Point>();
        var sand = new HashSet<Point>();

        Stream.of(lines)
                .map(s -> s.split(" -> "))
                .forEach(coors -> drawStone(coors, stone));

        var beingCaught = true;
        int comeToRest = -1;
        while(beingCaught) {
            comeToRest++;
            beingCaught = sandFall(sand, stone);
        }
        var vis = new Visualizer(sand, stone);
        vis.visualize();

        return comeToRest;
    }

    void drawStone(String[] coors, HashSet<Point> stone) {
        var start = getCoor(coors[0]);

        for(int i = 1; i < coors.length; i++) {
            var end = getCoor(coors[i]);
            int dx =(int) Math.signum(end.x - start.x);
            int dy =(int) Math.signum(end.y - start.y);
            int distance = Math.max(Math.abs(end.x - start.x), Math.abs(end.y - start.y));

            for(int j = 0; j <= distance; j++) {
                var here = new Point(start.x + dx * j, start.y + dy * j);
                stone.add(here);
                if(start.y + dy * j > lowestY) {
                    lowestY = start.y + dy * j;
                }
            }
            start = end;
        }
    }

    boolean sandFall(HashSet<Point> sand, HashSet<Point> stone) {
        var here = new Point(500, 0);
        while(true) {
            var down = new Point(here.x, here.y + 1);
            var leftDown = new Point(here.x - 1, here.y + 1);
            var rightDown = new Point(here.x + 1, here.y + 1);
            if(!sand.contains(down) && !stone.contains(down)) {
                here = down;
            } else if(!sand.contains(leftDown) && !stone.contains(leftDown)) {
                here = leftDown;
            } else if(!sand.contains(rightDown) && !stone.contains(rightDown)) {
                here = rightDown;
            } else {
                sand.add(here);
                return true;
            }
            if(here.y > lowestY) {
                return false;
            }
        }
    }

    Point getCoor(String coorStr) {
        var coor = coorStr.split(",");
        return new Point(Integer.parseInt(coor[0]), Integer.parseInt(coor[1]));
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());

        var stone = new HashSet<Point>();
        var sand = new HashSet<Point>();

        Stream.of(lines)
                .map(s -> s.split(" -> "))
                .forEach(coors -> drawStone(coors, stone));

        var beingCaught = true;
        int comeToRest = -1;
        while(beingCaught) {
            comeToRest++;
            beingCaught = sandFallFloor(sand, stone);
        }
        var vis = new Visualizer(sand, stone);
        vis.visualize();

        return comeToRest;
    }

    boolean sandFallFloor(HashSet<Point> sand, HashSet<Point> stone) {
        var here = new Point(500, 0);
        if (sand.contains(here)) {
            return false;
        }
        while(true) {
            var down = new Point(here.x, here.y + 1);
            var leftDown = new Point(here.x - 1, here.y + 1);
            var rightDown = new Point(here.x + 1, here.y + 1);
            if(!sand.contains(down) && !stone.contains(down)) {
                here = down;
            } else if(!sand.contains(leftDown) && !stone.contains(leftDown)) {
                here = leftDown;
            } else if(!sand.contains(rightDown) && !stone.contains(rightDown)) {
                here = rightDown;
            } else {
                sand.add(here);
                return true;
            }

            if(here.y == lowestY +1) {
                sand.add(here);
                return true;
            }
        }
    }
}

class Visualizer {
    private int minX = 9999;
    private int maxX = 0;
    private int maxY = 0;
    private final HashSet<Point> sand;
    private final HashSet<Point> stone;
    public Visualizer(HashSet<Point> sand, HashSet<Point> stone) {
        this.stone = stone;
        this.sand = sand;

        for (var solid : stone) {
            if(solid.x < minX) {
                minX = solid.x;
            }
            if(solid.x > maxX) {
                maxX = solid.x;
            }
            if(solid.y > maxY) {
                maxY = solid.y;
            }
        }
        for (var solid : sand) {
            if(solid.x < minX) {
                minX = solid.x;
            }
            if(solid.x > maxX) {
                maxX = solid.x;
            }
            if(solid.y > maxY) {
                maxY = solid.y;
            }
        }
    }

    public void visualize() {
        String[][] image = new String[maxX +1 - minX][maxY +1];
        for(var rock : stone) {
            image[rock.x - minX][rock.y] = "#";
        }for(var grain : sand) {
            image[grain.x - minX][grain.y] = "o";
        }
        StringBuilder imageBuilder =  new StringBuilder();
        for(int j = 0; j < image[0].length; j++) {
            for (String[] strings : image) {
                if (strings[j] != null) {
                    imageBuilder.append(strings[j]);
                } else {
                    imageBuilder.append(" ");
                }
            }
            imageBuilder.append("\n");
        }
        System.out.print(imageBuilder);
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