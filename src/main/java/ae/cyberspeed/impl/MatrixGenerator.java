package ae.cyberspeed.impl;

import ae.cyberspeed.interfaces.MatrixGeneratorInterface;
import ae.cyberspeed.model.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MatrixGenerator implements MatrixGeneratorInterface {
    Random rand = new Random();
    @Override
    public List<List<String>> generateMatrix(int rows, int columns, List<Config.Probabilities.SymbolProbability> symbolWeights,
                                             String bonusSymbolName, Config.Symbol bonusSymbol) {
        List<List<String>> matrix = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                row.add(null);
            }
            matrix.add(row);
        }

        for (Config.Probabilities.SymbolProbability symbolProbability: symbolWeights) {
            String symbol = getRandomWeightSymbol(symbolProbability);
            matrix.get(symbolProbability.getRow()).set(symbolProbability.getColumn(), symbol);
        }

        if (!"miss".equals(bonusSymbol.getImpact())) {
            matrix.get(rand.nextInt(rows)).set(rand.nextInt(columns), bonusSymbolName);
        }

        return matrix;
    }

    @Override
    public String getRandomWeightSymbol(Config.Probabilities.SymbolProbability symbolProbability) {
        int totalWeight = symbolProbability.getSymbols().values().stream().reduce(0, Integer::sum);
        int randomIdx = rand.nextInt(totalWeight);
        String symbol = null;
        for (Map.Entry<String, Integer> entry: symbolProbability.getSymbols().entrySet()) {
            if (randomIdx < entry.getValue()) {
                symbol = entry.getKey();
                break;
            }
            randomIdx -= entry.getValue();
        }
        return symbol;
    }
}
