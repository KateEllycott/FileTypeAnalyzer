package analyzer;

public class RabinKarpPattern {

    private final int priority;
    private final String value;
    private final String type;
    private final long patternHash;

    public RabinKarpPattern(int priority, String value, String type) {
        this.priority = priority;
        this.value = value;
        this.type = type;
        patternHash = patternHash();
    }

    private long patternHash() {
        int a = 53;
        long m = 1_000_000_000 + 9;
        long patternHash = 0;
        long pow = 1;

        for (int i = 0; i < value.length(); i++) {
            patternHash += charToLong(value.charAt(i)) * pow;
            patternHash %= m;
            if (i != value.length() - 1) {
                pow = pow * a % m;
            }
        }
        return patternHash;
    }

    public long getPatternHash() {
        return patternHash;
    }

    public int getPriority() {
        return priority;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public static long charToLong(char ch) {
        return (long)(ch);
    }
}
