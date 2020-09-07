package analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RabinKarpSearch {

    private final int a = 53;
    private final long m = 1_000_000_000 + 9;
    private Map<Long, List<RabinKarpPattern>> patterns;

    public RabinKarpSearch() {
    }

    public RabinKarpSearch (List<RabinKarpPattern> patterns) {
        this.patterns = groupByHash(patterns);
    }

    public void setPatterns(List<RabinKarpPattern> patterns) {
        this.patterns = groupByHash(patterns);
    }

    public List<RabinKarpPattern> search(String text, int substringLength) {


        List<RabinKarpPattern> foundedPatterns = new ArrayList<>();
        long currSubstrHash = 0;
        long pow = 1;

        for (int i = 0; i < substringLength; i++) {
            currSubstrHash += charToLong(text.charAt(text.length() - substringLength + i)) * pow;
            currSubstrHash %= m;
            if (i != substringLength - 1) {
                pow = pow * a % m;
            }
        }

        for (int i = text.length(); i >= substringLength; i--) {

                if (patterns.containsKey(currSubstrHash)) {
                    List<RabinKarpPattern> currentPatternList = patterns.get(currSubstrHash);

                    for(RabinKarpPattern currentPattern : currentPatternList) {

                        boolean patternIsFound = true;
                        for (int j = 0; j < substringLength; j++) {
                            if (text.charAt(i - substringLength + j) != currentPattern.getValue().charAt(j)) {
                                patternIsFound = false;
                                break;
                            }
                        }
                        if (patternIsFound) {
                            foundedPatterns.add(currentPattern);
                            break;
                        }
                    }
                }
            if (i > substringLength) {
                currSubstrHash = (currSubstrHash - charToLong(text.charAt(i - 1)) * pow % m + m) * a % m;
                currSubstrHash = (currSubstrHash + charToLong(text.charAt(i - substringLength - 1))) % m;
            }
        }
        return foundedPatterns;
    }

    public static long charToLong(char ch) {
        return (long)(ch);
    }

    private Map<Long, List<RabinKarpPattern>> groupByHash(List<RabinKarpPattern> patterns) {
        return patterns.stream()
                .collect(Collectors.groupingBy(pattern -> pattern.getPatternHash()));
    }
}
