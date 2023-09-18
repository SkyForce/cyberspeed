package ae.cyberspeed.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Output {
    List<List<String>> matrix;
    double reward;
    @JsonProperty("applied_winning_combinations")
    Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
    @JsonProperty("applied_bonus_symbol")
    String appliedBonusSymbol;
}
