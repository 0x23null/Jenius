package controller;

import java.io.IOException;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.text.Normalizer;
import java.util.logging.Level;
import java.util.logging.Logger;
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

            if (normalized.equals("show-history")) {
                view.displayHistory(model.history.getMessages());
                return;
            }

            // explicit memory commands
            if (normalized.startsWith("remember this for me") || normalized.startsWith("this is important")) {
                String fact = input.substring(input.indexOf(" ") + 1).replaceFirst("(?i)remember this for me", "").replaceFirst("(?i)this is important", "").replaceFirst(":" , "").trim();
                String analysis = model.saveToMemory(fact);
                model.history.addMessage("User", input);
                model.history.addMessage("Jenius", analysis);
                view.displayResponse(analysis);
                return;
            }

            model.saveImplicitFactFromMessage(input);
            model.history.addMessage("User", input);

            String next = input;
            while (true) {
                GenerateContentResponse response = model.generateResponse(next);
                next = null;

                if (response.automaticFunctionCallingHistory().isPresent()) {
                    boolean executed = false;
                    for (Content c : response.automaticFunctionCallingHistory().get()) {
                        if (c.parts().isPresent()) {
                            for (Part p : c.parts().get()) {
                                if (p.functionCall().isPresent()) {
                                    FunctionCall fc = p.functionCall().get();
                                    String name = fc.name().orElse("");
                                    Map<String, Object> args = fc.args().orElse(Map.of());
                                    String result = executeFunction(name, args);
                                    if (result != null) {
                                        executed = true;
                                        model.history.addMessage("Jenius", result);
                                        next = result;
                                    }
                                }
                            }
                        }
                    }
                    if (executed) {
                        continue;
                    }
                }

                String text = response.text();
                model.history.addMessage("Jenius", text);
                view.displayResponse(text);
                break;
            }
        } catch (Exception e) {
            view.displayError("Error processing command: " + e.getMessage());
        }
    }

    private String executeFunction(String name, Map<String, Object> args) {
        switch (name) {
            case "summarizeFile": {
                String path = args.values().stream().findFirst().map(Object::toString).orElse("");
                return model.summarizeFile(path);
            }
            case "createQuiz": {
                List<Object> vals = new ArrayList<>(args.values());
                String ctx = vals.size() > 0 ? vals.get(0).toString() : "";
                int count = vals.size() > 1 ? Integer.parseInt(vals.get(1).toString()) : 5;
                return model.createQuiz(ctx, count);
            }
            case "saveToMemory": {
                String fact = args.values().stream().findFirst().map(Object::toString).orElse("");
                return model.saveToMemory(fact);
            }
            case "proactiveAnalysis":
                return model.proactiveAnalysis();
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