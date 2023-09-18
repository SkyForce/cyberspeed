package ae.cyberspeed;

import ae.cyberspeed.game.Scratch;
import ae.cyberspeed.interfaces.MatrixGeneratorInterface;
import ae.cyberspeed.model.Config;
import ae.cyberspeed.model.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScratchTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Test
    void testScratchBet1() throws IOException {
        Config conf = objectMapper.readValue(new File("src/test/resources/config.json"), Config.class);
        Scratch scratch = new Scratch(conf, new MatrixGeneratorInterface() {
            @Override
            public List<List<String>> generateMatrix(int rows, int columns, List<Config.Probabilities.SymbolProbability> symbolWeights,
                                                     String bonusSymbolName, Config.Symbol bonusSymbol) {
                return List.of(List.of("A", "B", "C"), List.of("B", "A", "A"), List.of("D", "+500", "A"));
            }

            @Override
            public String getRandomWeightSymbol(Config.Probabilities.SymbolProbability bonusSymbols) {
                return "+500";
            }
        });
        Output output = scratch.bet(100);
        assertEquals(List.of(List.of("A", "B", "C"), List.of("B", "A", "A"), List.of("D", "+500", "A")), output.getMatrix());
        assertEquals(38000, output.getReward());
        assertEquals("+500", output.getAppliedBonusSymbol());
        assertEquals(2, output.getAppliedWinningCombinations().get("A").size());
        assertTrue(output.getAppliedWinningCombinations().get("A").containsAll(List.of("same_symbol_4_times",
                "same_symbols_diagonally_left_to_right")));
    }

    @Test
    void testScratchBet2() throws IOException {
        Config conf = objectMapper.readValue(new File("src/test/resources/config.json"), Config.class);
        Scratch scratch = new Scratch(conf, new MatrixGeneratorInterface() {
            @Override
            public List<List<String>> generateMatrix(int rows, int columns, List<Config.Probabilities.SymbolProbability> symbolWeights,
                                                     String bonusSymbolName, Config.Symbol bonusSymbol) {
                return List.of(List.of("A", "A", "A"), List.of("A", "A", "A"), List.of("A", "A", "A"));
            }

            @Override
            public String getRandomWeightSymbol(Config.Probabilities.SymbolProbability bonusSymbols) {
                return "MISS";
            }
        });
        Output output = scratch.bet(100);
        assertEquals(List.of(List.of("A", "A", "A"), List.of("A", "A", "A"), List.of("A", "A", "A")), output.getMatrix());
        assertEquals(10000000, output.getReward());
        assertNull(output.getAppliedBonusSymbol());
        assertEquals(5, output.getAppliedWinningCombinations().get("A").size());
        assertTrue(output.getAppliedWinningCombinations().get("A").containsAll(List.of("same_symbols_diagonally_right_to_left",
                "same_symbols_vertically", "same_symbol_9_times", "same_symbols_diagonally_left_to_right", "same_symbols_horizontally")));
    }

    @Test
    void testScratchBet3() throws IOException {
        Config conf = objectMapper.readValue(new File("src/test/resources/config.json"), Config.class);
        Scratch scratch = new Scratch(conf, new MatrixGeneratorInterface() {
            @Override
            public List<List<String>> generateMatrix(int rows, int columns, List<Config.Probabilities.SymbolProbability> symbolWeights,
                                                     String bonusSymbolName, Config.Symbol bonusSymbol) {
                return List.of(List.of("A", "B", "A"), List.of("A", "B", "A"), List.of("A", "B", "10x"));
            }

            @Override
            public String getRandomWeightSymbol(Config.Probabilities.SymbolProbability bonusSymbols) {
                return "10x";
            }
        });
        Output output = scratch.bet(100);
        assertEquals(List.of(List.of("A", "B", "A"), List.of("A", "B", "A"), List.of("A", "B", "10x")), output.getMatrix());
        assertEquals(250000, output.getReward());
        assertEquals("10x", output.getAppliedBonusSymbol());
        assertEquals(2, output.getAppliedWinningCombinations().get("A").size());
        assertTrue(output.getAppliedWinningCombinations().get("A").containsAll(List.of("same_symbols_vertically", "same_symbol_5_times")));
        assertEquals(2, output.getAppliedWinningCombinations().get("B").size());
        assertTrue(output.getAppliedWinningCombinations().get("B").containsAll(List.of("same_symbols_vertically", "same_symbol_3_times")));
    }
}
