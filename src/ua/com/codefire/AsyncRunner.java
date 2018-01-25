package ua.com.codefire;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class AsyncRunner implements Runnable {

    private final Map<String, Integer> result = new HashMap<>();
    private final String target;

    AsyncRunner(String target) {
        this.target = target;
    }

    public Map<String, Integer> getResult() {
        return result;
    }

    @Override
    public void run() {
        ExecutorService service = Executors.newFixedThreadPool(10);
        List<String> files = fileList(target);
        List<Map<String, Integer>> results = new ArrayList<>();
        List<Future<Map<String, Integer>>> tasks = new ArrayList<>();

        files.forEach(file -> tasks.add(service.submit(new AsyncProcessor(file))));

        tasks.forEach(task -> {
            try {
                results.add(task.get());
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        });

        service.shutdown();

        results.forEach(map -> map.forEach((key, value) -> {
            if (result.containsKey(key)) {
                result.put(key, result.get(key) + value);
            } else {
                result.put(key, value);
            }
        }));
    }

    private List<String> fileList(String directory) {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fileNames;
    }

}
