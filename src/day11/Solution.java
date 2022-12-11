package day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.UnaryOperator;
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
        String[] lines = stringData.split(System.lineSeparator() + System.lineSeparator());
        var monkeys = Stream.of(lines).map(Monkey::new).toList();


        for(int i =0; i < 20; i++) {
            for(var monkey : monkeys) {
                monkey.takeTurn(monkeys);
            }
        }

        return calculateMonkeyBusiness(monkeys);
    }

    Object part2(String stringData) {
        String[] lines = stringData.split(System.lineSeparator() + System.lineSeparator());
        var monkeys = Stream.of(lines).map(Monkey::new).toList();

        long commonDiv = 1;

        for(var monkey: monkeys) {
            commonDiv *= monkey.test;
        }

        for(int i =0; i < 10000 ; i++) {
            for(var monkey : monkeys) {
                monkey.takeTurnWorry(monkeys, commonDiv);
            }
        }

        return calculateMonkeyBusiness(monkeys);
    }

    private long calculateMonkeyBusiness(List<Monkey> monkeys) {
        return monkeys.stream()
                .map(m -> m.inspected)
                .sorted(Comparator.reverseOrder())
                .mapToLong(l -> l)
                .limit(2)
                .reduce(1, (a, b) -> a * b);
    }

    static class Monkey {
        public String id;
        public List<Long> inventory = new ArrayList<>();
        public UnaryOperator<Long> operator;
        public long test;
        public int ifTrue;
        public int ifFalse;

        public long inspected = 0;

        public void takeTurn(List<Monkey> monkeys) {
            inspected += inventory.size();
            inventory = new ArrayList<>(inventory.stream().map(operator).map(i -> i/3).toList());
            while(inventory.size() != 0) {
                var toThrow = inventory.remove(0);
                if(toThrow % test == 0) {
                    monkeys.get(ifTrue).inventory.add(toThrow);
                } else {
                    monkeys.get(ifFalse).inventory.add(toThrow);
                }
            }
        }

        public void takeTurnWorry(List<Monkey> monkeys, long commonDiv) {
            inspected += inventory.size();
            inventory = new ArrayList<>(inventory.stream().map(operator).map(i -> i % commonDiv).toList());

            while(inventory.size() != 0) {
                var toThrow = inventory.remove(0);
                if(toThrow % test == 0) {
                    monkeys.get(ifTrue).inventory.add(toThrow);
                } else {
                    monkeys.get(ifFalse).inventory.add(toThrow);
                }
            }
        }

        public Monkey (String s) {
            String[] lines = s.split(System.lineSeparator());

            id = lines[0].strip().split(" ")[1];

            var inventoryParts = lines[1].strip().split(" ");
            Arrays.stream(inventoryParts)
                    .skip(2)
                    .map(sAndComma -> sAndComma.substring(0, 2))
                    .map(Long::valueOf)
                    .forEach(inventory::add);

            var operatorParts = lines[2].strip().split(" ");

            if("+".equals(operatorParts[4])) {
                if(operatorParts[5].equals("old")) {
                    operator= i -> i + i;
                } else {
                    int temp =  Integer.parseInt(operatorParts[5]);
                    operator= i -> i + temp;
                }
            }
            else if("*".equals(operatorParts[4])) {
                if(operatorParts[5].equals("old")) {
                    operator= i -> i * i;
                } else {
                    int temp =  Integer.parseInt(operatorParts[5]);
                    operator= i -> i * temp;
                }
            } else {
                System.out.println("fuck " + operatorParts[5]);
            }

            test = Integer.parseInt(lines[3].strip().split(" ")[3]);
            ifTrue = Integer.parseInt(lines[4].strip().split(" ")[5]);
            ifFalse = Integer.parseInt(lines[5].strip().split(" ")[5]);
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