package day01;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
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
        var lines = stringData.split(System.lineSeparator() + System.lineSeparator());
        return Stream.of(lines)
                .map(s -> s.split(System.lineSeparator()))
                .map(array -> Stream.of(array).mapToLong(Long::valueOf).sum())
                .mapToLong(Long::valueOf)
                .max()
                .orElse(-1);
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator() + System.lineSeparator());
        return Stream.of(lines)
                .map(s -> s.split(System.lineSeparator()))
                .map(array -> Stream.of(array).mapToLong(Long::valueOf).sum())
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToLong(l ->l)
                .sum();
    }
}