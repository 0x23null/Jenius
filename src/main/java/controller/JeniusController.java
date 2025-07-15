package controller;

import java.io.IOException;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import java.util.Map;
import java.text.Normalizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.GenAIModel;
import model.Note;
import model.NotesManager;
import view.ConsoleView;

public class JeniusController {

    private final GenAIModel model;
    private final ConsoleView view;
    private final NotesManager notesManager;

    public JeniusController() throws IOException {
        String apiKey = System.getenv("GENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("GENAI_API_KEY environment variable is not set");
        }
        this.notesManager = new NotesManager();
        this.model = new GenAIModel(apiKey, notesManager);
        this.view = new ConsoleView();
    }

    public void start() {
        view.displayWelcomeMessage();

        while (true) {
            String input = view.getUserInput().trim();
            String normalized = normalize(input).toLowerCase();

            if (normalized.equals("exit") || normalized.equals("quit")) {
                break;
            }

            processCommand(input);
        }
    }

    private void processCommand(String input) {
        try {
            String normalized = normalize(input).toLowerCase();

            // Note management is handled via AI function calling.

            if (normalized.equals("list-notes")) {
                view.displayNotes(notesManager.getNotes());
                return;
            }

            // delete-note command removed; handled by AI.

            // summarize-note, questions-note and search-notes commands removed; handled by AI.

            if (normalized.equals("show-history")) {
                view.displayHistory(model.history.getMessages());
                return;
            }

            model.history.addMessage("User", input);
            GenerateContentResponse response = model.generateResponse(input);

            if (response.automaticFunctionCallingHistory().isPresent()) {
                for (Content c : response.automaticFunctionCallingHistory().get()) {
                    if (c.parts().isPresent()) {
                        for (Part p : c.parts().get()) {
                            if (p.functionCall().isPresent()) {
                                FunctionCall fc = p.functionCall().get();
                                String name = fc.name().orElse("");
                                String arg = fc.args().orElse(Map.of()).values().stream()
                                        .findFirst()
                                        .map(Object::toString)
                                        .orElse("");
                                String result = executeFunction(name, arg);
                                if (result != null) {
                                    model.history.addMessage("Jenius", result);
                                    view.displayResponse(result);
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            String text = response.text();
            model.history.addMessage("Jenius", text);
            view.displayResponse(text);
        } catch (Exception e) {
            view.displayError("Error processing command: " + e.getMessage());
        }
    }

    private String executeFunction(String name, String arg) {
        switch (name) {
            case "summarizeFile":
                return model.summarizeFile(arg);
            case "generateQuestions":
                return model.generateQuestions(arg);
            case "addNote":
                return model.addNoteFromString(arg);
            case "deleteNote":
                return model.deleteNoteById(arg);
            case "summarizeNote":
                return model.summarizeNoteById(arg);
            case "questionsNote":
                return model.questionsNoteById(arg);
            case "searchNotes":
                return model.searchNotes(arg);
            default:
                return null;
        }
    }


    private static String normalize(String s) {
        if (s == null) {
            return "";
        }
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    public static void main(String[] args) {
        Logger.getLogger("com.google.genai").setLevel(Level.SEVERE);
        try {
            JeniusController controller = new JeniusController();
            controller.start();
        } catch (IOException e) {
            System.err.println("Failed to initialize Jenius: " + e.getMessage());
        }
    }
}