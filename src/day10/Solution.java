package day10;

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
    int score = 0;
    Object part1(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var data = Stream.of(lines).toList();

        int x = 1;
        int cycleNumber =1;
        for (String line : data) {
            var parts = line.split(" ");
            if (parts[0].equals("addx")) {

                cycleNumber += 1;
                updateScore(cycleNumber, x);
                x += Integer.parseInt(parts[1]);
            }
            cycleNumber += 1;
            updateScore(cycleNumber, x);
        }

        return score;
    }

    void updateScore(int cycleNumber, int x) {
        if ((cycleNumber + 20) % 40 == 0) {
            score += cycleNumber * x;
        }
    }

    List<String> screenLine = new ArrayList<>();
    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var data = Stream.of(lines).toList();

        int cycleNumber =1;
        int x = 1;
        updateScreenLine(cycleNumber, x);
        for (String line : data) {
            var parts = line.split(" ");
            if (parts[0].equals("addx")) {
                cycleNumber += 1;
                updateScreenLine(cycleNumber, x);

                x += Integer.parseInt(parts[1]);
            }
            cycleNumber += 1;
            updateScreenLine(cycleNumber, x);

        }

        for(int i = 0; i < screenLine.size(); i++) {
            System.out.print(screenLine.get(i));
            if ((i + 1) % 40 == 0){
                System.out.println();
            }
        }

        return 1;
    }

    void updateScreenLine(int cycleNumber, int x) {
        int k = (cycleNumber - 1) % 40;
        if(x -1 <= k && k < x + 2){
            screenLine.add("#");
        } else {
            screenLine.add(" ");
        }
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