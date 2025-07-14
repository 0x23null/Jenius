package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MemoryManager {
    private static final Path MEMORY_PATH = Path.of("memory.json");
    private final Gson gson = new Gson();
    private List<String> facts;

    public MemoryManager() {
        load();
    }

    private void load() {
        try {
            if (Files.exists(MEMORY_PATH)) {
                try (Reader r = Files.newBufferedReader(MEMORY_PATH)) {
                    Type listType = new TypeToken<List<String>>(){}.getType();
                    facts = gson.fromJson(r, listType);
                }
            }
        } catch (IOException e) {
            facts = new ArrayList<>();
        }
        if (facts == null) {
            facts = new ArrayList<>();
        }
    }

    public synchronized void saveFact(String fact) {
        facts.add(fact);
        persist();
    }

    public synchronized List<String> getFacts() {
        return new ArrayList<>(facts);
    }

    private void persist() {
        try (Writer w = Files.newBufferedWriter(MEMORY_PATH)) {
            gson.toJson(facts, w);
        } catch (IOException ignored) {
        }
    }
}
