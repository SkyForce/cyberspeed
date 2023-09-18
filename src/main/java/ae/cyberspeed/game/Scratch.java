package ae.cyberspeed.game;

import ae.cyberspeed.interfaces.MatrixGeneratorInterface;
import ae.cyberspeed.model.Config;
import ae.cyberspeed.model.Output;

import java.util.*;

public class Scratch {
    private final Config config;
    private final Map<String, List<String>> winningCombinationsByGroup = new HashMap<>();
    private final MatrixGeneratorInterface matrixGenerator;
    private final List<String> standardSymbols;

    public Scratch(Config conf, MatrixGeneratorInterface matrixGenerator) {
        this.config = conf;
        this.matrixGenerator = matrixGenerator;
        conf.getWinCombinations().forEach((key, value) -> putWinningCombinationToMap(winningCombinationsByGroup, value.getGroup(), key));
        standardSymbols = conf.getSymbols().entrySet().stream()
                .filter(entry -> "standard".equals(entry.getValue().getType()))
                .map(Map.Entry::getKey)
                .toList();
    }

    public Output bet(double bettingAmount) {
        Output output = new Output();
        String bonusSymbolName = matrixGenerator.getRandomWeightSymbol(config.getProbabilities().getBonusSymbols());
        Config.Symbol bonusSymbol = config.getSymbols().get(bonusSymbolName);
        List<List<String>> matrix = matrixGenerator.generateMatrix(config.getRows(), config.getColumns(),
                config.getProbabilities().getStandardSymbols(), bonusSymbolName, bonusSymbol);
        output.setMatrix(matrix);

        Map<String, Integer> symbolsCount = new HashMap<>();
        matrix.forEach(row -> row.forEach(symb -> {
            if (symb != null) {
                symbolsCount.merge(symb, 1, Integer::sum);
            }
        }));

        double reward = 0;

        for (String symbol: standardSymbols) {
            double symbolReward = bettingAmount * config.getSymbols().get(symbol).getRewardMultiplier();
            boolean symbMatch = false;
            for (List<String> winningCombinations : winningCombinationsByGroup.values()) {
                for (String winningCombination: winningCombinations) {
                    Config.WinCombination winCombination = config.getWinCombinations().get(winningCombination);
                    if ("same_symbols".equals(winCombination.getWhen()) && winCombination.getCount() == symbolsCount.getOrDefault(symbol, 0)) {
                        symbolReward *= winCombination.getRewardMultiplier();
                        symbMatch = true;
                        putWinningCombinationToMap(output.getAppliedWinningCombinations(), symbol, winningCombination);
                        break;
                    }
                    else if ("linear_symbols".equals(winCombination.getWhen())) {
                        boolean areaMatch = false;
                        for (List<String> area: winCombination.getCoveredAreas()) {
                            areaMatch = area.stream().allMatch(cell -> {
                                String[] rc = cell.split(":");
                                return symbol.equals(matrix.get(Integer.parseInt(rc[0])).get(Integer.parseInt(rc[1])));
                            });
                            if (areaMatch) {
                                break;
                            }
                        }
                        if (areaMatch) {
                            symbMatch = true;
                            symbolReward *= winCombination.getRewardMultiplier();
                            putWinningCombinationToMap(output.getAppliedWinningCombinations(), symbol, winningCombination);
                            break;
                        }
                    }
                }
            }
            if (symbMatch) {
                reward += symbolReward;
            }
        }
        if (reward > 0 && !"miss".equals(bonusSymbol.getImpact())) {
            if ("multiply_reward".equals(bonusSymbol.getImpact())) {
                reward *= bonusSymbol.getRewardMultiplier();
            }
            else if ("extra_bonus".equals(bonusSymbol.getImpact())) {
                reward += bonusSymbol.getExtra();
            }
            output.setAppliedBonusSymbol(bonusSymbolName);
        }
        output.setReward(reward);
        return output;
    }

    private static void putWinningCombinationToMap(Map<String, List<String>> output, String symbol, String winningCombination) {
        output.putIfAbsent(symbol, new ArrayList<>());
        output.get(symbol).add(winningCombination);
    }
}

