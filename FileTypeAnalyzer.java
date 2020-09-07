package analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;

class FileTypeAnalyzer implements Callable<String> {

    private final RabinKarpSearch searchAlgorithm;
    private final File file;
    private  final Map<Integer, List<RabinKarpPattern>> patterns;
    private final TreeSet<RabinKarpPattern> patternsFound = new TreeSet<>(Comparator.comparingInt(RabinKarpPattern::getPriority).reversed());

    public FileTypeAnalyzer(RabinKarpSearch searchAlgorithm, Map<Integer, List<RabinKarpPattern>> patterns, File file) {
        this.searchAlgorithm = searchAlgorithm;
        this.patterns = patterns;
        this.file = file;
    }

    @Override
    public String call() throws Exception {
        search(file);
        if(patternsFound.isEmpty()) {
            return String.format("%s: %s", file.getName(), "Unknown file type");
        }
        else {
            return String.format("%s: %s", file.getName(), patternsFound.first().getType());
        }
    }

    public void search(File file) {

        for (Map.Entry<Integer, List<RabinKarpPattern>> entry : patterns.entrySet()) {

            int substringLength = entry.getKey();
            List<RabinKarpPattern> patterns = entry.getValue();
            searchAlgorithm.setPatterns(patterns);
            List<RabinKarpPattern> founded = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String fileLine = br.readLine();
                while ( fileLine != null && fileLine.length() >= substringLength) {
                    founded.addAll(searchAlgorithm.search(fileLine, substringLength));
                    if(founded.size() == patterns.size()) {
                        break;
                    }
                    fileLine = br.readLine();
                }
                patternsFound.addAll(founded);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
