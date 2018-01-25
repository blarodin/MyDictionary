package ua.com.codefire;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        AsyncRunner runner = new AsyncRunner("books");
        Thread thread = new Thread(runner);
        thread.start();
        thread.join();

        Map<String, Integer> unsortMap = runner.getResult();
        System.out.println("Collected: " + unsortMap.size());

        Map<String, Integer> result = unsortMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new
                        )
                );

        result.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
