package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages notes and persists them to a JSON file.
 */
public class NotesManager {
    private final List<Note> notes = new ArrayList<>();
    private final Path storagePath = Path.of("notes.json");
    private final Gson gson = new Gson();
    private int nextId = 1;

    public NotesManager() {
        load();
    }

    public void addNote(String title, String content) {
        notes.add(new Note(nextId++, title, content));
        save();
    }

    public List<Note> getNotes() {
        return new ArrayList<>(notes);
    }

    public Optional<Note> getNote(int id) {
        return notes.stream().filter(n -> n.getId() == id).findFirst();
    }

    public boolean deleteNote(int id) {
        boolean removed = notes.removeIf(n -> n.getId() == id);
        if (removed) {
            save();
        }
        return removed;
    }

    private void load() {
        if (Files.exists(storagePath)) {
            try {
                Type listType = new TypeToken<ArrayList<Note>>(){}.getType();
                List<Note> loaded = gson.fromJson(Files.readString(storagePath), listType);
                if (loaded != null) {
                    notes.addAll(loaded);
                    nextId = notes.stream().mapToInt(Note::getId).max().orElse(0) + 1;
                }
            } catch (IOException e) {
                // ignore loading errors and start fresh
            }
        }
    }

    private void save() {
        try {
            Files.writeString(storagePath, gson.toJson(notes));
        } catch (IOException e) {
            // ignore saving errors
        }
    }
}
