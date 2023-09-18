package ae.cyberspeed;

import ae.cyberspeed.game.Scratch;
import ae.cyberspeed.impl.MatrixGenerator;
import ae.cyberspeed.model.Config;
import ae.cyberspeed.model.Output;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) throws IOException {
        Map<String, String> argsMap = new HashMap<>();
        for (int i = 0; i < args.length - 1; i += 2) {
            argsMap.put(args[i], args[i+1]);
        }
        String config = getPropertyOrThrow(argsMap, "--config", "No config arg");
        String bettingAmountStr = getPropertyOrThrow(argsMap, "--betting-amount", "No betting amount arg");
        double bettingAmount = Double.parseDouble(bettingAmountStr);
        if (bettingAmount <= 0) {
            throw new IllegalArgumentException("betting amount must be positive");
        }
        Config conf = objectMapper.readValue(new File(config), Config.class);
        Scratch scratch = new Scratch(conf, new MatrixGenerator());
        Output output = scratch.bet(bettingAmount);
        objectMapper.writeValue(System.out, output);
    }

    private static String getPropertyOrThrow(Map<String, String> argsMap, String key, String errorMessage) {
        String value = argsMap.get(key);
        if (value == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value;
    }
}