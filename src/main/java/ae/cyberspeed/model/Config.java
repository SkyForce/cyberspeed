package ae.cyberspeed.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Config {
    int columns;
    int rows;
    Map<String, Symbol> symbols;
    Probabilities probabilities;
    @JsonProperty("win_combinations")
    Map<String, WinCombination> winCombinations;

    @Data
    public static class Symbol {
        @JsonProperty("reward_multiplier")
        double rewardMultiplier;
        String type;
        String impact;
        double extra;
    }

    @Data
    public static class Probabilities {
        @JsonProperty("standard_symbols")
        List<SymbolProbability> standardSymbols;
        @JsonProperty("bonus_symbols")
        SymbolProbability bonusSymbols;

        @Data
        public static class SymbolProbability {
            int column;
            int row;
            Map<String, Integer> symbols;
        }
    }

    @Data
    public static class WinCombination {
        @JsonProperty("reward_multiplier")
        double rewardMultiplier;
        String when;
        String group;
        int count;
        @JsonProperty("covered_areas")
        List<List<String>> coveredAreas;
    }
}
