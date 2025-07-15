package model;

import java.util.ArrayList;
import java.util.List;

public class NotesManager {
    private final List<Note> notes = new ArrayList<>();

    public void addNote(String title, String content) {
        notes.add(new Note(title, content));
    }

    public List<Note> getNotes() {
        return new ArrayList<>(notes);
    }

    public boolean deleteNote(String title) {
        return notes.removeIf(n -> n.getTitle().equalsIgnoreCase(title));
    }
}
