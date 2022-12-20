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
        var lines = stringData.split(System.lineSeparator());
        var originalNumbers = new ArrayList<>(Stream.of(lines).toList());
        for(int i = 0; i < originalNumbers.size(); i++) {
            originalNumbers.set(i, originalNumbers.get(i) + "," + i);
        }
//        originalNumbers = List.of("0", "0", "0", "-6");
        var mixed = new ArrayList<>(originalNumbers);
        System.out.println(" , " + mixed);


        for(var numberStr : originalNumbers) {
            var index = mixed.indexOf(numberStr);
            mixed.remove(numberStr);
            int number = Integer.parseInt(numberStr.split(",")[0]);

            int mod = mixed.size();
            int newIndex = (index + number + mod + mod) % mod;
            if(newIndex == 0 && number < 0) {
                mixed.add(mixed.size(), number + "");
            } else if(newIndex == 0 && number > 0) {
                mixed.add(mixed.size(), number + "");
            }else {
                mixed.add(newIndex, number + "");
            }
            if(mixed.size() < 10) {
                System.out.println(numberStr + ", " + mixed);
            }
        }
        var ints = mixed.stream().map(Integer::valueOf).toList();

        var start = mixed.indexOf("0");
        System.out.println(ints.get((start + 1000) % mixed.size()) + " " + ints.get((start + 2000) % mixed.size()) + " " + ints.get((start + 3000) % mixed.size()));
        return ints.get((start + 1000) % mixed.size()) + ints.get((start + 2000) % mixed.size()) + ints.get((start + 3000) % mixed.size());
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var originalNumbers = new ArrayList<>(Stream.of(lines).toList());
        for(int i = 0; i < originalNumbers.size(); i++) {
            long keyUsed = Long.parseLong(originalNumbers.get(i)) * 811589153L;
            originalNumbers.set(i, keyUsed + " " + i);
        }
//        originalNumbers = List.of("0", "0", "0", "-6");
        var mixed = new ArrayList<>(originalNumbers);
        System.out.println(" , " + mixed);

        for(int i = 0; i < 10; i++) {
            for(var numberStr : originalNumbers) {
                var index = mixed.indexOf(numberStr);
                mixed.remove(numberStr);
                long number = Long.parseLong(numberStr.split(" ")[0]);

                long mod = mixed.size();
                int newIndex = (int) ((index + number) % mod);
                if(newIndex < 0) {
                    newIndex += mod;
                }
                if(newIndex == 0 && number < 0) {
                    mixed.add(mixed.size(), numberStr);
                }
                else if(newIndex == 0 && number > 0) {
                    mixed.add(mixed.size(), numberStr);
                }
                else {
                    mixed.add(newIndex, numberStr);
                }
            }
            if(mixed.size() < 10) {
                System.out.println( mixed.stream().map(s-> s.split(" ")[0]).map(Long::valueOf).toList());
            }
        }
        var longs = mixed.stream().map(s-> s.split(" ")[0]).map(Long::valueOf).toList();

        var start = longs.indexOf(0L);
        System.out.println(longs.get((start + 1000) % mixed.size()) + " " + longs.get((start + 2000) % mixed.size()) + " " + longs.get((start + 3000) % mixed.size()));
        return longs.get((start + 1000) % mixed.size()) + longs.get((start + 2000) % mixed.size()) + longs.get((start + 3000) % mixed.size());
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