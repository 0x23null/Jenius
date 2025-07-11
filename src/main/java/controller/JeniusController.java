package controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import model.GenAIModel;
import view.ConsoleView;

public class JeniusController {

    private final GenAIModel model;
    private final ConsoleView view;

    public JeniusController() throws IOException {
        String apiKey = System.getenv("GENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("GENAI_API_KEY environment variable is not set");
        }
        this.model = new GenAIModel(apiKey);
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

            if (normalized.startsWith("tom tat ")) {
                String filePath = input.substring(7).trim();
                processFileSummary(filePath);
            } else if (normalized.startsWith("tao cau hoi ")) {
                String topicOrFile = input.substring(11).trim();
                processQuestions(topicOrFile);
            } else if (normalized.equals("show-history")) {
                // Show history
                // Display history in a more readable format, not just the raw model history
                view.displayHistory(model.history.getMessages());
            } else {
                String concept = input.trim();
                if (!concept.isEmpty()) {
                    // Add user message to history
                    model.history.addMessage("User", concept);
                    String response = model.explainConcept(concept);
                    // Add AI response to history
                    model.history.addMessage("Jenius", response);
                    view.displayResponse(response);

                } else {
                    view.displayError("Please provide a concept to explain");
                }
            }
        } catch (Exception e) {
            view.displayError("Error processing command: " + e.getMessage());
        }
    }

    private void processFileSummary(String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));
            // Add user message to history
            model.history.addMessage("User", "summarize " + filePath);
            String summary = model.summarizeContent(content);
            // Add AI response to history
            model.history.addMessage("Jenius", summary);
            view.displayResponse(summary);
        } catch (IOException e) {
            view.displayError("Error reading file: " + e.getMessage());
        }
    }

    private void processQuestions(String topicOrFile) {
        try {
            // Add user message to history
            model.history.addMessage("User", "questions " + topicOrFile);
            if (Files.exists(Paths.get(topicOrFile))) {
                String content = Files.readString(Paths.get(topicOrFile));
                String questions = model.generateQuestions(content);
                // Add AI response to history
                model.history.addMessage("Jenius", questions);
                view.displayResponse(questions);
            } else {
                String questions = model.generateQuestions(topicOrFile);
                // Add AI response to history
                model.history.addMessage("Jenius", questions);
                view.displayResponse(questions);
            }
        } catch (IOException e) {
            view.displayError("Error processing questions: " + e.getMessage());
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
        try {
            JeniusController controller = new JeniusController();
            controller.start();
        } catch (IOException e) {
            System.err.println("Failed to initialize Jenius: " + e.getMessage());
        }
    }
}