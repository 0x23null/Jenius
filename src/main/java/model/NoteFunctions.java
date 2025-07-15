package model;

import java.util.List;

/**
 * Static helper functions for AI tool calling related to notes management.
 */
public class NoteFunctions {
    private static NotesManager notesManager;
    private static GenAIModel model;

    /** Initialize with the active NotesManager and GenAIModel instances. */
    public static void init(NotesManager manager, GenAIModel gm) {
        notesManager = manager;
        model = gm;
    }

    /**
     * Add a new note. The title is generated automatically by summarizing the
     * content. Returns the generated title.
     */
    public static String addNote(String content) {
        if (notesManager == null || model == null) {
            return "Notes system not initialized";
        }
        String title = model.summarizeContent(content);
        // keep title short
        if (title.length() > 60) {
            title = title.substring(0, 60);
        }
        notesManager.addNote(title, content);
        return "Added note '" + title + "'";
    }

    /** Delete a note by its 1-based index. */
    public static String deleteNote(String indexStr) {
        if (notesManager == null) {
            return "Notes system not initialized";
        }
        try {
            int idx = Integer.parseInt(indexStr.trim()) - 1;
            Note removed = notesManager.deleteNote(idx);
            if (removed != null) {
                return "Deleted note '" + removed.getTitle() + "'";
            }
            return "Note not found";
        } catch (NumberFormatException e) {
            return "Invalid note id";
        }
    }

    /** List all notes with their indices. */
    public static String listNotes() {
        if (notesManager == null) {
            return "Notes system not initialized";
        }
        List<Note> notes = notesManager.getNotes();
        if (notes.isEmpty()) {
            return "No notes";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < notes.size(); i++) {
            sb.append(i + 1).append(". ").append(notes.get(i).getTitle()).append("\n");
        }
        return sb.toString();
    }

    /** Generate questions from a note's content using its 1-based index. */
    public static String generateQuestionsFromNote(String indexStr) {
        if (notesManager == null || model == null) {
            return "Notes system not initialized";
        }
        try {
            int idx = Integer.parseInt(indexStr.trim()) - 1;
            Note note = notesManager.getNote(idx);
            if (note != null) {
                return model.generateQuestions(note.getContent());
            }
            return "Note not found";
        } catch (NumberFormatException e) {
            return "Invalid note id";
        }
    }
}
