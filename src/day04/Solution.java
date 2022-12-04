package day04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solution {

    public static final String ANSI_BOLD = "\033[0;1m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String... input) {
        var solver = new Solver();
        String packageName = Solution.class.getPackage().getName();
        String stringData = readData(input[0]);

        System.out.println("\nexecuting part 1");
        Instant start1 = Instant.now();
        System.out.format(ANSI_BOLD + "result of %s part 1: %s\n" + ANSI_RESET, packageName, solver.part1(stringData));
        Instant end1 = Instant.now();
        System.out.println("Time taken: " + Duration.between(start1, end1) + "\n");

        System.out.println("\nexecuting part 2");
        Instant start2 = Instant.now();
        System.out.format(ANSI_BOLD + "result of %s part 2: %s\n" + ANSI_RESET, packageName, solver.part2(stringData));
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

        return data.stream()
                .map(s -> s.split(","))
                .filter(this::containsOther)
                .count();
    }

    boolean containsOther(String[] ss) {
        String[] first = ss[0].split("-");
        String[] second = ss[1].split("-");

        int first0 = Integer.parseInt(first[0]);
        int first1 = Integer.parseInt(first[1]);
        int second0 = Integer.parseInt(second[0]);
        int second1 = Integer.parseInt(second[1]);

        if(first0 <= second0 && second1 <= first1) {
            return true;
        }
        return second0 <= first0 && first1 <=  second1;
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var data = Stream.of(lines).toList();

        return data.stream()
                .map(s -> s.split(","))
                .filter(this::overlapOther)
                .count();
    }

    boolean overlapOther(String[] ss) {
        String[] first = ss[0].split("-");
        String[] second = ss[1].split("-");

        int first0 = Integer.parseInt(first[0]);
        int first1 = Integer.parseInt(first[1]);
        int second0 = Integer.parseInt(second[0]);
        int second1 = Integer.parseInt(second[1]);

        Set<Integer> firsts = IntStream.range(first0, first1 +1).boxed().collect(Collectors.toSet());
        Set<Integer> seconds = IntStream.range(second0, second1 +1).boxed().collect(Collectors.toSet());
        firsts.retainAll(seconds);
        return !firsts.isEmpty();
    }
}