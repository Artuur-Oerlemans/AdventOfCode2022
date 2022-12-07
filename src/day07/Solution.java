package day07;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

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

record Pad(Map<String, Pad> ls, Pad parent, boolean isFile, long size) {
    List<Long> dirsLessThan() {
        var lessDirs= new ArrayList<Long>();
        for (Pad pad: ls.values()) {
            if(!pad.isFile()) {
                lessDirs.addAll(pad.dirsLessThan());
            }
        }
        long localSize = contentSize();
        if(localSize <= 100000){
            lessDirs.add(localSize);
        }
        return lessDirs;
    }
    List<Long> directorySizes() {
        var dirs= new ArrayList<Long>();
        for (Pad pad: ls.values()) {
            if(!pad.isFile()) {
                dirs.addAll(pad.directorySizes());
            }
        }
        dirs.add(contentSize());
        return dirs;
    }

    long contentSize() {
        if(isFile)
            return size;
        long sum = 0;
        for (var pad: ls.values()) {
            sum += pad.contentSize();
        }
        return sum;
    }
}

class Solver {
    Object part1(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        Pad root = getRootView(lines);

        var values = root.dirsLessThan();

        return values.stream().mapToLong(l -> l)
                .sum();
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        Pad root = getRootView(lines);

        List<Long> values = root.directorySizes();
        long usedSpace = root.contentSize();
        long unusedSpace = 70000000 - usedSpace;
        long extraSpaceNeeded = 30000000 - unusedSpace;
        values.sort(Comparator.naturalOrder());

        for(var l: values) {
            if (l >= extraSpaceNeeded) {
                return l;
            }
        }

        return -1;
    }

    private Pad getRootView(String[] lines) {
        var root = new Pad(new HashMap<>(), null, false, 0);
        var current = root;

        for(int i = 1; i < lines.length; i++) {
            var parts = lines[i].split(" ");
            if(parts[0].equals("$") && parts[1].equals("cd")) {
                if (parts[2].equals("..")) {
                    current = current.parent();
                } else {
                    if (!current.ls().containsKey(parts[2])){
                        current.ls().put(parts[2], new Pad(new HashMap<>(), current, false, 0));
                    }
                    current = current.ls().get(parts[2]);
                }
            } else if (parts[0].equals("$") && parts[1].equals("ls")) {
                String[] otherParts;
                while(i + 1 < lines.length && !(otherParts = lines[i+1].split(" "))[0].equals("$")) {
                    i++;
                    if(otherParts[0].equals("dir")) {
                        if (!current.ls().containsKey(otherParts[1])){
                            current.ls().put(otherParts[1], new Pad(new HashMap<>(), current, false, 0));
                        }
                    } else {
                        if(!current.ls().containsKey(otherParts[1])) {
                            current.ls().put(otherParts[1], new Pad(null, current, true, Long.parseLong(otherParts[0])));
                        }
                    }
                }
            }
        }
        return root;
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