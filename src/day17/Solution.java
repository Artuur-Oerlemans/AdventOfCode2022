package day17;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.*;
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
        var jets = Helper.stringToList(stringData);
        var rocks = new Point[][]{
                new Point[]{new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0)},
                new Point[]{new Point(1,0), new Point(0,1), new Point(1,1), new Point(2,1), new Point(1,2)},
                new Point[]{new Point(0,0), new Point(1,0), new Point(2,0), new Point(2,1), new Point(2,2)},
                new Point[]{new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3)},
                new Point[]{new Point(0,0), new Point(1,0), new Point(0,1), new Point(1,1)}
        };
        var restingRocks = new HashSet<Point>();
        var jetIndex = 0;
        var height = 0;
        for(int r =0 ; r < 2022; r++) {
            var rock = rocks[r % 5];
            var positionVector = new Point(2, height + 3);

            while(true) {
                var dirVector = getDirection(jets.get(jetIndex % jets.size()));
                jetIndex++;
                var nextPositionVector = add(positionVector, dirVector);
                var nextRealPositionRock = realPositionRock(rock, nextPositionVector);
                if( canBeThere(nextRealPositionRock, restingRocks)) {
                    positionVector = nextPositionVector;
                }

                var downPositionVector = add(positionVector, new Point(0, -1));
                var downRealPositionRock = realPositionRock(rock, downPositionVector);
                if( canBeThere(downRealPositionRock, restingRocks)) {
                    positionVector = downPositionVector;
                } else {
                    break;
                }
            }
            var realPositionRock = realPositionRock(rock, positionVector);
            restingRocks.addAll(realPositionRock);
            height = getHeight(restingRocks);
        }
//        Visualizer.visualize(restingRocks, height);
        return height;
    }

    int getHeight(Set<Point> rock) {
        return rock.stream().mapToInt(p -> p.y).max().getAsInt() + 1;
    }

    Set<Point> realPositionRock(Point[] rock, Point positionVector) {
        var realPositionRock = new HashSet<Point>();
        for(var stone: rock) {
            realPositionRock.add(add(stone, positionVector));
        }
        return realPositionRock;
    }

    Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }

    Point getDirection(char dir) {
        if('>' == dir) {
            return new Point(1, 0);
        } else if('<' == dir) {
            return new Point(-1, 0);
        }
        throw new RuntimeException( "direction does not exist");
    }

    boolean canBeThere(Set<Point> realPositionRock, HashSet<Point> restingRocks) {
        for(var stone: realPositionRock) {
            if(restingRocks.contains(stone) || stone.x < 0 || 6 < stone.x || stone.y < 0) {
                return false;
            }
        }
        return true;
    }

    Object part2(String stringData) {
        var jets = Helper.stringToList(stringData);
        var rocks = new Point[][]{
                new Point[]{new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0)},
                new Point[]{new Point(1,0), new Point(0,1), new Point(1,1), new Point(2,1), new Point(1,2)},
                new Point[]{new Point(0,0), new Point(1,0), new Point(2,0), new Point(2,1), new Point(2,2)},
                new Point[]{new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3)},
                new Point[]{new Point(0,0), new Point(1,0), new Point(0,1), new Point(1,1)}
        };
        var restingRocks = new HashSet<Point>();
        var jetIndex = 0;
        var height = 0;
        List<Integer> heightChrono = new ArrayList<>();
        var heightEstimates = new LinkedList<Long>();

        for(int r =0 ; true; r++) {
            var rock = rocks[r % 5];
            var positionVector = new Point(2, height + 3);

            while(true) {
                var dirVector = getDirection(jets.get(jetIndex % jets.size()));
                jetIndex++;
                var nextPositionVector = add(positionVector, dirVector);
                var nextRealPositionRock = realPositionRock(rock, nextPositionVector);
                if( canBeThere(nextRealPositionRock, restingRocks)) {
                    positionVector = nextPositionVector;
                }

                var downPositionVector = add(positionVector, new Point(0, -1));
                var downRealPositionRock = realPositionRock(rock, downPositionVector);
                if( canBeThere(downRealPositionRock, restingRocks)) {
                    positionVector = downPositionVector;
                } else {
                    break;
                }
            }

            var realPositionRock = realPositionRock(rock, positionVector);
            restingRocks.addAll(realPositionRock);
            height = getHeight(restingRocks);
            heightChrono.add(height);

            var newHeightEstimates = makeHeightEstimate(heightChrono);
            int requiredTimes = 3;
            while(heightEstimates.size() + newHeightEstimates.size() > requiredTimes) {
                heightEstimates.remove();
            }
            heightEstimates.addAll(newHeightEstimates);
            if(heightEstimates.size() == requiredTimes) {
                long anEstimate = heightEstimates.remove();
                boolean allEqual = true;
                for( long otherEstimate: heightEstimates) {
                    allEqual = allEqual && anEstimate == otherEstimate;
                }
                if(allEqual) {
                    break;
                }
            }
        }

        return heightEstimates.remove();
    }

    List<Long> makeHeightEstimate(List<Integer> heightChrono) {
        int r = heightChrono.size();
        var estimates = new ArrayList<Long>();
        for(int b = Math.max((r/7 /5) * 5, 1000); b < r/ 3; b += 5) {
            int a = r - 3 * b - 1;
            if(maybeCyclicPattern(a, b, heightChrono)) {
                estimates.add(calcHeight(a, b, heightChrono));
            }
        }
        return estimates;
    }


    /**
     * As this only look at the height, it can give no guarantee if it's actually cyclical.
     * @param a the irregular portion of the fallen rocks
     * @param b how long the cycles take
     * @param heightChrono the recorded height after every fallen rock
     */
    private boolean maybeCyclicPattern(int a, int b, List<Integer> heightChrono) {
        return heightChrono.get(a + b) - heightChrono.get(a) == heightChrono.get(a + 2 * b) - heightChrono.get(a + b) &&
                heightChrono.get(a + 2 * b) - heightChrono.get(a + b) == heightChrono.get(a + 3 * b) - heightChrono.get(a + 2 * b);
    }

    long  calcHeight(long a, long b, List<Integer> heightChrono) {
        long remaining = 1000000000000L - a -1;
        long repeats = remaining / b;
        long finalStretch = remaining % b;

        long heightSegment = heightChrono.get((int) (a + 2 * b)) - heightChrono.get((int) (a + b));
        long finalStretchHeight = heightChrono.get((int) (a + b + finalStretch)) - heightChrono.get((int) (a + b));
        return heightChrono.get((int)a) + repeats * heightSegment + finalStretchHeight;
    }
}

class Visualizer {
    public static void visualize(Set<Point> restingRocks, int height) {
        visualize(restingRocks, height, 0);
    }
    public static void visualize(Set<Point> restingRocks, int height, int low) {
        for(int i = height + 1; i >=low; i--) {
            System.out.print("|");
            for(int j = 0; j < 7; j++) {
                if(restingRocks.contains(new Point(j, i))) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println("|");
        }
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