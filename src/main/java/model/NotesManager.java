package model;

import java.util.ArrayList;
import java.util.List;

public class NotesManager {
    private final List<Note> notes = new ArrayList<>();

    public void addNote(String title, String content) {
        notes.add(new Note(title, content));
    }

    public Note getNote(int index) {
        if (index >= 0 && index < notes.size()) {
            return notes.get(index);
        }
        return null;
    }

    public List<Note> getNotes() {
        return new ArrayList<>(notes);
    }

    public Note deleteNote(int index) {
        if (index >= 0 && index < notes.size()) {
            return notes.remove(index);
        }
        return null;
    }
}
