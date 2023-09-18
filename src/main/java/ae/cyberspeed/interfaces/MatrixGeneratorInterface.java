package ae.cyberspeed.interfaces;

import ae.cyberspeed.model.Config;

import java.util.List;

public interface MatrixGeneratorInterface {
    List<List<String>> generateMatrix(int rows, int columns, List<Config.Probabilities.SymbolProbability> symbolWeights,
                                      String bonusSymbolName, Config.Symbol bonusSymbol);

    String getRandomWeightSymbol(Config.Probabilities.SymbolProbability symbolProbability);
}
