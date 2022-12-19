package day19;

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
        try {
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
        return Stream.of(lines)
                .parallel()
                .map(this::makeBlueprint)
                .map(this::calcQualityLevel)
                .mapToInt(i -> i)
                .sum();
    }

    int calcQualityLevel(Blueprint blueprint) {
        var mem = new HashSet<State>();
        int geodes = recursive(new State(24, 0, 0, 0, 0, 1, 0, 0, 0), mem, blueprint);

        return geodes * blueprint.id();
    }

    State removeUnnecessaryResources(State state, HashSet<State> mem, Blueprint blueprint) {
        int maxOre = blueprint.maxOreCost() + (blueprint.maxOreCost() - state.oreBot()) * state.timeLeft();
        if(state.ore() > maxOre) {
            state = new State(state.timeLeft(), maxOre, state.clay(), state.obsidian(), state.geode(), state.oreBot(), state.clayBot(), state.obsidianBot(), state.geodeBot());
        }
        int maxClay = blueprint.obsRobotClay() + (blueprint.obsRobotClay() - state.clayBot()) * state.timeLeft();
        if(state.clay() > maxClay) {
            state = new State(state.timeLeft(), state.ore(), maxClay, state.obsidian(), state.geode(), state.oreBot(), state.clayBot(), state.obsidianBot(), state.geodeBot());
        }
        int maxObs = blueprint.geodeRobotObs() + (blueprint.geodeRobotObs() - state.obsidianBot()) * state.timeLeft();
        if(state.obsidian() > maxObs) {
            state = new State(state.timeLeft(), state.ore(), state.clay(), maxObs, state.geode(), state.oreBot(), state.clayBot(), state.obsidianBot(), state.geodeBot());
        }

        return state;
    }

    int recursive(State state, HashSet<State> mem, Blueprint blueprint) {
//        System.out.println(state);
        if (state.timeLeft() == 0) {
            return state.geode();
        } else if(state.timeLeft() < 0) {
            throw new RuntimeException();
        }
        state = removeUnnecessaryResources(state, mem, blueprint);
        if (mem.contains(state)) {
            return 0;
        }
        var highScore = 0;
        var doNothing = state.tick();
        var maxClayCost = blueprint.obsRobotClay();
        var maxObsidianCost = blueprint.geodeRobotObs();
        if (state.ore() <= blueprint.maxOreCost()) {
//            System.out.println("do nothing bot");
            int doNothingScore = recursive(doNothing, mem, blueprint);
            if (doNothingScore > highScore) {
                highScore = doNothingScore;
            }
        }

        if (blueprint.geodeRobotOre() <= state.ore() && blueprint.geodeRobotObs() <= state.obsidian()) {
            var newState = new State(
                    doNothing.timeLeft(),
                    doNothing.ore() - blueprint.geodeRobotOre(), doNothing.clay(), doNothing.obsidian() - blueprint.geodeRobotObs(),
                    doNothing.geode(), state.oreBot(), state.clayBot(), state.obsidianBot(), state.geodeBot() + 1
            );
            int score = recursive(newState, mem, blueprint);
            if (score > highScore) {
                highScore = score;
            }
        }

        if (state.obsidianBot() <= maxObsidianCost && blueprint.obsRobotOre() <= state.ore() && blueprint.obsRobotClay() <= state.clay()) {
            var newState = new State(doNothing.timeLeft(),
                    doNothing.ore() - blueprint.obsRobotOre(), doNothing.clay() - blueprint.obsRobotClay(), doNothing.obsidian(), doNothing.geode(),
                    state.oreBot(), state.clayBot(), state.obsidianBot() + 1, state.geodeBot()
            );
            int score = recursive(newState, mem, blueprint);
            if (score > highScore) {
                highScore = score;
            }
        }

        if (state.clayBot() <= maxClayCost && blueprint.clayRobotOre() <= state.ore()) {
//            System.out.println("build clay bot");
            var newState = new State(
                    doNothing.timeLeft(),
                    doNothing.ore() - blueprint.clayRobotOre(), doNothing.clay(), doNothing.obsidian(), doNothing.geode(),
                    state.oreBot(), state.clayBot() + 1, state.obsidianBot(), state.geodeBot()
            );
            int score = recursive(newState, mem, blueprint);
            if (score > highScore) {
                highScore = score;
            }
        }

        if (state.oreBot() <= blueprint.maxOreCost() && blueprint.oreRobotOre() <= state.ore()) {
//            System.out.println("build ore bot");
            var newState = new State(
                    doNothing.timeLeft(),
                    doNothing.ore() - blueprint.oreRobotOre(), doNothing.clay(), doNothing.obsidian(), doNothing.geode(),
                    state.oreBot() + 1, state.clayBot(), state.obsidianBot(), state.geodeBot()
            );
            int score = recursive(newState, mem, blueprint);
            if (score > highScore) {
                highScore = score;
            }
        }

        mem.add(state);
        return highScore;
    }

    Blueprint makeBlueprint(String data) {
        // Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 4 ore. Each obsidian robot costs 4 ore and 12 clay. Each geode robot costs 4 ore and 19 obsidian.
        var l = Helper.extractShorts(data);
        var maxOreCost = Stream.of(l.get(1),
                l.get(2),
                l.get(3),
                l.get(5)
        ).mapToInt(i -> i).max().getAsInt();
        return new Blueprint(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5), l.get(6), (short) maxOreCost);
    }

    int calcGeodes(Blueprint blueprint) {
        var mem = new HashSet<State>();
        int goedes = recursive(new State(32, 0, 0, 0, 0, 1, 0, 0, 0), mem, blueprint);
        System.out.println(blueprint.id() + "  " + goedes);
        return goedes;
    }

    Object part2(String stringData) {
        var lines = stringData.split(System.lineSeparator());
        var l = Stream.of(lines)
                .parallel()
                .map(this::makeBlueprint)
                .limit(3)
                .map(this::calcGeodes)
                .toList();

        return l.get(0) * l.get(1) * l.get(2);
    }
}

