package analyzer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Argument list error");
            System.exit(0);
        }

        File testFilesDirectory = new File(args[0]);
        File patternsDb = new File(args[1]);
        File[] testFiles = testFilesDirectory.listFiles();

        if (testFiles.length == 0) {
            System.out.println("The file directory is empty!");
            return;
        }

        List<RabinKarpPattern> patternFlatList =  getPatternsList(patternsDb);
        Map<Integer, List<RabinKarpPattern>> patterns = patternFlatList.stream()
                .collect(Collectors.groupingBy(pattern -> pattern.getValue().length()));

        List<FileTypeAnalyzer> workers = new ArrayList<>();
        for (int i = 0; i < testFiles.length; i++) {
            if (testFiles[i].isFile()) {
                workers.add(new FileTypeAnalyzer(new RabinKarpSearch(), patterns, testFiles[i]));
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(workers.size());

        List<Future<String>> futures;
        List<String> results = new ArrayList<>();

        try {
            futures = executorService.invokeAll(workers);
            for (Future<String> future : futures) {
                results.add(future.get());
            }

        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for (String result : results) {
            System.out.println(result);
        }
        executorService.shutdown();
    }

    private static List<RabinKarpPattern> getPatternsList(File patternsFile) {

        List<RabinKarpPattern> patterns = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(patternsFile, StandardCharsets.UTF_8))) {

            String fileLine = br.readLine();

            while (fileLine != null) {
                String[] arr = fileLine.split(";");
                int priority = Integer.parseInt(arr[0]);
                String value = arr[1].substring(1, arr[1].length() - 1);
                String type = arr[2].substring(1, arr[2].length() - 1);
                patterns.add(new RabinKarpPattern(priority, value, type));
                fileLine = br.readLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return patterns;
    }
}
