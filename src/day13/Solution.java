package day13;

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
        var lines = stringData.split(System.lineSeparator() + System.lineSeparator());

        int score = 0;

        for(int i = 0; i < lines.length; i++) {
            var pair = lines[i].split(System.lineSeparator());
            var left = extractUsefulValues(pair[0]);
            var right = extractUsefulValues(pair[1]);

            if(checkPair(left, right) == -1) {
                score += i +1;
            }
        }

        return score;
    }

    Object part2(String stringData) {
        stringData += System.lineSeparator() + System.lineSeparator() + "[[6]]" + System.lineSeparator() + "[[2]]";
        var lines = stringData.split(System.lineSeparator() + System.lineSeparator());
        List<String> packets = new ArrayList<>();

        for(var line: lines) {
            var pair = line.split(System.lineSeparator());
            packets.add(pair[0]);
            packets.add(pair[1]);
        }

        packets.sort((l, r) -> {
            var left = extractUsefulValues(l);
            var right = extractUsefulValues(r);
            return checkPair(left, right);
        });

        int two  =0 ;
        int six = 0;
        for(int i = 0; i < packets.size(); i++) {
            if("[[2]]".equals(packets.get(i))) {
                two = i +1;
            } else if("[[6]]".equals(packets.get(i))) {
                six = i +1;
            }
        }

        return two * six;
    }

    List<String> extractUsefulValues(String s) {
        Pattern p = Pattern.compile("([\\d]+|\\[|\\])");
        return p.matcher(s).results().map(MatchResult::group)
                .collect(Collectors.toList());
    }

    int checkPair(List<String> left, List<String> right) {

        if(left.size() == 0 && right.size() == 0) {
            return 0;
        }
        if(left.size() == 0) {
            return -1;
        }
        if(right.size() == 0) {
            return 1;
        }

        if(isInt(left.get(0)) && isInt(right.get(0)) ) {
            int l = Integer.parseInt(left.get(0));
            int r = Integer.parseInt(right.get(0));
            if(l < r) {
                return -1;
            } else if (l > r){
                return 1;
            } else {
                return checkPair(left.subList(1, left.size()), right.subList(1, right.size()));
            }
        }
        List<String> leftSub;
        int leftClose;
        List<String> rightSub;
        int rightClose;

        if("[".equals(left.get(0))) {
            leftClose = findIndexClose(left);
            leftSub = left.subList(1, leftClose);
        } else if(isInt(left.get(0))){
            leftClose = 0;
            leftSub = left.subList(0, 1);
        } else {
            throw new RuntimeException();
        }
        if("[".equals(right.get(0))) {
            rightClose = findIndexClose(right);
            rightSub = right.subList(1, rightClose);
        } else if(isInt(right.get(0))){
            rightClose = 0;
            rightSub = right.subList(0, 1);
        } else {
            throw new RuntimeException();
        }

        int result = checkPair(leftSub, rightSub);
        if(result == 0) {
            return checkPair(left.subList(leftClose +1, left.size()), right.subList(rightClose +1, right.size()));
        } else {
            return result;
        }
    }

    int findIndexClose(List<String> list) {
        int opens = 0;
        for(int i = 0; i < list.size(); i++) {
            if ("[".equals(list.get(i))) {
                opens++;
            } else if ("]".equals(list.get(i))){
                opens--;
                if (opens == 0) {
                    return i;
                }
            }

        }
        throw new RuntimeException("no close");
    }

    static boolean isInt(String s)
    {
        try
        { int i = Integer.parseInt(s); return true; }

        catch(NumberFormatException er)
        { return false; }
    }
}

class Helper {

    public static List<Integer> extractInts(String stringData) {
        Pattern p = Pattern.compile("([\\d]+)");
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