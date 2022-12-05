package day05;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Stack;
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
        var lines = stringData.split(System.lineSeparator()+System.lineSeparator());
        String[] startPosition = lines[0].split(System.lineSeparator());
        String[] moves = lines[1].split(System.lineSeparator());

        Stack<Character>[] stacks = getStacks(startPosition);

        Stream.of(moves)
                .map(s -> s.split(" "))
                .forEach(s -> doMove9000(stacks, Integer.parseInt(s[1]), Integer.parseInt(s[3]) -1, Integer.parseInt(s[5]) -1));

        return getScore(stacks);
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator()+System.lineSeparator());
        String[] startPosition = lines[0].split(System.lineSeparator());
        String[] moves = lines[1].split(System.lineSeparator());

        Stack<Character>[] stacks = getStacks(startPosition);

        Stream.of(moves)
                .map(s -> s.split(" "))
                .forEach(s -> doMove9001(stacks, Integer.parseInt(s[1]), Integer.parseInt(s[3]) -1, Integer.parseInt(s[5]) -1));

        return getScore(stacks);
    }

    private Stack<Character>[] getStacks(String[] startPosition){
        int numberOfStacks = startPosition[startPosition.length -1].replace(" ", "").length();
        Stack<Character>[] stacks = new Stack[numberOfStacks];
        for(int i = 0; i < numberOfStacks; i++) {
            stacks[i] = new Stack<>();
            for(int j = startPosition.length - 2; j >= 0; j--) {
                char car = startPosition[j].charAt(1+ i * 4);
                if(car != ' ') {
                    stacks[i].push(car);
                }
            }
        }

        return stacks;
    }

    private void doMove9000(Stack<Character>[] stacks, int move, int from, int to) {
        for(int i = 0; i < move; i++) {
            if (stacks[from].size() !=0) {
                Character car = stacks[from].pop();
                stacks[to].push(car);
            }
        }
    }

    private void doMove9001(Stack<Character>[] stacks, int move, int from, int to) {
        Stack<Character> inCrane = new Stack<>();
        for(int i = 0; i < move; i++) {
            if (stacks[from].size() !=0) {
                Character car = stacks[from].pop();
                inCrane.push(car);
            }
        }

        while(!inCrane.empty()) {
            stacks[to].push(inCrane.pop());
        }
    }

    private String getScore(Stack<Character>[] stacks) {
        StringBuilder result = new StringBuilder();
        for (Stack<Character> stack : stacks) {
            result.append(stack.peek());
        }
        return result.toString();
    }
}