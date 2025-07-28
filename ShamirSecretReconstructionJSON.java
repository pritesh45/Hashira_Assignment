import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigInteger;
import java.util.*;

public class ShamirSecretReconstructionJSON {

    static int N, K;
    static final BigInteger MOD = new BigInteger("104729"); 

    static class Share {
        BigInteger x, y;
        Share(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    static BigInteger lagrangeInterpolation(List<Share> shares) {
        BigInteger secret = BigInteger.ZERO;

        for (int i = 0; i < shares.size(); i++) {
            BigInteger xi = shares.get(i).x;
            BigInteger yi = shares.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < shares.size(); j++) {
                if (i == j) continue;
                BigInteger xj = shares.get(j).x;

                numerator = numerator.multiply(xj.negate()).mod(MOD);
                denominator = denominator.multiply(xi.subtract(xj)).mod(MOD);
            }

            BigInteger invDenominator = denominator.modInverse(MOD);
            BigInteger term = yi.multiply(numerator).mod(MOD).multiply(invDenominator).mod(MOD);

            secret = secret.add(term).mod(MOD);
        }

        return secret;
    }

    static void generateCombinations(List<Share> shares, int start, List<Share> current,
                                     List<List<Share>> result) {
        if (current.size() == K) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < shares.size(); i++) {
            current.add(shares.get(i));
            generateCombinations(shares, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Paste your JSON input below (end with empty line):");
        StringBuilder jsonInput = new StringBuilder();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) break;
            jsonInput.append(line);
        }

      
        JSONObject input = new JSONObject(jsonInput.toString());
        N = input.getInt("N");
        K = input.getInt("K");

        JSONArray sharesArray = input.getJSONArray("shares");
        List<Share> shares = new ArrayList<>();

        for (int i = 0; i < sharesArray.length(); i++) {
            JSONObject obj = sharesArray.getJSONObject(i);
            BigInteger x = new BigInteger(obj.get("x").toString());
            BigInteger y = new BigInteger(obj.get("y").toString());
            shares.add(new Share(x, y));
        }

        List<List<Share>> combinations = new ArrayList<>();
        generateCombinations(shares, 0, new ArrayList<>(), combinations);

        Map<BigInteger, Integer> frequencyMap = new HashMap<>();

        for (List<Share> combo : combinations) {
            BigInteger secret = lagrangeInterpolation(combo);
            frequencyMap.put(secret, frequencyMap.getOrDefault(secret, 0) + 1);
        }  
        BigInteger finalSecret = null;
        int maxFreq = 0;
        for (Map.Entry<BigInteger, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFreq) {
                maxFreq = entry.getValue();
                finalSecret = entry.getKey();
            }
        }
        System.out.println("\n Reconstructed Secret (f(0)) = " + finalSecret);
    }
}
