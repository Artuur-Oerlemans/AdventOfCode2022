package day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
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
        var monkeys = Stream.of(lines).map(this::makeMonkey).collect(Collectors.toMap(Monkey::name, m-> m));

        return monkeyValue("root", monkeys);
//        var waitsOn = new ConcurrentHashMap<String, List<String>>();
//        monkeys.values().stream()
//                .filter(monkey -> monkey.value() == null)
//                        .forEach(m -> {
//                            waitsOn.merge(m.mentionA(), new ConcurrentLinkedQueue<>(List.of(m.name())), (a, b) -> {a.addAll(b); return a;});
//                            waitsOn.merge(m.mentionB(), new ConcurrentLinkedQueue<>(List.of(m.name())), (a,b) -> {a.addAll(b); return a;});
//                        });
//        var valueMonekeys = new HashSet<>(monkeys.values()
//                .stream()
//                .filter(monkey -> monkey.value() == null)
//                .map(Monkey::name)
//                        .collect(Collectors.toSet()));
//        while(monkeys.get("root").value() == null) {
//            for(var monkeyName : waitsOn.keySet()) {
//                if(valueMonekeys.contains(monkeyName)) {
//                    for
//                }
//            }
//        }
    }

    long monkeyValue(String name, Map<String, Monkey> monkeys) {
        var monkey = monkeys.get(name);
        if(monkey.value() != null) {
            return monkey.value();
        } else {
            long a = monkeyValue(monkey.mentionA(), monkeys);
            long b = monkeyValue(monkey.mentionB(), monkeys);

            return switch (monkey.operation()) {
                case "*" -> a* b;
                case "-" -> a - b;
                case "/" -> a / b;
                case "+" -> a+ b;
                default -> throw new RuntimeException();
            };
        }
    }

    Monkey makeMonkey(String inputString) {
        var inputSplit = inputString.split(" ");
        if(inputSplit.length == 2) {
            return new Monkey(inputSplit[0].substring(0,4), Long.parseLong(inputSplit[1]), null, null, null);
        } else {
            return new Monkey(inputSplit[0].substring(0,4), null, inputSplit[1], inputSplit[3], inputSplit[2]);
        }
    }

    Object part2Bad(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var monkeys = Stream.of(lines).map(this::makeMonkey).collect(Collectors.toMap(Monkey::name, m-> m));

        monkeys.remove("humn");
        var valueB = monkeyValue(monkeys.get("root").mentionB(), monkeys);

        for(var humn = 0; true; humn++) {
            monkeys.put("humn", new Monkey("humn", (long) humn, null, null, null));
            if(monkeyValue(monkeys.get("root").mentionA(), monkeys) == valueB) {
                return humn;
            }
        }
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var monkeys = Stream.of(lines).map(this::makeMonkey).collect(Collectors.toMap(Monkey::name, m-> m));

        monkeys.remove("humn");
        var valueB = monkeyValue(monkeys.get("root").mentionB(), monkeys);

        return reverseEngineerHumn(valueB, monkeys.get("root").mentionA(), monkeys);
    }

    long reverseEngineerHumn(long targetValue, String name, Map<String, Monkey> monkeys) {
        if("humn".equals(name)) {
            return targetValue;
        }

        Monkey monkey = monkeys.get(name);
        Long valueA = null;
        Long valueB = null;
        try{
            valueA = monkeyValue(monkey.mentionA(), monkeys);
        } catch(NullPointerException e) {}
        try{
            valueB = monkeyValue(monkey.mentionB(), monkeys);
        } catch(NullPointerException e) {}

        if(valueB != null) {
            return switch (monkey.operation()) {
                case "*" -> reverseEngineerHumn(targetValue / valueB, monkey.mentionA(), monkeys);
                case "-" -> reverseEngineerHumn(targetValue + valueB, monkey.mentionA(), monkeys);
                case "/" -> reverseEngineerHumn(targetValue * valueB, monkey.mentionA(), monkeys);
                case "+" -> reverseEngineerHumn(targetValue - valueB, monkey.mentionA(), monkeys);
                default -> throw new RuntimeException();
            };
        }

        if(valueA != null) {
            return switch (monkey.operation()) {
                case "*" -> reverseEngineerHumn(targetValue / valueA, monkey.mentionB(), monkeys);
                case "-" -> reverseEngineerHumn(valueA - targetValue, monkey.mentionB(), monkeys);
                case "/" -> reverseEngineerHumn(valueA / targetValue, monkey.mentionB(), monkeys);
                case "+" -> reverseEngineerHumn(targetValue - valueA, monkey.mentionB(), monkeys);
                default -> throw new RuntimeException();
            };
        }
        throw new RuntimeException();
    }
}

record Monkey (String name, Long value, String mentionA, String mentionB, String operation) {}

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