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

// memory handling
import java.util.Optional;

public class GenAIModel {

    private final Client client;
    public final ConversationHistory history;
    private final MemoryManager memory;
    private static GenAIModel activeModel;

    public GenAIModel(String apiKey) {
        this.client = Client.builder().apiKey(apiKey).build();
        this.history = new ConversationHistory();
        this.memory = new MemoryManager();
        activeModel = this;
    }

    public String explainConcept(String concept) {
        return generateResponse(concept).text();
    }

    public String summarizeContent(String content) {
        String prompt = "Summarize the following content in 100-150 words:\n" + content;
        return generateTaskResponse(prompt).text();
    }

    public String createQuiz(String context, int questionCount) {
        String prompt = "Create " + questionCount + " multiple-choice questions based on the following text:\n" + context;
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

    public String saveToMemory(String fact) {
        memory.saveFact(fact);
        return proactiveAnalysis();
    }

    public String proactiveAnalysis() {
        String joined = memory.getFacts().stream().collect(Collectors.joining("\n"));
        String prompt = "You are a research analyst AI. Find non-obvious connections between these facts about the user and explain them succinctly:\n" + joined;
        return generateTaskResponse(prompt).text();
    }

    public String extractImportantFact(String message) {
        String prompt = "From the following user statement, extract a short factual snippet that would help you understand the user better. If nothing new is learned, just return an empty string.\n" + message;
        return generateTaskResponse(prompt).text().trim();
    }

    public void saveImplicitFactFromMessage(String message) {
        String fact = extractImportantFact(message);
        if (fact != null && !fact.isBlank()) {
            saveToMemory(fact);
        }
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

    /** Static wrapper for createQuiz */
    public static String createQuizStatic(String context, int questionCount) {
        if (activeModel == null) {
            throw new IllegalStateException("No active GenAIModel instance");
        }
        return activeModel.createQuiz(context, questionCount);
    }

    /** Static wrapper for saveToMemory */
    public static String saveToMemoryStatic(String fact) {
        if (activeModel == null) {
            throw new IllegalStateException("No active GenAIModel instance");
        }
        return activeModel.saveToMemory(fact);
    }

    /** Static wrapper for proactiveAnalysis */
    public static String proactiveAnalysisStatic() {
        if (activeModel == null) {
            throw new IllegalStateException("No active GenAIModel instance");
        }
        return activeModel.proactiveAnalysis();
    }


    public GenerateContentResponse generateResponse(String currentPrompt) {
        try {
            String fullPrompt = history.getHistoryForModel() + currentPrompt;

            Tool tool = Tool.builder()
                    .functions(List.of(
                            GenAIModel.class.getMethod("summarizeFileStatic", String.class),
                            GenAIModel.class.getMethod("createQuizStatic", String.class, int.class),
                            GenAIModel.class.getMethod("saveToMemoryStatic", String.class),
                            GenAIModel.class.getMethod("proactiveAnalysisStatic")
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
