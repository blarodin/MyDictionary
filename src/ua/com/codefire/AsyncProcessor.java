package ua.com.codefire;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

public class AsyncProcessor implements Callable<Map<String, Integer>> {

    private final String fileName;

    AsyncProcessor(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Map<String, Integer> call() {
        List<String> data = new ArrayList<>();
        Map<String, Integer> result = new HashMap<>();
        Path path = Paths.get(fileName);
        try(BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] words = line.split(Options.delims);
                Arrays.stream(words).forEach(word -> {
                    if(Options.letters <= 0 || word.length() > 3) {
                        data.add(word.toLowerCase());
                    }
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        data.forEach(key -> {
            if(result.containsKey(key)) {
                result.put(key, result.get(key) + 1);
            } else {
                result.put(key, 1);
            }
        });
        return result;
    }
}