record State(short timeLeft, short ore, short clay, short obsidian, short geode, short oreBot, short clayBot, short obsidianBot,
             short geodeBot) {
    public State(int timeLeft, int ore, int clay, int obsidian, int geode, int oreBot, int clayBot, int obsidianBot,
                 int geodeBot){
        this((short) timeLeft, (short) ore, (short) clay, (short) obsidian, (short) geode, (short) oreBot, (short) clayBot, (short) obsidianBot,
                (short) geodeBot);
    }

    public State tick() {
        return new State(timeLeft() - 1,
                ore() + oreBot(),
                clay() + clayBot(),
                obsidian() + obsidianBot(),
                geode() + geodeBot(),
                oreBot(),
                clayBot(),
                obsidianBot(),
                geodeBot()
        );
    }
}

record Blueprint(int id, short oreRobotOre, short clayRobotOre, short obsRobotOre, short obsRobotClay, short geodeRobotOre, short geodeRobotObs, short maxOreCost) {

}

class Helper {

    public static List<Short> extractShorts(String stringData) {
        Pattern p = Pattern.compile("(-?[\\d]+)");
        return p.matcher(stringData).results().map(MatchResult::group)
                .map(Short::valueOf)
                .collect(Collectors.toList());
    }

    public static List<Integer> extractInts(String stringData) {
        Pattern p = Pattern.compile("(-?[\\d]+)");
        return p.matcher(stringData).results().map(MatchResult::group)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public static Set<Character> stringToCharSet(String s) {
        return new HashSet<>(s.chars().mapToObj(e -> (char) e).toList());
    }

    public static List<Character> stringToList(String s) {
        return new ArrayList<>(s.chars().mapToObj(e -> (char) e).toList());
    }
}