package day20;

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
    Object part1(String stringData) {
        var rawNumbers = stringData.split(System.lineSeparator());
        var numberOrder = Stream.of(rawNumbers)
                .map(Long::parseLong)
                .map(NumberPojo::new)
                .toList();
        var mixed = new ArrayList<>(numberOrder);

        decrypt(numberOrder, mixed);

        // 4914
        return getResult(mixed);
    }

    Object part2(String stringData) {
        var rawNumbers = stringData.split(System.lineSeparator());
        var numberOrder = Stream.of(rawNumbers)
                .map(Long::parseLong)
                .map(l -> l * 811589153L)
                .map(NumberPojo::new)
                .toList();
        var mixed = new ArrayList<>(numberOrder);

        for(int i = 0; i < 10; i++) {
            decrypt(numberOrder, mixed);
        }

        // 7973051839072
        return getResult(mixed);
    }

    private void decrypt(List<NumberPojo> numberOrder, ArrayList<NumberPojo> mixed) {
        for(var numberPojo : numberOrder) {
            var index = mixed.indexOf(numberPojo);
            mixed.remove(numberPojo);
            long number =  numberPojo.number();

            int newIndex = Math.floorMod(index + number, mixed.size());
            mixed.add(newIndex, numberPojo);
        }
    }

    private long getResult(ArrayList<NumberPojo> mixed) {
        var zeroObject = mixed.parallelStream().filter(np -> np.number() == 0L).findFirst().get();
        var start = mixed.indexOf(zeroObject);
        return mixed.get((start + 1000) % mixed.size()).number() + mixed.get((start + 2000) % mixed.size()).number() + mixed.get((start + 3000) % mixed.size()).number();
    }
}

// use a class, not a record to guarantee the hash doesn't make objects with equal value equal.
class NumberPojo {
    private final long number;
    NumberPojo(long number) {
        this.number = number;
    }
    long number() {
        return number;
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