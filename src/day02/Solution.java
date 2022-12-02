package day02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
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

record Game(int elf, int you) {
    public int calcScore() {
        var elfValue = elf -'A';
        var youValue = you -'X';

        var won = (youValue - elfValue + 1 + 3) % 3;
        return won * 3 + youValue + 1;
    }

    public int calcScoreAlt() {
        var elfValue = elf -'A';
        var won = you -'X';
        int youValue = (elfValue + won - 1 + 3) %3;
        return youValue + won * 3 + 1;
    }
}


class Solver {
    Object part1(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var data = Stream.of(lines).toList();

        return data.stream()
                .map(s -> new Game(s.charAt(0), s.charAt(2)))
                .mapToLong(Game::calcScore)
                .sum();
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var data = Stream.of(lines).toList();

        return data.stream()
                .map(s -> new Game(s.charAt(0), s.charAt(2)))
                .mapToLong(Game::calcScoreAlt)
                .sum();
    }
}