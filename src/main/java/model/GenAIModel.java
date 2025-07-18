package model;

import com.google.genai.Client;
import com.google.genai.types.Candidate;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.Tool;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import model.NotesManager;

public class GenAIModel {

    private final Client client;
    public final ConversationHistory history;
    private static GenAIModel activeModel;
    private final NotesManager notesManager;

    public GenAIModel(String apiKey, NotesManager notesManager) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.history = new ConversationHistory();
        this.notesManager = notesManager;
        activeModel = this;
    }

    public String explainConcept(String concept) {
        return generateResponse(concept).text();
    }

    public String summarizeContent(String content) {
        String prompt = "Summarize the following content in 50-100 words:\n" + content;
        return generateTaskResponse(prompt).text();
    }

    public String generateQuestions(String input) {
        String prompt = "Generate 5 multiple-choice questions based on " + input + " with 4 answer options each.";
        return generateTaskResponse(prompt).text();
    }

    public String summarizeFile(String path) {
        try {
            String content = Files.readString(Path.of(path));
            return summarizeContent(content);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    // Note management helpers
    public String addNoteFromString(String input) {
        String[] parts = input.split("\\|", 2);
        if (parts.length < 2) {
            return "Invalid addNote format. Use 'title|content'.";
        }
        notesManager.addNote(parts[0].trim(), parts[1].trim());
        return "Note added";
    }

    public String deleteNoteById(String idStr) {
        try {
            int id = Integer.parseInt(idStr.trim());
            boolean removed = notesManager.deleteNote(id);
            return removed ? "Note deleted" : "Note not found";
        } catch (NumberFormatException e) {
            return "Invalid note id";
        }
    }

    public String summarizeNoteById(String idStr) {
        try {
            int id = Integer.parseInt(idStr.trim());
            return notesManager.getNote(id)
                    .map(n -> summarizeContent(n.getContent()))
                    .orElse("Note not found");
        } catch (NumberFormatException e) {
            return "Invalid note id";
        }
    }

    public String questionsNoteById(String idStr) {
        try {
            int id = Integer.parseInt(idStr.trim());
            return notesManager.getNote(id)
                    .map(n -> generateQuestions(n.getContent()))
                    .orElse("Note not found");
        } catch (NumberFormatException e) {
            return "Invalid note id";
        }
    }

    public String searchNotes(String query) {
        String q = query.toLowerCase();
        var results = notesManager.getNotes().stream()
                .filter(n -> n.getTitle().toLowerCase().contains(q)
                        || n.getContent().toLowerCase().contains(q))
                .map(n -> "[" + n.getId() + "] " + n.getTitle() + ": " + n.getContent())
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            return "No matching notes";
        }
        return String.join("\n", results);
    }

    /**
     * Static wrapper for summarizeFile that delegates to the active model
     * instance. This allows function calls to be registered using static
     * methods while still executing on the current model.
     */
    public static String summarizeFileStatic(String path) {
        if (activeModel == null) {
            throw new IllegalStateException("No active GenAIModel instance");
        }
        return activeModel.summarizeFile(path);
    }

    /**
     * Static wrapper for generateQuestions that delegates to the active model
     * instance.
     */
    public static String generateQuestionsStatic(String input) {
        if (activeModel == null) {
            throw new IllegalStateException("No active GenAIModel instance");
        }
        return activeModel.generateQuestions(input);
    }

    public static String deleteNoteStatic(String id) {
        if (activeModel == null) {
            throw new IllegalStateException("No active GenAIModel instance");
        }
        return activeModel.deleteNoteById(id);
    }

    public static String summarizeNoteStatic(String id) {
        if (activeModel == null) {
            throw new IllegalStateException("No active GenAIModel instance");
        }
        return activeModel.summarizeNoteById(id);
    }

    public static String questionsNoteStatic(String id) {
        if (activeModel == null) {
            throw new IllegalStateException("No active GenAIModel instance");
        }
        return activeModel.questionsNoteById(id);
    }

    public static String searchNotesStatic(String query) {
        if (activeModel == null) {
            throw new IllegalStateException("No active GenAIModel instance");
        }
        return activeModel.searchNotes(query);
    }

    public GenerateContentResponse generateResponse(String currentPrompt) {
        try {
            String fullPrompt = history.getHistoryForModel() + currentPrompt;

            Tool tool = Tool.builder()
                    .functions(List.of(
                            GenAIModel.class.getMethod("summarizeFileStatic", String.class),
                            GenAIModel.class.getMethod("generateQuestionsStatic", String.class),
                            GenAIModel.class.getMethod("deleteNoteStatic", String.class),
                            GenAIModel.class.getMethod("summarizeNoteStatic", String.class),
                            GenAIModel.class.getMethod("questionsNoteStatic", String.class),
                            GenAIModel.class.getMethod("searchNotesStatic", String.class)
                    ))
                    .build();

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .tools(List.of(tool))
                    .build();

            return client.models.generateContent("gemini-2.0-flash-001", fullPrompt, config);
        } catch (Exception e) {
            return GenerateContentResponse.builder()
                    .candidates(List.of(
                            Candidate.builder()
                                    .content(Content.builder()
                                            .parts(List.of(Part.fromText("Error generating response: " + e.getMessage())))
                                            .build())
                                    .build()))
                    .build();
        }
    }

    private GenerateContentResponse generateTaskResponse(String promptForTask) {
        try {
            GenerateContentConfig config = GenerateContentConfig.builder().build();
            return client.models.generateContent("gemini-2.0-flash-001", promptForTask, config);
        } catch (Exception e) {
            return GenerateContentResponse.builder()
                    .candidates(List.of(
                            Candidate.builder()
                                    .content(Content.builder()
                                            .parts(List.of(Part.fromText("Error generating response: " + e.getMessage())))
                                            .build())
                                    .build()))
                    .build();
        }
    }

    public ConversationHistory getHistory() {
        return history;
    }
}
