package day18;

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
        var lava = Stream.of(lines)
                .map(Helper::extractInts)
                .map(l -> new P3D(l.get(0), l.get(1), l.get(2)))
                .collect(Collectors.toSet());

        return lava.stream()
                        .map(p -> numberOfSides(p, lava))
                .mapToLong(l -> l)
                .sum();
    }

    Result getSurrounding(P3D here, Set<P3D> lava, Set<P3D> inside, Set<P3D> outside, Set<P3D> searched) {
        var surrounding = new HashSet<>(Set.of(
                new P3D(here.x() +1, here.y(), here.z()),
                new P3D(here.x() -1, here.y(), here.z()),
                new P3D(here.x(), here.y() -1, here.z()),
                new P3D(here.x(), here.y() +1, here.z()),
                new P3D(here.x(), here.y(), here.z()-1),
                new P3D(here.x(), here.y(), here.z() +1)
        ));
        surrounding.removeAll(searched);
        surrounding.removeAll(lava);
        if(surrounding.size() == 0) {
            return new Result(false, false, searched);
        }
        searched.addAll(surrounding);
        if(surrounding.stream()
                .anyMatch(outside::contains))
        {
            return new Result(false, true, searched);
        }
        if(surrounding.stream()
                .anyMatch(inside::contains))
        {
            return new Result(true, false, searched);
        }
        for(var point : surrounding) {
            if(point.x() <0 || point.x() > 20 ||point.y() <0 || point.y() > 20 ||point.z() <0 || point.z() > 20) {
                return new Result(false, true, searched);
            }
            var result = getSurrounding(point, lava, inside, outside, searched);
            if( result.inside() || result.outside()) {
                return result;
            }
        }
        return new Result(false, false, searched);
    }

    record Result(boolean inside, boolean outside, Set<P3D> searched){}

    long numberOfSides(P3D here, Set<P3D> lava) {
        var surrounding = List.of(
                new P3D(here.x() +1, here.y(), here.z()),
                new P3D(here.x() -1, here.y(), here.z()),
                new P3D(here.x(), here.y() -1, here.z()),
                new P3D(here.x(), here.y() +1, here.z()),
                new P3D(here.x(), here.y(), here.z()-1),
                new P3D(here.x(), here.y(), here.z() +1)
        );
        return surrounding.stream().filter(p -> !lava.contains(p)).count();
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var lava = Stream.of(lines)
                .map(Helper::extractInts)
                .map(l -> new P3D(l.get(0), l.get(1), l.get(2)))
                .collect(Collectors.toSet());

        var inside = new HashSet<P3D>();
        var outside = new HashSet<P3D>();
        for(int i = 0; i <= 20; i++) {
            for(int j = 0; j <= 20; j++) {
                for(int k = 0; k <= 20; k++) {
                    var p = new P3D(i, j, k);
                    if(lava.contains(p) || inside.contains(p) || outside.contains(p)){
                        continue;
                    }
                    var result = getSurrounding(p, lava, inside, outside, new HashSet<>(Set.of()));
                    if(result.outside()){
                        outside.addAll(result.searched());
                        outside.add(p);
                    } else {
                        inside.addAll(result.searched());
                        inside.add(p);
                    }
                }
            }
        }

        return lava.stream()
                .map(p -> numberOfSides(p, lava, inside))
                .mapToLong(l -> l)
                .sum();
    }

    long numberOfSides(P3D here, Set<P3D> lava, Set<P3D> inside) {
        var surrounding = List.of(
                new P3D(here.x() +1, here.y(), here.z()),
                new P3D(here.x() -1, here.y(), here.z()),
                new P3D(here.x(), here.y() -1, here.z()),
                new P3D(here.x(), here.y() +1, here.z()),
                new P3D(here.x(), here.y(), here.z()-1),
                new P3D(here.x(), here.y(), here.z() +1)
        );
        return surrounding.stream().filter(p -> !lava.contains(p) && !inside.contains(p)).count();
    }
}
record P3D(int x, int y, int z) {

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